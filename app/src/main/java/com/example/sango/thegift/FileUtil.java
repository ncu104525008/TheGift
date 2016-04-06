package com.example.sango.thegift;

import android.os.Environment;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by user on 2016/4/2.
 */
public class FileUtil {
    public static final String APP_DIR = "gift";

    public static boolean isExternalStorageWritable() {
        // 取得目前外部儲存設備的狀態
        String state = Environment.getExternalStorageState();

        // 判斷是否可寫入
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }

        return false;
    }

    public static File getExternalStorageDir(String dir) {
        File result = new File(
                Environment.getExternalStorageDirectory(), dir);

        if (!isExternalStorageWritable()) {
            return null;
        }

        if (!result.exists() && !result.mkdirs()) {
            return null;
        }

        return result;
    }

    public static String getUniqueFileName() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        return sdf.format(new Date());
    }
}
