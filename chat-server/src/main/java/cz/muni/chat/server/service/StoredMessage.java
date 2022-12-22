package cz.muni.chat.server.service;

import java.time.ZonedDateTime;

/**
 * Represents a message in the service layer.
 */
public record StoredMessage(String id, ZonedDateTime timestamp, String text, String author, String textColor, String backgroundColor) {
}
