package com.feed2017.news.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Process;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.JavascriptInterface;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.feed2017.news.R;
import com.feed2017.news.util.AppUtil;
import com.feed2017.news.util.Constant;
import com.feed2017.news.util.DownloadUtil;
import com.feed2017.news.view.MoreMenuPopWindow;
import com.feed2017.news.view.X5WebView;
import com.tencent.smtt.export.external.interfaces.IX5WebChromeClient;
import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
import com.tencent.smtt.sdk.CookieSyncManager;
import com.tencent.smtt.sdk.DownloadListener;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;
import com.tencent.smtt.utils.TbsLog;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;

import java.net.MalformedURLException;
import java.net.URL;

import static anet.channel.util.Utils.context;

public class BrowserActivity extends Activity implements OnClickListener{
	//进度条
	private ProgressBar mPb;
	private X5WebView mWebView;
	//底部状态栏
	private FrameLayout mViewParent;
	//ZXing图标
	private LinearLayout llQrcode;
	private ImageView ivQrcode;
	//输入框图标
	private ImageView ivWebIcon;
	private TextView tvInputUrl;
	//刷新建
	private ImageView ivRefresh;
	private LinearLayout llRefresh;
	//返回键
	private LinearLayout llBack;
	private ImageView ivBack;
	//前进键
	private LinearLayout llForward;
	private ImageView ivForward;
	//状态栏第三键
	private LinearLayout llMenu;
	private ImageView ivMenu;
	//主页键
	private LinearLayout llHome;
	private ImageView ivHome;
	//状态栏第五键
	private LinearLayout llMultiWin;
	private Button btnMultiWindows;
	//状态栏布局
	private LinearLayout llBottom;
	//状态栏第三键展示更多popupwindow
	private MoreMenuPopWindow menuPopWindow;
	private URL mIntentUrl;
	private WebViewClient mWebViewClient;
	private WebChromeClient mWebChromeClient;
	private DownloadListener mDownloadListener;
	//全屏播放
	private FrameLayout mVideoContainer;
	private IX5WebChromeClient.CustomViewCallback mCallBack;
	private String webUrl;
	private final float disable = 0.3f; //按钮半透明
	private final float enable = 1.0f; //按钮透明
	private boolean loadDone = true;
	private long firstClickTime;
	private DownloadUtil downloadUtil;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		setContentView(R.layout.activity_main);
		//设置多长时间内重新启动页面不计入统计
		MobclickAgent.setSessionContinueMillis(1000*60*10);
		PushAgent.getInstance(this).onAppStart();
		initView();
		initData();
		initListener();
	}


	private class JsObject{
		@JavascriptInterface
		public void fullscreen(){
			//监听到用户点击全屏按钮
			fullScreen();
		}
	}
	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		if (intent == null || mWebView == null || intent.getData() == null) {
			return;
		}
		mWebView.loadUrl(intent.getData().toString());
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if (mWebView != null) {
			mWebView.destroy();
		}
		super.onDestroy();
	}
	
	private void initView() {
		mVideoContainer= (FrameLayout) findViewById(R.id.videoContainer);
		mViewParent = (FrameLayout)findViewById(R.id.x5_wv_main);
		mPb = (ProgressBar)findViewById(R.id.pb_web_loading);
		llQrcode = (LinearLayout)findViewById(R.id.ll_ic_qrcode);
		ivQrcode = (ImageView)findViewById(R.id.iv_qrcode);
		ivWebIcon = (ImageView)findViewById(R.id.iv_web_icon);
		tvInputUrl = (TextView)findViewById(R.id.tv_search_input);
		llRefresh = (LinearLayout)findViewById(R.id.ll_ic_refresh);
		ivRefresh = (ImageView)findViewById(R.id.iv_refresh);
		llBottom = (LinearLayout)findViewById(R.id.ll_bottom);
		llBack = (LinearLayout)findViewById(R.id.ll_ic_back);
		llForward = (LinearLayout)findViewById(R.id.ll_ic_forward);
		llMenu = (LinearLayout)findViewById(R.id.ll_ic_menu);
		llHome = (LinearLayout)findViewById(R.id.ll_ic_home);
		llMultiWin = (LinearLayout)findViewById(R.id.ll_muti_windows);
		ivBack = (ImageView)findViewById(R.id.iv_back);
		ivForward = (ImageView)findViewById(R.id.iv_forward);
		ivMenu = (ImageView)findViewById(R.id.iv_menu);
		ivHome = (ImageView)findViewById(R.id.iv_home);
		btnMultiWindows = (Button)findViewById(R.id.bt_muti_windows);

	}
	
	private void initListener() {
		llQrcode.setOnClickListener(this);
		tvInputUrl.setOnClickListener(this);
		llRefresh.setOnClickListener(this);
		llBack.setOnClickListener(this);
		llForward.setOnClickListener(this);
		llMenu.setOnClickListener(this);
		llHome.setOnClickListener(this);
		llMultiWin.setOnClickListener(this);
	}
	
	private void initWebView() {

		Intent intent = getIntent();
		if (intent != null) {
			try {
				mIntentUrl = new URL(intent.getData().toString());
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (NullPointerException e) {

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		try {
			if (Integer.parseInt(android.os.Build.VERSION.SDK) >= 11) {
				getWindow().setFlags(android.view.WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
								android.view.WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		mWebView = new X5WebView(this, null);
		mViewParent.addView(mWebView, new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.MATCH_PARENT,
				FrameLayout.LayoutParams.MATCH_PARENT));
		X5WebView.setSmallWebViewEnabled(true);
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebViewClient = new MyWebViewClient();
		mWebChromeClient = new MyWebChromeClient();
		mDownloadListener = new MyWebDownloadListener();
	}

	
	private void initData() {
		initWebView();
		mWebView.setWebViewClient(mWebViewClient);
		mWebView.setWebChromeClient(mWebChromeClient);
		mWebView.setDownloadListener(mDownloadListener);
		long time = System.currentTimeMillis();
		if (mIntentUrl == null) {
			mWebView.loadUrl(Constant.URL_HOME_PAGE);
		} else {
			mWebView.loadUrl(mIntentUrl.toString());
		}
		mWebView.addJavascriptInterface(new JsObject(),"onClick");
		TbsLog.d("time-cost", "cost time: "
				+ (System.currentTimeMillis() - time));
		CookieSyncManager.createInstance(this);
		CookieSyncManager.getInstance().sync();
	}
	
	private void toInputSearchActivity() {
		Intent it = new Intent(BrowserActivity.this, InputSearchActivity.class);
		it.setData(Uri.parse(webUrl));
		startActivity(it);
	}
	
	private void btnRefreshAction() {
		if (loadDone) {
			//刷新网页
			mWebView.reload();
		} else {
			//停止加载网页
			mWebView.stopLoading();
		}
	}
	//设置底部状态栏图标的透明度
	private void changeBackForwardButton(WebView webView) {
		if (webView.canGoBack()) {
			ivBack.setAlpha(enable);
			llBack.setEnabled(true);
		} else {
			ivBack.setAlpha(disable);
			llBack.setEnabled(false);
		}
		if (webView.canGoForward()) {
			ivForward.setAlpha(enable);
			llForward.setEnabled(true);
		} else {
			ivForward.setAlpha(disable);
			llForward.setEnabled(false);
		}
		if (webView.getUrl() != null && webView.getUrl().equalsIgnoreCase(Constant.URL_HOME_PAGE)) {
			ivHome.setAlpha(disable);
			llHome.setEnabled(false);
		} else {
			ivHome.setAlpha(enable);
			llHome.setEnabled(true);
		}
	}
	//返回上一页
	private void goBackPage() {
		if (mWebView != null && mWebView.canGoBack()) {
			mWebView.goBack();
		}
	}
	//前进一也
	private void goForwardPage() {
		if (mWebView != null && mWebView.canGoForward()) {
			mWebView.goForward();
		}
	}
	//home键返回主页
	private void goHomePage() {
		if (mWebView != null) {
			mWebView.loadUrl(Constant.URL_HOME_PAGE);
		}
	}
	//扫描二维码调用该方法
	private void goToScanQrcode() {
		//startActivity(new Intent(BrowserActivity.this, CaptureActivity.class));
	}
	
	private void showMoreMenu() {
		menuPopWindow = new MoreMenuPopWindow(BrowserActivity.this, mViewParent);
		menuPopWindow.showAtLocation(llBottom, Gravity.BOTTOM, 0, llBottom.getHeight());
	}
	
	private void showToastMsg(String msg) {
		Toast.makeText(BrowserActivity.this, msg, Toast.LENGTH_SHORT).show();
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()) {
			//二维码图标调用方法
		case R.id.ll_ic_qrcode:
			Toast.makeText(getBaseContext(),"ZXing",Toast.LENGTH_SHORT).show();
			//goToScanQrcode();
			break;
		//点击输入框跳转到搜索页面
		case R.id.tv_search_input:
			toInputSearchActivity();
			break;
		//顶部刷新按钮，页面更新
		case R.id.ll_ic_refresh:
			btnRefreshAction();
			break;
		//后退
		case R.id.ll_ic_back:
			goBackPage();
			break;
		//前进
		case R.id.ll_ic_forward:
			goForwardPage();
			break;
		//状态栏第三个按键，更多
		case R.id.ll_ic_menu:
//			showMoreMenu();
			showToastMsg("敬请期待");
			break;
		//home键
		case R.id.ll_ic_home:
			goHomePage();
			break;
		//状态栏最后一键
		case R.id.ll_muti_windows:
			showToastMsg("敬请期待");
			break;
		default:
			break;
		}
	}
	//手机自身返回键监听
	private void exitApp() {
		long currClickTime = System.currentTimeMillis();
		if (currClickTime - firstClickTime >= 2000) {
			firstClickTime = System.currentTimeMillis();
			showToastMsg("再按一次退出浏览器");
		} else {
//			finish();
			Process.killProcess(Process.myPid());
		}
	}
	//手机返回键回退页面
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (mWebView != null && mWebView.canGoBack()) {
				mWebView.goBack();
				return true;
			} else {
				//退无可退就退出APP
				exitApp();
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	private class MyWebViewClient extends WebViewClient {
		//设置直接使用webview加载URL，不跳转到系统浏览器
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			// TODO Auto-generated method stub
			view.loadUrl(url);
			return true;
		}
		
		@Override
		public WebResourceResponse shouldInterceptRequest(WebView arg0,
                                                          String arg1) {
			// TODO Auto-generated method stub
			return super.shouldInterceptRequest(arg0, arg1);
		}
		//页面正在加载状态监听
		@Override
		public void onPageStarted(WebView view, String url, Bitmap arg2) {
			// TODO Auto-generated method stub
			super.onPageStarted(view, url, arg2);
			webUrl = url;
			loadDone = false;
			ivRefresh.setBackgroundResource(R.mipmap.ic_stop);
		}
		//加载完成
		@Override
		public void onPageFinished(WebView view, String url) {
			// TODO Auto-generated method stub
			super.onPageFinished(view, url);
			webUrl = url;
			loadDone = true;
			ivRefresh.setBackgroundResource(R.mipmap.ic_refersh);
			changeBackForwardButton(view);
//			String js=TagUtils.getJs(url);
//			view.loadUrl(js);
		}
	}
	@Override
	public void onBackPressed() {
		if (mWebView.canGoBack()){
			mWebView.goBack();
		}else {
			super.onBackPressed();
		}
	}
	private void fullScreen() {
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		} else {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
	}
	private class MyWebChromeClient extends WebChromeClient {

		@Override
		public void onShowCustomView(View view, IX5WebChromeClient.CustomViewCallback callback) {
			fullScreen();
			mWebView.setVisibility(View.GONE);
			mVideoContainer.setVisibility(View.VISIBLE);
			mVideoContainer.addView(view);
			mCallBack =callback;
			super.onShowCustomView(view, callback);
		}

		@Override
		public void onHideCustomView() {
			fullScreen();
			if (mCallBack !=null){
				mCallBack.onCustomViewHidden();
			}
			mWebView.setVisibility(View.VISIBLE);
			mVideoContainer.removeAllViews();
			mVideoContainer.setVisibility(View.GONE);
			super.onHideCustomView();
		}
		//进度条状态
		@Override
		public void onProgressChanged(WebView view, int progress) {
			// TODO Auto-generated method stub
			super.onProgressChanged(view, progress);
			if (progress >= 100) {
				progress = 100;
				mPb.setProgress(progress);
				mPb.setVisibility(View.GONE);
			} else {
				if (progress >= 10) {
					mPb.setProgress(progress);
				}
				mPb.setVisibility(View.VISIBLE);
			}
		}
		//获取网页标题
		@Override
		public void onReceivedTitle(WebView arg0, String title) {
			// TODO Auto-generated method stub
			super.onReceivedTitle(arg0, title);
			if (tvInputUrl != null && !AppUtil.isStringEmpty(title)) {
				tvInputUrl.setText(title);
			}
		}
		//获取网页图标
		@SuppressWarnings("deprecation")
		@Override
		public void onReceivedIcon(WebView arg0, Bitmap icon) {
			// TODO Auto-generated method stub
			super.onReceivedIcon(arg0, icon);
//			if (ivWebIcon != null) {
//				Drawable db = AppUtil.bitmap2Drawable(icon);
//				if (db != null) {
//					ivWebIcon.setBackgroundDrawable(db);
//				}
//			}
		}
	}
	
	private class MyWebDownloadListener implements DownloadListener {

		@Override
		public void onDownloadStart(String url, String arg1, String arg2,
				String arg3, long arg4) {
			// TODO Auto-generated method stub
			AppUtil.logInfo("downloadStart...");
			downloadUtil = new DownloadUtil(BrowserActivity.this, url);
			downloadUtil.startDownload();
		}
		
	}
	//友盟统计
	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
	// /对于好多应用，会在程序中杀死 进程，这样会导致我们统计不到此时Activity结束的信息，
	// /对于这种情况需要调用 'MobclickAgent.onKillProcess( Context )'
	// /方法，保存一些页面调用的数据。正常的应用是不需要调用此方法的。
	private void Hook() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setPositiveButton("退出应用", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				MobclickAgent.onKillProcess(BrowserActivity.this);

				int pid = android.os.Process.myPid();
				android.os.Process.killProcess(pid);
			}
		});
		builder.setNeutralButton("后退一下", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				finish();
			}
		});
		builder.setNegativeButton("点错了", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
			}
		});
		builder.show();
	}

}
