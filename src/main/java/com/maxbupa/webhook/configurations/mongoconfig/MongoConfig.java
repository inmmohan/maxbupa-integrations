package com.maxbupa.webhook.configurations.mongoconfig;


import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;

@Configuration
public class MongoConfig {

    @Value("${mongoClientURI}")
    private String mongoClientURI;

    @Value("${simpleMongoDbFactory}")
    private String simpleMongoDbFactory;


    @Bean
    public MongoTemplate mongoTemplate(MongoDbFactory mongoDbFactory)
    {
        return new MongoTemplate(mongoDbFactory);
    }


    @Bean
    public SimpleMongoDbFactory mongoDbFactory()
    {
        MongoClientURI uri = new MongoClientURI(mongoClientURI);
        MongoClient mongoClient = new MongoClient(uri);
        return new SimpleMongoDbFactory(mongoClient,simpleMongoDbFactory);

    }
}