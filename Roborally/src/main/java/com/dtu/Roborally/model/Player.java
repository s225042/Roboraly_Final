package com.dtu.Roborally.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "player")
public class Player {

    @Id
    private int playerID;

    private String program1;
    private String program2;
    private String program3;
    private String program4;
    private String program5;



}
