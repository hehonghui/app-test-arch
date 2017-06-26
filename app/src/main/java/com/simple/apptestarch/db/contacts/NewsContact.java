package com.simple.apptestarch.db.contacts;

/**
 * Created by mrsimple on 26/6/17.
 */

public class NewsContact {

    // Contacts table name
    public static final String TABLE_NAME = "news";

    // Contacts Table Columns names
    public static final String ID = "id";
    public static final String TITLE = "title";
    public static final String IMAGE_URL = "image_url";
    public static final String PUBLISH_TIME = "pub_time";

    public static final String CREATE_SQL = "CREATE TABLE " + NewsContact.TABLE_NAME + "("
            + NewsContact.ID + " TEXT,"
            + NewsContact.TITLE + " TEXT," + NewsContact.IMAGE_URL + " TEXT, " +  NewsContact.PUBLISH_TIME + " TEXT" + ")";
}
