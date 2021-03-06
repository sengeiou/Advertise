package com.hs.advertise.mvp.present;

import android.content.Context;

import com.hs.advertise.mvp.model.iterfaces.UserInfoBiz;
import com.hs.advertise.mvp.model.requests.UserInfoRequest;
import com.hs.advertise.mvp.subscribers.ProgressSubscriber;
import com.hs.advertise.mvp.subscribers.SubscriberOnNextListener;

import java.util.Map;

public class UserInfoPresent {

    private final Context mContext;
    private final UserInfoRequest userInfoRequest;
    private final SubscriberOnNextListener<Object> onNextListener;

    public UserInfoPresent(Context mContext, final UserInfoBiz userInfoBiz) {

        this.mContext = mContext;
        userInfoRequest = new UserInfoRequest();
        this.onNextListener = new SubscriberOnNextListener<Object>() {
            @Override
            public void onNext(String url, Object subjects) {

                if (userInfoBiz != null) {
                    userInfoBiz.onSuccess(url, subjects);
                }
            }

            @Override
            public void onError(Throwable e) {

                if (userInfoBiz != null) {
                    userInfoBiz.onError(e);
                }
            }
        };
    }

    /**
     * 通用操作
     */
    public void commonOperate(String url, Map<String, Object> fieldsMap, String requestMethod) {

        userInfoRequest.commonOperate(mContext, new ProgressSubscriber(onNextListener, mContext, url), url, fieldsMap, requestMethod);
    }

//    /**
//     * 通用操作
//     */
//    public void commonOperate(Context context, String url, Map<String, Object> fieldsMap, String requestMethod, SubscriberOnNextListener<String> listener) {
//
//        userInfoRequest.commonOperate(mContext, new ProgressSubscriber(listener, context, url), url, fieldsMap, requestMethod);
//    }
//
//
//    /**
//     * 通用操作
//     */
//    public void commonOperate(String url, Map<String, Object> fieldsMap, String requestMethod, boolean isShowLoading) {
//
//        userInfoRequest.commonOperate(mContext, new ProgressSubscriber(isShowLoading, onNextListener, mContext, url), url, fieldsMap, requestMethod);
//    }
//
//    /**
//     * 上传文件
//     */
//    public void uploadFile(File file) {
//
//        userInfoRequest.uploadFile(mContext, file, new ProgressSubscriber(onNextListener, mContext, ""));
//    }
//
//    public void upLoadImgList(List<File> files) {
//
//        userInfoRequest.uploadFileList(mContext, files, new ProgressSubscriber(onNextListener, mContext, ""));
//    }

}