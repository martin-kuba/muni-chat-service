package cz.muni.chat.generated.server.api;

import cz.muni.chat.generated.server.model.BackgroundColorEnum;
import cz.muni.chat.generated.server.model.ChatMessage;
import cz.muni.chat.generated.server.model.ErrorMessage;
import cz.muni.chat.generated.server.model.NewChatMessageRequest;
import cz.muni.chat.generated.server.model.NewChatMessageRequest.TextColorEnum;
import cz.muni.chat.generated.server.model.PageChatMessage;
import cz.muni.chat.generated.server.model.PageableObject;
import cz.muni.chat.generated.server.model.SortObject;
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

    @SuppressWarnings({"rawtypes","unchecked"})
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
                    .path("/api/message/"+id)
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
    public ResponseEntity<PageChatMessage> paged(Integer page, Integer size, List<String> sort) {
        log.debug("paged(page={}, size={})", page, size);
        PageRequest p = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "timestamp"));
        List<ChatMessage> chatMessages = messages.stream().skip(p.getOffset()).limit(p.getPageSize()).toList();
        Page<ChatMessage> m = new PageImpl<>(chatMessages, p, messages.size());

        // copy to generated models :-(
        List<SortObject> s = p.getSort().get().map(order -> new SortObject()
                .property(order.getProperty())
                .ascending(order.isAscending())
                .direction(order.getDirection().name())
                .ignoreCase(order.isIgnoreCase())
                .nullHandling(order.getNullHandling().name())
        ).toList();
        PageableObject pageableObject = new PageableObject()
                .paged(p.isPaged())
                .unpaged(p.isUnpaged())
                .pageNumber(p.getPageNumber())
                .pageSize(p.getPageSize())
                .sort(s)
                .offset(p.getOffset());
        PageChatMessage pageChatMessage = new PageChatMessage()
                .content(chatMessages)
                .pageable(pageableObject)
                .last(m.isLast())
                .first(m.isFirst())
                .empty(m.isEmpty())
                .totalPages(m.getTotalPages())
                .totalElements(m.getTotalElements())
                .number(m.getNumber())
                .numberOfElements(m.getNumberOfElements())
                .size(m.getSize())
                .sort(s);
        return new ResponseEntity<>(pageChatMessage, HttpStatus.OK);
    }

    private final List<ChatMessage> messages = new CopyOnWriteArrayList<>();


}
