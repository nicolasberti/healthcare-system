#!/usr/bin/env bash
set -e

awslocal s3 mb s3://claims || true
echo "✅ Bucket 'claims' creado"