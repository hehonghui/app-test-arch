package com.simple.apptestarch.data;

import com.simple.apptestarch.domain.News;

import java.util.List;

/**
 * Created by mrsimple on 26/6/17.
 */
public interface NewsListener {
    void onComplete(List<News> newsList);
}
