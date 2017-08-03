package com.feed2017.news;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.TbsListener;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;

import java.lang.reflect.Method;

public class SBrowserApplication extends Application{
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);

        PushAgent mPushAgent = PushAgent.getInstance(getApplicationContext());
//注册推送服务，每次调用register方法都会回调该接口
        mPushAgent.register(new IUmengRegisterCallback() {

            @Override
            public void onSuccess(String deviceToken) {
                //注册成功会返回device token
				Log.e("qqq",deviceToken);
			}

            @Override
            public void onFailure(String s, String s1) {
				Log.e("ppp","deviceToken");
            }
        });
		QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {
			
			@Override
			public void onViewInitFinished(boolean arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onCoreInitFinished() {
				// TODO Auto-generated method stub
				
			}
		};
		QbSdk.setTbsListener(new TbsListener() {

			@Override
			public void onDownloadFinish(int arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onDownloadProgress(int arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onInstallFinish(int arg0) {
				// TODO Auto-generated method stub
				
			}
			
		});
		QbSdk.initX5Environment(getApplicationContext(), cb);
		//getDeviceInfo(getApplicationContext());
	}
	public static boolean checkPermission(Context context, String permission) {
		boolean result = false;
		if (Build.VERSION.SDK_INT >= 23) {
			try {
				Class<?> clazz = Class.forName("android.content.Context");
				Method method = clazz.getMethod("checkSelfPermission", String.class);
				int rest = (Integer) method.invoke(context, permission);
				if (rest == PackageManager.PERMISSION_GRANTED) {
					result = true;
				} else {
					result = false;
				}
			} catch (Exception e) {
				result = false;
			}
		} else {
			PackageManager pm = context.getPackageManager();
			if (pm.checkPermission(permission, context.getPackageName()) == PackageManager.PERMISSION_GRANTED) {
				result = true;
			}
		}
		return result;
	}
//	public static String getDeviceInfo(Context context) {
//		try {
//			org.json.JSONObject json = new org.json.JSONObject();
//			android.telephony.TelephonyManager tm = (android.telephony.TelephonyManager) context
//					.getSystemService(Context.TELEPHONY_SERVICE);
//			String device_id = null;
//			if (checkPermission(context, Manifest.permission.READ_PHONE_STATE)) {
//				device_id = tm.getDeviceId();
//			}
//			String mac = null;
//			FileReader fstream = null;
//			try {
//				fstream = new FileReader("/sys/class/net/wlan0/address");
//			} catch (FileNotFoundException e) {
//				fstream = new FileReader("/sys/class/net/eth0/address");
//			}
//			BufferedReader in = null;
//			if (fstream != null) {
//				try {
//					in = new BufferedReader(fstream, 1024);
//					mac = in.readLine();
//				} catch (IOException e) {
//				} finally {
//					if (fstream != null) {
//						try {
//							fstream.close();
//						} catch (IOException e) {
//							e.printStackTrace();
//						}
//					}
//					if (in != null) {
//						try {
//							in.close();
//						} catch (IOException e) {
//							e.printStackTrace();
//						}
//					}
//				}
//			}
//			json.put("mac", mac);
//			if (TextUtils.isEmpty(device_id)) {
//				device_id = mac;
//			}
//			if (TextUtils.isEmpty(device_id)) {
//				device_id = android.provider.Settings.Secure.getString(context.getContentResolver(),
//						android.provider.Settings.Secure.ANDROID_ID);
//
//			}
//			json.put("device_id", device_id);
//			Log.e("aaa",json.toString());
//			return json.toString();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return null;
//	}

}
