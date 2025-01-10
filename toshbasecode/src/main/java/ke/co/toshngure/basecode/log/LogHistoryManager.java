/*
 * Copyright (c) 2017.
 *
 * Anthony Ngure
 *
 * Email : anthonyngure25@gmail.com
 */

package ke.co.toshngure.basecode.log;


import java.util.ArrayList;
import java.util.List;


/**
 * Created by Anthony Ngure on 9/11/2016.
 * Email : anthonyngure25@gmail.com.
 * Company : Laysan Incorporation
 */
public class LogHistoryManager {

    private static LogHistoryManager mInstance;
    private List<LogItem> logItems;

    public static LogHistoryManager getInstance() {
        LogHistoryManager localInstance = mInstance;
        if (localInstance == null) {
            synchronized (LogHistoryManager.class) {
                localInstance = mInstance;
                if (localInstance == null) {
                    mInstance = localInstance = new LogHistoryManager();
                }
            }
        }
        return localInstance;
    }

    public List<LogItem> getLogItems() {
        if (logItems == null) {
            logItems = new ArrayList<>();
        }
        return logItems;
    }

    public void add(LogItem logItem) {
        getLogItems().add(logItem);
    }
}
