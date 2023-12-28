package org.example.dao;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import org.example.config.DynamoDBConfig;
import org.example.domain.FileStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class DynamoDBFileStoreDao {

    private static final Logger logger = LoggerFactory.getLogger(DynamoDBFileStoreDao.class);
    private static final DynamoDBMapper mapper = DynamoDBConfig.mapper();
    private static DynamoDBFileStoreDao daoInstance;

    private DynamoDBFileStoreDao(){}

    public static DynamoDBFileStoreDao getInstance(){
        if(daoInstance == null){
            synchronized (DynamoDBFileStoreDao.class){
                if( daoInstance == null)
                    daoInstance = new DynamoDBFileStoreDao();
            }
        }
        return  daoInstance;
    }

    public void saveFileInfo(FileStore fileStore){
        mapper.save(fileStore);
    }

    public List<FileStore> getAllFilesInfo(){
        return mapper.scan(FileStore.class, new DynamoDBScanExpression());
    }
}
