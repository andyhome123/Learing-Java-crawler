package crawler;

import fetcher.PageFetcher;
import fetcher.PageResult;
import frontier.customBDBQueue.Frontier;
import frontier.customBDBQueue.filter.UrlFilter;
import parser.Parser;
import url.WebUrl;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by 俊毅 on 2015/3/14.
 */
public class Crawler implements Runnable {
    private Frontier frontier;
    private Parser parser;
    private PageFetcher pageFetcher;
    private UrlFilter urlFilter;
    private List<WebUrl> webUrlList;
    private static AtomicLong count = new AtomicLong(0);
    private static AtomicLong count2 = new AtomicLong(0);
    public void init(Frontier frontier, PageFetcher pageFetcher,Parser parser,UrlFilter urlFilter) {
        this.frontier = frontier;
        this.pageFetcher = pageFetcher;
        this.urlFilter = urlFilter;
        this.parser = parser;
    }
    public static long getCount(){
        return count.get();
    }
    public static long getCount2(){
        return count2.get();
    }
    public int getSingleCrawlerWorkingList() {
        return webUrlList.size();
    }
    @Override
    public void run() {
        while (!(Thread.currentThread().isInterrupted())) {
            webUrlList = frontier.getNextUrls((frontier.getWorkQueueLength() > 20) ? 20 : frontier.getWorkQueueLength());
            if (webUrlList == null) {
                try {
                    TimeUnit.MILLISECONDS.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            for (WebUrl webUrl : webUrlList) {
                if (webUrl == null) {
                    continue;
                }
                PageResult pageResult = pageFetcher.fetchPage(webUrl);
                try {
                    if (pageResult == null) {
                        continue;
                    }
                    Set<WebUrl> fetchedUrlList = parser.parse(pageResult);
                    if (fetchedUrlList == null) {
                        continue;
                    }

                    Set<WebUrl> filterUrlList = new HashSet<WebUrl>();
                    for(WebUrl e : fetchedUrlList){
                        if(urlFilter.setAdd(e.getUrl())){
                            filterUrlList.add(e);
                        }
                    }
                    count.addAndGet(fetchedUrlList.size());
                    count2.addAndGet(filterUrlList.size());
                    frontier.scheduleAll(filterUrlList);
                }  catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }

}

