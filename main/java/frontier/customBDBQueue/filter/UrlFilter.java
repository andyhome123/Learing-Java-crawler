/*
 *  This file is part of the Heritrix web crawler (crawler.archive.org).
 *
 *  Licensed to the Internet Archive (IA) by one or more individual
 *  contributors.
 *
 *  The IA licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package frontier.customBDBQueue.filter;

import com.sleepycat.bind.tuple.LongBinding;
import com.sleepycat.je.*;

import java.io.File;
import java.util.concurrent.atomic.AtomicLong;

public class UrlFilter {
    private static final String COLON_SLASH_SLASH = "://";
    private static DatabaseEntry ZERO_LENGTH_ENTRY = new DatabaseEntry(new byte[0]);
    private Database alreadySeen;
    private Environment env;
    private static AtomicLong count = new AtomicLong(0);
    private final Object mutex = new Object();
    public static long createKey(CharSequence uri) {
        String url = uri.toString();
        long schemeAuthorityKeyPart = calcSchemeAuthorityKeyBytes(url);
        return schemeAuthorityKeyPart | (FPGenerator.std40.fp(url) >>> 24);
    }

    protected static long calcSchemeAuthorityKeyBytes(String url) {
        int index = url.indexOf(COLON_SLASH_SLASH);
        if (index > 0) {
            index = url.indexOf('/', index + COLON_SLASH_SLASH.length());
        }
        CharSequence schemeAuthority = (index == -1)? url: url.subSequence(0, index);
        return FPGenerator.std24.fp(schemeAuthority);
    }
    public UrlFilter(){
        EnvironmentConfig environmentConfig = new EnvironmentConfig();
        environmentConfig.setAllowCreate(true);
        DatabaseConfig databaseConfig = new DatabaseConfig();
        databaseConfig.setAllowCreate(true);
        File file = new File("E:/test");
        env = new Environment(file,environmentConfig);
        alreadySeen = env.openDatabase(null,"filter",databaseConfig);
    }
    public boolean setAdd(CharSequence uri) {
        synchronized(mutex){
            DatabaseEntry key = new DatabaseEntry();
            LongBinding.longToEntry(createKey(uri), key);
            OperationStatus status = null;
            try {

                status = alreadySeen.putNoOverwrite(null, key, ZERO_LENGTH_ENTRY);
            } catch (DatabaseException e) {
                e.printStackTrace();
            }
            if (status == OperationStatus.SUCCESS) {

            }
            if(status == OperationStatus.KEYEXIST) {
                count.incrementAndGet();
                return false; // not added
            } else {
                return true;
            }
        }
    }
    public void close(){
        alreadySeen.close();
        env.close();
    }
    public static long getCount(){
        return count.get();
    }
}