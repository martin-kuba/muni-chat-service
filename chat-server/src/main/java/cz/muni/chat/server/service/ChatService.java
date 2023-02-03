package cz.muni.chat.server.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ChatService {

    List<StoredMessage> getAllMessages();

    StoredMessage createNewMessage(String text, String author, String textColor, String backgroundColor);

    StoredMessage getMessage(String id);

    Page<StoredMessage> getPageOfMessages(Pageable pageable);
}
