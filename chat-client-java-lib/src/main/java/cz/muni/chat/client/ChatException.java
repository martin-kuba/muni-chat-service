package cz.muni.chat.client;

import cz.muni.chat.client.invoker.ApiException;
import cz.muni.chat.client.model.ErrorMessage;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.time.OffsetDateTime;

/**
 * Exception created from ErrorMessage sent from ChatService for non-200 HTTP status codes.
 */
public class ChatException extends RuntimeException {

    public ChatException(String message, Throwable cause) {
        super(message, cause);
    }

    private OffsetDateTime timestamp;

    private Integer httpStatus;

    private String httpStatusName;

    private String path;

    private static final ObjectMapper objectMapper = JsonMapper.builder()
            .findAndAddModules()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .build();

    public static ChatException from(ApiException ex) {
        try {
            ErrorMessage em = objectMapper.readValue(ex.getResponseBody(), ErrorMessage.class);
            ChatException che = new ChatException(em.getMessage(), ex);
            che.timestamp = em.getTimestamp();
            che.httpStatus = em.getStatus();
            che.httpStatusName = em.getError();
            che.path = em.getPath();
            return che;
        } catch (Exception ioe) {
            return new ChatException("cannot parse remote Exception", ex);
        }
    }

    public OffsetDateTime getTimestamp() {
        return timestamp;
    }

    public Integer getHttpStatus() {
        return httpStatus;
    }

    public String getHttpStatusName() {
        return httpStatusName;
    }

    public String getPath() {
        return path;
    }

    @Override
    public String toString() {
        return "ChatException: " +
                getMessage() + '\'' +
                ", httpStatus=" + httpStatus + " (" + httpStatusName + ')' +
                ", path='" + path + '\'' +
                ", timestamp=" + timestamp
                ;
    }
}
