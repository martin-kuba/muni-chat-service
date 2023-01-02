package cz.muni.chat.client;

import cz.muni.chat.client.invoker.ApiClient;
import cz.muni.chat.client.invoker.ApiException;
import cz.muni.chat.client.model.BackgroundColorEnum;
import cz.muni.chat.client.model.ChatMessage;
import cz.muni.chat.client.model.NewChatMessageRequest;

import java.time.ZoneId;
import java.util.Random;

import static cz.muni.chat.client.model.NewChatMessageRequest.TextColorEnum;

public class Main {

    public static void main(String[] args) throws Exception {

        System.out.println("starting");
        ChatApi chat = new ChatApi(new ApiClient());

        // list all messages
        for (ChatMessage chatMessage : chat.getAllMessages()) {
            System.out.println("chatMessage = " + chatMessage);
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

        System.out.println("new message = " + message);
        System.out.println("timestamp: " + message.getTimestamp().atZoneSameInstant(ZoneId.systemDefault()));

        // deliberately make a wrong call to show catching an exception
        try {
            chat.getMessage("1");
        } catch (ApiException ex) {
            ChatException.from(ex).printStackTrace();
        }

    }
}
