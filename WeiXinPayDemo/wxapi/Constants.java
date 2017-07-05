package com.yunke.android.wxapi;

import android.app.Activity;

import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class Constants {

	//商户ID
    public static final String APP_ID = "";

    
    public static final String MCH_ID = "";

    //应用签名 MD5值
    public static final String API_KEY = "";

    // 判断手机是否安装微信
    public static boolean isInstallWX(Activity activity) {
        IWXAPI api = WXAPIFactory.createWXAPI(activity, Constants.APP_ID, true);
        if(api.isWXAppInstalled() && api.isWXAppSupportAPI()) {
            return true;
        }else {
            return false;
        }
    }
}
