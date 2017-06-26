package com.simple.apptestarch.ui.main;

import java.util.Random;

/**
 * Created by mrsimple on 26/6/17.
 */

public class RefreshMonitor {

    /**
     * 是否应该从网络上获取新闻
     * @return
     */
    public boolean shouldRefresh()  {
        return new Random().nextInt(10) % 2 == 0;
    }
}
