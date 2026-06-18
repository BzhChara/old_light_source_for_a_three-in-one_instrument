package com.zkzk.pra.activity;

import java.util.List;

import org.xutils.x;
import org.xutils.view.annotation.ViewInject;

import com.whswzz.prfluroanalyzer.app.MyApp;
import com.whswzz.prfluroanalyzer.base.BaseActivity;
import com.whswzz.prfluroanalyzer.consts.Consts;
import com.zkzk.pra.R;
import com.zkzk.pra.adapter.ProjectAdapter;
import com.zkzk.pra.entity.Project;
import com.zkzk.pra.utils.TTS;
import com.zkzk.pra.utils.Tools;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class ProjActivity extends BaseActivity implements OnItemLongClickListener, OnItemClickListener {
	@ViewInject(R.id.lv_projs)
	private ListView lvProjs;
	private List<Project> projs;
	private ProjectAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_proj);

		x.view().inject(this);
		init();
		setListeners();
	}

	private void init() {
		projs = MyApp.getApp().getProjs();
		adapter = new ProjectAdapter(this, R.layout.item_proj_msg, projs);
		lvProjs.setAdapter(adapter);
		
		if (MyApp.getApp().isTtsOk()) {
			TTS.stop();
			TTS.speak(getResources().getString(R.string.proj_activity_notice));
		}
	}

	private void setListeners() {
		lvProjs.setOnItemClickListener(this);
		lvProjs.setOnItemLongClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		return false;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (Consts.KEY_ADD_OK == resultCode) {
			adapter.notifyDataSetChanged();
		}
	}
}
