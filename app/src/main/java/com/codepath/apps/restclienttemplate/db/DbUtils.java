package com.codepath.apps.restclienttemplate.db;

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
