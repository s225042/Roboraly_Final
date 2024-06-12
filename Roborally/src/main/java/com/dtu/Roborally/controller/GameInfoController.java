package com.dtu.Roborally.controller;

import com.dtu.Roborally.model.GameInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController

@RequestMapping("/gameInfos")
public class GameInfoController {

    private GameInfo gameInfo;


}
