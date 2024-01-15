package com.mongodb.api.repository;

import com.mongodb.api.models.Chunk;
import com.mongodb.api.models.HundredChunk;
import com.mongodb.api.models.ThousandChunk;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HundredEmbeddingsRepository extends MongoRepository<HundredChunk, String> {


    @Aggregation(pipeline={"{'$vectorSearch': {queryVector: ?0, path: 'embeddings', numCandidates: 200, index: 'vector_index',limit: 10}}"})
    public List<Chunk> vectorSearch(double[] embeddings);

    

}
