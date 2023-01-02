package cz.muni.chat.client;

import cz.muni.chat.client.invoker.ApiClient;
import cz.muni.chat.client.invoker.ApiException;
import cz.muni.chat.client.model.BackgroundColorEnum;
import cz.muni.chat.client.model.ChatMessage;
import cz.muni.chat.client.model.NewChatMessageRequest;
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

        // list all messages
        for (ChatMessage chatMessage : chat.getAllMessages()) {
            log.info("message: {}", chatMessage);
        }

        // create a new message
        BackgroundColorEnum[] colors = BackgroundColorEnum.values();
        BackgroundColorEnum bg = colors[new Random().nextInt(colors.length)];
        ChatMessage message = chat.createMessage(
                new NewChatMessageRequest()
                        .text("Hello!")
                        .textColor(TextColorEnum.BLACK)
                        .backgroundColor(bg),
                "me",
                "UltraChat 1.0");

        log.info("new message = {}", message);
        ZonedDateTime timestamp = message.getTimestamp().atZoneSameInstant(ZoneId.systemDefault());
        log.info("timestamp: {}", timestamp);

        // create more messages
        for (int i = 3; i <= 8; i++) {
            chat.createMessage(new NewChatMessageRequest().text("Message num " + i), "robot", null);
        }
        //get paged messages
        PageChatMessage paged = chat.paged(1, 3, null);
        log.info("paged messages:");
        for (ChatMessage chatMessage : paged.getContent()) {
            log.info("msg: {}", chatMessage);
        }

        // deliberately make a wrong call to show catching an exception
        try {
            chat.getMessage("1");
        } catch (ApiException ex) {
            log.info("expected exception", ChatException.from(ex));
        }

    }
}
