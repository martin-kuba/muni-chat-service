package cz.muni.chat.server.facade;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.stream.Stream;

/**
 * Enum for background color.
 */
@Schema(
        name = "BackgroundColorEnum",
        title = "background color",
        description = "enumeration of allowed background colors",
        enumAsRef = true
)
public enum BackgroundColor {
    // available values
    LIGHTGRAY("lightgray"),
    WHITE("white"),
    AQUAMARINE("aquamarine"),
    LIGHTYELLOW("lightyellow"),
    LIGHTBLUE("lightblue"),
    BISQUE("#ffe4c4");

    private final String value;

    BackgroundColor(String value) {
        this.value = value;
    }

    /**
     * Converts from string to enum when parsing JSON.
     */
    @JsonCreator
    public static BackgroundColor fromValue(String value) {
        return Stream.of(values()).filter(x -> x.value.equals(value)).findFirst().orElse(null);
    }

    /**
     * Converts from enum to string when generating JSON.
     */
    @JsonValue
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
