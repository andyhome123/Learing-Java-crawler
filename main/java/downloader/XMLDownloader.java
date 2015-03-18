package downloader;

import com.sleepycat.bind.serial.SerialBinding;
import com.sleepycat.je.*;
import frontier.customBDBQueue.CustomBdbQueue;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import url.WebUrl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
* Created by 俊毅 on 2015/3/14.
*/
public class XMLDownloader {
    private CustomBdbQueue downloadQueues;
    private Database db;
    private SerialBinding<WebUrl> serialBinding;
    public XMLDownloader() {
        downloadQueues = new CustomBdbQueue("E:/Test for Java", "download", WebUrl.class);
        db = downloadQueues.getQueueDb();
        serialBinding = downloadQueues.getSerialBinding();
    }

    public void save() throws IOException {
        Transaction txn;
        OutputFormat format = OutputFormat.createPrettyPrint();
        File file = new File("E:/Test for Java/test.xml");
        file.createNewFile();
        FileWriter fw = new FileWriter(file);
        org.dom4j.Document doc = DocumentHelper.createDocument();
        DatabaseEntry key = new DatabaseEntry();
        DatabaseEntry value = new DatabaseEntry();
        Cursor cursor = db.openCursor(null, null);
        org.dom4j.Element rootElement = doc.addElement("tweet");
        OperationStatus result = cursor.getFirst(key, value, null);
        while (result == OperationStatus.SUCCESS) {
            WebUrl url = serialBinding.entryToObject(value);
            String s = url.getUrl();
            String ps = url.getParentUrl();
            String t = url.getText();
//            long ID = url.getUID();
//            long parentID = url.getParentUID();
            short depth = url.getDepth();
            short ParentDepth = url.getParentDepth();
            org.dom4j.Element URLElement = rootElement.addElement("URL");
            org.dom4j.Element PElement = URLElement.addElement("Parameters");
            URLElement.addAttribute("url", s);
            URLElement.addAttribute("Parenturl", ps);
//            PElement.addAttribute("ID", Long.toString(ID));
//            PElement.addAttribute("PID", Long.toString(parentID));
            PElement.addAttribute("Depth", Short.toString(depth));
            PElement.addAttribute("PDepth", Short.toString(ParentDepth));
            result = cursor.getNext(key, value, null);
        }
        XMLWriter xw = new XMLWriter(fw, format);
        xw.write(doc);
        if (cursor != null) {
            cursor.close();
        }
        if (xw != null) {
            xw.close();
        }
        downloadQueues.close();
    }

}
