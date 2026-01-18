#!/usr/bin/env bash
set -e

BUCKET_NAME="claims"
QUEUE_NAME="documentos-clasificacion-queue"

echo "🪣 Creating S3 bucket '$BUCKET_NAME'..."
awslocal s3 mb s3://$BUCKET_NAME || true
echo "✅ Bucket '$BUCKET_NAME' ready"

echo "📬 Creating SQS queue '$QUEUE_NAME'..."
QUEUE_URL=$(awslocal sqs create-queue \
  --queue-name "$QUEUE_NAME" \
  --query QueueUrl \
  --output text)

echo "✅ Queue created: $QUEUE_URL"

QUEUE_ARN=$(awslocal sqs get-queue-attributes \
  --queue-url "$QUEUE_URL" \
  --attribute-names QueueArn \
  --query Attributes.QueueArn \
  --output text)

echo "🔗 Queue ARN: $QUEUE_ARN"

echo "🔔 Configuring S3 Event Notification → SQS..."

cat > s3-notification.json <<EOF
{
  "QueueConfigurations": [
    {
      "QueueArn": "$QUEUE_ARN",
      "Events": ["s3:ObjectCreated:*"]
    }
  ]
}
EOF

awslocal s3api put-bucket-notification-configuration \
  --bucket "$BUCKET_NAME" \
  --notification-configuration file://s3-notification.json

echo "✅ S3 Event Notification configured successfully"
