#!/bin/bash

awslocal ssm put-parameter --name /spring/config/application.password --value secret --type String --region us-east-1
awslocal ssm put-parameter --name /spring/config/application.username --value appuser --type String --region us-east-1
echo "List of ssm parameters:"
echo "-------------------------------"
awslocal ssm get-parameters --names /spring/config/application.username /spring/config/application.password
