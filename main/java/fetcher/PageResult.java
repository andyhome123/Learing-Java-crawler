package fetcher;

import org.apache.http.Header;
import org.apache.http.HttpEntity;

/**
 * Created by 俊毅 on 2015/3/13.
 */
public class PageResult {
    private int statusCode;
    private Header[] responseHeaders = null;
    private String fetchedUrl = null;
    private String movedToUrl = null;
    private short originDepth;
    private String originUrl;
    private byte[] contentData;
    private HttpEntity entity;
    public void setContentData(byte[] contentData){

        this.contentData = contentData;
    }
    public byte[] getContentData(){
        return contentData;
    }

    public void setOriginDepth(short originDepth) {
        this.originDepth = originDepth;
    }

    public void setOriginUrl(String originUrl) {
        this.originUrl = originUrl;
    }
    public short getOriginDepth(){
        return originDepth;
    }
    public String getOriginUrl(){
        return originUrl;
    }
    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public void setEntity(HttpEntity entity) {
        this.entity = entity;
    }

    public void setResponseHeaders(Header[] responseHeaders) {
        this.responseHeaders = responseHeaders;
    }

    public void setFetchedUrl(String fetchedUrl) {
        this.fetchedUrl = fetchedUrl;
    }

    public void setMovedToUrl(String movedToUrl) {
        this.movedToUrl = movedToUrl;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public HttpEntity getEntity() {
        return entity;
    }

    public Header[] getResponseHeaders() {
        return responseHeaders;
    }

    public String getFetchedUrl() {
        return fetchedUrl;
    }

    public String getMovedToUrl() {
        return movedToUrl;
    }

}
