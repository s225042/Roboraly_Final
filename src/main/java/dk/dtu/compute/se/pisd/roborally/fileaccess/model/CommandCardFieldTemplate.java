package dk.dtu.compute.se.pisd.roborally.fileaccess.model;

import dk.dtu.compute.se.pisd.roborally.model.CommandCard;
import dk.dtu.compute.se.pisd.roborally.model.CommandCardField;
import dk.dtu.compute.se.pisd.roborally.model.Player;
/**
 @author Rebecca Moss, s225042@dtu.dk
 */
public class CommandCardFieldTemplate {

    public CommandCard card;

    public boolean visible;
    public CommandCardFieldTemplate(CommandCardField cards){
        this.card = cards.getCard();
        this.visible = cards.isVisible();
    }
}
