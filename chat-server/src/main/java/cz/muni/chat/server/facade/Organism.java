package cz.muni.chat.server.facade;

import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        discriminatorProperty = "typeName",
        subTypes = Animal.class,
        discriminatorMapping = {
                @DiscriminatorMapping(value = "cz.muni.chat.server.facade.Animal", schema = Animal.class),
                @DiscriminatorMapping(value = "cz.muni.chat.server.facade.Pet", schema = Pet.class)
        }
)
public class Organism {

    public String getTypeName() {
        return this.getClass().getTypeName();
    }

    String latinName;

    public String getLatinName() {
        return latinName;
    }

    public void setLatinName(String latinName) {
        this.latinName = latinName;
    }

}
