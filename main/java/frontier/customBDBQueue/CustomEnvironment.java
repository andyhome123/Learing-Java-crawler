package frontier.customBDBQueue;

import com.sleepycat.bind.serial.StoredClassCatalog;
import com.sleepycat.je.*;

import java.io.File;

/**
 * Created by 俊毅 on 2015/3/17.
 */
public class CustomEnvironment extends Environment{
    StoredClassCatalog storedClassCatalog;
    Database classCatalogDB;
    public CustomEnvironment(File envHome, EnvironmentConfig environmentConfig){
        super(envHome,environmentConfig);
    }
    public StoredClassCatalog getStoredClassCatalog(){
        if(storedClassCatalog == null){
            DatabaseConfig dbConfig = new DatabaseConfig();
            dbConfig.setAllowCreate(true);
            try{
                classCatalogDB = openDatabase(null, "classCatalogDB", dbConfig);
                storedClassCatalog = new StoredClassCatalog(classCatalogDB);
            }
            catch(DatabaseException e){
                System.out.println("Create cutstomEnvironment fail");
            }
        }
        return storedClassCatalog;
    }

    @Override
    public synchronized void close(){
       try{
           if(classCatalogDB != null){
               classCatalogDB.close();
           }
           super.close();
       }
       catch(DatabaseException e){
           System.out.println("close CustomEnvironment fail");
       }
    }
}
