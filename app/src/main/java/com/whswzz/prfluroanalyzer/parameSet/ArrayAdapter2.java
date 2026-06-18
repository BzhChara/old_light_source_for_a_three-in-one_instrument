package com.whswzz.prfluroanalyzer.parameSet;


import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class ArrayAdapter2<IConcat, T> extends ArrayAdapter<T>{
	public ArrayAdapter2(Context context, int textViewResourceId, List<IConcat> objects) {
		super(context, textViewResourceId);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		return super.getView(position, convertView, parent);
	}

	
}
