package fetcher;

import crawler.CrawlConfig;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.ConnectionPoolTimeoutException;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import url.WebUrl;

import javax.net.ssl.SSLException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.IllegalFormatCodePointException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by 俊毅 on 2015/3/13.  learning from crawler4j "https://github.com/yasserg/crawler4j"
 */
public class PageFetcher {
    private CloseableHttpClient httpClient = null;
    private PoolingHttpClientConnectionManager connectionManager = null;
    private IdleConnectionMonitorThread connectionMonitor = null;
    private CrawlConfig config;
    private static BlockingQueue<String> queue = new LinkedBlockingDeque<String>();
    private String toFetchUrl;
    public PageFetcher(CrawlConfig config) {
        this.config = config;
        RequestConfig requsetConfig = RequestConfig.custom().setSocketTimeout(config.getSocketTimeout()).setConnectTimeout(config.getConnectTimeout())
                .setRedirectsEnabled(config.getRedirectsEnabled()).setExpectContinueEnabled(config.getExceptionContinue()).setCookieSpec(CookieSpecs.DEFAULT).build();
        RegistryBuilder<ConnectionSocketFactory> connRegistryBuilder = RegistryBuilder.create();
        connRegistryBuilder.register("http", PlainConnectionSocketFactory.INSTANCE);
        Registry<ConnectionSocketFactory> connRegistry = connRegistryBuilder.build();
        HttpRequestRetryHandler requestRetryHandler = new HttpRequestRetryHandler() {
            @Override
            public boolean retryRequest(
                    IOException exception,
                    int executionCount,
                    HttpContext context) {
                if (executionCount > 1) {
                    // Do not retry if over max retry count
                    queue.add(toFetchUrl);
                    return false;
                }
                if (exception instanceof InterruptedIOException) {
                    // Timeout
                    return false;
                }
                if (exception instanceof UnknownHostException) {
                    // Unknown host
                    return false;
                }
                if (exception instanceof ConnectTimeoutException) {
                    // Connection refused
                    return false;
                }
                if (exception instanceof SSLException) {
                    // SSL handshake exception
                    return false;
                }
                HttpClientContext clientContext = HttpClientContext.adapt(context);
                HttpRequest request = clientContext.getRequest();
                boolean idempotent = !(request instanceof HttpEntityEnclosingRequest);
                if (idempotent) {
                    // Retry if the request is considered idempotent
                    return true;
                }
                return false;
            }
        };
        connectionManager = new PoolingHttpClientConnectionManager(connRegistry);
        connectionManager.setMaxTotal(config.getConnMaxTotal());
        connectionManager.setDefaultMaxPerRoute(config.getConnMaxPerRoute());
        HttpClientBuilder clientBuilder = HttpClientBuilder.create();
        clientBuilder.setDefaultRequestConfig(requsetConfig);
        clientBuilder.setConnectionManager(connectionManager);
        clientBuilder.setUserAgent(config.getUserAgent());
        clientBuilder.setRetryHandler(requestRetryHandler);
        httpClient = clientBuilder.build();
        if (connectionMonitor == null) {
            connectionMonitor = new IdleConnectionMonitorThread(connectionManager);
        }
        connectionMonitor.start();
    }

    public PageResult fetchPage(WebUrl webUrl){
        HttpGet httpGet = null;
        PageResult fetchResult = new PageResult();
        try {
            toFetchUrl = webUrl.getUrl();
            if (queue.contains(toFetchUrl)) {
                return null;
            }
            httpGet = new HttpGet(toFetchUrl);
            CloseableHttpResponse response = httpClient.execute(httpGet);
            fetchResult.setContentData(EntityUtils.toByteArray(response.getEntity()));
//            fetchResult.setEntity(response.getEntity());
            fetchResult.setResponseHeaders(response.getAllHeaders());
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                fetchResult.setStatusCode(HttpStatus.SC_OK);
                fetchResult.setOriginDepth(webUrl.getDepth());
                fetchResult.setOriginUrl(webUrl.getUrl());
                return fetchResult;
            } else {
                return null;
            }

        } catch (ClientProtocolException e) {
            System.out.println("protocol wrong");
            return null;
        } catch (ConnectionPoolTimeoutException e) {
            System.out.println(httpGet + "  Timeout");
            return null;
        } catch (org.apache.http.conn.UnsupportedSchemeException e) {
            System.out.println(httpGet + "unsupport 443");
        } catch (IllegalFormatCodePointException e) {
            System.out.println(httpGet + "  wrong");
            return null;
        } catch (UnknownHostException e) {
            System.out.println(httpGet + "  unKnown");
            return null;
        } catch (org.apache.http.NoHttpResponseException e) {
            queue.add(toFetchUrl);
            System.out.println(httpGet + "  noResponse");
            return null;
        }catch(SocketTimeoutException e){
            System.out.println(httpGet + "  sockettimeout");
            return null;
        }catch(IllegalArgumentException e){
            queue.add(toFetchUrl);
            System.out.println("illegal");
            return null;
        }
        catch (IOException e) {
            e.printStackTrace();
        } finally {
            if ((fetchResult.getEntity() == null) && (httpGet != null)) {
                httpGet.abort();
            }
        }

        return null;
    }

    public int getCurrentActiveConnection() {
        return connectionManager.getTotalStats().getLeased();
    }
    public int getIdlePersistentConnection() {
        return connectionManager.getTotalStats().getAvailable();
    }
    public int getPendingConnection(){
        return connectionManager.getTotalStats().getPending();
    }
    public synchronized void shutDown() {
        if (connectionMonitor != null) {
            connectionMonitor.shutdown();
            connectionManager.shutdown();
        }
    }

}
