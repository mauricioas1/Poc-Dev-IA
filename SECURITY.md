# Segurança

Reportar vulnerabilidades:

- Se encontrar uma vulnerabilidade, por favor reporte em privado para o time de segurança da Roadcard.
- Não divulgue vulnerabilidades publicamente até que exista um plano de mitigação.

Boas práticas para chaves e segredos:
- Não versionar chaves privadas nem secrets.
- Em produção, use AWS Secrets Manager, Parameter Store ou Kubernetes Secrets.
- Restrinja permissões das chaves apenas ao necessário (principle of least privilege).
