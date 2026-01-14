#!/bin/bash
set -e

echo "📬 Creating SQS queues..."

awslocal sqs create-queue \
  --queue-name audit-queue

awslocal sqs create-queue \
  --queue-name documentos-clasificacion-queue

echo "✅ SQS queue audit-queue created"