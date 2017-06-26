package com.simple.apptestarch.data.remote;

import android.os.Handler;

import com.simple.apptestarch.data.NewsDataSource;
import com.simple.apptestarch.data.NewsListener;
import com.simple.apptestarch.db.NewsDAO;
import com.simple.apptestarch.domain.News;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 从服务器获取数据
 * Created by mrsimple on 26/6/17.
 */
public class NewsRemoteSource implements NewsDataSource {

    private static final SimpleDateFormat DATA_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
    private static int sCount = 0 ;

    @Override
    public void fetchNews(final NewsListener listener) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if ( listener != null ) {
                    listener.onComplete(mockNews());
                }
            }
        }, 500);
    }

    private static List<News> mockNews() {
        List<News> newsList = new ArrayList<>() ;
        for (int i = 0; i < 3; i++) {
            News news = new News();
            news.id = "news-"  + System.currentTimeMillis() + sCount++;
            news.title = "news-" + sCount;
            news.publishTime = DATA_FORMAT.format(new Date());
            newsList.add(news) ;
        }
        NewsDAO dao = new NewsDAO() ;
        dao.insert(newsList);
        return newsList ;
    }
}
