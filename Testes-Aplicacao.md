## O fluxo esperado é esse:
1. A API recebe um payload no endpoint.
2. Ela responde rapidamente com HTTP 200.
3. Em seguida, processa o conteúdo recebido.
4. Se o payload vier criptografado, o serviço de descriptografia precisa extrair e abrir o JSON.
5. Depois disso, identifica o evento e publica o conteúdo em SNS.


## Sobre as chaves
Na arquitetura descrita no projeto, a ideia é
. o webhook chega com um payload protegido
. a aplicação usa uma chave AES para descriptografar o conteúdo
. essa chave, por sua vez, foi protegida com RSA
então o fluxo de descriptografia envolve
 1. obter a chave protegida
 2. descriptografar a chave com RSA
 3. usar a chave AES para abrir o payload

Em outras palavras, a “chave” não é só uma string simples; ela é parte de um fluxo de criptografia assimétrico e simétrico.

## O que você pode testar
Para validar a descriptografia, você pode usar um cenário como:

. entrada: um JSON simples
. criptografar esse JSON com AES
. empacotar a chave AES com RSA
. enviar esse conteúdo para a API
. verificar se a aplicação consegue:
  . descriptografar
  .recuperar o JSON original
  .identificar o evento
  .publicar no SNS

## Importante   
O ponto crucial é que, para testar isso de verdade, você precisa de:

 .um payload criptografado válido
 .a chave correta
 .o mesmo algoritmo usado pela aplicação

 Se a implementação atual ainda não tiver um fluxo completo de geração de payload criptografado, você pode montar um teste de “mock” de descriptografia usando um valor conhecido, mas o ideal é testar com um payload real criptografado.

## Para testar a API localmente, faça assim:

1.Entre na pasta do projeto
No terminal:
cd c:\Poc-Dev-IA

2.Execute a aplicação

3.Se estiver usando Maven:
mvn spring-boot:run
A API deve subir na porta padrão do Spring Boot
Geralmente:
http://localhost:8080

4.Teste o endpoint
Exemplo com curl:
curl -X POST http://localhost:8080/api/v1/webhooks/dock
-H "Content-Type: application/json"
-d '{"example":"payload"}'

5.Se quiser validar com um payload mais próximo do exemplo do projeto, use o conteúdo do arquivo Prompt.md ou um JSON semelhante ao esperado pelo DTO.

6.Se quiser ver os logs e confirmar que a requisição chegou, acompanhe a saída do terminal onde o Spring Boot está rodando.

1) Comando pronto para testar no Postman/Insomnia
Se você preferir usar o terminal para simular a chamada, este comando já funciona:

curl -X POST http://localhost:8080/api/v1/webhooks/dock \
  -H "Content-Type: application/json" \
  -d '{
    "purchase_id": 151243393,
    "account_id": 52,
    "card_id": 50,
    "product_id": 2,
    "transaction_type_id": "11111",
    "transaction_type_description": "Compra a Vista Debito - Pre-Pago",
    "pan": "4174********6858",
    "purchase_date": "2020-06-01T09:54:00.000Z",
    "amount": 10.7720,
    "source_amount": 1.9200,
    "settlement_amount": 1.9200,
    "amount_with_tax": 10.7700,
    "installment": 1,
    "amount_first_installment": 10.7720,
    "amount_next_installment": 10.7700,
    "tax_percent": 0.000000000,
    "iof_amount": 0.0000,
    "tac_amount": 0.0000,
    "total_tax": 0.0000,
    "authorization_code": "233726",
    "merchant": "DFV Digital             888-802-3080 US",
    "merchant_id": 2,
    "mcc": 5818,
    "entry_mode": "0100",
    "nsu": 623130,
    "authorization_date": "2020-04-01T09:50:00",
    "transaction_identification": 30009254493885,
    "status": "Pendente",
    "status_id": 3,
    "currency_code": 840,
    "exchange_rate": 5.4100,
    "mcc_description": "AVIANCA",
    "mcc_group_id": "16",
    "mcc_group_description": "Entertainment",
    "is_tokenized_transaction": "false",
    "transaction_uuid": "dad7f6a8-5fde-48e2-8897-7da9ef2ed894",
    "merchant_code": "000000004470930",
    "origin": "VSN",
    "resolution_origin": "VSN",
    "terminal": "10056236",
    "incoming_id": null,
    "acquiring_code": 15055,
    "properties": {
      "cmd_seq": 1,
      "dt_capture": "2021-11-05T14:00:27.337Z",
      "dt_publish": "2021-11-05T14:00:27.574Z",
      "dt_transaction": "2021-11-05T14:00:26.497Z",
      "issuer_id": 225,
      "issuer_name": "PagoSy",
      "operation": "del"
    }
  }'

  2) Exemplo de payload correto para o webhook
Esse payload é compatível com o DTO usado pela aplicação e pode ser colado direto no Postman/Insomnia no body como JSON.

