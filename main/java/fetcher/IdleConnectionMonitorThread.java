package fetcher;

import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import java.util.concurrent.TimeUnit;

/**
 * Created by 俊毅 on 2015/3/13.
 */
public class IdleConnectionMonitorThread extends Thread {

    private final PoolingHttpClientConnectionManager connectionManager;
    private volatile boolean shutdown;

    public IdleConnectionMonitorThread(PoolingHttpClientConnectionManager connectionManager) {
        super();
        this.connectionManager = connectionManager;
    }

    @Override
    public void run() {
        try {
            while (!shutdown) {
                synchronized (this) {
                    wait(5000);
                    // Close expired connections
                    connectionManager.closeExpiredConnections();
                    // Optionally, close connections
                    // that have been idle longer than 30 sec
                    connectionManager.closeIdleConnections(30, TimeUnit.SECONDS);
                }
            }
        } catch (InterruptedException ex) {
            // terminate
        }
    }

    public void shutdown() {
        shutdown = true;
        synchronized (this) {
            notifyAll();
        }
    }

}
