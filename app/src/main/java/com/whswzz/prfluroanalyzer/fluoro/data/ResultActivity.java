package com.whswzz.prfluroanalyzer.fluoro.data;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.Map;

import org.xutils.ex.DbException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.whswzz.prfluroanalyzer.app.MyApp;
import com.whswzz.prfluroanalyzer.base.BaseActivity;
import com.whswzz.prfluroanalyzer.consts.Consts;
import com.whswzz.prfluroanalyzer.entity.IData;
import com.whswzz.prfluroanalyzer.enzyme.entity.EnzymeData;
import com.whswzz.prfluroanalyzer.fluoro.dal.Database;
import com.whswzz.prfluroanalyzer.fluoro.dal.imp.XDao;
import com.whswzz.prfluroanalyzer.fluoro.entity.FluData;
import com.whswzz.prfluroanalyzer.model.HttpModel;
import com.whswzz.prfluroanalyzer.param.Params;
import com.whswzz.prfluroanalyzer.photometer.entity.PhotometerData;
import com.whswzz.prfluroanalyzer.utils.LimitUnitUtil;
import com.whswzz.prfluroanalyzer.utils.PrinterJPW;
import com.zkzk.pra.R;
import com.zkzk.pra.entity.Data;
import com.zkzk.pra.model.imp.NetModel;
import com.zkzk.pra.ui.QRCodeDialog;
import com.zkzk.pra.utils.ToastUtil;
import com.zkzk.pra.utils.Tools;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.print.PrintHelper;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.format.DateFormat;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import top.jemen.interfaces.ICallback;
import top.jemen.utils.ExceptionHandler;
import top.jemen.utils.LogUtil;
import top.jemen.utils.QRCodeUtil;
import top.jemen.utils.StringTool;

