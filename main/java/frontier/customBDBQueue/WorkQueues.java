package frontier.customBDBQueue;

import url.WebUrl;

/**
 * Created by 俊毅 on 2015/3/17.
 */
public class WorkQueues extends CustomBdbQueue{
    public WorkQueues(String envHome, Class<WebUrl> valueClass) {
        super(envHome, "WorkQueues", valueClass);
    }
}
