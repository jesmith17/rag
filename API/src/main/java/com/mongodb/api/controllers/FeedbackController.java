package com.mongodb.api.controllers;

import com.mongodb.api.models.Feedback;
import com.mongodb.api.repository.FeedbackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@CrossOrigin(origins = {"http://localhost:4200", "**"}, maxAge = 3600)
@RequestMapping(value="feedback", consumes = "*/*", produces = MediaType.APPLICATION_JSON_VALUE)
public class FeedbackController {

    @Autowired
    FeedbackRepository feedbackRepository;

    @PostMapping
    public ResponseEntity<Feedback> createFeedback(@RequestBody Feedback feedback) {
        try {
            Feedback _Feedback = feedbackRepository.save(feedback);
            return new ResponseEntity<>(_Feedback, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Feedback> getFeedbackById(@PathVariable("id") String id) {
        Optional<Feedback> feedback = feedbackRepository.findById(id);

        if (feedback.isPresent()) {
            return new ResponseEntity<>(feedback.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/feedback/{id}")
    public ResponseEntity<HttpStatus> deleteFeedback(@PathVariable("id") String id) {
        try {
            feedbackRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
