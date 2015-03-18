package parser;

import fetcher.PageResult;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import url.UrlResolver;
import url.WebUrl;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by 俊毅 on 2015/3/14.
 */
public class Parser {
    private Document doc;

    public Parser() {

    }
    public Set<WebUrl> parse(PageResult fetchResult) throws IOException {
        Set<WebUrl> fetchedUrls = new HashSet<WebUrl>();
        String baseUrl = fetchResult.getOriginUrl();
//        InputStream inputStream = new BufferedInputStream(fetchResult.getEntity().getContent());
//        doc = Jsoup.parse(inputStream,"UTF-8",fetchResult.getOriginUrl());
        doc = Jsoup.parse(new String(fetchResult.getContentData()), baseUrl);
        Elements hrefElements = doc.select("a[href]");
        for (Iterator<Element> it = hrefElements.iterator(); it.hasNext(); ) {
            Element hrefElement = it.next();
            WebUrl webUrl = new WebUrl();
            String url = hrefElement.attr("href");
            String text = hrefElement.text();
            if ((url.startsWith("http://")) && !(url.startsWith("https://"))) {
                webUrl.setDepth((short) (fetchResult.getOriginDepth() + 1));
                webUrl.setParentUrl(fetchResult.getOriginUrl());
                webUrl.setParentDepth(fetchResult.getOriginDepth());
                byte[] b = url.getBytes("utf-8");
                webUrl.setUrl(new String(b));
                webUrl.setText(text);
                fetchedUrls.add(webUrl);
            } else if (!(url.startsWith("https://")) && !(url.startsWith("java")) && !(url.startsWith("#")) && !(url.startsWith("mailto"))) {
                String absUrl = UrlResolver.resolveUrl(fetchResult.getOriginUrl(), url);
                webUrl.setDepth((short) (fetchResult.getOriginDepth() + 1));
                webUrl.setParentUrl(fetchResult.getOriginUrl());
                webUrl.setParentDepth(fetchResult.getOriginDepth());
                byte[] b = absUrl.getBytes("utf-8");
                webUrl.setUrl(new String(b));
                webUrl.setText(text);
                fetchedUrls.add(webUrl);
            }
        }
//        inputStream.close();
//        fetchResult.getEntity().consumeContent();
        return fetchedUrls;
    }
}
