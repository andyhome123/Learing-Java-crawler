package frontier.customBDBQueue;

import url.WebUrl;

/**
 * Created by 俊毅 on 2015/3/17.
 */
public class DownloadQueues extends CustomBdbQueue{
    public DownloadQueues(String envHome, Class<WebUrl> valueClass) {
        super(envHome, "DownloadQueues", valueClass);
    }
}
