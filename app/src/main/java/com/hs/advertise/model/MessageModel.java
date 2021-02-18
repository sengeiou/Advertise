package com.hs.advertise.model;

import android.content.Context;
import android.os.Environment;

import com.lunzn.tool.log.LogUtil;

import java.io.File;

public class MessageModel {

    private static final String TAG = "Tag_" + MessageModel.class.getSimpleName();

    /**
     * 下载apk进度更新
     */
    public static final int MSG_DOWNLOAD_PRECENT_REFRESH = 0X1000001;

    /**
     * 下载apk完成
     */
    public static final int MSG_DOWNLOAD_COMPELETE = 0X1000002;

    /**
     * 安装apk完成
     */
    public static final int MSG_INSTALL_COMPELETE = 0X1000003;

    /**
     * apk下载失败
     */
    public static final int MSG_DWONLOAD_FAIL = 0X1000004;

    /**
     * apk安装失败
     */
    public static final int MSG_INSTALL_FAILED = 0X1000005;

    /**
     * apk无下载空间
     */
    public static final int MSG_DWONLOAD_FAIL_NO_STORAGE = 0X1000009;

    /**
     * apk开始安装
     */
    public static final int MSG_INSTALL_APK = 0X1000006;

    /**
     * 开始下载apk
     */
    public static final int MSG_START_DOWNLOAD = 0X1000007;


    /**
     * SDCARD路径
     */
    public final static String SDCARD_PATH = Environment.getExternalStorageDirectory().getPath();

    /**
     * APK下载的保存路径
     */
    public static String APK_DOWNLOAD_PATH = SDCARD_PATH + "/launcher/download/";

    /**
     * 下载的临时路径
     */
    public static String DOWNLOAD_CACHE_PATH;// = CACHE_PATH + "/launcher/cache/";
    /**
     * 测试json文件保存路径
     */
    public static String JSON_LOCAL_PATH;
    /**
     * 软件的缓存路径
     */
    public static String CACHE_PATH;
    /**
     * temp临时文件保存路径
     */
    public static String JSON_TEMP_LOCAL_PATH = "";
    /**
     * 视频的缓存路径
     */
    public static String VIDEO_CACHE_LOCAL_PATH = "";
    /**
     * 图片的缓存路径
     */
    public static String IMAGE_CACHE_LOCAL_PATH = "";

    /**
     * temp临时文件保存路径上一级
     */
    public static String JSON_TEMP_LOCAL_LAST_PATH = "";

    public static void initCachePath(Context context) {
        CACHE_PATH = context.getApplicationContext().getFilesDir().getAbsolutePath();
        JSON_LOCAL_PATH = CACHE_PATH + "/ad_json/total/log.json";
        JSON_TEMP_LOCAL_PATH = CACHE_PATH + "/ad_json/temp/" + System.currentTimeMillis();
        JSON_TEMP_LOCAL_LAST_PATH = CACHE_PATH + "/ad_json/temp/";
        DOWNLOAD_CACHE_PATH = CACHE_PATH + "/launcher/cache/";
        LogUtil.i(TAG, "CACHE_PATH:" + CACHE_PATH);
        VIDEO_CACHE_LOCAL_PATH = Environment.getExternalStorageDirectory() + "/Android/data/" + context.getPackageName() + "/cache/video-cache";
        IMAGE_CACHE_LOCAL_PATH = context.getCacheDir() + "/image_manager_disk_cache";

        init(CACHE_PATH + "/ad_json/total");
        init(CACHE_PATH + "/ad_json/temp");
    }

    public static boolean init(String filePath) {
        boolean flag = false;
        try {
            // 保证创建一个新文件
            File file = new File(filePath);
            if (!file.getParentFile().exists()) { // 如果父目录不存在，创建父目录
                flag = file.getParentFile().mkdir();
            }
            if (!file.exists()) {// 如果文件不存在，创建文件
                flag = file.mkdir();
            }

        } catch (Exception e) {
            flag = false;
            e.printStackTrace();
        }
        LogUtil.i(TAG, filePath + "创建 成功？" + flag);
        return flag;
    }
}
