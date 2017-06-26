package com.simple.apptestarch.services;

import android.support.test.InstrumentationRegistry;

import com.simple.apptestarch.data.NewsDataSource;
import com.simple.apptestarch.data.NewsListener;
import com.simple.apptestarch.domain.News;
import com.simple.apptestarch.ui.main.MainPresenter;
import com.simple.apptestarch.ui.main.MainView;
import com.simple.apptestarch.ui.main.RefreshMonitor;

import junit.framework.TestCase;

import org.mockito.ArgumentCaptor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * MainPresenter 测试类
 * Created by mrsimple on 26/6/17.
 */
public class MainPresenterTestCase extends TestCase {

    MainPresenter mPresenter ;
    NewsDataSource mLocalSource ;
    NewsDataSource mRemoteSource  ;
    RefreshMonitor mRefreshMonitor ;
    MainView mMainView ;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        // mock 对象, 隔离外部依赖, 例如数据库、网络请求等
        mLocalSource = mock(NewsDataSource.class) ;
        mRemoteSource = mock(NewsDataSource.class) ;
        mRefreshMonitor = mock(RefreshMonitor.class) ;

        // mock main view
        mMainView = mock(MainView.class);

        mPresenter = new MainPresenter(mLocalSource, mRemoteSource, mRefreshMonitor) ;
        mPresenter.attach(InstrumentationRegistry.getContext(), mMainView);
    }


    /**
     * 测试只从数据库中读取新闻. 这个测试用例模拟的情况为:
     *
     * 从数据库中读取了三条新闻缓存, 并且不应该从网络上获取新闻. 获取到数据库缓存之后会将缓存新闻通过 MainView 的 onFetchNews 回调给 MainActivity,
     * 并且不会调用 mRemoteSource 的fetchNews 方法, 因为我们预设了条件 mRefreshMonitor.shouldRefresh() 返回false.
     *
     * @throws Exception
     */
    public void testFetchNewsFromDb() throws Exception {
        // ========= 1. 条件准备部分
        // 当调用mRefreshMonitor.shouldRefresh() 返回 false. 表示不应该从网络上获取新闻
        when(mRefreshMonitor.shouldRefresh()).thenReturn(false) ;

        // ======== 2. 执行操作
        mPresenter.fetchNews();

        // 当调用 mLocalSource.fetchNews 函数时捕获它的 NewsListener 参数, 然后调用 NewsListener 对象的 onComplete 函数, 参数通过 createNews 返回.
        ArgumentCaptor<NewsListener> captor = ArgumentCaptor.forClass(NewsListener.class) ;
        // 参数捕获
        verify(mLocalSource).fetchNews(captor.capture());
        // 执行回调, 将 createNews() 返回的数据回调给 MainPresenter
        captor.getValue().onComplete(createNews());

        // =======  3. 验证部分
        // 调用了 mMainView的 onFetchNews 函数
        verify(mMainView).onFetchNews(anyListOf(News.class));
        // 不会调用 mRemoteSource的fetchNews函数
        verify(mRemoteSource, never()).fetchNews(any(NewsListener.class));
    }


    /**
     * 测试只从数据库中读取新闻为null, 此时会触发 从网络上获取新闻的请求
     * @throws Exception
     */
    public void testNullFromDb() throws Exception {
        // ========= 1. 条件准备部分
        // 当调用mRefreshMonitor.shouldRefresh() 返回 false.
        when(mRefreshMonitor.shouldRefresh()).thenReturn(false) ;

        // ======== 2. 执行操作
        mPresenter.fetchNews();

        // 当调用 mLocalSource.fetchNews 函数时捕获它的 NewsListener 参数, 然后调用 NewsListener 对象的 onComplete 函数, 参数通过 createNews 返回.
        ArgumentCaptor<NewsListener> captor = ArgumentCaptor.forClass(NewsListener.class) ;
        // 参数捕获
        verify(mLocalSource).fetchNews(captor.capture());
        // 执行回调, 模拟返回 null
        captor.getValue().onComplete(null);

        // =======  3. 验证部分
        // 这种情况下不调用 mMainView的 onFetchNews 函数
        verify(mMainView, never()).onFetchNews(anyListOf(News.class));
        // 但是会调用 mRemoteSource的fetchNews函数
        verify(mRemoteSource, times(1)).fetchNews(any(NewsListener.class));
    }


    private static List<News> createNews() {
        List<News> newsList = new ArrayList<>() ;
        for (int i = 0; i < 3; i++) {
            News news = new News();
            news.id = "news-"  + i;
            news.title = "news-" + i;
            news.publishTime = new Date().toString();
            newsList.add(news) ;
        }
        return newsList ;
    }
}
