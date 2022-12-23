![](../../workflows/test%20build/badge.svg)

# muni-chat-service
Example of a web service with OpenAPI description in [openapi.yml](openapi.yml)

## Installation

Prerequisites: git, [Apache Maven](https://maven.apache.org/) and JDK 17+ 

Download and compile:
```bash
git clone https://github.com/martin-kuba/muni-chat-service.git
cd muni-chat-service
mvn install
```

It contains several Maven modules:
* [chat-server](chat-server/) - server
* [chat-client-java](chat-client-java/) - command-line client written in Java 

## Run server

```bash
cd chat-server
mvn spring-boot:run
```
Then visit the service with your browser: http://localhost:8080/

### Running the chat server with TLS enabled

Create a PKCS12 keystore with:
```bash
openssl pkcs12 -export -name "mycert" -inkey key.pem -in cert.pem -certfile chain.pem -out mykeystore.p12
```
then run with the following options:
```bash
java -jar target/chat_service.jar \
     --server.port=8443 \
     --server.ssl.key-store-type=pkcs12 \
     --server.ssl.key-store=mykeystore.p12 \
     --server.ssl.key-store-password=password
```
