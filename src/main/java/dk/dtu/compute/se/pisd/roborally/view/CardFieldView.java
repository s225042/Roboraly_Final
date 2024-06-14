package dk.dtu.compute.se.pisd.roborally.view;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.model.*;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import org.jetbrains.annotations.NotNull;

public class CardFieldView extends GridPane implements ViewObserver {

    final public static DataFormat ROBO_RALLY_CARD = new DataFormat("boards/games/roborally/cards");

    final public static int CARDFIELD_WIDTH = 65;
    final public static int CARDFIELD_HEIGHT = 100;

    final public static Border BORDER = new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, null, new BorderWidths(2)));

    final public static Background BG_DEFAULT = new Background(new BackgroundFill(Color.WHITE, null, null));
    final public static Background BG_DRAG = new Background(new BackgroundFill(Color.GRAY, null, null));
    final public static Background BG_DROP = new Background(new BackgroundFill(Color.LIGHTGRAY, null, null));

    final public static Background BG_ACTIVE = new Background(new BackgroundFill(Color.YELLOW, null, null));
    final public static Background BG_DONE = new Background(new BackgroundFill(Color.GREENYELLOW,  null, null));

    private CommandCardField field;

    private Label label;

    private GameController gameController;

    public CardFieldView(@NotNull GameController gameController, @NotNull CommandCardField field) {
        this.gameController = gameController;
        this.field = field;

        this.setAlignment(Pos.CENTER);
        this.setPadding(new Insets(5, 5, 5, 5));

        this.setBorder(BORDER);
        this.setBackground(BG_DEFAULT);

        this.setPrefWidth(CARDFIELD_WIDTH);
        this.setMinWidth(CARDFIELD_WIDTH);
        this.setMaxWidth(CARDFIELD_WIDTH);
        this.setPrefHeight(CARDFIELD_HEIGHT);
        this.setMinHeight(CARDFIELD_HEIGHT);
        this.setMaxHeight(CARDFIELD_HEIGHT);

        label = new Label();
        label.setWrapText(true);
        label.setMouseTransparent(true);
        this.add(label, 0, 0);

        this.setOnDragDetected(new OnDragDetectedHandler());
        this.setOnDragOver(new OnDragOverHandler());
        this.setOnDragEntered(new OnDragEnteredHandler());
        this.setOnDragExited(new OnDragExitedHandler());
        this.setOnDragDropped(new OnDragDroppedHandler());
        this.setOnDragDone(new OnDragDoneHandler());

        field.attach(this);
        update(field);
    }

    private String cardFieldRepresentation(CommandCardField cardField) {
        if (cardField.player != null) {

            for (int i = 0; i < Player.NO_REGISTERS; i++) {
                CommandCardField other = cardField.player.getProgramField(i);
                if (other == cardField) {
                    return "P," + i;
                }
            }

            for (int i = 0; i < Player.NO_CARDS; i++) {
                CommandCardField other = cardField.player.getCardField(i);
                if (other == cardField) {
                    return "C," + i;
                }
            }
        }
        return null;

    }

    private CommandCardField cardFieldFromRepresentation(String rep) {
        if (rep != null && field.player != null) {
            String[] strings = rep.split(",");
            if (strings.length == 2) {
                int i = Integer.parseInt(strings[1]);
                if ("P".equals(strings[0])) {
                    if (i < Player.NO_REGISTERS) {
                        return field.player.getProgramField(i);
                    }
                } else if ("C".equals(strings[0])) {
                    if (i < Player.NO_CARDS) {
                        return field.player.getCardField(i);
                    }
                }
            }
        }
        return null;
    }

    @Override
    public void updateView(Subject subject) {
        if (subject == field && subject != null) {
            CommandCard card = field.getCard();
            if (card != null && field.isVisible()) {
                label.setText(card.getName());
            } else {
                label.setText("");
            }
        }
    }

    private class OnDragDetectedHandler implements EventHandler<MouseEvent> {
        @Override
        public void handle(MouseEvent event) {
            Object t = event.getTarget();
            if (t instanceof CardFieldView) {
                CardFieldView source = (CardFieldView) t;
                CommandCardField cardField = source.field;
                if (cardField != null && cardField.getCard() != null &&
                        cardField.player != null &&
                        cardField.player.board != null &&
                        cardField.player.board.getPhase().equals(Phase.PROGRAMMING)) {
                    Dragboard db = source.startDragAndDrop(TransferMode.MOVE);
                    Image image = source.snapshot(null, null);
                    db.setDragView(image);

                    ClipboardContent content = new ClipboardContent();
                    content.put(ROBO_RALLY_CARD, cardFieldRepresentation(cardField));

                    db.setContent(content);
                    source.setBackground(BG_DRAG);
                }
            }
            event.consume();
        }
    }

    private class OnDragOverHandler implements EventHandler<DragEvent> {
        @Override
        public void handle(DragEvent event) {
            Object t = event.getTarget();
            if (t instanceof CardFieldView) {
                CardFieldView target = (CardFieldView) t;
                CommandCardField cardField = target.field;
                if (cardField != null &&
                        (cardField.getCard() == null || event.getGestureSource() == target) &&
                        cardField.player != null &&
                        cardField.player.board != null) {
                    if (event.getDragboard().hasContent(ROBO_RALLY_CARD)) {
                        event.acceptTransferModes(TransferMode.MOVE);
                    }
                }
            }
            event.consume();
        }
    }

    private class OnDragEnteredHandler implements EventHandler<DragEvent> {
        @Override
        public void handle(DragEvent event) {
            Object t = event.getTarget();
            if (t instanceof CardFieldView) {
                CardFieldView target = (CardFieldView) t;
                CommandCardField cardField = target.field;
                if (cardField != null &&
                        cardField.getCard() == null &&
                        cardField.player != null &&
                        cardField.player.board != null) {
                    if (event.getGestureSource() != target &&
                            event.getDragboard().hasContent(ROBO_RALLY_CARD)) {
                        target.setBackground(BG_DROP);
                    }
                }
            }
            event.consume();
        }
    }

    private class OnDragExitedHandler implements EventHandler<DragEvent> {
        @Override
        public void handle(DragEvent event) {
            Object t = event.getTarget();
            if (t instanceof CardFieldView) {
                CardFieldView target = (CardFieldView) t;
                CommandCardField cardField = target.field;
                if (cardField != null &&
                        cardField.getCard() == null &&
                        cardField.player != null &&
                        cardField.player.board != null) {
                    if (event.getGestureSource() != target &&
                            event.getDragboard().hasContent(ROBO_RALLY_CARD)) {
                        target.setBackground(BG_DEFAULT);
                    }
                }
            }
            event.consume();
        }
    }

    private class OnDragDroppedHandler implements EventHandler<DragEvent> {
        @Override
        public void handle(DragEvent event) {
            Object t = event.getTarget();
            if (t instanceof CardFieldView) {
                CardFieldView target = (CardFieldView) t;
                CommandCardField cardField = target.field;

                Dragboard db = event.getDragboard();
                boolean success = false;
                if (cardField != null &&
                        cardField.getCard() == null &&
                        cardField.player != null &&
                        cardField.player.board != null) {
                    if (event.getGestureSource() != target &&
                            db.hasContent(ROBO_RALLY_CARD)) {
                        Object object = db.getContent(ROBO_RALLY_CARD);
                        if (object instanceof String) {
                            CommandCardField source = cardFieldFromRepresentation((String) object);
                            if (source != null && gameController.moveCards(source, cardField)) {
                                success = true;
                            }
                        }
                    }
                }
                event.setDropCompleted(success);
                target.setBackground(BG_DEFAULT);
            }
            event.consume();
        }
    }

    private class OnDragDoneHandler implements EventHandler<DragEvent> {
        @Override
        public void handle(DragEvent event) {
            Object t = event.getTarget();
            if (t instanceof CardFieldView) {
                CardFieldView source = (CardFieldView) t;
                source.setBackground(BG_DEFAULT);
            }
            event.consume();
        }
    }
}