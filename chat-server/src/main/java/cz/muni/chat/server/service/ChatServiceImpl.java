package cz.muni.chat.server.service;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class ChatServiceImpl implements ChatService {

    private static final Logger log = LoggerFactory.getLogger(ChatServiceImpl.class);

    // in-memory storage of messages
    private final List<StoredMessage> messages = new ArrayList<>();

    @PostConstruct
    public void init() {
        this.createNewChatMessage("first message", "\uD83D\uDEC8 system", "black", "lightblue");
        log.info("the first message added");
    }

    @Override
    public List<StoredMessage> getAllChatMessages() {
        synchronized (messages) {
            return List.copyOf(messages);
        }
    }

    @Override
    public StoredMessage createNewChatMessage(String text, String author, String textColor, String backgroundColor) {
        UUID uuid = UUID.randomUUID();
        StoredMessage c = new StoredMessage(uuid.toString(), ZonedDateTime.now(), author, text, textColor, backgroundColor);
        synchronized (messages) {
            messages.add(c);
        }
        return c;
    }

    @Override
    public StoredMessage getMessage(String id) {
        synchronized (messages) {
            return messages.stream().filter(x -> x.id().equals(id)).findFirst().orElse(null);
        }
    }
}
