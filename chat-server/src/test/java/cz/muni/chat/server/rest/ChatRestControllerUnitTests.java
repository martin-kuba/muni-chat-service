package cz.muni.chat.server.rest;

import cz.muni.chat.server.facade.BackgroundColor;
import cz.muni.chat.server.facade.NewChatMessageRequest;
import cz.muni.chat.server.service.ChatService;
import cz.muni.chat.server.service.StoredMessage;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests.
 * <p>
 * Unit tests are run by Maven's surefire plugin in the "mvn test" phase,
 * the class name must end with "Test" or "Tests".
 * <p>
 * The unit tests test the RestController as a separated unit, with mock (fake)
 * implementation of ChatService which provides exactly the expected data only
 * and with mock implementation of Spring MVC that calls the RestController.
 */
@WebMvcTest(ChatRestController.class)
@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)
class ChatRestControllerUnitTests {

    private static final Logger log = LoggerFactory.getLogger(ChatRestControllerUnitTests.class);

    // injected mock implementation of Spring MVC
    @Autowired
    private MockMvc mockMvc;

    // injected mock implementation of the ChatService interface
    @MockitoBean
    private ChatService mockChatService;

    // injected mapper for converting classes into JSON strings
    @Autowired
    private ObjectMapper objectMapper;

    static ZonedDateTime now = ZonedDateTime.of(2023, 1, 5, 10, 0, 0, 0, ZoneId.systemDefault());
    static StoredMessage message01 = new StoredMessage("01", now, "hello", null, null, null, null);
    static List<StoredMessage> allMessages = List.of(message01);

    @BeforeAll
    static void beforeAll() {
    }

    @AfterAll
    static void afterAll() {
    }

    @BeforeEach
    void beforeEach() {
    }

    @AfterEach
    void afterEach() {
    }

    @Test
    void getAllMessages() throws Exception {
        log.debug("getAllMessages");
        // define what mock service returns when called
        given(mockChatService.getAllMessages()).willReturn(allMessages);
        // call controller and check the result
        mockMvc.perform(get("/api/messages").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[?(@.id=='01')].text").value("hello"))
        ;
    }

    @Test
    void getExistingMessage() throws Exception {
        log.debug("getMessage existing");
        // define what mock service returns when called
        given(mockChatService.getMessage(anyString())).willReturn(message01);
        // call controller and check the result
        mockMvc.perform(get("/api/message/01").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[?(@.id=='01')].text").value("hello"))
        //.andDo(print())
        ;
    }

    @Test
    void getNonExistingMessage() throws Exception {
        log.debug("getMessage non-existing");
        // define what mock service returns when called
        given(mockChatService.getMessage("-1")).willReturn(null);
        // call controller and check the result
        mockMvc.perform(get("/api/message/-1"))
                .andExpect(status().isNotFound())
        ;
    }

    @Test
    void createMessage() throws Exception {
        log.debug("createMessage");
        // define what mock service returns when called
        String id = "X";
        String text = "To be or not to be";
        String author = "Shakespeare";
        String textColor = "black";
        BackgroundColor backgroundColor = BackgroundColor.WHITE;
        StoredMessage msg = new StoredMessage(id, now, text, author, textColor, backgroundColor.getValue(), null);
        given(mockChatService.createNewMessage(text, author, textColor, backgroundColor.getValue())).willReturn(msg);
        // call controller and check the result
        NewChatMessageRequest n = new NewChatMessageRequest();
        n.setText(text);
        n.setTextColor(textColor);
        n.setBackgroundColor(BackgroundColor.WHITE);
        mockMvc.perform(post("/api/messages?author=" + author)
                        .header("User-Agent", "007")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(n))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.text").value(text))
                .andExpect(jsonPath("$.author").value(author))
                .andExpect(jsonPath("$.textColor").value(textColor))
                .andExpect(jsonPath("$.backgroundColor").value(backgroundColor.getValue()))
        ;
    }

    @Test
    void createMessageWithoutText() throws Exception {
        log.debug("createMessageWithoutText");
        // mock service is not needed in this test
        // call controller and check the result
        NewChatMessageRequest n = new NewChatMessageRequest();
        n.setText(null);
        n.setTextColor("black");
        mockMvc.perform(post("/api/messages")
                        .header("User-Agent", "007")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(n))
                )
                .andExpect(status().isBadRequest())
        //.andDo(print())
        ;
    }

    @Test
    void paged() throws Exception {
        log.debug("paged");
        // define what mock service returns when called
        Pageable p = PageRequest.of(0, 2);
        Page<StoredMessage> page = new PageImpl<>(allMessages, p, allMessages.size());
        given(mockChatService.getPageOfMessages(any())).willReturn(page);
        // call controller and check the result
        mockMvc.perform(get("/api/paged?page={page}&size={size}", p.getPageNumber(), p.getPageSize()).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value("01"))
                .andExpect(jsonPath("$.content.length()").value(1))
        //.andDo(print())
        ;
    }

}