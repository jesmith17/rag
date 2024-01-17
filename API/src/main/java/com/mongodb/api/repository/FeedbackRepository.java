package com.mongodb.api.repository;

import com.mongodb.api.models.Feedback;
import com.mongodb.api.models.HundredChunk;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedbackRepository extends MongoRepository<Feedback, String> {
}
