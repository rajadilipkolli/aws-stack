#!/bin/bash

echo "Registering ses:"
awslocal ses verify-email-identity --email-address sender@example.com --region us-east-1 --endpoint-url=http://localhost:4566
echo "-------------------------------"

