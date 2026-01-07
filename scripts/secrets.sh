#!/bin/bash
set -e

echo "🔐 Creating Secrets Manager secrets..."

# DB Secret
if ! awslocal secretsmanager describe-secret --secret-id security-service/db >/dev/null 2>&1; then
  awslocal secretsmanager create-secret \
    --name security-service/db \
    --secret-string '{
      "username": "postgres",
      "password": "postgres"
    }'
  echo "✅ Secret security-service/db created"
else
  echo "ℹ️ Secret security-service/db already exists"
fi

# JWT Secret
if ! awslocal secretsmanager describe-secret --secret-id security-service/jwt >/dev/null 2>&1; then
  awslocal secretsmanager create-secret \
    --name security-service/jwt \
    --secret-string '{
      "secretKey": "9fJ8sLw2XkQm7D5RZB3YcN6A0EoV4MUT"
    }'
  echo "✅ Secret security-service/jwt created"
else
  echo "ℹ️ Secret security-service/jwt already exists"
fi

echo "🎉 Secrets ready"
