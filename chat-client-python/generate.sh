#!/bin/bash

GENERATOR_VERSION=7.8.0
if [ ! -f  "openapi-generator-cli-$GENERATOR_VERSION.jar" ] ; then
  wget https://repo1.maven.org/maven2/org/openapitools/openapi-generator-cli/$GENERATOR_VERSION/openapi-generator-cli-$GENERATOR_VERSION.jar
fi

rm -rf chat_openapi
# see https://openapi-generator.tech/docs/usage#generate
# and https://openapi-generator.tech/docs/generators/python
java \
 --add-opens java.base/java.util=ALL-UNNAMED \
 --add-opens java.base/java.lang=ALL-UNNAMED \
 -DapiDocs=true \
 -DapiTests=false \
 -DmodelDocs=true \
 -DmodelTests=false \
 -jar openapi-generator-cli-$GENERATOR_VERSION.jar generate \
 --generator-name python \
 --input-spec ../openapi.yaml \
 --additional-properties=generateSourceCodeOnly=true,packageName=chat_openapi

