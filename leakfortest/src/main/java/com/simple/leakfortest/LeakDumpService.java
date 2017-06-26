package com.simple.leakfortest;

import android.util.Log;

import com.squareup.leakcanary.AbstractAnalysisResultService;
import com.squareup.leakcanary.AnalysisResult;
import com.squareup.leakcanary.CanaryLog;
import com.squareup.leakcanary.HeapDump;
import com.squareup.leakcanary.LeakCanary;

import static com.simple.leakfortest.StorageUtils.saveLeakInfo;

/**
 * Created by mrsimple on 9/3/17.
 */
public class LeakDumpService extends AbstractAnalysisResultService {

    @Override
    protected final void onHeapAnalyzed(HeapDump heapDump, AnalysisResult result) {
        if ( !result.leakFound || result.excludedLeak ) {
            return;
        }
        Log.e("", "### *** onHeapAnalyzed in onHeapAnalyzed , dump dir :  " + heapDump.heapDumpFile.getParentFile().getAbsolutePath());
        String leakInfo = LeakCanary.leakInfo(this, heapDump, result, true);
        CanaryLog.d(leakInfo);
        // save leak info
        saveLeakInfo(leakInfo);
    }
}