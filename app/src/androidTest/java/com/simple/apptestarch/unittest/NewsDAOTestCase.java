package com.simple.apptestarch.unittest;

import android.support.test.InstrumentationRegistry;
import android.test.InstrumentationTestCase;

import com.simple.apptestarch.db.DatabaseHelper;
import com.simple.apptestarch.db.NewsDAO;
import com.simple.apptestarch.domain.News;

import java.util.ArrayList;
import java.util.List;

/**
 * NewsDAO 测试用例, Android Unittest.
 * Created by mrsimple on 26/6/17.
 */
public class NewsDAOTestCase extends InstrumentationTestCase {
    NewsDAO mDao;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        // 初始化
        DatabaseHelper.init( InstrumentationRegistry.getTargetContext());
        mDao = new NewsDAO();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        mDao.deleteAll();
    }

    /**
     * 插入单条新闻, 并且验证数据
     * @throws Exception
     */
    public void testInsertOneNew() throws Exception {
        assertEquals(0, mDao.queryAll().size());
        // 1. 准备数据
        News item1 = new News();
        item1.id = "id-1";
        item1.title = "title-1";
        item1.imageUrl = "test_url";
        item1.publishTime = "2017-06-26 12:30:24";

        // 2. 执行操作
        mDao.insert(item1);

        // 3. 验证数据
        List<News> caches = mDao.queryAll();
        assertEquals(1, caches.size());

        assertEquals("id-1", caches.get(0).id);
        assertEquals("title-1", caches.get(0).title);
        assertEquals("test_url", caches.get(0).imageUrl);
        assertEquals("2017-06-26 12:30:24", caches.get(0).publishTime);
    }


    /**
     * 插入多条新闻,并且验证数据
     * @throws Exception
     */
    public void testInsertMultiNew() throws Exception {
        assertEquals(0, mDao.queryAll().size());

        // 1. 准备数据
        List<News> newsList = new ArrayList<>() ;
        for (int i = 0; i < 2; i++) {
            News item1 = new News();
            item1.id = "id-" + i;
            item1.title = "title-" + i;
            item1.imageUrl = "test_url";
            item1.publishTime = "2017-06-26 12:30:24";

            newsList.add(item1) ;
        }

        // 2. 执行操作
        mDao.insert(newsList);

        // 3. 验证数据
        List<News> caches = mDao.queryAll();
        assertEquals(2, caches.size());

        // 第一条数据
        assertEquals("id-0", caches.get(0).id);
        assertEquals("title-0", caches.get(0).title);
        assertEquals("test_url", caches.get(0).imageUrl);
        assertEquals("2017-06-26 12:30:24", caches.get(0).publishTime);

        // 第二条数据
        assertEquals("id-1", caches.get(1).id);
        assertEquals("title-1", caches.get(1).title);
        assertEquals("test_url", caches.get(1).imageUrl);
        assertEquals("2017-06-26 12:30:24", caches.get(1).publishTime);
    }
}
