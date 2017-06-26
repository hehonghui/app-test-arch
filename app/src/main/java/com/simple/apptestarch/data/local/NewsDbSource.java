package com.simple.apptestarch.data.local;

import android.os.Handler;
import android.os.Looper;

import com.simple.apptestarch.data.NewsDataSource;
import com.simple.apptestarch.data.NewsListener;
import com.simple.apptestarch.db.NewsDAO;
import com.simple.apptestarch.domain.News;

import java.util.List;

/**
 * 从数据库中获取数据
 * Created by mrsimple on 26/6/17.
 */

public class NewsDbSource implements NewsDataSource {

    @Override
    public void fetchNews(final NewsListener listener) {
        new Thread() {
            @Override
            public void run() {
                NewsDAO dao = new NewsDAO() ;
                final List<News> cacheNews = dao.queryAll() ;
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if ( listener != null ) {
                            listener.onComplete(cacheNews);
                        }
                    }
                }, 2000);
            }
        }.start();
    }
}
