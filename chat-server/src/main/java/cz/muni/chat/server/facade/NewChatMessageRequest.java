package cz.muni.chat.server.facade;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * Represents JSON-formatted body of HTTP request to createMessage operation.
 */
@Schema(description = """
        Object for requesting new message.
        **Text** of the message must be located in the request body because URLs are limited in size.
        See [What is the maximum length of a URL in different browsers?](https://stackoverflow.com/questions/417142/what-is-the-maximum-length-of-a-url-in-different-browsers)
        """
)
public class NewChatMessageRequest {

    @NotBlank
    @Schema(description = "text of message", example = "Hello! \uD83D\uDE00")
    private String text;

    @Schema(description = "HTML color name or RGB hex code", example = "black")
    private String textColor = "black";

    private BackgroundColor backgroundColor;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTextColor() {
        return textColor;
    }

    public void setTextColor(String textColor) {
        this.textColor = textColor;
    }

    public BackgroundColor getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(BackgroundColor backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    @Override
    public String toString() {
        return "NewChatMessageRequest{" +
                "text='" + text + '\'' +
                ", textColor='" + textColor + '\'' +
                ", backgroundColor=" + backgroundColor +
                '}';
    }
}
