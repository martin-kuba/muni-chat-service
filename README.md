![](../../workflows/test%20build/badge.svg)

# muni-chat-service
Example of a web service with OpenAPI description in [openapi.yml](openapi.yml)

Prerequisites: git, [Apache Maven](https://maven.apache.org/) and JDK 17+ 

Download and compile:
```bash
git clone https://github.com/martin-kuba/muni-pa160-chat-service.git
cd muni-pa160-chat-service
mvn spring-boot:run
```
Alternatively, you can create an executable JAR file and execute it:
```bash
mvn clean install
java -jar target/chat_service.jar
```
Then visit the service with your browser: http://localhost:8080/

## Running the chat service with TLS

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
# CI/CD experiments
## Continuous integration
1. fork this git repository by clicking on "Fork" in the top right corner
1. see the content of the file [.github/workflows/test.yml](.github/workflows/test.yml) that controls the test CI workflow
1. edit the file [ChatService.java](chat-server/src/main/java/cz/muni/chat/ChatService.java) by clicking the pencil icon
1. comment out the third line with `import org.slf4j.Logger;` and commit the change
1. visit the **[Actions](../../actions)** page and check that the two builds have failed, the badge in README is now red
1. edit the file again, uncomment the line and commit the change
1. visit the **[Actions](../../actions)** page and check that the two builds are passing
1. the badge becomes green again, if not, press CTRL+SHIFT+R to reload its image
## Continuous delivery
1. see the content of the file [.github/workflows/release_assets.yml](.github/workflows/release_assets.yml) that controls the CD workflow
1. visit the **[Releases](../../releases)** page and click on **Draft a new release**
1. Type a tag name into the *Tag version* form field and a title into *Release title*
1. click **Publish release**
1. visit the **[Actions](../../actions)** page to see how the workflow is executed
1. after it finishes, visit the **Releases** page to see the created JAR file 