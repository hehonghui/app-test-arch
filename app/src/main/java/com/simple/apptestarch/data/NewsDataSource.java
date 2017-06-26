package com.simple.apptestarch.data;

/**
 * Created by mrsimple on 26/6/17.
 */
public interface NewsDataSource {
    void fetchNews(final NewsListener listener);
}
