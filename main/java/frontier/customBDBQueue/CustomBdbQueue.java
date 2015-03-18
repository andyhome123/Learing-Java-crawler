package frontier.customBDBQueue;

import com.sleepycat.bind.EntryBinding;
import com.sleepycat.bind.serial.SerialBinding;
import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.collections.StoredMap;
import com.sleepycat.je.*;
import url.WebUrl;

import java.io.File;
import java.io.Serializable;
import java.util.AbstractQueue;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by 俊毅 on 2015/3/17.
 */
public class CustomBdbQueue extends AbstractQueue<WebUrl> implements Serializable{
    private static final long serialVersionUID = 90182301982374L;
    private transient CustomEnvironment env;
    private transient Database queueDb;
    private transient StoredMap<Long,WebUrl> queueMap;
    private AtomicLong headIndex;
    private AtomicLong tailIndex;
    private transient WebUrl processItem = null;
    private transient SerialBinding<WebUrl> serialBinding;
    public CustomBdbQueue(String envHome, String dbName, Class<WebUrl> valueClass) {
        headIndex = new AtomicLong(0);
        tailIndex = new AtomicLong(0);
        init(envHome,dbName,valueClass);
    }

    public Database getQueueDb(){
        return queueDb;
    }
    public SerialBinding<WebUrl> getSerialBinding(){
        return serialBinding;
    }
    public void init(String envHome,String dbName, Class<WebUrl> valueClass){
        try{
            File envFile = new File(envHome);
            EnvironmentConfig environmentConfig = new EnvironmentConfig();
            environmentConfig.setAllowCreate(true);
            environmentConfig.setTransactional(false);
            DatabaseConfig databaseConfig = new DatabaseConfig();
            databaseConfig.setAllowCreate(true);
            databaseConfig.setTransactional(false);
            databaseConfig.setDeferredWrite(true);
            env = new CustomEnvironment(envFile,environmentConfig);
            queueDb = env.openDatabase(null,dbName,databaseConfig);
            EntryBinding<WebUrl> valueBinding = TupleBinding.getPrimitiveBinding(valueClass); // return null cause not supported
            if(valueBinding == null){
                valueBinding = new SerialBinding<WebUrl>(env.getStoredClassCatalog(), valueClass);
                serialBinding = new SerialBinding<WebUrl>(env.getStoredClassCatalog(), valueClass);
            }
            queueMap = new StoredMap<Long, WebUrl>(queueDb,TupleBinding.getPrimitiveBinding(Long.class),valueBinding,true);
        } catch (DatabaseExistsException e) {
            e.printStackTrace();
        } catch (DatabaseException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }
    @Override
    public Iterator<WebUrl> iterator() {
        return queueMap.values().iterator();
    }

    @Override
    public int size() {
        synchronized (tailIndex){
            synchronized (headIndex){
                return (int) (tailIndex.get()-headIndex.get());
            }
        }
    }

    @Override
    public boolean offer(WebUrl webUrl) {
        synchronized (tailIndex){
            queueMap.put(tailIndex.getAndIncrement(),webUrl);
        }
        return true;
    }

    @Override
    public WebUrl peek() {
        synchronized (headIndex){
            if(processItem != null){
                return processItem;
            }
            WebUrl headItem = null;
            while(headItem == null && headIndex.get() < tailIndex.get()){
                headItem = queueMap.get(headIndex.get());
                if(headItem != null){
                    processItem = headItem;
                    continue;
                }
                headIndex.incrementAndGet(); // cause headItem == null
            }
            return headItem;
        }
    }

    @Override
    public WebUrl poll() {
        synchronized (headIndex){
            WebUrl headItem = peek();
            if(headItem != null){
                queueMap.remove(headIndex.getAndIncrement());
                processItem = null;
                return headItem;
            }
        }
        return null;
    }
    public void close(){
        try{
            if(queueDb != null){
                queueDb.sync();
                queueDb.close();
            }
        }
        catch(DatabaseException e){
            e.printStackTrace();
        }
        catch (UnsupportedOperationException e){
            e.printStackTrace();
        }
    }
    public void closeEnv(){
        env.sync();
        env.close();
    }
    public DatabaseEntry check(DatabaseEntry key){
        DatabaseEntry value = new DatabaseEntry();
        OperationStatus status = queueDb.get(null,key,value,null);
        if(status == OperationStatus.SUCCESS){
            return key;
        }
        return null;
    }
    public int getLength(){
        return (int) queueDb.count();
    }
}
