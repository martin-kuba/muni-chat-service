package cz.muni.chat.client;

import cz.muni.chat.client.invoker.ApiClient;
import cz.muni.chat.client.invoker.ApiException;
import cz.muni.chat.client.model.BackgroundColorEnum;
import cz.muni.chat.client.model.ChatMessage;
import cz.muni.chat.client.model.NewChatMessageRequest;
import cz.muni.chat.client.model.Organism;
import cz.muni.chat.client.model.PageChatMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Random;

import static cz.muni.chat.client.model.NewChatMessageRequest.TextColorEnum;

@SpringBootApplication
public class Main implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("starting");
        ChatApi chat = new ChatApi(new ApiClient());

        Organism pokus = chat.pokus();
        log.info("pokus {}", pokus);
        log.info("pokus class name {}", pokus.getClass().getName());

    }
}
