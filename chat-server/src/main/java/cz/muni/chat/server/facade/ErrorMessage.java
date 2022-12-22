package cz.muni.chat.server.facade;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Just for description of JSON returned for ResponseStatusException.
 */
@Schema(title = "error message",
        description = "response body for HTML statuses"
)
public record ErrorMessage(
        @Schema(format = "date-time", description = "time in ISO format", example = "2022-12-21T18:52:10.757+00:00")
        String timestamp,
        @Schema(description = "HTTP status code", example = "404")
        int status,
        @Schema(description = "HTTP status text", example = "Not Found")
        String error,
        @Schema(description = "reason for error", example = "entity not found")
        String message,
        @Schema(description = "URL path", example = "/api/message/1")
        String path){}
