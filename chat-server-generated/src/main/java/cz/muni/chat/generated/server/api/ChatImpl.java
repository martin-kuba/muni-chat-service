package cz.muni.chat.generated.server.api;

import cz.muni.chat.generated.server.model.BackgroundColorEnum;
import cz.muni.chat.generated.server.model.ChatMessage;
import cz.muni.chat.generated.server.model.ErrorMessage;
import cz.muni.chat.generated.server.model.NewChatMessageRequest;
import cz.muni.chat.generated.server.model.NewChatMessageRequest.TextColorEnum;
import cz.muni.chat.generated.server.model.PageMetadata;
import cz.muni.chat.generated.server.model.PagedModelChatMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Implementation of API methods for generated controller.
 */
@Component
public class ChatImpl implements ChatApiDelegate {

    private static final Logger log = LoggerFactory.getLogger(ChatImpl.class);

    @Override
    public ResponseEntity<List<ChatMessage>> getAllMessages() {
        log.debug("getAllMessages()");
        return new ResponseEntity<>(messages, HttpStatus.OK);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public ResponseEntity<ChatMessage> getMessage(String id) {
        log.debug("getMessage({})", id);
        ChatMessage chatMessage = messages.stream().filter(x -> x.getId().equals(id)).findFirst().orElse(null);
        if (chatMessage != null) {
            return new ResponseEntity<>(chatMessage, HttpStatus.OK);
        } else {
            ErrorMessage errorMessage = new ErrorMessage()
                    .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                    .status(HttpStatus.NOT_FOUND.value())
                    .path("/api/message/" + id)
                    .timestamp(OffsetDateTime.now())
                    .message("message with id=" + id + " not found");
            return new ResponseEntity(errorMessage, HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public ResponseEntity<ChatMessage> createMessage(NewChatMessageRequest r, String author, String userAgent) {
        log.debug("createMessage({},{},{})", r, author, userAgent);
        String text = r.getText();
        BackgroundColorEnum backgroundColor = r.getBackgroundColor();
        TextColorEnum textColor = r.getTextColor();
        ChatMessage chatMessage = new ChatMessage(UUID.randomUUID().toString(), OffsetDateTime.now(), text)
                .author(author)
                .textColor(textColor != null ? textColor.getValue() : null)
                .backgroundColor(backgroundColor);
        messages.addFirst(chatMessage);
        return new ResponseEntity<>(chatMessage, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<PagedModelChatMessage> paged(Integer page, Integer size, List<String> sort) {
        log.debug("paged(page={}, size={})", page, size);
        PageRequest p = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "timestamp"));
        List<ChatMessage> chatMessages = messages.stream().skip(p.getOffset()).limit(p.getPageSize()).toList();
        Page<ChatMessage> m = new PageImpl<>(chatMessages, p, messages.size());

        // copy to generated models :-(
        PageMetadata pageMetadata = new PageMetadata()
                .size((long) m.getSize())
                .number((long) m.getNumber())
                .totalElements(m.getTotalElements())
                .totalPages((long) m.getTotalPages());
        PagedModelChatMessage pagedModelChatMessage = new PagedModelChatMessage()
                .content(chatMessages)
                .page(pageMetadata);
        return new ResponseEntity<>(pagedModelChatMessage, HttpStatus.OK);
    }

    private final List<ChatMessage> messages = new CopyOnWriteArrayList<>();


}
