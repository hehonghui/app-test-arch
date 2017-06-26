package com.simple.apptestarch;

import android.app.Application;
import android.widget.Toast;

import com.simple.apptestarch.db.DatabaseHelper;

import java.lang.reflect.Method;

/**
 * Created by mrsimple on 26/6/17.
 */

public class MainApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // init database
        DatabaseHelper.init(this);

        integrateLeakCanaryForMoneyTest(this);
    }

    private void integrateLeakCanaryForMoneyTest(Application application) {
        if (BuildConfig.FLAVOR.equalsIgnoreCase("monkey")) {
            try {
                Class leakCanaryClz = Class.forName("com.simple.leakfortest.LeakCanaryForTest");
                Method method = leakCanaryClz.getDeclaredMethod("install", Application.class) ;
                method.setAccessible(true);
                method.invoke(null, application) ;
                Toast.makeText(application, "setup LeakCanary for Test", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