@SuppressLint("SimpleDateFormat")
public class ResultActivity extends BaseActivity implements OnClickListener {
	private TextView tvSn, tvSpecimen, tvLimit, tvCustomer, tvOperator, tvTime, tvProj, tvResult, tvWorkOrg;
	private Button btPrint, btDelete, btUpload, btOriginal;
	private ImageView ivCode;
	private Resources resources;
	private IData data;
	public static final String COLON = "：";
	private boolean inDatabase;
	private TextView tvAddr,tvInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_result);
		init();
		setDisplay();
		setListeners();
	}

	private void init() {
		try {
			tvSn = (TextView) findViewById(R.id.tv_result_sn);
			tvSpecimen = (TextView) findViewById(R.id.tv_result_name);
			tvLimit = (TextView) findViewById(R.id.tv_result_limit);
			tvCustomer = (TextView) findViewById(R.id.tv_result_customer);
			tvOperator = (TextView) findViewById(R.id.tv_result_operator);
			tvTime = (TextView) findViewById(R.id.tv_result_time);
			tvProj = (TextView) findViewById(R.id.tv_result_proj);
			tvResult = (TextView) findViewById(R.id.tv_result_result);
			tvWorkOrg = (TextView) findViewById(R.id.tv_result_work_org);
			btPrint = (Button) findViewById(R.id.bt_result_print);
			btDelete = (Button) findViewById(R.id.bt_result_delete);
			btUpload = (Button) findViewById(R.id.bt_result_upload);
			
			btOriginal = (Button) findViewById(R.id.bt_result_original);
			ivCode = (ImageView) findViewById(R.id.iv_code);
			tvAddr=(TextView) findViewById(R.id.tv_result_addr);
			tvInfo=(TextView) findViewById(R.id.tv_result_info);
			
			
			resources = getResources();
//			inDatabase = getIntent().getBooleanExtra(Consts.KEY_DATA_FROM_DB, false);
			data = (IData) getIntent().getSerializableExtra(Consts.KEY_DATA);
			LogUtil.d("data="+data.hashCode());
//			data=XDao.findById(getIntent().getIntExtra(Consts.KEY_ID,0)); // 
		} catch (Exception e) {
			ExceptionHandler.handleException(e);
		}

	}

	private void setDisplay() {
		try {
			if(null==data) {
				LogUtil.e("data is null");
			}
			
			LogUtil.d("tvsn="+tvSn+",data="+data);
			
			tvSn.setText(resources.getString(R.string.specimen_sn) + COLON + data.getSn());
			
			tvSpecimen.setText(resources.getString(R.string.specimen_name) + COLON + data.getSpecimen());
			tvLimit.setText(resources.getString(R.string.reference_limit) + COLON + getReferenceLimitText());

			tvCustomer.setText(resources.getString(R.string.customer_org) + COLON + data.getSourceUnit());
			tvOperator.setText(resources.getString(R.string.operator) + COLON + data.getOperator());
			Calendar calendar = Calendar.getInstance(Tools.getTimeZone());
			calendar.setTimeInMillis(data.getTime());
			tvTime.setText(resources.getString(R.string.detect_time) + COLON
					+ DateFormat.format(Consts.YMDHM_FORMAT, calendar));

			tvProj.setText(resources.getString(R.string.detect_proj) + COLON + data.getProj());
			tvWorkOrg.setText(resources.getString(R.string.detect_org) + COLON + data.getUserName());
			tvAddr.setText("样品产地："+data.getSourceAddr());
			if(data instanceof Data) {
				tvInfo.setText("抑制率："+(int)(((Data)data).getInhibitionRatio()* 100) + "%");
			}else if(data instanceof FluData) {
				FluData fd=(FluData) data;
				tvInfo.setText("T/C："+String.format("%.3f", fd.getT()/fd.getC()));
			}
			
			
			String result = data.getResult();

			String text = resources.getString(R.string.detect_result) + COLON;
			text += result;
			ivCode.setVisibility(View.GONE);
			SpannableString style = new SpannableString(text);
			for (int i = 0; i < text.length() - 4;) {
				int start = text.indexOf(getString(R.string._oos_), i);
				if (start < 0) {
					start = text.indexOf(getString(R.string._suspect_), i);
				}
				if (start > 0) {
					style.setSpan(new ForegroundColorSpan(Color.RED), start, start + 4,
							Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
					i = start + 4;
				} else {
					break;
				}
			}
			
			
			tvResult.setText(style);
			if (ivCode.getVisibility() == View.VISIBLE) {
				GsonBuilder builder = new GsonBuilder();
				builder.excludeFieldsWithoutExposeAnnotation();
				Gson gson = builder.create();
				String json = gson.toJson(data);
				LogUtil.d("拟显示的二维码内容:" + json);
				sText = StringTool.encrypt(json);
				ivCode.post(new Runnable() {
					@Override
					public void run() {
						Bitmap code = QRCodeUtil.getCodeBitmap(sText, ivCode.getWidth(), ivCode.getHeight());
						if (null != code) {
							ivCode.setImageBitmap(code);
							ivCode.setBackgroundColor(Color.CYAN);
						}
					}
				});
			}

		} catch (NotFoundException e) {
			ExceptionHandler.handleException(e);
		}

	}

	private String getReferenceLimitText() {
		if(data instanceof PhotometerData) {
			return "";
		}
		if (!(data instanceof FluData)) { //分光农残和酶片式酶抑制率
			return "50%抑制率";
		}

		String key = data.getProj() + "-" + data.getSpecimen();
		LogUtil.d(key);
		Map<String, Double> limits = MyApp.getApp().getLimits();
		if(null!=limits) {
			Double v = limits.get(key);
			if(null!=v) {
				return LimitUnitUtil.formatLimit(v, MyApp.getApp().getLimitUnits().get(key));
			}
		}

		if(data instanceof FluData) {
			return LimitUnitUtil.formatLimit(((FluData) data).getLimit(), null);
		}
		return "";
	}

	private String sText;

	private void setListeners() {
		btPrint.setOnClickListener(this);
		btDelete.setOnClickListener(this);
		btUpload.setOnClickListener(this);
		btOriginal.setOnClickListener(this);

		tvWorkOrg.setOnClickListener(this);
		tvOperator.setOnClickListener(this);
		tvCustomer.setOnClickListener(this);
		if (!inDatabase) // 不允许更改数据库中的样品编号。
			tvSn.setOnClickListener(this);
		ivCode.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_result_print:
			PrinterJPW.showPrintChoice(this,data, (Button) v);

			break;
		case R.id.bt_result_delete:
			AlertDialog.Builder builder = new Builder(this);
			builder.setTitle(R.string.alert).setMessage(R.string.msg_delete)
					.setPositiveButton(R.string.ok, new Dialog.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if (inDatabase) {
								// TODO
							} else {
								// TODO
							}
							Intent intent = new Intent();
							setResult(Consts.DELETE_DATA_OK, intent);
							ResultActivity.this.finish();
						}
					}).setNegativeButton(R.string.cancel, null);
			originalDialog = builder.create();
			originalDialog.show();
			break;
		case R.id.bt_result_upload:
			LogUtil.d("执行上传操作");
			btUpload.setEnabled(false);
			HttpModel.get().send(data,new ICallback() {
				@Override
				public void onSuccess(Object obj) {
					ToastUtil.showText(R.string.data_upload_succed, Toast.LENGTH_SHORT);
					btUpload.setEnabled(true);	
					edited=true;
					if(data instanceof FluData) {
						((FluData)data).setUpLoded(true);
						try {
							XDao.getDb().update(data, Database.CollaurumData.Columns.UPLOADED);
						} catch (DbException e) {
							e.printStackTrace();
						}
					}else if(data instanceof EnzymeData) {
						((EnzymeData)data).setUpLoded(true);
						try {
							XDao.getDb().update(data, Database.CollaurumData.Columns.UPLOADED);
						} catch (DbException e) {
							e.printStackTrace();
						}
					}else {
						Data d=(Data) data;
						d.setUploaded(true);
						MyApp.getApp().getDataDb().setUpload(d.getId(), true);
					}
				}
				
				@Override
				public void onFailed(Object obj) {
					ToastUtil.showText(getString(R.string.data_upload_failed) + "," + (String) obj, Toast.LENGTH_SHORT);
					btUpload.setEnabled(true);
				}
			});
			
			
			break;

		case R.id.bt_result_original:
			// List<Value> values=data.getValues();
			// if(values!=null) {
			// Intent toShow=new Intent(this, DataShowActivity.class);
			// toShow.putExtra(Consts.VALUES, (Serializable)values);
			// toShow.putExtra(Consts.SENSITIVITY, 5); //注意，一旦自动调节灵敏度再次启用，此处也必须处理。
			// startActivity(toShow);
			// overrideAnim();
			// }
			if(data instanceof FluData) {
				new OriginalDialog(this, (FluData)data).show();
			}
			break;


		case R.id.tv_result_work_org:
		case R.id.tv_result_sn:
		case R.id.tv_result_operator:
		case R.id.tv_result_customer:
