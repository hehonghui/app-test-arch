package com.simple.leakfortest;

import android.app.Application;
import android.os.Process;
import android.util.Log;

import com.squareup.leakcanary.AndroidExcludedRefs;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;
import com.squareup.leakcanary.internal.DisplayLeakActivity;
import com.squareup.leakcanary.internal.LeakCanaryInternals;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

/**
 * see : http://wetest.qq.com/lab/view/175.html
 * Created by mrsimple on 9/3/17.
 */
public final class LeakCanaryForTest {

    public static String sAppPackageName = "";
    private static RefWatcher sWatcher ;

    public static void install(Application application) {
        if (LeakCanary.isInAnalyzerProcess(application)) {
            return;
        }

        sAppPackageName = application.getPackageName();
        // 设置定制的 LeakDumpService , 将 leak 信息输出到指定的目录
        sWatcher = LeakCanary.refWatcher(application).listenerServiceClass(LeakDumpService.class).excludedRefs(AndroidExcludedRefs.createAppDefaults().build())
                .buildAndInstall();
        // disable DisplayLeakActivity
        LeakCanaryInternals.setEnabled(application, DisplayLeakActivity.class, false);

        // 设置 crash 信息捕获
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                final Writer result = new StringWriter();
                final PrintWriter printWriter = new PrintWriter(result);
                e.printStackTrace(printWriter);
                printWriter.close();

                StorageUtils.saveCrashInfo(result.toString());
                // kill the process
                Process.killProcess(Process.myPid());
            }
        });

        Log.e("", "### LeakCanaryForTest install invoked.") ;
    }

    /**
     * 手动监控一个对象, 比如在 Fragment 的 onDestroy 函数中 调用 watch 监控Fragment是否被回收.
     * @param target
     */
    public static void watch(Object target) {
        if ( sWatcher != null ) {
            sWatcher.watch(target);
        }
    }
}
