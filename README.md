# Dock Webhook Receiver

Projeto mínimo para receber webhooks da Dock, descriptografar e publicar no SNS.

Decisões e suposições importantes:
- O implementado espera um envelope JSON com campos Base64: `encrypted_key`, `iv`, `ciphertext`.
- RSA: usa `RSA/ECB/OAEPWithSHA-256AndMGF1Padding` para desembalar a chave AES.
- AES: usa `AES/GCM/NoPadding` para descriptografar o `ciphertext` com o `iv`.
- Persistência: o envelope cru é salvo localmente em `data/envelopes/` como fallback.
- SNS: usa AWS SDK v2; configurar `aws.sns.topic-arn` e credenciais via ambiente.

Se alguma dessas suposições diverge da documentação real da Dock, atualize `DockDecryptService` e `application.yml` antes de usar em produção.

Execução:

```bash
mvn spring-boot:run
```

Integration tests:

These tests use Testcontainers + LocalStack to validate SNS/SQS flows.

```bash
mvn -DskipUnitTests=false -DskipITs=false test
```

To run the specific integration test class:

```bash
mvn -Dtest=com.roadcard.dockwebhook.integration.AwsSnsSqsIntegrationTest test
```

Event identification and SNS attribute
------------------------------------

This project identifies event type heuristically from the decrypted JSON using the following order:

- `event` field if present (explicit event name),
- `status_id`: `0` -> `Purchase_processed`, `3` -> `Purchase_approved`,
- `status` text: `"Processada"` -> `Purchase_processed`, `"Pendente"` -> `Purchase_approved`.

When publishing to SNS the app sets a message attribute `EventType` with the detected event (if any). Consumers can use this attribute to route messages.

