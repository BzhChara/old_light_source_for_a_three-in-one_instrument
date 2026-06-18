package com.whswzz.prfluroanalyzer.settings.fragment;

import java.util.Calendar;
import java.util.Locale;

import com.whswzz.prfluroanalyzer.app.MyApp;
import com.whswzz.prfluroanalyzer.consts.Consts;
import com.zkzk.pra.R;
import com.zkzk.pra.app.Target;
import com.zkzk.pra.utils.SystemDateTime;
import com.zkzk.pra.utils.Tools;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.app.DatePickerDialog.OnDateSetListener;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

public class NormalSetFragment extends PreferenceFragment implements OnClickListener, OnCheckedChangeListener {
	private View root;
//	private DatePicker datePicker;
//	private TimePicker timePicker;
	private TextView tvDate, tvTime;
	private Switch switchLanguage,switchPrint,switchPrtMsg,switchVoice,switchMap;
	DateFormat dateFormat = new DateFormat();
	Calendar calendar=Calendar.getInstance(Tools.getTimeZone());
	private MyApp app;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			root = inflater.inflate(R.layout.fragment_normal_set, null);
		init();
		setListeners();
		return root;
	}

	private void init() {
		app=MyApp.getApp();
		switchLanguage=(Switch) root.findViewById(R.id.switch_language);
		switchPrint=(Switch) root.findViewById(R.id.switch_print);
		switchPrint.setChecked(app.getPrintSet());
		
		
		switchPrtMsg=(Switch) root.findViewById(R.id.switch_print_msg);
		switchPrtMsg.setChecked(app.printDetail());
		
		switchVoice=(Switch) root.findViewById(R.id.switch_voice_guide);
		switchVoice.setChecked(app.getVoiceGuide());
		
		switchMap=(Switch) root.findViewById(R.id.switch_map);
		switchMap.setChecked(Target.mapTag);
		
//		datePicker = (DatePicker) root.findViewById(R.id.dp_set);
//		timePicker = (TimePicker) root.findViewById(R.id.tp_set);
		tvTime = (TextView) root.findViewById(R.id.tv_set_normal_time);
		tvDate = (TextView) root.findViewById(R.id.tv_set_normal_date);
		tvTime.setText(calendar.get(Calendar.HOUR_OF_DAY)+":"+calendar.get(Calendar.MINUTE));
		tvDate.setText(calendar.get(Calendar.YEAR)+"-"+(calendar.get(Calendar.MONTH)+1)+"-"+calendar.get(Calendar.DAY_OF_MONTH)+" ");
//		datePicker.setCalendarViewShown(false);
		if(null==calendar)
			calendar =Calendar.getInstance(Tools.getTimeZone());
		int year = calendar.get(Calendar.YEAR);
		int monthOfYear = calendar.get(Calendar.MONTH);
		int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
		if("zh".equalsIgnoreCase(Locale.getDefault().getLanguage())){	//2018-4-2将equals改为equalsIgnoreCase
			switchLanguage.setChecked(true);
		}else{
			switchLanguage.setChecked(false);
		}
		switchPrint.setChecked(MyApp.getApp().getPrintSet());
		
		
		
		
//		Log.d("jemen","language="+Locale.getDefault().getLanguage());//jemen:中文得到zh
		/*****下面这两个控件的颜色不好设置，目前使用的DatePickerDialog做的。***/
//		datePicker.init(year, monthOfYear, dayOfMonth, new OnDateChangedListener() {
//			public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
//				tvDate.setText(year + "年" + (monthOfYear + 1) + "月" + dayOfMonth + "日");
//			}
//		});
//		timePicker.setOnTimeChangedListener(new OnTimeChangedListener() {
//			public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
//				tvTime.setText(hourOfDay + "时" + minute + "分。");
//			}
//		});
	}

	private void setListeners() {
		switchLanguage.setOnCheckedChangeListener(this);
		switchPrint.setOnCheckedChangeListener(this);
		switchPrtMsg.setOnCheckedChangeListener(this);
		switchVoice.setOnCheckedChangeListener(this);
		switchMap.setOnCheckedChangeListener(this);
		tvTime.setOnClickListener(this);
		tvDate.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_set_normal_date:
			calendar.setTimeInMillis(System.currentTimeMillis());
			new DatePickerDialog(getActivity(), new OnDateSetListener() {
				@Override
				public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
					try {
						tvDate.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth + " ");
						calendar.set(Calendar.YEAR, year);
						calendar.set(Calendar.MONTH, monthOfYear);
						calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
//						SystemClock.setCurrentTimeMillis(calendar.getTimeInMillis());
						SystemDateTime.setDate(year, monthOfYear, dayOfMonth);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE)).show();
			break;
		case R.id.tv_set_normal_time:
			new TimePickerDialog(getActivity(),new OnTimeSetListener() {
				@Override
				public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
					try {
						tvTime.setText(hourOfDay+":"+minute);
						calendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
						calendar.set(Calendar.MINUTE, minute);
						SystemDateTime.setTime(hourOfDay, minute);
//						SystemClock.setCurrentTimeMillis(calendar.getTimeInMillis());

					} catch (Exception e) {
						e.printStackTrace();
					} 
				}
			}, calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),true).show();
			break;
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		switch (buttonView.getId()) {
		case R.id.switch_language:
				app.setLanguage(isChecked);
//				LogUtil.e("locale="+Locale.getDefault().getCountry());
			break;
		case R.id.switch_print:
			app.setPrint(isChecked);
			break;
		case R.id.switch_print_msg:
			app.setPrintMsg(isChecked);
			break;
		case R.id.switch_voice_guide:
			app.setVoiceGuide(isChecked);
			break;
		case R.id.switch_map:
			Target.mapTag=isChecked;
			MyApp.getApp().getPref().edit().putBoolean(Consts.KEY_MAP_TAG, isChecked).apply();
			break;
		}
		
	};
}
