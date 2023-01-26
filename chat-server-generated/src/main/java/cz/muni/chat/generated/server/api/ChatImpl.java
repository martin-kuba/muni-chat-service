package cz.muni.chat.generated.server.api;

import cz.muni.chat.generated.server.model.BackgroundColorEnum;
import cz.muni.chat.generated.server.model.ChatMessage;
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

    @Override
    public ResponseEntity<ChatMessage> getMessage(String id) {
        log.debug("getMessage({})", id);
        ChatMessage chatMessage = messages.stream().filter(x -> x.getId().equals(id)).findFirst().orElse(null);
        return new ResponseEntity<>(chatMessage, chatMessage != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }

    @Override
    public ResponseEntity<ChatMessage> createMessage(NewChatMessageRequest r, String author, String userAgent) {
        log.debug("createMessage({},{},{})", r, author, userAgent);
        String text = r.getText();
        BackgroundColorEnum backgroundColor = r.getBackgroundColor();
        TextColorEnum textColor = r.getTextColor();
        ChatMessage chatMessage = new ChatMessage()
                .id(UUID.randomUUID().toString())
                .timestamp(OffsetDateTime.now())
                .author(author)
                .text(text)
                .textColor(textColor.getValue())
                .backgroundColor(backgroundColor);
        messages.add(0, chatMessage);
        return new ResponseEntity<>(chatMessage, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<PageChatMessage> paged(Integer page, Integer size, List<String> sort) {
        log.debug("paged(page={}, size={}, sort={})", page, size, sort);
        PageRequest p = PageRequest.of(page, size);
        List<ChatMessage> chatMessages = messages.stream().skip(p.getOffset()).limit(p.getPageSize()).toList();
        Page<ChatMessage> m = new PageImpl<>(chatMessages, p, messages.size());

        // copy to generated models :-(
        SortObject s = new SortObject()
                .sorted(p.getSort().isSorted())
                .unsorted(p.getSort().isUnsorted())
                .empty(p.getSort().isEmpty())
                ;
        PageableObject pageableObject = new PageableObject()
                .paged(p.isPaged())
                .unpaged(p.isUnpaged())
                .pageNumber(p.getPageNumber())
                .pageSize(p.getPageSize())
                .sort(s)
                .offset(p.getOffset())
                ;
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
                .sort(s)
                ;
        return new ResponseEntity<>(pageChatMessage,  HttpStatus.OK);
    }

    private final List<ChatMessage> messages = new CopyOnWriteArrayList<>();


}
