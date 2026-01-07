#!/usr/bin/env bash
set -e

echo "Creating S3 bucket 'claims'..."

awslocal s3 mb s3://claims || true

echo "✅ Bucket 'claims' ready"