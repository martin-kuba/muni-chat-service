package cz.muni.chat.server.facade;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema( discriminatorProperty = "typeName", subTypes = Pet.class)
public class Animal extends Organism {
    int numberOfLegs;

    public int getNumberOfLegs() {
        return numberOfLegs;
    }

    public void setNumberOfLegs(int numberOfLegs) {
        this.numberOfLegs = numberOfLegs;
    }
}
