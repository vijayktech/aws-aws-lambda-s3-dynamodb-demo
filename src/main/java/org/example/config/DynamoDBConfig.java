package org.example.config;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

public class DynamoDBConfig {
    private static DynamoDBMapper mapper;
    private static DynamoDBConfig dbInstance;

    private DynamoDBConfig(){
        AmazonDynamoDBClient dynamoDBClient = new AmazonDynamoDBClient();
        dynamoDBClient.setRegion(Region.getRegion(Regions.AP_SOUTH_1));
        mapper = new DynamoDBMapper(dynamoDBClient);
    }

    public static DynamoDBConfig getDBInstance(){
       if (dbInstance == null)
           synchronized (DynamoDBConfig.class){
               dbInstance = new DynamoDBConfig();
           }
        return dbInstance;
    }

    public static DynamoDBMapper mapper() {
        DynamoDBConfig dbConnection = getDBInstance();
        return dbConnection.mapper;
    }
}
