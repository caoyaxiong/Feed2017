package com.feed2017.news.activity;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.feed2017.news.R;
import com.feed2017.news.util.AppUtil;
import com.feed2017.news.util.Constant;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;

public class InputSearchActivity extends Activity implements OnClickListener{
	private View viewBody;
	private EditText etSearchInput;
	private LinearLayout llGo;
	private String inputString;
	private String webUrl;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_input_search);
		//设置多长时间内重新启动页面不计入统计
		MobclickAgent.setSessionContinueMillis(1000*60*10);
		PushAgent.getInstance(this).onAppStart();
		initView();
		initListener();
		initData();
	}
	private void initView() {
		//etSearchInput获得焦点时,填充的黑色区域（类似popupwindow非焦点区域）
		viewBody = (View)findViewById(R.id.view_body);
		etSearchInput = (EditText)findViewById(R.id.et_search_input);
		//加载链接按钮
		llGo = (LinearLayout)findViewById(R.id.ll_ic_go);
	}
	
	private void initListener() {
		viewBody.setOnClickListener(this);
		llGo.setOnClickListener(this);
	}
	
	private void initData() {
		if (getIntent() != null) {
			webUrl = getIntent().getDataString();
			if (!AppUtil.isStringEmpty(webUrl)) {
				etSearchInput.setText(webUrl);
				//获得焦点时全选文本
				etSearchInput.setSelectAllOnFocus(true);
			}
		}
	}
	
	private void toSearch() {
		inputString = etSearchInput.getText().toString().trim();
		if (AppUtil.isStringEmpty(inputString)) {
			Toast.makeText(InputSearchActivity.this, "请输入搜索内容或网址", Toast.LENGTH_SHORT).show();
		} else {
			if (AppUtil.isContainsChinese(inputString)) {
				inputString = Constant.URL_SEARCH_KEYWORD + inputString;
			} else {
				if (!(inputString.startsWith("http://") || inputString.startsWith("https://"))) {
					inputString = "http://" + inputString;
				}
			}
			Intent it = new Intent(InputSearchActivity.this, BrowserActivity.class);
			it.setData(Uri.parse(inputString));
			startActivity(it);
			finish();
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()) {
		case R.id.view_body:
			finish();
			break;
		case R.id.ll_ic_go:
			toSearch();
			break;
		default:
			break;
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
//	private void Hook() {
//		AlertDialog.Builder builder = new AlertDialog.Builder(this);
//		builder.setPositiveButton("退出应用", new DialogInterface.OnClickListener() {
//			public void onClick(DialogInterface dialog, int whichButton) {
//				MobclickAgent.onKillProcess(InputSearchActivity.this);
//
//				int pid = android.os.Process.myPid();
//				android.os.Process.killProcess(pid);
//			}
//		});
//		builder.setNeutralButton("后退一下", new DialogInterface.OnClickListener() {
//			public void onClick(DialogInterface dialog, int whichButton) {
//				finish();
//			}
//		});
//		builder.setNegativeButton("点错了", new DialogInterface.OnClickListener() {
//			public void onClick(DialogInterface dialog, int whichButton) {
//			}
//		});
//		builder.show();
//	}
}
