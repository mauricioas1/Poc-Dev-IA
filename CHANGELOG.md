# Changelog

Todas as mudanças importantes neste projeto seguem o formato "Keep a Changelog".

## [Unreleased]
- Estrutura inicial do receptor de webhooks Dock, inclusão de decrypt, SNS publish, DLQ (SQS) e idempotency JPA.

## [0.1.0] - 2026-07-03
- Versão inicial com: Controller, Service, DockDecryptService (RSA-OAEP + AES-GCM), SNS/DLQ, Idempotency (JPA), testes unitários e integração (LocalStack).
