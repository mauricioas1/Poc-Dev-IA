                     Dock
                       │
                       │ POST (Payload Criptografado)
                       ▼
             /api/v1/webhooks/dock
                       │
            Salva payload recebido
                       │
             HTTP 200 imediatamente
                       │
────────────────────────────────────────────
         Worker / Processamento
                       │
                       ▼
             Key Management
        ┌──────────────────────┐
        │ private_key.pem      │
        │ AES Key              │
        └──────────────────────┘
                       │
                       ▼
            Descriptografar Payload
                       │
                       ▼
       Purchase_approved
               ou
       Purchase_processed
                       │
                       ▼
               Publica SNS