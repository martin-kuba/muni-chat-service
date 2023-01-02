package cz.muni.chat.server.service;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class ChatServiceImpl implements ChatService {

    private static final Logger log = LoggerFactory.getLogger(ChatServiceImpl.class);

    // in-memory storage of messages
    private final List<StoredMessage> messages = new CopyOnWriteArrayList<>();

    @PostConstruct
    public void init() {
        this.createNewChatMessage("first message", "\uD83D\uDEC8 system", "black", "lightblue");
        log.info("the first message added");
    }

    @Override
    public List<StoredMessage> getAllChatMessages() {
        return messages;
    }

    @Override
    public StoredMessage createNewChatMessage(String text, String author, String textColor, String backgroundColor) {
        UUID uuid = UUID.randomUUID();
        StoredMessage c = new StoredMessage(uuid.toString(), ZonedDateTime.now(), author, text, textColor, backgroundColor);
        messages.add(c);
        return c;
    }

    @Override
    public StoredMessage getMessage(String id) {
        return messages.stream().filter(x -> x.id().equals(id)).findFirst().orElse(null);
    }
}
