package com.whswzz.prfluroanalyzer.photometer.data;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.xutils.x;
import org.xutils.common.Callback;
import org.xutils.common.Callback.CancelledException;
import org.xutils.view.annotation.ViewInject;

import com.whswzz.prfluroanalyzer.fluoro.entity.FluData;
import com.whswzz.prfluroanalyzer.photometer.entity.PhotometerData;
import com.zkzk.pra.R;
import com.zkzk.pra.utils.Tools;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import top.jemen.utils.ExceptionHandler;
import top.jemen.utils.LogUtil;
import android.widget.CompoundButton.OnCheckedChangeListener;

/**
 * 适配器
 * @author Jemen Chen
 *
 */
public class PhotometerDataAdapter extends BaseAdapter {
	private List<PhotometerData> datas;
	private LayoutInflater inflater;
	private Calendar mCalendar;
	public PhotometerDataAdapter(Context context, List<PhotometerData> datas) {
		try {
			this.datas = datas;
			this.inflater = LayoutInflater.from(context);	//
			mCalendar=Calendar.getInstance(Tools.getTimeZone());
		}catch(Exception e) {
			ExceptionHandler.handleException(e);
		}
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return datas.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return datas.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_list_data, null);
			holder = new ViewHolder();
			x.view().inject(holder, convertView);
			convertView.setTag(holder);
			holder.cb.setOnCheckedChangeListener(new CheckListene(holder)); //实现监听器的复用。
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		PhotometerData data = datas.get(position);
		holder.tvId.setText(""+data.getId());
		holder.tvChannel.setText(data.getChannel());
		holder.tvSn.setText(data.getSn());
		mCalendar.setTimeInMillis(data.getTime());
		holder.tvTime.setText(DateFormat.format("yy-MM-dd", mCalendar));
		holder.tvName.setText(data.getSpecimen());
		holder.tvProj.setText(data.getProj());
//		LogUtil.d("tvLimit="+holder.tvLimit+",V="+data.getLimit());
		holder.tvResult.setText(""+data.getResult());
		holder.tvCustom.setText(data.getSourceUnit());
		holder.tvWorkOrg.setText(data.getUserOrg());
		holder.tvOperator.setText(data.getOperator());
		holder.cb.setChecked(data.isChecked());
		if(data.isUpLoded()) {
			holder.tvUploaded.setText(R.string.yes);
		}else {
			holder.tvUploaded.setText(R.string.no);
		}
		holder.tvTC.setText(String.format("%.2f", data.getAbsorbancy()));
		holder.position=position;
		
		
		
		return convertView;
	}

	private class ViewHolder {
		@ViewInject(R.id.tv_item_id)
		private TextView tvId;
		
		@ViewInject(R.id.tv_item_channel)
		private TextView tvChannel;
		@ViewInject(R.id.tv_item_sn)
		private TextView tvSn;
		@ViewInject(R.id.tv_time)
		private TextView tvTime;
		@ViewInject(R.id.tv_name)
		private TextView tvName;
		@ViewInject(R.id.tv_proj)
		private TextView tvProj;
		@ViewInject(R.id.tv_inhibit_ratio)
		private TextView tvTC;
		@ViewInject(R.id.tv_result)
		private TextView tvResult;
		@ViewInject(R.id.tv_custom_org)
		private TextView tvCustom;
		@ViewInject(R.id.tv_work_org)
		private TextView tvWorkOrg;
		@ViewInject(R.id.tv_operator)
		private TextView tvOperator;
		@ViewInject(R.id.cb_item_select)
		private CheckBox cb;
		@ViewInject(R.id.tv_uploaded)
		private TextView tvUploaded;
		
		public int position;
	}
	
	
	class CheckListene implements OnCheckedChangeListener{
		private ViewHolder holder;
		public CheckListene(ViewHolder holder) {
			super();
			this.holder = holder;
		}

		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			try {
				datas.get(holder.position).setChecked(isChecked);//?index out of bound?
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
}
