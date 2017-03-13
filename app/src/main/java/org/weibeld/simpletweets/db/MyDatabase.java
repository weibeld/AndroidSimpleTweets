package org.weibeld.simpletweets.db;

import com.raizlabs.android.dbflow.annotation.Database;

import org.weibeld.simpletweets.models.Tweet;
import org.weibeld.simpletweets.models.User;

@Database(name = MyDatabase.NAME, version = MyDatabase.VERSION)
public class MyDatabase {
    public static final String NAME = "SimpleTweetsDatabase";
    public static final int VERSION = 6;


    // Delete all rows from all tables of the database
    public static void clearDatabase() {
        Tweet.clearTable();
        User.clearTable();
    }

    public static void clearTweets(int type) {

    }
}
