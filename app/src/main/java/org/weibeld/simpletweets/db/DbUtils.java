package org.weibeld.simpletweets.db;

import org.weibeld.simpletweets.models.Tweet;
import org.weibeld.simpletweets.models.User;

/**
 * Created by dw on 03/03/17.
 */

public class DbUtils {

    // Delete all rows from all tables of the database
    public static void clearTables() {
        Tweet.clearTable();
        User.clearTable();
    }

}
