package util;

import java.io.File;

/**
 * Created by 俊毅 on 2015/3/18.
 */
public class IO {
    public static boolean deleteAllFiles(String path){
        File file = new File(path);
        File[] files = file.listFiles();
        for(File e : files){
            if(e.isFile()){
               if(!e.delete()){
                   return false;
               }
            }
        }
        return true;
    }

}
