package com.whswzz.prfluroanalyzer.settings.ui;

import com.whswzz.prfluroanalyzer.model.DataModel;
import com.whswzz.prfluroanalyzer.param.Params;
import com.zkzk.pra.R;

import android.app.Dialog;
import android.content.Context;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import top.jemen.ui.BaseDialog;

public class PreviewDialog extends BaseDialog{
	private SeekBar sbLight;
	public PreviewDialog(Context context) {
		super(context,R.style.defaultDialogStyle);
		
	}
	private boolean lightChanged=false;
	protected void onCreate(android.os.Bundle savedInstanceState) {
		setContentView(R.layout.dialog_preview);
		setTitle("调焦");
		sbLight=(SeekBar) findViewById(R.id.sb_light);
		sbLight.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				Params.LIGHT=progress*10;
				DataModel.getInstance().lightOn(Params.LIGHT);
				lightChanged=true;
			}
		});
	};
	@Override
	public void show() {
		super.show();
		DataModel.getInstance().lightOn(Params.LIGHT);
	}
	
	@Override
	public void dismiss() {
		super.dismiss();
		DataModel.getInstance().lightOn(0);
		Params.savaLight();
	}

}