//			edit(v);
			break;

		case R.id.ib_title_back:
		case R.id.bt_title_home:
		case R.id.ib_bottom_home:
		case R.id.ib_bottom_back:
			if (edited) {
				Intent intent = new Intent();
				intent.putExtra(Consts.KEY_DATA, data);
				setResult(Consts.EDIT_DATA_OK, intent);
				
			}
			ResultActivity.this.finish();
			break;
		case R.id.iv_code:
			new QRCodeDialog(this,"", sText).show();
			break;
		default:
			super.onClick(v);
		}

	}

//	private void edit(final View v) {
//		final TextView tv = (TextView) v;
//		String s = tv.getText().toString();
//		final String[] ss = s.split(COLON);
//		EditDialog dialog = new EditDialog(this, new EditDialog.EditListener() {
//			@Override
//			public void back(String text) {
//				tv.setText(ss[0] + COLON + text);
//				switch (v.getId()) {
//				case R.id.tv_result_work_org:
//					data.setWorkOrg(text);
//					break;
//				case R.id.tv_result_sn:
//					data.setSn(text);
//					break;
//				case R.id.tv_result_operator:
//					data.setOperator(text);
//					break;
//				case R.id.tv_result_customer:
//					data.setCustomerOrg(text);
//					break;
//				}
//				edited = true;
//				LogUtil.d(data.toString());
//			}
//		}, getString(R.string.edit), ss[0]);
//		dialog.show();
//		if (ss.length >= 2 && null != ss[1]) {
//			dialog.setEdit(ss[1]);
//		}
//
//	}

	private void overrideAnim() {
		overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
	}

	private boolean edited = false;

	private Dialog originalDialog;

	@Override
	protected void onDestroy() {
		if (edited) {
			LogUtil.d("edited");
			Intent intent = new Intent();
			intent.putExtra(Consts.KEY_DATA, data);
			setResult(Consts.EDIT_DATA_OK, intent);
			ResultActivity.this.finish();
		}
		if (null != originalDialog) {
			originalDialog.cancel();
		}
		super.onDestroy();
		// MyApplication.getApp().dataToBt=null;

	}

}
