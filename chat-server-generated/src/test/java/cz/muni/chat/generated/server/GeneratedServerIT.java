package cz.muni.chat.generated.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.muni.chat.generated.server.model.BackgroundColorEnum;
import cz.muni.chat.generated.server.model.ChatMessage;
import cz.muni.chat.generated.server.model.NewChatMessageRequest;
import cz.muni.chat.generated.server.model.NewChatMessageRequest.TextColorEnum;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class GeneratedServerIT {

    private static final Logger log = LoggerFactory.getLogger(GeneratedServerIT.class);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void testMessageIds() throws Exception {
        log.debug("Integration Test - testMessageIds");
        //create more messages
        List<ChatMessage> msgs = new ArrayList<>();
        for(int i = 1; i<=2; i++) {
            String author = "Joe";
            String text = "message " + i;
            TextColorEnum textColor = TextColorEnum.BLACK;
            BackgroundColorEnum backgroundColor = BackgroundColorEnum.WHITE;
            NewChatMessageRequest n = new NewChatMessageRequest();
            n.setText(text);
            n.setTextColor(textColor);
            n.setBackgroundColor(backgroundColor);
            String response = mockMvc.perform(post("/api/messages?author=" + author)
                            .header("User-Agent", "007")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(n))
                    )
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.text").value(text))
                    .andExpect(jsonPath("$.author").value(author))
                    .andExpect(jsonPath("$.textColor").value(textColor.getValue()))
                    .andExpect(jsonPath("$.backgroundColor").value(backgroundColor.getValue()))
                    .andReturn().getResponse().getContentAsString();
            log.debug("response = {}", response);
            ChatMessage chatMessage = objectMapper.readValue(response, ChatMessage.class);
            log.debug("msg = {}", chatMessage);
            msgs.add(chatMessage);
        }

        // get each message by calling URL and compare its content
        for (ChatMessage msg : msgs) {
            // read each message by calling the RestController
            String response = mockMvc.perform(get("/api/message/{id}",msg.getId()).contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();
            log.debug("response: {}", response);
            ChatMessage chatMessage = objectMapper.readValue(response, ChatMessage.class);
            assertThat("id", chatMessage.getId(), is(equalTo(msg.getId())));
            assertThat("text", chatMessage.getText(), is(equalTo(msg.getText())));
        }
    }

}
