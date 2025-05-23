package cz.muni.chat.server.facade;

import cz.muni.chat.server.service.StoredMessage;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import java.time.ZonedDateTime;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

/**
 * DTO for a chat message. Data Transfer Object that is stable for API,
 * independent of internal StoredMessage class.
 *
 * @see cz.muni.chat.server.service.StoredMessage
 */
@Schema(title = "chat message",
        description = "represents a message in a chat"
)
public record ChatMessage(@NotBlank
                          @Schema(description = "uuid", example = "123e4567-e89b-12d3-a456-426614174000", requiredMode = REQUIRED)
                          String id,
                          @NotBlank
                          @Schema(type = "string", format = "date-time",
                                  description = "time of creation",
                                  example = "2022-12-22T12:04:04.493908908+01:00",
                                  requiredMode = REQUIRED)
                          ZonedDateTime timestamp,
                          @NotBlank
                          @Schema(description = "text of message", example = "Hello! \uD83D\uDE00", requiredMode = REQUIRED)
                          String text,
                          @Schema(description = "author of message", example = "John")
                          String author,
                          @Schema(description = "HTML color name or RGB hex code", example = "black")
                          String textColor,
                          BackgroundColor backgroundColor) {

    public static ChatMessage fromStoredMessage(StoredMessage m) {
        return new ChatMessage(m.id(), m.timestamp(), m.text(),
                m.author(), m.textColor(), BackgroundColor.fromValue(m.backgroundColor()));
    }
}
