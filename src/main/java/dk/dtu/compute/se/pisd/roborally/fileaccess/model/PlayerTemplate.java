package dk.dtu.compute.se.pisd.roborally.fileaccess.model;

import dk.dtu.compute.se.pisd.roborally.model.CommandCardField;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;

import java.util.ArrayList;
import java.util.List;
/**
 @author Rebecca Moss, s225042@dtu.dk
 */
public class PlayerTemplate {
    public String name;
    public String color;

    public Heading heading;

    public int x;
    public int y;
    public List<CommandCardFieldTemplate> programmingCards = new ArrayList<>();
    public List<CommandCardFieldTemplate> commandCards = new ArrayList<>();

    public PlayerTemplate(Player player){
        this.name = player.getName();
        this.color = player.getColor();
        this.heading = player.getHeading();
        this.x = player.getSpace().x;
        this.y = player.getSpace().y;

        for (int i = 0; i<Player.NO_REGISTERS; i++){
            this.programmingCards.add(new CommandCardFieldTemplate(player.getProgramField(i)));
        }

        for (int i = 0; i<Player.NO_CARDS; i++){
            this.commandCards.add(new CommandCardFieldTemplate(player.getCardField(i)));
        }
    }
}
