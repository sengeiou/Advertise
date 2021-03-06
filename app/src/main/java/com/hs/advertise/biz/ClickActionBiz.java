package com.hs.advertise.biz;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.hs.advertise.R;
import com.hs.advertise.model.MessageModel;
import com.hs.advertise.mvp.model.bean.ClickAction;
import com.lunzn.download.FinalFileLoad;
import com.lunzn.download.command.DownloadUrlInfo;
import com.lunzn.download.util.BaseDownloadInfoCallBack;
import com.lunzn.systool.pkg.ApkManageBiz;
import com.lunzn.tool.log.LogUtil;
import com.lunzn.tool.toast.LzToast;
import com.lunzn.tool.util.CommonUtil;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * view的点击事件处理
 *
 * @author renweiming
 * @version [版本号]
 * @date 2017年2月8日
 * @project COM.LZ.M02.LAUNCHER
 * @package com.lz.m02.launcher.biz
 * @package ClickActionBiz.java
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public class ClickActionBiz {

    private static final String TAG = ClickActionBiz.class.getSimpleName();

    public static void onClick(ClickAction data, Context context, BaseDownloadInfoCallBack downloadCallback) {
        openClickDataNoReplace(data, context, downloadCallback);
    }

    /**
     * 不需要打开替换应用的点击事件
     */
    private static void openClickDataNoReplace(ClickAction data, final Context context,
                                               BaseDownloadInfoCallBack downloadCallback) {
//        data.setPkname("com.fmxos.app.smarttv");
        String[] splitParams = getRightParam(data);
        String pkname = splitParams[0];
        String action = splitParams[1];
        String param = splitParams[2];
        String extra = splitParams[3];
//        String resourceId = splitParams[4]; // 视界新增的字段，用于确定需要上报的
        if ("com.ktcp.tvvideo".equals(pkname)) {
            // 进入腾讯
            openTecentApp(data.getAppPkgName(), pkname, action, param, context, downloadCallback);
        } else if ("com.gitvvideo.lanzheng".equals(pkname)) {
            openAiQiYiApp(data, context, downloadCallback, pkname, action, param);
        } else if ("android.intent.action.VIEW".equalsIgnoreCase(pkname)) {
            LogUtil.i("android.intent.action.VIEW:" + action + "android.intent.action.VIEW:" + param);
            //显示网页
            showWebviewPopwindow(param, context);
        } else if ("android.intent.action.play".equalsIgnoreCase(pkname)) {
            Uri uri = Uri.parse(param);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setPackage(data.getAppPkgName());//getAppPkname()
            // context.startActivity(intent);
            customOpenIntent(context, intent);
        } else if ("com.lz.show.image".equalsIgnoreCase(action)) {
            LogUtil.i("com.lz.show.image:" + data.getAppPkgName() + "    com.lz.show.image:" + param + "    context: " + context);//getAppPkname()
            Map<String, String> paramMap = new HashMap<String, String>();
            paramMap.put("imgurl", param);
            ApkManageBiz.startApkByPkgnameAndParam(context, data.getAppPkgName(), paramMap);//getAppPkname()
        } else if ("com.lzpd.inlive".equals(data.getPkgName())) {//getPkname()
            //不要直播了
        } else if (!CommonUtil.isEmpty(data.getAppPkgName()) && !"null".equals(data.getAppPkgName())) {//getAppPkname()
            // 应用
            ClickApkData(data, context, downloadCallback);
        } else {
            // 隐式Intent
            openAppByAction(action, param, extra, context, pkname);
        }
    }

    /**
     * 打开爱奇艺app
     *
     * @param data   点击item数据
     * @param pkname 应用包名
     * @param action 动作
     * @param param  参数
     */
    private static void openAiQiYiApp(ClickAction data, Context context, BaseDownloadInfoCallBack downloadCallback, String pkname, String action, String param) {
        if (!openIQiYiByAction(context, data)) {
            if (!ApkManageBiz.startApkByPkgname(context, pkname)) {
                //打开应用失败时
                //标识未配置下载app
                boolean flag = false;
                //判断配置其他app
                flag = downloadApp(data.getAppPkgName(), action, param, context, downloadCallback, flag);//getAppPkname()
                if (!flag) {
                    LzToast.showToast(context, "请安装银河奇异果", 2000);

                }
            }
        } else {
            // action 打开成功
        }
    }


    /**
     * 通过隐式intent打开应用
     */
    private static void openAppByAction(String action, String param, String extra, Context context, String pkname) {
        LogUtil.i("open app. action:" + action + ",param:" + param + ",extra:" + extra + ",pkname:" + pkname);
        try {
            Intent intent = new Intent(action);
            if (!CommonUtil.isEmpty(param)) {
                intent.setData(Uri.parse(param));

            }
            int pullType = 0;
            if (CommonUtil.isNotEmpty(extra)) {
                Object[] object = setLauncherParams(intent, extra, pkname);
                pullType = (int) object[0];
                intent = (Intent) object[1];
            }
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (pullType == 0) {
                // context.startActivity(intent);
                customOpenIntent(context, intent);
            } else if (pullType == 1) {
                context.startService(intent);
            } else {
                // context.startActivity(intent);
                customOpenIntent(context, intent);
            }
            //"start app by action"
        } catch (Exception e) {
            e.printStackTrace();
            //"open app by action failed"
        }
    }

    public static String[] getRightParam(ClickAction data) {
        String[] result = new String[4];
        String pkname = data.getPkgName();//getPkname()
        String action = data.getAction();
        String param = data.getParam();
        String extra = data.getExtraParam();
        result[0] = pkname;
        result[1] = action;
        result[2] = param;
        result[3] = extra;
        return result;
    }


    /**
     * 点击的view携带的数据是apk数据时的点击处理
     *
     * @param data
     * @param context
     */
    public static void ClickApkData(ClickAction data, Context context,
                                    BaseDownloadInfoCallBack downloadCallback) {
        LogUtil.d("start app : " + data.getAppPkgName() + ", bak app : " + data.getPkgName());
        int operateType = -1;
        String[] rightParam = getRightParam(data);
        String pkName = rightParam[0];
        String action = rightParam[1];
        String param = rightParam[2];
        String extra = rightParam[3];
        Intent launcherIntent = getLauncherIntent(context, data.getAppPkgName());//getAppPkname()
        if (launcherIntent != null && launcherIntent.getComponent() != null) {
            LogUtil.i(TAG, "跳转去广告APP 包名是：" + launcherIntent.getComponent().getPackageName());
        }
        boolean result = false;
        try {
            if (launcherIntent != null) {
                int pullType = 0;
                if (extra != null) {
                    Object[] object = setLauncherParams(launcherIntent, extra, data.getAppPkgName());//getAppPkname()
                    pullType = (int) object[0];
                    launcherIntent = (Intent) object[1];
                }
                launcherIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (pullType == 0) {
                    //context.startActivity(launcherIntent);
                    customOpenIntent(context, launcherIntent);
                } else if (pullType == 1) {
                    context.startService(launcherIntent);
                }
                result = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        LogUtil.i(TAG, "--------------");
        //打开应用失败时
        if (!result) {
            //标识未配置下载app
            boolean flag = false;
            flag = downloadApp(data.getAppPkgName(), action, param, context, downloadCallback, flag);//getAppPkname()
            if (!flag) {
                LzToast.showToast(context, "应用未安装", 2000);
            }
        } else {

        }
    }

    private static Object[] setLauncherParams(Intent intent, String extra, String pkname) {
        Object[] result = new Object[2];
        int pullType = 0;
        try {
            JSONObject extraObject = new JSONObject(extra);
            Map<String, String> extraMap = new HashMap<String, String>();
            Iterator<String> iterator = extraObject.keys();
            while (iterator != null && iterator.hasNext()) {
                String key = iterator.next();
                extraMap.put(key, extraObject.getString(key));
            }
            if (extraMap.size() > 0) {
                if (extraMap.containsKey("clsname")) {
                    String clsNmae = extraMap.remove("clsname");
                    intent = new Intent();
                    intent.setClassName(pkname, clsNmae);
                }
                // 启动方式 0-activity， 1-service
                if (extraMap.containsKey("pulltype")) {
                    pullType = CommonUtil.toInt(extraMap.remove("pulltype"));
                }
                for (Map.Entry<String, String> entry : extraMap.entrySet()) {
                    intent.putExtra(entry.getKey(), entry.getValue());
                }
            }
            intent.setPackage(pkname);


        } catch (Exception e) {
            e.printStackTrace();
        }

        result[0] = pullType;
        result[1] = intent;
        return result;
    }

    private static Intent getLauncherIntent(Context context, String packageName) {
        PackageManager pm = context.getPackageManager();
        PackageInfo pi;
        Intent intent = null;
        try {
            pi = context.getPackageManager().getPackageInfo(packageName, 0);
            Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
            resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            resolveIntent.setPackage(pi.packageName);
            List<ResolveInfo> apps = pm.queryIntentActivities(resolveIntent, 0);
            ResolveInfo ri = apps.iterator().next();
            if (ri != null) {
                packageName = ri.activityInfo.packageName;
                String className = ri.activityInfo.name;
                intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                ComponentName cn = new ComponentName(packageName, className);
                intent.setComponent(cn);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return intent;
    }

    /**
     * 下载应用
     */
    private static boolean downloadApp(String appPkname, String action, String param, Context context, BaseDownloadInfoCallBack downloadCallback, boolean flag) {
        LogUtil.i("createIItemView", "action:  " + action + ";  param:  " + param);
        if (!CommonUtil.isEmpty(action) && !CommonUtil.isEmpty(param)) {
            String[] splitAction = action.split(";");
            String[] splitParam = param.split(";");
            if (!CommonUtil.isEmpty(splitAction) && !CommonUtil.isEmpty(splitParam)) {
                for (int i = 0; i < splitAction.length; i++) {
                    if ("com.lz.downloadApp".equalsIgnoreCase(splitAction[i]) && splitParam[i].contains(",")) {
                        flag = true;
                        // 推荐应用，点击下载
                        LogUtil.i("应用未安装，包名：" + appPkname);
                        //data.setImgurl("http://www.apk3.com/uploads/soft/guiguangbao/txsjgj.apk");
                        //data.setImgCode(1374795368);
                        //取出对应的Param[i]
                        if (com.smart.util.Utils.isNetConnected(context)) {
                            ClickActionBiz.downFile(context, splitParam[i], downloadCallback);
                        } else {
                            LzToast.showToast(context, "网络已断开，请连接网络", 2000);
                        }
                        break;
                    }
                }
            }
        }
        return flag;
    }

    public static void downFile(Context context, String dataParam,
                                BaseDownloadInfoCallBack downloadCallback) {
        FinalFileLoad fileLoader = new FinalFileLoad(context);
        boolean isDownload = false;
        if (!isDownload) {
            Map<String, Long> crcCode = new HashMap<String, Long>();
            String[] params = dataParam.split(",");
            if (params != null && params.length > 1) {
                crcCode.put(params[0], Long.parseLong(params[1]));
            }
            DownloadUrlInfo[] downloadInfo = new DownloadUrlInfo[1];
            downloadInfo[0] = new DownloadUrlInfo(params[0], 1, crcCode);
            //文件名
            String fileName = getFileNameByApkDownloadUrl(params[0]);
            String timeTamp = String.valueOf(System.currentTimeMillis());
            //最终保存路径
            String name = timeTamp + "-" + fileName;
            LogUtil.i(TAG, "downFile#fileName  =" + name);
//            String name =  fileName;
            downloadInfo[0].setFileLocalPath(MessageModel.APK_DOWNLOAD_PATH + name);
            //保存的文件名
            downloadInfo[0].setFileName(name);
            //下载
            fileLoader.downloadFile(downloadInfo, downloadCallback, MessageModel.DOWNLOAD_CACHE_PATH);
        }
    }

    /**
     * 打开腾讯app或者下载app
     */
    private static void openTecentApp(String appPkname, String pkname, String action, String param, Context context,
                                      BaseDownloadInfoCallBack downloadCallback) {
        if (!openTencentAppByAction(action, param, context, downloadCallback)) {
            if (!ApkManageBiz.startApkByPkgname(context, pkname)) {
                //打开应用失败时
                //标识未配置下载app
                boolean flag = false;
                //判断配置其他app
                flag = downloadApp(appPkname, action, param, context, downloadCallback, flag);
                if (!flag) {
                    LzToast.showToast(context, "请安装云视听·极光", 2000);
                }
            }
        } else {
            // 正常打开了

        }
    }

    /**
     * 根据action打开腾讯应用
     */
    private static boolean openTencentAppByAction(String action, String param, Context context,
                                                  BaseDownloadInfoCallBack downloadCallback) {
        LogUtil.i("openTencentApp,action:" + action + "openTencentApp,param:" + param);
        //标识是否进入腾讯
        boolean flag = false;
        if (!CommonUtil.isEmpty(action) && !CommonUtil.isEmpty(param)) {
            String[] splitAction = action.split(";");
            String[] splitParam = param.split(";");
            if (!CommonUtil.isEmpty(splitAction) && !CommonUtil.isEmpty(splitParam)) {
                for (int i = 0; i < splitAction.length; i++) {
                    if ("com.tencent.qqlivetv.open".equalsIgnoreCase(splitAction[i])) {
                        Intent intent = new Intent(splitAction[i]);
                        intent.setData(Uri.parse(splitParam[i]));
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setPackage("com.ktcp.tvvideo");//设置视频包名，要先确认包名
                        PackageManager packageManager = context.getPackageManager();
                        List<ResolveInfo> activities = packageManager.queryIntentActivities
                                (intent, 0);
                        boolean isIntentSafe = activities.size() > 0;
                        if (isIntentSafe) {
                            flag = true;
                            try {
                                // context.startActivity(intent);
                                customOpenIntent(context, intent);
                            } catch (Exception e) {
                                e.printStackTrace();
                                flag = false;
                            }
                        }
                        break;
                    }
                }
            }
        }
        return flag;
    }


    private static boolean openIQiYiByAction(Context context, ClickAction viewData) {
        try {
//          action =   "com.gitvvideo.lanzheng.action.ACTION_DETAIL"
//          包名    =   "com.gitvvideo.lanzheng"
//          ("playType", "history");  //否  从哪里进入的
//          ("history", "0");         //否  播放视频的起始时间(毫秒)。
//          ("videoId", "227801800"); //专辑 ID
//          ("episodeId", "2278018"); //视频 ID
//          ("chnId", "1");           //频道 ID
//          ("customer", "lunzn");    //客户渠道
            String action = viewData.getAction();
            if (CommonUtil.isNotEmpty(action)) {
                String param = viewData.getParam();
                String appPkname = viewData.getPkgName();//getPkname()
                Intent intent = new Intent(action);
                intent.setClassName(appPkname, "com.gala.video.lib.share.ifimpl.openplay.broadcast.activity.LoadingActivity");
                Bundle bundle = new Bundle();
                bundle.putString("playInfo", param);
                LogUtil.i(TAG, "openIQiYiByAction param = " + param);
                intent.putExtras(bundle);
                intent.setPackage(appPkname);
                intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TOP);
                // context.startActivity(intent);
                customOpenIntent(context, intent);
            } else {
                Intent intent = getLauncherIntent(context, viewData.getPkgName());//getPkname()
                if (intent != null) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    //context.startActivity(intent);
                    customOpenIntent(context, intent);

                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.i(TAG, "openIQiYiByAction failed!");
        }

        return false;
    }

    /**
     * 通过URL截取保存文件名
     *
     * @param url
     * @return
     */
    private static String getFileNameByApkDownloadUrl(String url) {
        return url.substring(url.lastIndexOf("/") + 1, url.length());
    }

    /**
     * 通过popwindow 显示webview 网页内容
     *
     * @param url      网页地址
     * @param mContext 上下文
     */
    private static void showWebviewPopwindow(String url, Context mContext) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.popwindow_webview_layout, null);
        final WebView mWebView = (WebView) view.findViewById(R.id.netWebview);
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setLayoutAlgorithm(LayoutAlgorithm.NARROW_COLUMNS);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT); // 开启 DOM storage API 功能
        webSettings.setDomStorageEnabled(true);
        Dialog dialog = new Dialog(mContext, R.style.dialog);
        dialog.setContentView(view);
        mWebView.loadUrl(url);
        mWebView.setWebViewClient(new WebViewClient() {
            //覆盖shouldOverrideUrlLoading 方法  
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        dialog.show();
        dialog.setOnKeyListener(new OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface arg0, int arg1, KeyEvent event) {
                LogUtil.i("dialog", "===onKeyListener==");
                int keyCode = event.getKeyCode();
                if (keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()) {
                    mWebView.goBack();
                    return true;
                }
                return false;
            }
        });
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.getWindow().setAttributes(lp);
        //"打开网页"
    }


    private static void customOpenIntent(Context context, Intent intent) {
        LogUtil.i(TAG, "跳转activity");
        try {
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
