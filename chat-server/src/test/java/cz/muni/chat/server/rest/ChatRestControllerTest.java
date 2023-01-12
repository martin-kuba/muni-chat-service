package cz.muni.chat.server.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ChatRestController.class)
class ChatRestControllerTest {

    private static final Logger log = LoggerFactory.getLogger(ChatRestControllerTest.class);

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ChatService mockChatService;

    @Autowired
    private ObjectMapper objectMapper;

    static ZonedDateTime now = ZonedDateTime.of(2023, 1, 5, 10, 0, 0, 0, ZoneId.systemDefault());
    static StoredMessage message01 = new StoredMessage("01", now, "hello", null, null, null);
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
        given(mockChatService.getAllChatMessages()).willReturn(allMessages);
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
        StoredMessage msg = new StoredMessage(id, now, text, author, textColor, backgroundColor.getValue());
        given(mockChatService.createNewChatMessage(text, author, textColor, backgroundColor.getValue())).willReturn(msg);
        // call controller and check the result
        NewChatMessageRequest n = new NewChatMessageRequest();
        n.setText(text);
        n.setTextColor(textColor);
        n.setBackgroundColor(BackgroundColor.WHITE);
        mockMvc.perform(post("/api/messages?author="+author)
                        .header("User-Agent","007")
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
        // define what mock service returns when called
        // call controller and check the result
        NewChatMessageRequest n = new NewChatMessageRequest();
        n.setText(null);
        n.setTextColor("black");
        mockMvc.perform(post("/api/messages")
                        .header("User-Agent","007")
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
        mockMvc.perform(get("/api/paged").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value("01"))
                .andExpect(jsonPath("$.content.length()").value(1))
        ;
    }

}