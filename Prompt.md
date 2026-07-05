# Objetivo

Implementar uma API Java para receber Webhooks da Dock.

## Projeto

Empresa: Roadcard

Projeto: Prestação de Contas

Antes de escrever qualquer código execute esta sequência:

1. Analise toda a documentação.
2. Faça um resumo da arquitetura.
3. Liste as regras de negócio encontradas.
4. Liste dúvidas ou inconsistências.
5. Somente depois gere o código.

## Documentos anexados

- Documentação Dock
- Diagrama da solução

A documentação é a fonte da verdade.

Não invente comportamentos.

## Endpoint

POST

/api/v1/webhooks/dock

## Eventos

- Purchase_approved
- Purchase_processed

## Payloads dos eventos

- Purchase_approved
JSON
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

- Purchase_processed
JSON
{
    "purchase_id": 3221,
    "account_id": 7103,
    "card_id": 1791,
    "product_id": 4,
    "transaction_type_id": "318",
    "transaction_type_description": "A vista sem juros - Visa",
    "pan": "4998********0165",
    "purchase_date": "2020-07-01T11:32:00.000Z",
    "amount": 5.0000,
    "source_amount": 5.0000,
    "settlement_amount": 0.5600,
    "amount_with_tax": 5.0000,
    "installment": 1,
    "amount_first_installment": 5.0000,
    "amount_next_installment": 5.0000,
    "tax_percent": 0.000000000,
    "iof_amount": 0.0000,
    "tac_amount": 0.0000,
    "total_tax": 0.0000,
    "authorization_code": "424138",
    "merchant": "Estabelecimento 1",
    "merchant_id": 1,
    "mcc": 5735,
    "entry_mode": "0051",
    "nsu": 280007,
    "authorization_date": "2021-02-28T09:07:00",
    "transaction_identification": 461058666535923,
    "status": "Processada",
    "status_id": 0,
    "currency_code": 986,
    "exchange_rate": 5.7000,
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
        "dt_capture": "2021-02-11T00:26:22.462Z",
        "dt_publish": "2021-02-11T00:26:22.626Z",
        "dt_transaction": "2021-02-11T00:06:39.917Z",
        "issuer_id": 1,
        "issuer_name": "Sample",
        "operation": "ins"
    }
}

## Fluxo esperado

1. Receber o POST enviado pela Dock.

2. Responder HTTP 200 imediatamente.

3. Após responder, iniciar o processamento.

4. Descriptografar o payload recebido.

5. Identificar qual evento foi recebido.

6. Publicar o JSON descriptografado em um tópico SNS.

## Criptografia

Segundo a documentação da Dock:

- o payload chega criptografado
- utilizar a chave AES
- a chave AES foi protegida utilizando RSA
- utilizar o processo descrito na documentação para descriptografar

Nunca alterar esse fluxo.

## Arquitetura esperada

Controller

↓

Service

↓

DecryptService

↓

SNSService

## Entregáveis

Gerar:

- estrutura de pacotes
- Controller
- DTOs
- Service
- Serviço de descriptografia
- Serviço de publicação SNS
- Configurações necessárias
- Exceptions
- Logs
- Testes unitários
- README técnico

## Regras

- Código Java limpo.
- Código pronto para produção.
- Não inventar regras.
- Utilizar apenas informações presentes na documentação.
- Caso exista alguma dúvida na documentação, listar antes de gerar o código.