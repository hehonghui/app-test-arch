package com.simple.leakfortest;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 保存内存泄露信息
 * Created by mrsimple on 9/3/17.
 */
public final class StorageUtils {

    // 用于格式化日期,作为日志文件名的一部分
    private static DateFormat sFormatter = new SimpleDateFormat("yyyy-MM-dd");

    private static boolean isClear = false;


    static void saveLeakInfo(String leakInfo) {
        flush(leakInfo, generateLogFileName("leak"));
    }


    static void saveCrashInfo(String leakInfo) {
        flush(leakInfo, generateLogFileName("crash"));
    }


    private static void flush(String leakInfo, String fileName) {
        BufferedWriter bos = null;
        try {
            Log.e("", "### log file name : " + fileName);
            // 每天的log 追加在同一个文件中.
            bos = new BufferedWriter(new FileWriter(fileName, true));
            bos.append(leakInfo);
            bos.append("\n\n");
            bos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private static String generateLogFileName(String subDir) {
        String fileName = sFormatter.format(new Date()) + "-" + subDir + ".txt";
        if (isExistSdCard()) {
            String path = Environment.getExternalStorageDirectory().getPath();
            //获取跟目录
            path = path + File.separator + LeakCanaryForTest.sAppPackageName + File.separator + subDir + File.separator;
            File dir = new File(path);
            // 清空缓存
            if (!isClear) {
                isClear = true;
                deleteDirectory(dir);
            }
            if (!dir.exists()) {
                dir.mkdirs();
            }
            fileName = path + fileName;
        }
        return fileName;
    }

    /**
     * 删除目录
     *
     * @param directory
     * @return
     */
    private static boolean deleteDirectory(File directory) {
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (null != files) {
                for (int i = 0; i < files.length; i++) {
                    if (files[i].isDirectory()) {
                        deleteDirectory(files[i]);
                    } else {
                        files[i].delete();
                    }
                }
            }
        }
        return (directory.delete());
    }

    /**
     * 判断sd卡是否存在
     *
     * @return
     */
    private static boolean isExistSdCard() {
        return Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
    }

}
