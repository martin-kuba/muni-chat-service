package cz.muni.chat.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Simple microservice implemented using Spring Boot framework.
 *
 * @author Martin Kuba makub@ics.muni.cz
 */
@SpringBootApplication // this annotation marks this class as a runnable Spring Boot app
public class ChatApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChatApplication.class, args);
    }

}
