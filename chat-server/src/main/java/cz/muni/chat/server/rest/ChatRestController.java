package cz.muni.chat.server.rest;

import cz.muni.chat.server.facade.BackgroundColor;
import cz.muni.chat.server.facade.ChatMessage;
import cz.muni.chat.server.facade.ErrorMessage;
import cz.muni.chat.server.facade.NewChatMessageRequest;
import cz.muni.chat.server.service.ChatService;
import cz.muni.chat.server.service.StoredMessage;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.servers.ServerVariable;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Spring REST Controller for JSON communication.
 */
@RestController // this annotation marks this class as a Spring REST Controller
@OpenAPIDefinition( // metadata for inclusion into OpenAPI document
        // see javadoc at https://javadoc.io/doc/io.swagger.core.v3/swagger-annotations/latest/index.html
        // see docs at https://github.com/swagger-api/swagger-core/wiki/Swagger-2.X---Annotations
        info = @Info(title = "Example Chat Service",
                version = "1.1",
                description = """
                                Simple service for chatting. The API has operations for:
                                - getting all messages
                                - creating a new message
                                - getting messages in pages, i.e. last 20 messages or previous 20 messages
                                - getting a specific message by its id
                                
                                The description of the API in OpenAPI format is generated by
                                the tool [SpringDoc v2](https://springdoc.org/v2/) from annotated Java classes,
                                mainly the controller class
                                [ChatRestController](https://github.com/martin-kuba/muni-chat-service/blob/main/chat-server/src/main/java/cz/muni/chat/server/rest/ChatRestController.java)
                                and the DTO classes in
                                [cz.muni.chat.server.facade](https://github.com/martin-kuba/muni-chat-service/tree/main/chat-server/src/main/java/cz/muni/chat/server/facade)
                                package.
                                """,
                contact = @Contact(name = "Martin Kuba", email = "makub@ics.muni.cz", url = "https://www.muni.cz/lide/3988-martin-kuba"),
                license = @License(name = "Apache 2.0", url = "https://www.apache.org/licenses/LICENSE-2.0.html")
        ),
        servers = @Server(description = "my server", url = "{scheme}://{server}:{port}", variables = {
                @ServerVariable(name = "scheme", allowableValues = {"http", "https"}, defaultValue = "http"),
                @ServerVariable(name = "server", defaultValue = "localhost"),
                @ServerVariable(name = "port", defaultValue = "8080"),
        })
)
@Tag(name = "Chat", description = "microservice for chat")
@RequestMapping("/api")
public class ChatRestController {

    // logging with SLF4J
    private static final Logger log = LoggerFactory.getLogger(ChatRestController.class);

    private final ChatService chatService;

    @Autowired
    public ChatRestController(ChatService chatService) {
        this.chatService = chatService;
    }


    /**
     * REST method returning all messages.
     */
    @Operation(
            summary = "Get all messages",
            description = """
                    Returns an array of objects representing chat messages, ordered from the oldest to the newest.
                    Each message must have a **text** and **timestamp**, and optionally may have an **author**,
                    a **text color** and a **background color**.
                    It is possible to use [MarkDown](https://www.markdownguide.org/) in descriptions.
                    """)
    @GetMapping(path = "/messages", produces = MediaType.APPLICATION_JSON_VALUE)
    @CrossOrigin(origins = "*")
    public List<ChatMessage> getAllMessages(HttpServletRequest req) {
        log.info("GET /messages called from {}", req.getRemoteHost());
        return chatService.getAllChatMessages().stream().map(ChatMessage::fromStoredMessage).toList();
    }


    /**
     * REST method returning message with specified id.
     */
    @Operation(
            summary = "Returns identified message",
            description = "Looks up a message by its id.",
            responses = {
                    @ApiResponse(responseCode = "200", ref = "#/components/responses/SingleMessageResponse"),
                    @ApiResponse(responseCode = "404", description = "message not found",
                            content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
            }
    )
    @GetMapping(path = "/message/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @CrossOrigin(origins = "*")
    public ChatMessage getMessage(@PathVariable String id) {
        StoredMessage m = chatService.getMessage(id);
        if (m != null) return ChatMessage.fromStoredMessage(m);
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "message with id=" + id + " not found");
    }


    /**
     * REST method for creating a new message.
     */
    @Operation(
            summary = "Create a new message",
            description = """
                    Receives data both in request body and as URL parameter and stores them as a new message.
                    Returns the new message as its response.
                    """,
            responses = {
                    @ApiResponse(responseCode = "201", ref = "#/components/responses/SingleMessageResponse"),
                    @ApiResponse(responseCode = "400", description = "input data were not correct",
                            content = @Content(schema = @Schema(implementation = ErrorMessage.class))
                    ),
            }
    )
    @PostMapping(path = "/messages", produces = MediaType.APPLICATION_JSON_VALUE)
    @CrossOrigin(origins = "*")
    @ResponseStatus(HttpStatus.CREATED)
    public ChatMessage createMessage(@Valid @RequestBody NewChatMessageRequest r,
                                     BindingResult bindingResult,
                                     @RequestParam(required = false) String author,
                                     HttpServletRequest httpServletRequest,
                                     @RequestHeader(value = "User-agent", required = false) String userAgent
    ) {
        log.info("POST /messages called from {} by {}", httpServletRequest.getRemoteHost(), userAgent);
        // check validity of input data
        if (bindingResult.hasErrors()) {
            log.warn("binding errors found");
            for (FieldError fe : bindingResult.getFieldErrors()) {
                log.warn("FieldError: {} - {}", fe.getField(), fe.getDefaultMessage());
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, fe.getField() + " - " + fe.getDefaultMessage());
            }
        }
        // default values
        if (author == null) {
            author = httpServletRequest.getRemoteHost();
        }
        BackgroundColor bc = (r.getBackgroundColor() == null) ? BackgroundColor.LIGHTGRAY : r.getBackgroundColor();
        StoredMessage message = chatService.createNewChatMessage(r.getText(), author, r.getTextColor(), bc.getValue());
        return ChatMessage.fromStoredMessage(message);
    }

    /**
     * REST method returning messages in pages.
     */
    @Operation(
            summary = "Paged messages",
            description = """
                    Returns a page of chat messages.
                    The parameter `page` specifies zero-based index of the requested page,
                    and the parameter `size` specifies the size of the page.
                    """)
    @GetMapping(path = "/paged", produces = MediaType.APPLICATION_JSON_VALUE)
    @CrossOrigin(origins = "*")
    public Page<ChatMessage> paged(@ParameterObject Pageable pageable) {
        return chatService.getPageOfMessages(pageable).map(ChatMessage::fromStoredMessage);
    }

}
