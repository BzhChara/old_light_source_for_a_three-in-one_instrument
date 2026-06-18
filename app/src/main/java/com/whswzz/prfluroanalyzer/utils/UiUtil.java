package com.whswzz.prfluroanalyzer.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.pickerviewlibrary.picker.TeaPickerView;
import com.example.pickerviewlibrary.picker.entity.PickerData;
import com.example.pickerviewlibrary.picker.listener.OnPickerClickListener;
import com.whswzz.prfluroanalyzer.entity.Species;
import com.zkzk.pra.utils.ListUtil;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import top.jemen.interfaces.ICallback;
import top.jemen.utils.LogUtil;

public class UiUtil {
	public static void show2Pickview(final TextView tv, List<Species> species) {
		 show2Pickview(tv,species,null);
	}


	public static void show2Pickview(final TextView tv,List<Species> species, ICallback callback) {
		if(null==species) {
			return;
		}
		List<String> kinds = new ArrayList<>();
		Map<String, List<String>> map = new HashMap<>();
		for (Species sp : species) {
			String name = sp.getName();
			kinds.add(name);
			if (null != sp.getSubSpecies()) {
				List<String> subNames = ListUtil.getRootNames(sp.getSubSpecies());
				map.put(name, subNames);
			}
		}
		// 设置数据有多少层级
		PickerData data = new PickerData();
		data.setFirstDatas(kinds);
		data.setSecondDatas(map);
		data.setInitSelectText("请选择");
		final TeaPickerView specimenPickerView = new TeaPickerView((Activity) tv.getContext(), data);
		specimenPickerView.setScreenH(3).setDiscolourHook(true).setRadius(25).setContentLine(true).setRadius(25)
				.build();
		// 选择器点击事件
		specimenPickerView.setOnPickerClickListener(new OnPickerClickListener() {
			@Override
			public void OnPickerClick(PickerData pickerData) {
				Toast.makeText(tv.getContext(),
						pickerData.getFirstText() + ":" + pickerData.getSecondText() + "," + pickerData.getThirdText(),
						Toast.LENGTH_SHORT).show();
				tv.setText(pickerData.getSecondText());
				specimenPickerView.dismiss();// 关闭选择器
				if(null!=callback){
					callback.onSuccess(pickerData.getSecondText());
				}
			}
		});
		specimenPickerView.showAsDropDown(tv, 0, 0);
	}


	public static void show1Pickview(final TextView tv, List<String> ls, ICallback callback) {
		if(null==ls||null==tv) {
			return;
		}
		// 设置数据有多少层级
		PickerData data = new PickerData();
		data.setFirstDatas(ls);
		data.setInitSelectText("请选择");
		final TeaPickerView specimenPickerView = new TeaPickerView((Activity) tv.getContext(), data);
		specimenPickerView.setScreenH(3).setDiscolourHook(true).setRadius(25).setContentLine(true)
				.build();
		// 选择器点击事件
		specimenPickerView.setOnPickerClickListener(new OnPickerClickListener() {
			@Override
			public void OnPickerClick(PickerData pickerData) {
				if(tv instanceof TextView)
					tv.setText(pickerData.getFirstText());
				specimenPickerView.dismiss();// 关闭选择器
				if(null!=callback){
					callback.onSuccess(pickerData.getFirstDatas());
				}
			}
		});
		specimenPickerView.showAsDropDown(tv, 0, 0);
	}
}
