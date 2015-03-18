import com.sleepycat.bind.serial.SerialBinding;
import com.sleepycat.je.Cursor;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.OperationStatus;
import frontier.customBDBQueue.CustomBdbQueue;
import url.WebUrl;

/**
 * Created by 俊毅 on 2015/3/17.
 */
public class test {
    public static void main(String[] args){
        CustomBdbQueue customBdbQueue = new CustomBdbQueue("E:/test","123", WebUrl.class);
//        WebUrl webUrl = new WebUrl();
//        webUrl.setUrl("123");
//        customBdbQueue.offer(webUrl);
        Database db = customBdbQueue.getQueueDb();
        Cursor cursor = db.openCursor(null,null);
        DatabaseEntry key = new DatabaseEntry();
        DatabaseEntry value = new DatabaseEntry();
        OperationStatus status = cursor.getFirst(key,value,null);
        if(status == OperationStatus.SUCCESS){
            System.out.print("su");
        }
        else{
            System.out.println("dd");
        }
        SerialBinding<WebUrl> serialBinding = customBdbQueue.getSerialBinding();
        WebUrl e = serialBinding.entryToObject(value);
        System.out.println(e.getUrl());
        cursor.close();
        customBdbQueue.close();
        customBdbQueue.closeEnv();
    }
}
