package com.zkzk.pra.adapter;

import java.util.LinkedList;
import java.util.List;

import org.xutils.x;
import org.xutils.view.annotation.ViewInject;

import com.whswzz.prfluroanalyzer.app.MyApp;
import com.whswzz.prfluroanalyzer.consts.Consts;
import com.zkzk.pra.R;
import com.zkzk.pra.entity.Project;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class ProjectAdapter extends BaseAdapter{
	private Context context;
	private LayoutInflater inflater;
	private Object resource;
	private List<Project> projs;
	public ProjectAdapter(Context context,int resuorce,List<Project> projs) {
		this.context=context;
		inflater=LayoutInflater.from(context);
		this.resource=resource;
		this.projs=projs;
	}
	@Override
	public int getCount() {
		return projs.size();
	}

	@Override
	public Object getItem(int position) {
		return projs.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		Project proj=projs.get(position);
		if(null==convertView) {
			convertView=inflater.inflate(R.layout.item_proj_msg, null);
			holder = new ViewHolder();
			x.view().inject(holder, convertView);
			OnClickListener itemListener=new BtClickListener(holder);
			holder.cbIsCommon.setOnCheckedChangeListener(new CheckedListener(holder));
//			holder.btAdd.setOnClickListener(itemListener);	//执行同样的操作，此监听器没必要更新。
//			holder.btAdd.setTag(Consts.KEY_TAG_TYPE,1);		//与具体内容无关的项目，不需每次更新。
			convertView.setTag(holder);
		}else {
			holder=(ViewHolder) convertView.getTag();
			holder.proj=proj;
		}

		holder.proj=proj;
		holder.cbIsCommon.setChecked(proj.isCommon());
		holder.tvProj.setText(proj.getProj());
		holder.tvMethod.setText(proj.getMethod());
		holder.tvContrast.setText(String.format("%.3f", proj.getContrast()));
		holder.tvRefer.setText("0-"+String.format("%.1f", proj.getLimit()*100)+"%");
		holder.tvOpened.setText(R.string.have_opened);
//		holder.tvOpened.setText(R.string.havenot_opened);
		return convertView;
	}
	
	private class ViewHolder {
		Project proj;
		@ViewInject(R.id.cb_is_common)
		private CheckBox cbIsCommon;
		@ViewInject(R.id.tv_item_proj)
		private TextView tvProj;
		@ViewInject(R.id.tv_item_method)
		private TextView tvMethod;
		@ViewInject(R.id.tv_detect_line_refer)	//阴性参考值
		private TextView tvRefer;
		@ViewInject(R.id.tv_contrast)
		private TextView tvContrast;
		@ViewInject(R.id.tv_can_use)
		private TextView tvOpened;
	}

	
	
	class selectedListener implements OnItemSelectedListener{
		ViewHolder holder;
		public selectedListener(ViewHolder holder) {
			super();
			this.holder=holder;
		}
		@Override
		public void onNothingSelected(AdapterView<?> parent) {
		}
		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		}
	};
	
	private class CheckedListener implements OnCheckedChangeListener{
		ViewHolder holder;
		public CheckedListener(ViewHolder holder) {
			super();
			this.holder = holder;
		}
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			holder.proj.setCommon(isChecked);
		}
		
	}
	private class BtClickListener implements OnClickListener{
		private ViewHolder holder;
		public BtClickListener(ViewHolder holder) {
			super();
			this.holder = holder;
		}
		@Override
		public void onClick(View v) {
			Log.d("jemen","v="+v);
			switch((Integer)v.getTag(Consts.KEY_TAG_TYPE)) {
			case 1:	//添加按钮
//				Intent toAdd=new Intent(context,ProjEditActivity.class);
//				toAdd.putExtra(Consts.KEY_PROJ, holder.proj.getProj());
////				context.startActivity(toAdd);
//				((Activity)context).startActivityForResult(toAdd, 0);
//				((Activity)context).overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left); 
//				break;
			case 2://删除按钮
//				int position=(Integer) v.getTag(Consts.KEY_TAG_ID);
//				projs.remove(position);
//				
//				String type=(String) holder.spType.getSelectedItem();
//				if(null==type)return;
//				holder.types.clear();
//				MyApplication.getApp().saveProjs();
//				notifyDataSetChanged();
				break;
			}
			
		}
		
	};
	
}
