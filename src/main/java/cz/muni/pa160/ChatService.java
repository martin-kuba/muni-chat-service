package cz.muni.pa160;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.text.StringEscapeUtils.escapeHtml4;

/**
 * Simple web service implemented using Spring Boot framework.
 *
 * @author Martin Kuba makub@ics.muni.cz
 */
@SpringBootApplication
@Controller
@SuppressWarnings("unused")
public class ChatService {

    // start this class as a Spring Boot application
    public static void main(String[] args) {
        SpringApplication.run(ChatService.class, args);
    }

    // logging with SLF4J
    private static final Logger log = LoggerFactory.getLogger(ChatService.class);

    // in-memory storage of messages
    private final List<ChatMessage> chatMessages = new ArrayList<>();

    // the last step of initialization
    @PostConstruct
    public void init() {
        chatMessages.add(new ChatMessage("\uD83D\uDEC8 system","first message", "black", "#b8fcfa"));
        log.info("the first message added");
    }

    /**
     * REST method returning all messages.
     */
    @RequestMapping(value = "/api/messages", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @CrossOrigin(origins = "*")
    public ResponseEntity<?> messages(HttpServletRequest req) {
        log.info("GET /messages called from {}", req.getRemoteHost());
        return ResponseEntity.ok().body(chatMessages);
    }

    /**
     * REST method creating a new message.
     */
    @RequestMapping(value = "/api/messages", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @CrossOrigin(origins = "*")
    public ResponseEntity<?> createMessage(HttpServletRequest req,
                                           @RequestParam(required = false) String author,
                                           @RequestParam() String text,
                                           @RequestParam(defaultValue = "black") String textColor,
                                           @RequestParam(defaultValue = "lightgray") String backgroundColor
    ) {
        log.info("POST /messages called from {}", req.getRemoteHost());
        if (author == null) {
            author = req.getRemoteHost();
        }
        ChatMessage c = new ChatMessage(author, text, textColor, backgroundColor);
        synchronized (chatMessages) {
            chatMessages.add(c);
        }
        return ResponseEntity.ok().body(c);
    }

    @RequestMapping(value = "/chat", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public void chat(HttpServletResponse res) throws IOException {
        // we are generating HTML page
        res.setContentType("text/html; charset=utf-8");
        // the page should refresh itself every 2 seconds
        res.setHeader("Refresh", "2");
        // generate messages in reverse order
        PrintWriter out = res.getWriter();
        out.println("<html><body><h1>PA160 Chat Service</h1>");
        // prevent race condition on concurrent accesses
        synchronized (chatMessages) {
            // iterate messages in reverse order
            for (int i = chatMessages.size(); i-- > 0; ) {
                ChatMessage c = chatMessages.get(i);
                out.println("<div class=\"message\" style=\"" +
                        "margin: 10px ; padding: 10px " +
                        "; color: " + escapeHtml4(c.getTextColor()) +
                        "; background-color: " + escapeHtml4(c.getBackgroundColor())
                        + "\" >");
                out.println("from: <b>" + escapeHtml4(c.getAuthor()) + "</b><br/><br/>");
                out.println(escapeHtml4(c.getText()) + "<br/>");
                out.println("</div>");
            }
        }
    }

    /**
     * Entity class for a chat message.
     */
    public static class ChatMessage {
        String author;
        String text;
        String textColor;
        String backgroundColor;

        public ChatMessage(String author, String text, String textColor, String backgroundColor) {
            this.author = author;
            this.text = text;
            this.textColor = textColor;
            this.backgroundColor = backgroundColor;
        }

        public String getAuthor() {
            return author;
        }

        public String getText() {
            return text;
        }

        public String getTextColor() {
            return textColor;
        }

        public String getBackgroundColor() {
            return backgroundColor;
        }
    }
}
