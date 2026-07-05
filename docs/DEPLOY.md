# Deploy and Run

Build locally:

```bash
mvn clean package -DskipTests
docker build -t dock-webhook:latest .
```

Run container (configure env vars for AWS or mount credentials):

```bash
docker run -e AWS_ACCESS_KEY_ID=... -e AWS_SECRET_ACCESS_KEY=... -e AWS_REGION=us-east-1 \
  -e AWS_SNS_TOPIC_ARN=arn:aws:sns:... dock-webhook:latest
```

Kubernetes/ECS: provide environment variables and mount `private_key.pem` into the container path configured by `dock.private-key-path`.

Database: configure `spring.datasource.*` env vars for Postgres if using JPA idempotency.
