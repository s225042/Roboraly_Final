/*
 *  This file is part of the initial project provided for the
 *  course "Project in Software Development (02362)" held at
 *  DTU Compute at the Technical University of Denmark.
 *
 *  Copyright (C) 2019, 2020: Ekkart Kindler, ekki@dtu.dk
 *
 *  This software is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; version 2 of the License.
 *
 *  This project is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this project; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package dk.dtu.compute.se.pisd.roborally.fileaccess;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import dk.dtu.compute.se.pisd.roborally.fileaccess.model.BoardTemplate;
import dk.dtu.compute.se.pisd.roborally.fileaccess.model.CommandCardFieldTemplate;
import dk.dtu.compute.se.pisd.roborally.fileaccess.model.PlayerTemplate;
import dk.dtu.compute.se.pisd.roborally.fileaccess.model.SpaceTemplate;
import dk.dtu.compute.se.pisd.roborally.controller.FieldAction;
import dk.dtu.compute.se.pisd.roborally.model.*;

import java.io.*;
import java.util.List;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 */
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

		// In simple cases, we can create a Gson object with new Gson():
        GsonBuilder simpleBuilder = new GsonBuilder().
                registerTypeAdapter(FieldAction.class, new Adapter<FieldAction>());
        Gson gson = simpleBuilder.create();

		Board result;
		// FileReader fileReader = null;
        JsonReader reader = null;
		try {
			// fileReader = new FileReader(filename);
			reader = gson.newJsonReader(new InputStreamReader(inputStream));
			BoardTemplate template = gson.fromJson(reader, BoardTemplate.class);

			result = new Board(template.width, template.height);
			for (SpaceTemplate spaceTemplate: template.spaces) {
			    Space space = result.getSpace(spaceTemplate.x, spaceTemplate.y);
			    if (space != null) {
                    space.getActions().addAll(spaceTemplate.actions);
                    space.getWalls().addAll(spaceTemplate.walls);
                }
            }
            result.setPhase(template.phase);
            for (int i = 0; i<template.players.size(); i++) {
                loadPlayer(result, template.players.get(i));
            }
            result.setCurrentPlayer(result.getPlayer(template.current));
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
    /**
    @author Rebecca Moss, s225042@dtu.dk
    @param bordResult (type Board)
    @param template (type PlayerTemplate)
    */
    public static void loadPlayer(Board bordResult, PlayerTemplate template){
        Player player = new Player(bordResult, template.color, template.name);
        int x = template.x;
        int y = template.y;
        if(x >= 0 && y >= 0 && x<bordResult.width && y<bordResult.height){
            Space space = bordResult.getSpace(x,y);
            if (space != null){
                player.setSpace(space);
                bordResult.addPlayer(player);
            }
        }

        loadCommandCards(player, template.commandCards);
        loadProgrammingCards(player, template.programmingCards);
    }

    /**
     @author Rebecca Moss, s225042@dtu.dk
     @param player (type Player)
     @param commandCardFieldTemplate (type List<CommandCardFieldTemplate>)
     */
    public static  void  loadCommandCards(Player player, List<CommandCardFieldTemplate> commandCardFieldTemplate){
        for (int i = 0; i < commandCardFieldTemplate.size(); i++) {

            CommandCardField commandCardField = player.getCardField(i);
            commandCardField.setCard(commandCardFieldTemplate.get(i).card);
            commandCardField.setVisible(commandCardFieldTemplate.get(i).visible);

            player.getCardField(i);
        }
    }

    /**
     @author Rebecca Moss, s225042@dtu.dk
     @param player (type Player)
     @param commandCardFieldTemplates (type List<CommandCardFieldTemplate>)
     */
    public static void loadProgrammingCards(Player player, List<CommandCardFieldTemplate> commandCardFieldTemplates){
        for (int i = 0; i < commandCardFieldTemplates.size(); i++) {
            CommandCardField commandCardField = player.getProgramField(i);
            commandCardField.setCard(commandCardFieldTemplates.get(i).card);
            commandCardField.setVisible(commandCardFieldTemplates.get(i).visible);

        }
    }



    /**
     @author Rebecca Moss, s225042@dtu.dk
     @param board (type Board)
     @param name (type name)
     */
    public static void saveBoard(Board board, String name) {
        BoardTemplate template = new BoardTemplate();
        template.width = board.width;
        template.height = board.height;
        template.phase = board.getPhase();
        template.current = board.getPlayerNumber(board.getCurrentPlayer());

        for (int i=0; i<board.width; i++) {
            for (int j=0; j<board.height; j++) {
                Space space = board.getSpace(i,j);
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
        // TODO: this is not very defensive, and will result in a NullPointerException
        //       when the folder "resources" does not exist! But, it does not need
        //       the file "simpleCards.json" to exist!
        String filename = classLoader.getResource(BOARDSFOLDER).getPath() + "/games/" + name + "." + JSON_EXT;

        // In simple cases, we can create a Gson object with new:
        //
        //   Gson gson = new Gson();
        //
        // But, if you need to configure it, it is better to create it from
        // a builder (here, we want to configure the JSON serialisation with
        // a pretty printer):
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
                } catch (IOException e2) {}
            }
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException e2) {}
            }
        }
    }

}
