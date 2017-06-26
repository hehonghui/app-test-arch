package com.simple.apptestarch.ui.main;

import com.simple.apptestarch.data.NewsDataSource;
import com.simple.apptestarch.data.NewsListener;
import com.simple.apptestarch.domain.News;
import com.simple.mvp.Presenter;

import java.util.List;

/**
 * MainActivity的逻辑处理类, 该类将 mLocalSource、mRemoteSource、mRemoteSource作为外部依赖注入, 而不是在声明字段时直接使用 NewsDataSource mLocalSource = new NewsDbSource(); 创建,
 * 这是因为在对该类进行测试时我们需要隔离这几个依赖, 通过mock将这几个对象传递进来,这样我们就能够不真正的依赖数据库、网络等条件进行测试,而只需要关注 MainPresenter 本身的业务逻辑.
 *
 * Created by mrsimple on 26/6/17.
 */
public class MainPresenter extends Presenter<MainView> {

    // 本地新闻源, 从数据库获取新闻
    NewsDataSource mLocalSource  ;
    // 网络数据源, 从服务器获取新闻
    NewsDataSource mRemoteSource  ;

    // 是否应该自动刷新
    RefreshMonitor mRefreshMonitor;

    public MainPresenter(NewsDataSource local, NewsDataSource remote, RefreshMonitor refreshMonitor) {
        this.mLocalSource = local;
        this.mRemoteSource = remote;
        this.mRefreshMonitor = refreshMonitor;
    }

    private boolean isNotEmpty(List<News> newsList) {
        return newsList != null && newsList.size() > 0 ;
    }

    public void fetchNews() {
        // load from database
        mLocalSource.fetchNews(new NewsListener() {
            @Override
            public void onComplete(List<News> newsList) {
                if ( getView() != null && isNotEmpty(newsList)) {
                    getView().onFetchNews(newsList);
                }

                // load news from server
                if (newsList == null || newsList.size() == 0 || mRefreshMonitor.shouldRefresh()) {
                    mRemoteSource.fetchNews(mNewsListener);
                }
            }
        });
    }

    NewsListener mNewsListener = new NewsListener() {
        @Override
        public void onComplete(List<News> newsList) {
            if ( getView() != null ) {
                getView().onFetchNews(newsList);
            }
        }
    } ;
}
