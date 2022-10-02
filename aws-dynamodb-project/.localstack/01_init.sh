#!/bin/bash

# -- > Create DynamoDb Table
echo Creating  DynamoDb \'customer\' table ...
awslocal dynamodb create-table --cli-input-json '{"TableName":"customer","KeySchema":[{"AttributeName":"id","KeyType":"HASH"}],"AttributeDefinitions":[{"AttributeName":"id","AttributeType":"S"}],"ProvisionedThroughput":{"ReadCapacityUnits":5,"WriteCapacityUnits":5},"Tags":[{"Key":"Owner","Value":"localstack"}]}'

# --> List DynamoDb Tables
echo Listing tables ...
awslocal dynamodb list-tables