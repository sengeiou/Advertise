package com.hs.advertise.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.hs.advertise.business.PlayLogicUtil;
import com.hs.advertise.config.IntentParam;
import com.hs.advertise.config.IntentValue;
import com.hs.advertise.model.MessageModel;
import com.hs.advertise.mvp.model.bean.AdInfo;
import com.hs.advertise.thread.ThreadManager;
import com.hs.advertise.utils.FileUtil;
import com.hs.advertise.utils.StringUtils;
import com.lunzn.tool.log.LogUtil;
import com.smart.localfile.LocalFileCRUDUtils;

import java.io.File;

/**
 * Desc: 开机时合并日志，启动视频播放的界面，以及启动下载数据服务
 * <p>
 * Author: ganxinrong
 * PackageName: com.hs.advertise.service
 * ProjectName: Advertise
 * Date: 2020/3/11 18:18
 */
public class BootService extends Service {

    private static final String TAG = "Tag_" + BootService.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        bootStartCopyLog();
    }

    private void bootStartCopyLog() {
        long currentTime = System.currentTimeMillis();
        ThreadManager.getShortPool().execute(new Runnable() {
            @Override
            public void run() {
                File file = new File(MessageModel.JSON_TEMP_LOCAL_LAST_PATH);
                String[] files = file.list();
                if (files != null) {
                    for (int i = 0; i < files.length; i++) {
                        String path = files[i];
                        String timeStr = path.substring(path.lastIndexOf("/") + 1);
                        if (StringUtils.isDigit(timeStr)) {
                            long time = Long.parseLong(timeStr);
                            LogUtil.e(TAG, "currentTime " + currentTime + " time " + time);
                            if (currentTime > time) {
                                String oldDirsPath = MessageModel.JSON_TEMP_LOCAL_LAST_PATH + time;
                                File oldTempDirs = new File(oldDirsPath);
                                if (oldTempDirs.isDirectory()) {
                                    String[] oldFiles = oldTempDirs.list();
                                    if (oldFiles != null) {
                                        for (int j = 0; j < oldFiles.length; j++) {
                                            String oldTempPath = oldDirsPath + "/" + oldFiles[j];
                                            LogUtil.e(TAG, "oldTempPath：" + oldTempPath);
                                            if (FileUtil.copyDirectToTotal(oldTempPath)) {
                                                LogUtil.e(TAG, "删除oldTempPath");
                                                // 删除temp文件
                                                LocalFileCRUDUtils.deleteFile(oldTempPath);
                                            }
                                        }
                                        LogUtil.e(TAG, "删除目录oldDirsPath：" + oldDirsPath);
                                        LocalFileCRUDUtils.deleteFile(oldDirsPath);
                                    }
                                }
                            }
                        }

                    }
                }

            }
        });
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.i(TAG, "bootService onStartCommand");
        AdInfo mFullScreenAds = PlayLogicUtil.getFullScreenAds();
        Intent mServiceintent = new Intent(this, DownloadService.class);
        if (mFullScreenAds != null) {
            PlayLogicUtil.bootStartActivity(getApplicationContext(), mFullScreenAds);
        } else {
            mServiceintent.putExtra(IntentParam.KEY_ACTIVITY_FLAG, IntentValue.ACTIVITY_FLAG_NO);
        }
        startService(mServiceintent);

        return START_NOT_STICKY;
    }
}
