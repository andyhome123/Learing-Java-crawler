import crawler.CrawlControler;

/**
 * Created by 俊毅 on 2015/3/14.
 */
public class QuickStart extends CrawlControler {

    public QuickStart(int threads,boolean clean) throws IllegalAccessException, InstantiationException {
        super(threads,clean);
    }
    public QuickStart(){
        super();
    }
    public static void main(String[] args){
        try{
            QuickStart qq = new QuickStart(20,true);
//            QuickStart qq = new QuickStart();
//            qq.XMLSave();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }


    }
}
