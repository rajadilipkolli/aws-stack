#!/bin/bash

awslocal secretsmanager create-secret --name /spring/secret --secret-string file://myCreds.json --region us-east-1
echo "List of secretsmanager secrets:"
echo "-------------------------------"
awslocal secretsmanager list-secrets
