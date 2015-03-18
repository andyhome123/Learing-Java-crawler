package frontier.customBDBQueue;

import url.WebUrl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by 俊毅 on 2015/3/17.
 */
public class Frontier {
    private static AtomicLong count = new AtomicLong(0);
    private static AtomicLong count2 = new AtomicLong(0);
    private CustomBdbQueue workQueue;
    private CustomBdbQueue download;

    public Frontier(CustomBdbQueue workQueue, CustomBdbQueue download) {
        this.workQueue = workQueue;
        this.download = download;
    }
    public void schedule(WebUrl webUrl){
        count.incrementAndGet();
        workQueue.add(webUrl);
    }
    public void scheduleAll(Set<WebUrl> webUrlList){
        for(WebUrl webUrl: webUrlList){
            count.incrementAndGet();
            workQueue.offer(webUrl);
            download.offer((webUrl));
        }
    }
    public long getCount(){
        return count.get();
    }

    public long getCount2() {
        return count2.get();
    }
    public int getWorkQueueLength(){
        return workQueue.getLength();
    }
    public int getDownloadLength(){
        return download.getLength();
    }
    public List<WebUrl> getNextUrls(int max) {
        List<WebUrl> webUrlList = new ArrayList<WebUrl>();
        while(max-- > 0 ){
            webUrlList.add(workQueue.poll());
        }
        count2.addAndGet(webUrlList.size());
        return webUrlList;
    }
    public void close(){
        workQueue.close();
        download.close();
        workQueue.closeEnv();
    }
}