{
  "purchase_id": 151243393,
  "account_id": 52,
  "card_id": 50,
  "product_id": 2,
  "transaction_type_id": "11111",
  "transaction_type_description": "Compra a Vista Debito - Pre-Pago",
  "pan": "4174********6858",
  "purchase_date": "2020-06-01T09:54:00.000Z",
  "amount": 10.7720,
  "source_amount": 1.9200,
  "settlement_amount": 1.9200,
  "amount_with_tax": 10.7700,
  "installment": 1,
  "amount_first_installment": 10.7720,
  "amount_next_installment": 10.7700,
  "tax_percent": 0.000000000,
  "iof_amount": 0.0000,
  "tac_amount": 0.0000,
  "total_tax": 0.0000,
  "authorization_code": "233726",
  "merchant": "DFV Digital             888-802-3080 US",
  "merchant_id": 2,
  "mcc": 5818,
  "entry_mode": "0100",
  "nsu": 623130,
  "authorization_date": "2020-04-01T09:50:00",
  "transaction_identification": 30009254493885,
  "status": "Pendente",
  "status_id": 3,
  "currency_code": 840,
  "exchange_rate": 5.4100,
  "mcc_description": "AVIANCA",
  "mcc_group_id": "16",
  "mcc_group_description": "Entertainment",
  "is_tokenized_transaction": "false",
  "transaction_uuid": "dad7f6a8-5fde-48e2-8897-7da9ef2ed894",
  "merchant_code": "000000004470930",
  "origin": "VSN",
  "resolution_origin": "VSN",
  "terminal": "10056236",
  "incoming_id": null,
  "acquiring_code": 15055,
  "properties": {
    "cmd_seq": 1,
    "dt_capture": "2021-11-05T14:00:27.337Z",
    "dt_publish": "2021-11-05T14:00:27.574Z",
    "dt_transaction": "2021-11-05T14:00:26.497Z",
    "issuer_id": 225,
    "issuer_name": "PagoSy",
    "operation": "del"
  }
}

Configuração no Postman/Insomnia
Método: POST
URL: http://localhost:8080/api/v1/webhooks/dock
Header:
Content-Type: application/json

## JSON de webhook já pronto e criptografado em Java.

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class WebhookAesExample {

    public static void main(String[] args) throws Exception {
        String webhookJson = """
            {
              "purchase_id": 151243393,
              "account_id": 52,
              "card_id": 50,
              "product_id": 2,
              "transaction_type_id": "11111",
              "transaction_type_description": "Compra a Vista Debito - Pre-Pago",
              "pan": "4174********6858",
              "purchase_date": "2020-06-01T09:54:00.000Z",
              "amount": 10.7720,
              "status": "Pendente"
            }
            """;

        // Chave AES de 16 bytes e IV de 16 bytes
        byte[] key = "Sixteen byte key".getBytes(StandardCharsets.UTF_8);
        byte[] iv = "1234567890abcdef".getBytes(StandardCharsets.UTF_8);

        String encrypted = encrypt(webhookJson, key, iv);
        System.out.println("Payload criptografado:");
        System.out.println(encrypted);

        String decrypted = decrypt(encrypted, key, iv);
        System.out.println("Payload descriptografado:");
        System.out.println(decrypted);
    }

    private static String encrypt(String plainText, byte[] key, byte[] iv) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);

        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    private static String decrypt(String encryptedText, byte[] key, byte[] iv) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);

        byte[] decodedBytes = Base64.getDecoder().decode(encryptedText);
        byte[] decryptedBytes = cipher.doFinal(decodedBytes);
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }
}

Como usar
Copie esse código para um arquivo chamado WebhookAesExample.java
Compile com

javac WebhookAesExample.java

Execute com:
java WebhookAesExample

O que você recebe
O programa imprime:

um valor de payload criptografado
o mesmo payload descriptografado de volta

import json
import base64
import requests
from Crypto.Cipher import AES
from Crypto.Util.Padding import pad

webhook_json = {
    "purchase_id": 151243393,
    "account_id": 52,
    "card_id": 50,
    "product_id": 2,
    "transaction_type_id": "11111",
    "transaction_type_description": "Compra a Vista Debito - Pre-Pago",
    "pan": "4174********6858",
    "purchase_date": "2020-06-01T09:54:00.000Z",
    "amount": 10.7720,
    "status": "Pendente"
}

key = b"Sixteen byte key"
iv = b"1234567890abcdef"

cipher = AES.new(key, AES.MODE_CBC, iv)
ciphertext = cipher.encrypt(pad(json.dumps(webhook_json).encode("utf-8"), AES.block_size))

body = {
    "encrypted_payload": base64.b64encode(ciphertext).decode("utf-8"),
    "key": base64.b64encode(key).decode("utf-8"),
    "iv": base64.b64encode(iv).decode("utf-8")
}

response = requests.post(
    "http://localhost:8080/api/v1/webhooks/dock",
    json=body,
    headers={"Content-Type": "application/json"}
)

print("Status:", response.status_code)
print("Response:", response.text)


Como usar
Instale a dependência:

pip install pycryptodome requests
Salve como send_webhook.py
Execute
python send_webhook.py

## Requisito
A API precisa estar rodando em:

Se quiser, eu também posso te entregar a versão que:

usa RSA + AES
ou já imprime o JSON descriptografado para validar o fluxo completo.