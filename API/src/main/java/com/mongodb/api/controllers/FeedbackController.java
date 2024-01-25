package com.mongodb.api.controllers;

import com.mongodb.api.models.Feedback;
import com.mongodb.api.repository.FeedbackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.print.attribute.standard.Media;
import java.util.Optional;

@RestController
@CrossOrigin(origins = {"http://localhost:3000"}, maxAge = 3600)
@RequestMapping(value="feedback", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public class FeedbackController {

    @Autowired
    FeedbackRepository feedbackRepository;

    @PostMapping("/{id}")
    public Feedback updateFeedback(@RequestBody Feedback feedback,@PathVariable("id") String id ) {
        Feedback savedFeedback = feedbackRepository.findById(id).get();
        savedFeedback.setThumbsUp(feedback.isThumbsUp());
        savedFeedback.setJustification(feedback.getJustification());
        return feedbackRepository.save(savedFeedback);
    }

    @GetMapping("/{id}")
    public Optional<Feedback> getFeedbackById(@PathVariable("id") String id) {
        return feedbackRepository.findById(id);


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
