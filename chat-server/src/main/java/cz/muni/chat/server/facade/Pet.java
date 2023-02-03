package cz.muni.chat.server.facade;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public class Pet extends Animal {
    String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

