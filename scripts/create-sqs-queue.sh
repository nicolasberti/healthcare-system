#!/bin/bash
set -e

echo "📬 Creating SQS queues..."

awslocal sqs create-queue \
  --queue-name audit-queue

echo "✅ SQS queue audit-queue created"