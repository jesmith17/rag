package com.mongodb.api.controllers;

import com.mongodb.api.models.ChatRequest;
import com.mongodb.api.models.ChatResponse;
import com.mongodb.api.services.StabilityService;
import com.mongodb.api.services.TitanService;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin(origins = {"http://localhost:4200", "**"}, maxAge = 3600)
@RequestMapping(value="text", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public class TextController {


    @Autowired
    private TitanService service;


    @PostMapping
    private ChatResponse getText(@RequestBody ChatRequest request){


        return service.generateResponse(request);
    }

}
