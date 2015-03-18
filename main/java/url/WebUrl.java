package url;

import java.io.Serializable;

/**
 * Created by 俊毅 on 2015/3/13.
 */
public class WebUrl implements Serializable {
    private static final long serialVersionUID = 1234L;
    private String url;
    private String parentUrl;
    private short depth;
    private short parentDepth;
    private String domain;
    private String path;
    private String text;

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setParentUrl(String parentUrl) {
        this.parentUrl = parentUrl;
    }


    public void setDepth(short depth) {
        this.depth = depth;
    }

    public void setParentDepth(short parentDepth) {
        this.parentDepth = parentDepth;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getUrl() {
        return url;
    }

    public String getParentUrl() {
        return parentUrl;
    }


    public short getDepth() {
        return depth;
    }

    public short getParentDepth() {
        return parentDepth;
    }

    public String getDomain() {
        return domain;
    }

    public String getPath() {
        return path;
    }
}
