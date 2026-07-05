# Arquitetura da Solução

Visão geral dos componentes e responsabilidades.

Componentes principais
- Controller: `WebhookController` — expõe o endpoint POST `/api/v1/webhooks/dock`, responde 200 imediatamente e delega processamento.
- Service: `WebhookService` — processamento assíncrono do envelope, persistência do envelope cru e orquestração de decrypt + publicação.
- DecryptService: `DockDecryptService` — descriptografa o envelope usando RSA (desembala chave AES) e AES-GCM para obter o JSON do evento.
- SNSService: `AwsSnsService` — publica mensagens no tópico SNS; em falhas envia para DLQ (SQS) via `AwsSqsDlqService`.
- Idempotency: `JpaIdempotencyService` — previne reprocessamento usando tabela `idempotency_record`.
- Persistence fallback: `LocalFilePersistenceService` — salva envelope cru no disco.

Fluxo de execução
1. Recebe POST no Controller e responde HTTP 200.
2. Controller chama `WebhookService.processAsync(rawEnvelope)`.
3. `WebhookService` persiste rawEnvelope (fallback), chama `DecryptService.decrypt()` e obtém JSON.
4. Extrai chave de idempotência (`transaction_uuid` ou `purchase_id`) e registra via `IdempotencyService`.
5. Se não duplicado, publica no SNS. Se SNS falhar, envia mensagem para SQS DLQ.

Observabilidade e segurança
- Logs em pontos chave: recebimento, início de processamento, sucesso/erro de decrypt, publicação SNS, envio para DLQ.
- Chaves privadas não devem estar no repositório; usar Secrets Manager ou Kubernetes Secrets em produção.
