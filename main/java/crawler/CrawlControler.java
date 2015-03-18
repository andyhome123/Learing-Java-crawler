package crawler;

import downloader.XMLDownloader;
import fetcher.PageFetcher;
import frontier.customBDBQueue.CustomBdbQueue;
import frontier.customBDBQueue.Frontier;
import frontier.customBDBQueue.filter.UrlFilter;
import parser.Parser;
import url.WebUrl;
import util.IO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by 俊毅 on 2015/3/14.
 */
public class CrawlControler {
    private ExecutorService executorService;
    private PageFetcher pageFetcher;
    private CrawlConfig config;
    private List<Crawler> CrawlerSet;
    private Monitor monitor;
    private Parser parser;
    private CustomBdbQueue customBdbQueue;
    private CustomBdbQueue downloadQueues;
    private Frontier frontier;
    private UrlFilter urlFilter;
    private long runTime;
    private long startTime = System.currentTimeMillis();

    public CrawlControler() {

    }

    public void init() {
        urlFilter = new UrlFilter();
        customBdbQueue = new CustomBdbQueue("E:/Test for Java","workqueue",WebUrl.class);
        downloadQueues = new CustomBdbQueue("E:/Test for Java","download",WebUrl.class);
        frontier = new Frontier(customBdbQueue,downloadQueues);
        startTime = System.currentTimeMillis();
        WebUrl seed = new WebUrl();
        seed.setDepth((short) 0);
        seed.setUrl("http://mslab.csie.asia.edu.tw/~jackjow/");
        config = new CrawlConfig();
        parser = new Parser();
        pageFetcher = new PageFetcher(config);
        frontier.schedule(seed);
    }



    public CrawlControler(int threads, boolean clean) throws IllegalAccessException, InstantiationException {
        if (clean == true) {
            clean();
        }
        executorService = Executors.newFixedThreadPool(threads+3);
        init();
        CrawlerSet = new ArrayList<Crawler>(threads);
        while ((threads--) > 0) {
            Class crawler = Crawler.class;
            Crawler crawlerInstance = (Crawler) crawler.newInstance();
            crawlerInstance.init(frontier, pageFetcher,parser,urlFilter);
            CrawlerSet.add(crawlerInstance);
            executorService.execute(crawlerInstance);
        }

        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        monitor = new Monitor();
        monitor.start();


    }

    public void shutDown() throws InterruptedException {
        executorService.shutdown();
        pageFetcher.shutDown();
        urlFilter.close();
        frontier.close();
        System.out.println("shutdown");
        while (!executorService.isShutdown()) {
            TimeUnit.SECONDS.sleep(1);
        }
        System.out.println("shutdown success");
    }



    class Monitor extends Thread {

        JFrame frame = new JFrame();
        public void s(){
            frame.dispose();
        }
        @Override
        public void run() {
            JTextArea area = new JTextArea();
            area.setLineWrap(true);
            frame.setLayout(new BorderLayout());
            frame.add(area, BorderLayout.NORTH);
            frame.setSize(600,600);
            frame.setVisible(true);
            JButton button = new JButton("STOP");
            frame.add(button, BorderLayout.SOUTH);
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        s();
                        System.out.println("SHUT");
                        shutDown();
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                }
            });
            while (!Thread.currentThread().isInterrupted()) {
                try {

                    TimeUnit.MILLISECONDS.sleep(600);
                    long runTime = (System.currentTimeMillis() - startTime) / 1000;
                    area.setText("");
                    area.append("-------Monitor-------\n");
                    area.append("TIME   "+runTime+"\n");
                    area.append("put"+frontier.getCount()+"\n");
                    for(int i=0;i<CrawlerSet.size();i++){
                        area.append("Thread " + i + " workingList" + "   " + CrawlerSet.get(i).getSingleCrawlerWorkingList()+"\n");
                    }
                    area.append("CurrentActiveConnections" + "   " + pageFetcher.getCurrentActiveConnection()+"\n");
                    area.append("PendingConnection    "+pageFetcher.getPendingConnection()+"\n");
                    area.append("IdlePersistentConnection    "+pageFetcher.getIdlePersistentConnection()+"\n");
                    area.append("parser get" + Crawler.getCount()+"\n");
                    area.append("filter get" + UrlFilter.getCount()+"\n");
                    area.append("frontier get " + Crawler.getCount2()+"\n");
                    area.append("take current urls" + frontier.getCount2()+"\n");
                    area.append("workqueue" + frontier.getWorkQueueLength()+ "\n");
                    area.append("download" + frontier.getDownloadLength() / runTime + "\n");
                    area.append("-------Monitor-------"+"\n");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }

        }
    }

    public void setRunTime(long runTime){
        this.runTime = runTime;
    }
    public long getRunTime(){
        return runTime;
    }
    public void clean() {
        IO.deleteAllFiles("E:/test");
        IO.deleteAllFiles("E:/Test for Java");
    }
    public void XMLSave() throws IOException {
        XMLDownloader downloader = new XMLDownloader();
        downloader.save();
    }
}



