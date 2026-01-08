#!/bin/bash
set -e

echo "🗄️ Creating DynamoDB table audit_events..."

awslocal dynamodb create-table \
  --table-name audit_events \
  --attribute-definitions \
    AttributeName=id,AttributeType=S \
    AttributeName=timestamp,AttributeType=S \
  --key-schema \
    AttributeName=id,KeyType=HASH \
    AttributeName=timestamp,KeyType=RANGE \
  --billing-mode PAY_PER_REQUEST

echo "✅ DynamoDB table audit_events created"