package crawler;

/**
 * Created by 俊毅 on 2015/3/13.
 */
public class CrawlConfig {
    private String envPath = "E:/Test for Java";
    private String userAgent  = "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2272.89 Safari/537.36Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2272.89 Safari/537.36";
    private int socketTimeout = 20000;
    private int connectTimeout = 30000;
    private int connectionRequestTimeout = 20000;
    private boolean redirectsEnabled = false;
    private boolean exceptionContinue = false;
    private int connMaxTotal = 200;
    private int connMaxPerRoute = 20;
    private int politeDelay = 200;
    public int getPoliteDelay(){
        return politeDelay;
    }
    public void setPoliteDelay(int politeDelay){
        this.politeDelay = politeDelay;
    }
    public int getConnMaxTotal() {
        return connMaxTotal;
    }

    public int getConnMaxPerRoute() {
        return connMaxPerRoute;
    }

    public void setConnMaxTotal(short connMaxTotal) {
        this.connMaxTotal = connMaxTotal;
    }
    public void setConnMaxPerRoute(short connMaxPerRoute){
        this.connMaxPerRoute = connMaxPerRoute;
    }
    public boolean getExceptionContinue() {
        return exceptionContinue;
    }
    public void setExceptionContinue(boolean exceptionContinue){
        this.exceptionContinue = exceptionContinue;
    }
    public boolean getRedirectsEnabled(){
        return redirectsEnabled;
    }
    public void setRedirectsEnabled(boolean redirectsEnabled){
        this.redirectsEnabled = redirectsEnabled;
    }
    public int getSocketTimeout() {
        return socketTimeout;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }
    public int getConnectionRequestTimeout() {
        return connectionRequestTimeout;
    }

    public void setSocketTimeout(short socketTimeout) {
        this.socketTimeout = socketTimeout;
    }

    public void setConnectTimeout(short connectTimeout) {
        this.socketTimeout = socketTimeout;
    }

    public void setConnectionRequestTimeout(short connectionRequestTimeout) {
        this.connectionRequestTimeout = connectionRequestTimeout;
    }
    public String getUserAgent(){
        return userAgent;
    }
    public void setUserAgent(String userAgent){
        this.userAgent = userAgent;
    }
    public String getEnvPath(){
        return envPath;
    }
    public void setEnvPath(String envPath){
        this.envPath = envPath;
    }
}
