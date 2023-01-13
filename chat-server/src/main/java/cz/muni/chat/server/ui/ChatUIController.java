package cz.muni.chat.server.ui;

import cz.muni.chat.server.service.ChatService;
import cz.muni.chat.server.service.StoredMessage;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.apache.commons.text.StringEscapeUtils.escapeHtml4;

/**
 * Simple Spring MVC Controller generating HTML page on URL /chat displaying all chat messages.
 */
@Controller
public class ChatUIController {

    private final ChatService chatService;

    @Autowired
    public ChatUIController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping(path = "/chat", produces = MediaType.TEXT_HTML_VALUE)
    public void chat(HttpServletResponse res) throws IOException {
        // we are generating HTML page
        res.setContentType("text/html; charset=utf-8");
        // the page should refresh itself every 2 seconds
        res.setHeader("Refresh", "2");
        // generate messages in reverse order
        PrintWriter out = res.getWriter();
        out.println("<html><body><h1>Chat Service</h1>");
        // prevent race condition on concurrent accesses
        List<StoredMessage> chatMessages = chatService.getAllChatMessages();
        // iterate messages in reverse order
        for (int i = 0 ; i < chatMessages.size(); i++) {
            StoredMessage cm = chatMessages.get(i);
            out.println("<div class=\"message\" style=\"" +
                    "margin: 10px ; padding: 10px " +
                    "; color: " + escapeHtml4(cm.textColor()) +
                    "; background-color: " + escapeHtml4(cm.backgroundColor())
                    + "\" >");
            out.println("from: <b>" + escapeHtml4(cm.author()) + "</b>");

            out.println("time: <b>" + escapeHtml4(DateTimeFormatter.RFC_1123_DATE_TIME.format(cm.timestamp())) + "</b><br/><br/>");
            out.println(escapeHtml4(cm.text()) + "<br/>");
            out.println("</div>");
        }
    }

}
