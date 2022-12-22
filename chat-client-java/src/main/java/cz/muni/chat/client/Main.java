package cz.muni.chat.client;

import cz.muni.chat.client.invoker.ApiClient;
import cz.muni.chat.client.model.BackgroundColorEnum;
import cz.muni.chat.client.model.ChatMessage;
import cz.muni.chat.client.model.NewChatMessageRequest;
import org.springframework.web.client.RestClientException;

import java.time.ZoneId;

public class Main {

    public static void main(String[] args) {

        System.out.println("starting");
        ChatApi chat = new ChatApi(new ApiClient());

        try {
            for (ChatMessage chatMessage : chat.getAllMessages()) {
                System.out.println("chatMessage = " + chatMessage);
            }

            ChatMessage message = chat.createMessage(
                    new NewChatMessageRequest().text("Hello!").textColor("black").backgroundColor(BackgroundColorEnum.AQUAMARINE),
                    "me",
                    null);
            System.out.println("new message = " + message);
            System.out.println("timestamp: "+ message.getTimestamp().atZoneSameInstant(ZoneId.systemDefault()));

        } catch (RestClientException ex) {
            ex.printStackTrace();
        }

    }
}
