package cz.muni.chat.server.service;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class ChatServiceImpl implements ChatService {

    private static final Logger log = LoggerFactory.getLogger(ChatServiceImpl.class);
    private static final Random random = new Random();

    // in-memory storage of messages
    private final List<StoredMessage> messages = new CopyOnWriteArrayList<>();

    @PostConstruct
    public void init() {
        this.createNewMessage("first message", "\uD83D\uDEC8 system", "black", "lightblue");
        log.info("the first message added");
    }

    @Override
    public List<StoredMessage> getAllMessages() {
        return messages;
    }

    @Override
    public StoredMessage createNewMessage(String text, String author, String textColor, String backgroundColor) {
        UUID uuid = UUID.randomUUID();
        StoredMessage c = new StoredMessage(uuid.toString(), ZonedDateTime.now(), text, author,
                textColor, backgroundColor, random.nextLong());
        messages.addFirst(c);
        return c;
    }

    @Override
    public StoredMessage getMessage(String id) {
        return messages.stream().filter(x -> x.id().equals(id)).findFirst().orElse(null);
    }

    @Override
    public Page<StoredMessage> getPageOfMessages(Pageable pageable) {
        // In practice Pageable object is propagated into Spring Data repositories and transformed
        // into LIMIT, OFFSET, and ORDER BY keywords in SQL queries, so the pagination is
        // performed efficiently by the database engine when reading data from disk,
        // which is the most time-consuming part of getting data.
        //
        // In this simple example, there is no persistence layer, so the following code gets all messages
        // and then filters them in memory. That would be highly inefficient when using a real database.
        // DO NOT perform pagination at the service level, do it at the persistence level!
        List<StoredMessage> msgs = messages.stream()
                .skip(pageable.getOffset())
                .limit(pageable.getPageSize())
                .toList();
        Page<StoredMessage> page = new PageImpl<>(msgs, PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                Sort.Direction.DESC, "timestamp"), messages.size());
        log.debug("pageable = {}", pageable);
        log.debug("page: {}", page);
        return page;
    }
}
