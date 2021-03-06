package com.tracyzhang.localefilebrowser;

import java.util.Collections;
import java.util.List;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;


/***
 * 本地多媒体文件 浏览器
 * 图片 音频 视频
 * 
 * @author zhanglei
 *
 */
public class LocaleMediaFileBrowser extends Activity implements OnItemClickListener {
	
//	private String tag = "LocaleMediaFileBrowser";
	private ListView lv;
	private List<BXFile> data;
	private LocaleFileAdapter adapter;
	private TextView emptyView;
	private BXFileManager bfm;
	private TextView localefile_bottom_tv;
	private Button localefile_bottom_btn;
	
	private Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			if(1 == msg.what){
				lv.setVisibility(View.VISIBLE);
				emptyView.setVisibility(View.GONE);
				adapter = new LocaleFileAdapter(data,LocaleMediaFileBrowser.this,null,null);
				lv.setAdapter(adapter);
			}else if(0 == msg.what){
				lv.setVisibility(View.GONE);
				emptyView.setVisibility(View.VISIBLE);
				emptyView.setText(getString(R.string.curCatagoryNoFiles));
			}
			super.handleMessage(msg);
		}
		
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.localefile_browser);
		bfm = BXFileManager.getInstance();
		initViews();
		initData();
		onFileClick();
	}

	private void initData() {
		// TODO Auto-generated method stub
		Intent intent = getIntent();
		setTitle(intent.getStringExtra("title"));
		
		setData(intent.getData());
	}
	
	private void setData(final Uri uri){
		LFBApplication app = (LFBApplication) getApplication();
		app.execRunnable(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				data= bfm.getMediaFiles(LocaleMediaFileBrowser.this, uri);
				if(null != data){
					Collections.sort(data);
					handler.sendEmptyMessage(1);
				}
				else
					handler.sendEmptyMessage(0);
			}
			
		});
	}

	private void initViews() {
		// TODO Auto-generated method stub
		TextView curDir = (TextView) findViewById(R.id.curDir);
		curDir.setVisibility(View.GONE);
		lv = (ListView) findViewById(R.id.listView);
		lv.setOnItemClickListener(this);
		emptyView = (TextView) findViewById(R.id.emptyView);
		localefile_bottom_btn = (Button) findViewById(R.id.localefile_bottom_btn);
		localefile_bottom_tv = (TextView) findViewById(R.id.localefile_bottom_tv);
	}
	
	public void onClick(View v){
		switch(v.getId()){
		case R.id.localefile_bottom_btn:
			setResult(2);
			finish();
			break;
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		menu.add(0, 0, 0, getString(R.string.cancel));
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		if(0 == item.getItemId()){
			setResult(1);
			finish();
		}
		return super.onOptionsItemSelected(item);
	}

	//点击文件进行勾选操作
	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int pos, long arg3) {
		// TODO Auto-generated method stub
		CheckBox fileCheckBox = (CheckBox) view.findViewById(R.id.fileCheckBox);
		BXFile bxfile = data.get(pos);
		List<BXFile> choosedFiles = bfm.getChoosedFiles();
		if(choosedFiles.contains(bxfile)){
			choosedFiles.remove(bxfile);
			fileCheckBox.setChecked(false);
		}else{
			choosedFiles.add(bxfile);
			fileCheckBox.setChecked(true);
		}
		onFileClick();
	}
	
	//点击文件，触发ui更新
	//onResume，触发ui更新
	private void onFileClick() {
		localefile_bottom_tv.setText(bfm.getFilesSizes());
		int cnt = bfm.getFilesCnt();
		localefile_bottom_btn.setText(String.format(getString(R.string.bxfile_choosedCnt), cnt));
		localefile_bottom_btn.setEnabled(cnt>0);
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(null!=data){
			data.clear();
		}
		data = null;
		adapter = null;
		handler = null;
	}

}
