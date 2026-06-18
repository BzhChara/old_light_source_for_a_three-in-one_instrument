package com.whswzz.prfluroanalyzer.settings.ui;

import java.util.LinkedList;
import java.util.List;

import com.whswzz.prfluroanalyzer.fluoro.uvc.CameraTool2;
import com.whswzz.prfluroanalyzer.fluoro.uvc.CameraTool2.CamreaCallback;
import com.whswzz.prfluroanalyzer.model.DataModel;
import com.whswzz.prfluroanalyzer.param.Params;
import com.zkzk.pra.R;
import com.zkzk.pra.utils.ToastUtil;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;
import top.jemen.interfaces.ICallback;
import top.jemen.ui.BaseDialog;
import top.jemen.utils.LogUtil;
import top.jemen.utils.QRCodeUtil;
import top.jemen.utils.threadpool.AsyncProcessor;

public class PicSegmentDialog extends BaseDialog{
	private Context context;
	private ImageView iv;
	private List<EditText> ets=new LinkedList<EditText>();
	private Button bt;
	
	private Canvas canvas;
	private Paint bgPaint;
	private float[][] borders;
	private Spinner spChannel;

	public PicSegmentDialog(Context context) {
		super(context);
		this.context=context;
	}
	private Bitmap bmpx;
	protected void onCreate(android.os.Bundle savedInstanceState) {
		setContentView(R.layout.dialog_segment_pic);
		setTitle("切片参数");
		init();
		setListeners();
		AsyncProcessor.executeTask(new Runnable() {
			@Override
			public void run() {
				CameraTool2.shotX(3, new CamreaCallback() {
		
					@Override
					public void onSuccess(Bitmap[] bmps) {
						 bmpx= bmps[bmps.length - 1];
						int w = bmpx.getWidth(), h = bmpx.getHeight();
						LogUtil.d("w,h:" + w + "," + h);

						 Bitmap bmpBg = Bitmap.createBitmap(w, h, bmpx.getConfig());
						 
						canvas = new Canvas(bmpBg);
					
						iv.setImageBitmap(bmpBg);
						
						
						segment() ;
					}
				});
			}
		});
		
	};
	
	private int channelIndex;
	private void init() {
		iv=(ImageView) findViewById(R.id.iv);
		spChannel=(Spinner) findViewById(R.id.sp_channels);
		bt=(Button) findViewById(R.id.bt);
		initEts(findViewById(R.id.ll_borders));
		borders=Params.getBorders();
		
		bgPaint = new Paint();
		bgPaint.setColor(Color.RED);
		bgPaint.setStyle(Style.STROKE);
		bgPaint.setStrokeWidth(3);
		bgPaint.setAntiAlias(true);
		bgPaint.setTextSize(21);
		
		String[] channels = { "六联卡", "三联卡", "单卡", "十二联卡" };
		ArrayAdapter<String> aad = new ArrayAdapter<>(context, R.layout.my_spinner, channels);
		aad.setDropDownViewResource(R.layout.my_spinner_dropdown);
		spChannel.setAdapter(aad);
		
	}
	
	private void initEts(View root) {
		if(root instanceof ViewGroup) {
			for(int i=0;i<((ViewGroup) root).getChildCount();i++) {
				initEts(((ViewGroup) root).getChildAt(i));
			}
			return;
		}else if(root instanceof EditText) {
			ets.add((EditText) root);
		}
	}
	
	private void setListeners() {
		bt.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				for(int i=0;i<ets.size()&&i<borders[channelIndex].length;i++) {
					String text=ets.get(i).getText().toString();
					if(TextUtils.isEmpty(text)) {
						ToastUtil.showText("请输入正确的参数", Toast.LENGTH_SHORT);
						return;
					}
					float f=Float.parseFloat(text);
					if(f<0.01||f>0.99) {
						ToastUtil.showText("请输入正确的参数", Toast.LENGTH_SHORT);
						return;
					}
					borders[channelIndex][i]=f;
				}
				
				
				Params.saveBorders(borders, new ICallback() {
					@Override
					public void onSuccess(Object obj) {
						ToastUtil.showText("保存成功", Toast.LENGTH_SHORT);
						segment();
					}
					
					@Override
					public void onFailed(Object obj) {
						ToastUtil.showText("保存参数失败", Toast.LENGTH_SHORT);
					}
				});
			}
		});
		spChannel.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				channelIndex=position;
				for(int i=0;i<5;i++) {
					ets.get(i).setText(borders[position][i]+"");
				}
				segment();
			}
		});
	}
	
	
	@Override
	public void show() {
		super.show();
		
	}
	
	@Override
	public void dismiss() {
		super.dismiss();
	}

	
	
	private int sizes[]= {6,3,1,6};
	private void segment() {
		if(null==canvas) {
			return;
		}
		AsyncProcessor.executeTask(new Runnable() {
			@Override
			public void run() {
				canvas.drawBitmap(bmpx, 0, 0, bgPaint); // 必须新建一个画上去，否则不能修改。
				int w = canvas.getWidth();
				int h = canvas.getHeight();
//				float l = 0.19f, t = 0.45f, dx = 0.11f, b = 0.69f,w=0.027; // 六连卡的默认位置
//				float l = 0.2879f, t = 0.4f, dx = 0.083f, b = 0.59f,lw=0.023f; // 六连卡的默认位置
				float l =borders[channelIndex][0], 
						t =borders[channelIndex][1], 
						dx =borders[channelIndex][2], 
						b =borders[channelIndex][3],
						lw=borders[channelIndex][4]; // 六连卡的默认位置
				for (int i = 0; i <sizes[channelIndex]; i++) {
					float left = w * (l + i * dx);
					float top = h * t;
					float right = w * (l + lw + i * dx);
					float bottom = h * b;
					canvas.drawRect(left, top, right, bottom, bgPaint);
				}
				
				
				
				
				
				
				
//				l=w*0.3f;
//				t=h*0.3f;
//				float r = w*0.7f;
//				b=h*0.85f;
//				canvas.drawRect(l,t, r,b, bgPaint);
//				Bitmap bmp=Bitmap.createBitmap(bmpx,(int)l, (int)t,(int)(r-l)	,(int)(b-t));
//
//				String msg=QRCodeUtil.simpleDecode(bmp);
//
//				canvas.drawText("识别："+msg, w*0.3f, h*0.9f, bgPaint);
//
//				iv.post(new Runnable() {
//					@Override
//					public void run() {
//						iv.invalidate();
//					}
//				});
			}
		});
	}
	
}
