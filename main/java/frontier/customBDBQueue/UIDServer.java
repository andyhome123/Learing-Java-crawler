package frontier.customBDBQueue;//package frontier.customBDBQueue;
//
//import com.sleepycat.je.*;
//import url.WebUrl;
//import util.Util;
//
//import java.io.File;
//import java.security.NoSuchAlgorithmException;
//import java.util.HashSet;
//import java.util.Set;
//import java.util.concurrent.atomic.AtomicLong;
//import java.util.concurrent.locks.ReadWriteLock;
//import java.util.concurrent.locks.ReentrantReadWriteLock;
//
///**
// * Created by 俊毅 on 2015/3/17.
// */
//public class UIDServer {
//    private AtomicLong lastUID = new AtomicLong(0);
//    private ReadWriteLock lock = new ReentrantReadWriteLock();
//    private static AtomicLong parseCount = new AtomicLong(0);
//    private static AtomicLong beforeCount = new AtomicLong(0);
//    private static AtomicLong afterCount = new AtomicLong(0);
//    private static AtomicLong count = new AtomicLong(0);
//    private SerialBinding<WebUrl> serialBinding;
//    private Environment env;
//    private Database db;
//    public UIDServer(String envHome, Class<WebUrl> valueClass) {
//        super(envHome, "UIDServer", valueClass);
//        serialBinding = getSerialBinding();
//    }
//    public UIDServer(String envHome, String dbName){
//        File file = new File(envHome);
//        EnvironmentConfig environmentConfig = new EnvironmentConfig();
//        environmentConfig.setAllowCreate(true);
//        env = new Environment(file,environmentConfig);
//        DatabaseConfig dbConfig = new DatabaseConfig();
//        dbConfig.setAllowCreate(true);
//        dbConfig.setDeferredWrite(true);
//        db = env.openDatabase(null,dbName,dbConfig);
//    }
//    public static long getCount() {
//        return count.get();
//    }
//
//    public static long getParseCount() {
//        return parseCount.get();
//    }
//
//    public static long getBeforeCount() {
//        return beforeCount.get();
//    }
//
//    public static long getAfterCount() {
//        return afterCount.get();
//    }
//
//    public Set<WebUrl> handleRepeatUrl(Set<WebUrl> fetchedUrlList) throws InterruptedException, NoSuchAlgorithmException {
//        Set<WebUrl> filterUrlList = new HashSet<>();
//        lock.readLock().lock();
//        try {
//            for (WebUrl webUrl : fetchedUrlList) {
//
//                DatabaseEntry key = new DatabaseEntry(webUrl.getUrl().getBytes());
//                DatabaseEntry value = new DatabaseEntry();
//                OperationStatus status = db.get(null,key,value,null);
//                if((status == OperationStatus.SUCCESS) && (value.getData().length > 0)){
//
//                }
//                else{
//                    filterUrlList.add(webUrl);
//                }
//            }
//            parseCount.addAndGet(fetchedUrlList.size());
//            beforeCount.addAndGet(filterUrlList.size());
//            lock.readLock().unlock();
//            lock.writeLock().lock();
//            filterUrlList = getUID(filterUrlList);
//            afterCount.addAndGet(filterUrlList.size());
//            return filterUrlList;
//        } finally {
//            lock.writeLock().unlock();
//        }
//    }
//
//    public Set<WebUrl> getUID(Set<WebUrl> filterUrlList) throws InterruptedException, NoSuchAlgorithmException {
//        for (WebUrl webUrl : filterUrlList) {
//
//            long last = lastUID.getAndIncrement();
//            webUrl.setUID(last);
//            db.put(null, new DatabaseEntry(webUrl.getUrl().getBytes()), new DatabaseEntry(Util.long2ByteArray(webUrl.getUID())));
//        }
//        return filterUrlList;
//    }
//    public void close(){
//        db.close();
//        env.close();
//    }
//}
//