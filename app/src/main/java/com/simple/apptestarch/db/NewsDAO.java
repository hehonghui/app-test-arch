package com.simple.apptestarch.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.simple.apptestarch.db.contacts.NewsContact;
import com.simple.apptestarch.domain.News;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mrsimple on 26/6/17.
 */

public class NewsDAO {

    public void insert(List<News> newsList) {
        SQLiteDatabase database = DatabaseHelper.getInstance().getWritableDatabase() ;
        try {
            database.beginTransaction();
            for (News item : newsList) {
                insert(item);
            }
            database.setTransactionSuccessful();
        }  catch (Exception e) {
            e.printStackTrace();
        } finally {
            database.endTransaction();
        }
    }


    public void insert(News item) {
        DatabaseHelper.getInstance().getWritableDatabase() .insert(NewsContact.TABLE_NAME, null, convertRecord(item)) ;
    }

    private ContentValues convertRecord(News item) {
        final ContentValues contentValues = new ContentValues() ;
        contentValues.put(NewsContact.ID, item.id);
        contentValues.put(NewsContact.TITLE, item.title);
        contentValues.put(NewsContact.IMAGE_URL, item.imageUrl);
        contentValues.put(NewsContact.PUBLISH_TIME, item.publishTime);
        return contentValues;
    }


    public List<News> queryAll() {
        List<News> cacheNews = new ArrayList<>() ;
        Cursor cursor = DatabaseHelper.getInstance().getReadableDatabase().query(NewsContact.TABLE_NAME,
                        null, null, null, null, null, null) ;
        while ( cursor.moveToNext() ) {
            News item = new News() ;
            item.id = cursor.getString(0) ;
            item.title = cursor.getString(1) ;
            item.imageUrl = cursor.getString(2) ;
            item.publishTime = cursor.getString(3) ;

            cacheNews.add(item) ;
        }
        return cacheNews;
    }

    public void deleteAll() {
        DatabaseHelper.getInstance().getWritableDatabase().execSQL("delete from " + NewsContact.TABLE_NAME);
    }
}
