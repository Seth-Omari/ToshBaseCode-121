/*
 * Copyright (c) 2017.
 *
 * Anthony Ngure
 *
 * Email : anthonyngure25@gmail.com
 */

package ke.co.toshngure.basecode.log;


/**
 * Created by Anthony Ngure on 9/11/2016.
 * Email : anthonyngure25@gmail.com.
 * Company : Laysan Incorporation
 */
public class LogItem {

    private String title, details;

    public LogItem(String title, String details) {
        this.title = title;
        this.details = details;
    }


    public String getTitle() {
        return title;
    }

    public String getDetails() {
        return details;
    }

}
