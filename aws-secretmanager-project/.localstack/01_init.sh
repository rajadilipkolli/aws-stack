#!/bin/bash

awslocal secretsmanager create-secret --name /spring/secret --secret-string '{"application.username":"appuser","application.password":"password"}' --region us-east-1
echo "List of secretsmanager secrets:"
echo "-------------------------------"
awslocal secretsmanager list-secrets
