package cz.muni.chat.server.rest;

import cz.muni.chat.server.facade.BackgroundColor;
import cz.muni.chat.server.facade.ChatMessage;
import cz.muni.chat.server.facade.NewChatMessageRequest;
import cz.muni.chat.server.service.ChatService;
import cz.muni.chat.server.service.StoredMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Objects;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests.
 * <p>
 * Integration tests are run by Maven's failsafe plugin in the "mvn verify" phase,
 * the class name must end with "IT".
 * <p>
 * These tests start a mock Spring MVC environment, but use the real implementation
 * of ChatService, and if there was a persistence layer, it would be used too.
 * Each test has a new instance of ChatService.
 */
@SpringBootTest
@AutoConfigureMockMvc
public class ChatIT {

    private static final Logger log = LoggerFactory.getLogger(ChatIT.class);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // real ChatService, new for each test
    @Autowired
    private ChatService chatService;

    @BeforeEach
    void beforeEach() {
    }

    @Test
    void testPaging() throws Exception {
        log.debug("Integration Test - testPaging");
        String author = "Ian Flemming";
        int totalMessages = 10;
        for(int i = 1; i< totalMessages; i++) {
            NewChatMessageRequest n = new NewChatMessageRequest();
            n.setText("message "+i);
            n.setTextColor("black");
            n.setBackgroundColor(BackgroundColor.WHITE);
            mockMvc.perform(post("/api/messages?author=" + author)
                            .header("User-Agent", "007")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(n))
                    )
                    .andExpect(status().isCreated());
        }
        int pageIndex = 2;
        int pageSize = 3;
        mockMvc.perform(get("/api/paged?page={page}&size={size}", pageIndex, pageSize).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.size").value(pageSize))
                .andExpect(jsonPath("$.page.number").value(pageIndex))
                .andExpect(jsonPath("$.page.totalElements").value(totalMessages))
                .andExpect(jsonPath("$.page.totalPages").value(4))
                .andExpect(jsonPath("$.content.length()").value(pageSize))
//                .andDo(print())
                ;
    }

    @Test
    void testMessageIds() throws Exception {
        log.debug("Integration Test - testMessageIds");
        //create 3 more messages directly in service
        for(int i = 1; i<4; i++) {
            chatService.createNewMessage("message "+i,null,null, null);
        }
        // get each message by calling URL and compare its content
        for (StoredMessage storedMessage : chatService.getAllMessages()) {
            // read each message by calling the RestController
            String response = mockMvc.perform(get("/api/message/{id}",storedMessage.id()).contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();
            log.debug("response: {}", response);
            ChatMessage chatMessage = objectMapper.readValue(response, ChatMessage.class);
            assertThat("id", chatMessage.id(), is(equalTo(storedMessage.id())));
            assertThat("text", chatMessage.text(), is(equalTo(storedMessage.text())));
        }
    }

    /**
     * This test that does not use mock MVC, but calls the real server started for integration maven tests phase.
     * It works only when executed by "mvn verify", not in development environment.
     */
    @Test
    void testRealHTTP() throws Exception {
        log.debug("Integration Test - testRealHTTP");
        RestTemplate restTemplate = new RestTemplate();
        // get list of messages converted into List of CHatMessage
        ResponseEntity<List<ChatMessage>> entity = restTemplate.exchange("http://localhost:8080/api/messages",
                HttpMethod.GET, HttpEntity.EMPTY, new ParameterizedTypeReference<>() {
        });
        for (ChatMessage chatMessage : Objects.requireNonNull(entity.getBody())) {
            log.debug("message: "+chatMessage);
        }
        // get list of messages as JSON nodes
        JsonNode root = restTemplate.getForObject("http://localhost:8080/api/messages", JsonNode.class);
        // the JSON root is a list, iterate over it
        for (JsonNode message : Objects.requireNonNull(root)) {
            String id = message.get("id").asText();
            String text = message.get("text").asText();
            log.debug("message id={} text={}", id, text);
        }
    }
}
