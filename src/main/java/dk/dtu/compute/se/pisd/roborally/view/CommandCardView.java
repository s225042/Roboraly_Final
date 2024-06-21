package dk.dtu.compute.se.pisd.roborally.view;

import dk.dtu.compute.se.pisd.roborally.model.Command;
import dk.dtu.compute.se.pisd.roborally.model.CommandCard;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class CommandCardView extends VBox {

    public CommandCardView(CommandCard commandCard) {
        if (commandCard == null) {
            throw new IllegalArgumentException("CommandCard cannot be null");
        }

        Command command = commandCard.getCommand();

        ImageView imageView = new ImageView();
        try {
            Image image = new Image(getClass().getResource(command.imagePath).toExternalForm());
            imageView.setImage(image);
            imageView.setFitWidth(60);  // Set the desired width
            imageView.setFitHeight(60); // Set the desired height
        } catch (Exception e) {
            System.err.println("Error loading image: " + command.imagePath);
            e.printStackTrace();
        }

        Text commandText = new Text(command.displayName);

        this.getChildren().addAll(imageView, commandText);
    }
}
