package cz.muni.chat.server.service;

import java.util.List;

public interface ChatService {

    List<StoredMessage> getAllChatMessages();

    StoredMessage createNewChatMessage(String text, String author, String textColor, String backgroundColor);

    StoredMessage getMessage(String id);
}
