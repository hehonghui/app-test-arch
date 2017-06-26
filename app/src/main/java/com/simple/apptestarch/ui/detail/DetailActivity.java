package com.simple.apptestarch.ui.detail;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.simple.apptestarch.R;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by mrsimple on 26/6/17.
 */
public class DetailActivity extends AppCompatActivity {

    private static List<Activity> sRecords = new LinkedList<>() ;

    Handler mHandler = new Handler(Looper.getMainLooper()) ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        sRecords.add(this) ;

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                DetailActivity.this.finish();
                if ( sRecords.size() >= 3 ) {
                    Toast.makeText(DetailActivity.this, "模拟崩溃", Toast.LENGTH_SHORT).show();
                    sRecords.clear();

                    throw new IllegalStateException("Detail Leak !!! Please check !") ;
                }
            }
        }, 1500);
    }
}
