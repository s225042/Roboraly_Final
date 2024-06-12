package com.dtu.Roborally.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "gameInfo")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GameInfo {
    @Id
    private int gameID;

    private int turnID;

    private String board;


    // @OneToMany
    // private List<Player> players;

}
