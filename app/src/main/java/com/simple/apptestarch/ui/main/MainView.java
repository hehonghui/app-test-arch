package com.simple.apptestarch.ui.main;

import com.simple.apptestarch.domain.News;
import com.simple.mvp.MvpView;

import java.util.List;

/**
 * Created by mrsimple on 26/6/17.
 */
public interface MainView extends MvpView {
    void onFetchNews(List<News> newsList);
}
