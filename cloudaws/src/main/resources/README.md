9. Teste local com LocalStack

# S3
awslocal s3 mb s3://bucket-pagamentos

# SNS
awslocal sns create-topic --name pagamento-topic