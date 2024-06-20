package dk.dtu.compute.se.pisd.roborally.fileaccess;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import dk.dtu.compute.se.pisd.roborally.controller.FieldAction;
import dk.dtu.compute.se.pisd.roborally.fileaccess.model.BoardTemplate;
import dk.dtu.compute.se.pisd.roborally.fileaccess.model.CommandCardFieldTemplate;
import dk.dtu.compute.se.pisd.roborally.fileaccess.model.PlayerTemplate;
import dk.dtu.compute.se.pisd.roborally.fileaccess.model.SpaceTemplate;
import dk.dtu.compute.se.pisd.roborally.model.*;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class LoadBoard {

    private static final String BOARDSFOLDER = "boards";
    private static final String DEFAULTBOARD = "Optional[Hey]";
    private static final String JSON_EXT = "json";

    public static Board loadBoard(String boardname) {
        if (boardname == null) {
            boardname = DEFAULTBOARD;
        }

        ClassLoader classLoader = LoadBoard.class.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(BOARDSFOLDER + "/" + boardname + "." + JSON_EXT);
        if (inputStream == null) {
            inputStream = classLoader.getResourceAsStream(BOARDSFOLDER + "/" + "Optional[Hey]" + "." + JSON_EXT);
        }

        GsonBuilder simpleBuilder = new GsonBuilder().
                registerTypeAdapter(FieldAction.class, new Adapter<FieldAction>());
        Gson gson = simpleBuilder.create();

        Board result;
        JsonReader reader = null;
        try {
            reader = gson.newJsonReader(new InputStreamReader(inputStream));
            BoardTemplate template = gson.fromJson(reader, BoardTemplate.class);

            result = new Board(template.width, template.height, template.antennaX, template.antennaY);
            for (SpaceTemplate spaceTemplate : template.spaces) {
                Space space = result.getSpace(spaceTemplate.x, spaceTemplate.y);
                if (space != null) {
                    space.getActions().addAll(spaceTemplate.actions);
                    space.getWalls().addAll(spaceTemplate.walls);
                }
            }
            result.setMaxNumberofChekpoints();

            // Set the reboot token location
            result.setRebootSpace(template.rebootX, template.rebootY);

            if (template.rebootDirection != null) {
                result.setRebootDirection(Heading.valueOf(template.rebootDirection));
            }

            // Load spawn points
            for (SpawnPoint spawnPointTemplate : template.spawnPoints) {
                result.addSpawnPoint(new SpawnPoint(
                        spawnPointTemplate.x,
                        spawnPointTemplate.y
                ));
            }

            result.setPhase(template.phase);
            for (int i = 0; i < template.players.size(); i++) {
                loadPlayer(result, template.players.get(i));
            }
            result.setCurrentPlayer(result.getPlayer(template.current));
            result.setCounter(template.counter);
            reader.close();

            return result;
        } catch (IOException e1) {
            if (reader != null) {
                try {
                    reader.close();
                    inputStream = null;
                } catch (IOException e2) {}
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e2) {}
            }
        }

        return null;
    }

    public static void loadPlayer(Board board, PlayerTemplate template) {
        Player player = new Player(board, template.color, template.name);
        int x = template.x;
        int y = template.y;
        if (x >= 0 && y >= 0 && x < board.width && y < board.height) {
            Space space = board.getSpace(x, y);
            if (space != null) {
                player.setSpace(space);
                board.addPlayer(player);
            }
        }
        player.setCheckpoint(template.checkpointTokens);
        loadCommandCards(player, template.commandCards);
        loadProgrammingCards(player, template.programmingCards);
    }

    public static void loadCommandCards(Player player, List<CommandCardFieldTemplate> commandCardFieldTemplate) {
        for (int i = 0; i < commandCardFieldTemplate.size(); i++) {
            CommandCardField commandCardField = player.getCardField(i);
            commandCardField.setCard(commandCardFieldTemplate.get(i).card);
            commandCardField.setVisible(commandCardFieldTemplate.get(i).visible);
        }
    }

    public static void loadProgrammingCards(Player player, List<CommandCardFieldTemplate> commandCardFieldTemplates) {
        for (int i = 0; i < commandCardFieldTemplates.size(); i++) {
            CommandCardField commandCardField = player.getProgramField(i);
            commandCardField.setCard(commandCardFieldTemplates.get(i).card);
            commandCardField.setVisible(commandCardFieldTemplates.get(i).visible);
        }
    }

    public static void saveBoard(Board board, String name) {
        BoardTemplate template = new BoardTemplate();
        template.width = board.width;
        template.height = board.height;
        template.antennaX = board.getAntenna().x;
        template.antennaY = board.getAntenna().y;
        template.phase = board.getPhase();
        template.current = board.getPlayerNumber(board.getCurrentPlayer());
        template.counter = board.getCounter();
        template.rebootX = board.getRebootSpace().getX();
        template.rebootY = board.getRebootSpace().getY();

        for (int i = 0; i < board.width; i++) {
            for (int j = 0; j < board.height; j++) {
                Space space = board.getSpace(i, j);
                if (!space.getWalls().isEmpty() || !space.getActions().isEmpty()) {
                    SpaceTemplate spaceTemplate = new SpaceTemplate();
                    spaceTemplate.x = space.x;
                    spaceTemplate.y = space.y;
                    spaceTemplate.actions.addAll(space.getActions());
                    spaceTemplate.walls.addAll(space.getWalls());
                    template.spaces.add(spaceTemplate);
                }
            }
        }

        for (Player player : board.getPlayers()) {
            template.players.add(new PlayerTemplate(player));
        }

        ClassLoader classLoader = LoadBoard.class.getClassLoader();
        String filename = classLoader.getResource(BOARDSFOLDER).getPath() + "/games/" + name + "." + JSON_EXT;

        GsonBuilder simpleBuilder = new GsonBuilder().
                registerTypeAdapter(FieldAction.class, new Adapter<FieldAction>()).
                setPrettyPrinting();
        Gson gson = simpleBuilder.create();

        FileWriter fileWriter = null;
        JsonWriter writer = null;
        try {
            fileWriter = new FileWriter(filename);
            writer = gson.newJsonWriter(fileWriter);
            gson.toJson(template, template.getClass(), writer);
            writer.close();
        } catch (IOException e1) {
            if (writer != null) {
                System.out.println(e1.getMessage());
                try {
                    writer.close();
                    fileWriter = null;
                } catch (IOException e2) {
                }
            }
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException e2) {
                }
            }
        }

    }
}
