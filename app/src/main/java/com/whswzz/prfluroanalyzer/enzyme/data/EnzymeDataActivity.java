package com.whswzz.prfluroanalyzer.enzyme.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

import org.w3c.dom.ls.LSResourceResolver;
import org.xutils.ex.DbException;

import com.example.pickerviewlibrary.picker.TeaPickerView;
import com.example.pickerviewlibrary.picker.entity.PickerData;
import com.example.pickerviewlibrary.picker.listener.OnPickerClickListener;
import com.google.gson.Gson;
import com.whswzz.prfluroanalyzer.app.MyApp;
import com.whswzz.prfluroanalyzer.base.BaseActivity;
import com.whswzz.prfluroanalyzer.consts.Consts;
import com.whswzz.prfluroanalyzer.entity.IData;
import com.whswzz.prfluroanalyzer.entity.Species;
import com.whswzz.prfluroanalyzer.enzyme.EnzymeSpecimenDialog;
import com.whswzz.prfluroanalyzer.enzyme.entity.EnzymeData;
import com.whswzz.prfluroanalyzer.fluoro.dal.Database;
import com.whswzz.prfluroanalyzer.fluoro.dal.imp.XDao;
import com.whswzz.prfluroanalyzer.fluoro.data.FluDataActivity;
import com.whswzz.prfluroanalyzer.fluoro.data.ResultActivity;
import com.whswzz.prfluroanalyzer.fluoro.entity.FluData;
import com.whswzz.prfluroanalyzer.model.HttpModel;
import com.whswzz.prfluroanalyzer.utils.PrinterJPW;
import com.zkzk.pra.R;
import com.zkzk.pra.model.imp.FileModel;
import com.zkzk.pra.model.imp.NetModel;
import com.zkzk.pra.receiver.UsbReceiver;
import com.zkzk.pra.ui.MyDialog;
import com.zkzk.pra.ui.ProDialog;
import com.zkzk.pra.ui.SpecimenDialog;
import com.zkzk.pra.ui.PswDialog;
import com.zkzk.pra.ui.QRCodeDialog;
import com.zkzk.pra.utils.ExceptionHandler;
import com.zkzk.pra.utils.ListUtil;
import com.zkzk.pra.utils.TTS;
import com.zkzk.pra.utils.ToastUtil;
import com.zkzk.pra.utils.Tools;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import top.jemen.interfaces.ICallback;
import top.jemen.ui.OnCustomDialogListener;
import top.jemen.utils.LogUtil;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 检测结果查询展示的类，此处暂留一个问题，即尚未作分页查询处理，当处理大量数据是应该做一下。
 * @author Jemen Chen
 */
public class EnzymeDataActivity extends BaseActivity implements OnClickListener, OnItemLongClickListener, OnCheckedChangeListener {
	private Button btQuery, btUpload, btPrint,btInput,btOutput;
	private TextView tvStartTime, tvEndTime,tvTotal,tvQRCode;
	private Calendar startCalendar, endCalendar;
	private Spinner spResult;
	private EditText etType, etTargetUnit;
	private CheckBox cbAll,cbAllPage;

	private TextView tvPages, tvCurrent;
	private ImageButton ibFirst, ibLast, ibNext, ibEnd,ibType;
	List<String> projs;
	private String proj, type;
	private long startTime, endTime;
	private ListView lvData;
	private List<EnzymeData> datas; // 用于展示的。
	private List<EnzymeData> allDatas;
	private DataAdapter dataAdapter;

	private static final int TPP = 10;// 每页显示的数据条数
	private int page = 0;
	private Dialog qrDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pr_data);
		init();
		registerForContextMenu(lvData);
		setSpinner();
		setAdapter();
		setListeners();
		// query(null, null);


		String sql="select * from "+Database.EnzymeData.TABLE_NAME
//				+" where "+Database.EnzymeData.Columns.TIME + " > "+
//				startCalendar.getTimeInMillis()+" and "+Database.EnzymeData.Columns.TIME+" < "+endCalendar.getTimeInMillis()
				+" order by "+Database.EnzymeData.Columns.TIME+" DESC LIMIT 50;";
		query(sql);
	}
	
	@Override
	protected void onDestroy() {
		if(null!=qrDialog&&qrDialog.isShowing()) {
			qrDialog.cancel();
		}
		super.onDestroy();
	}

	
	private void init() {
		try {
			btQuery = (Button) findViewById(R.id.bt_query);
			btUpload = (Button) findViewById(R.id.bt_upload);
			btInput=(Button) findViewById(R.id.bt_input);
			btOutput=(Button) findViewById(R.id.bt_output);
			btPrint = (Button) findViewById(R.id.bt_print);
			spResult=(Spinner) findViewById(R.id.spinner_result);
			etType = (EditText) findViewById(R.id.et_type);
			etTargetUnit = (EditText) findViewById(R.id.et_target_org);
			etType.clearFocus();
			lvData = (ListView) findViewById(R.id.lv_data);
			tvStartTime = (TextView) findViewById(R.id.tv_data_start_time);
			tvEndTime = (TextView) findViewById(R.id.tv_data_end_time);
			tvTotal=(TextView) findViewById(R.id.tv_total);
			tvTotal.setText(getString(R.string.total_num_)+MyApp.getApp().getDataNum());
			startCalendar = Calendar.getInstance(Tools.getTimeZone());
			startCalendar.setTimeInMillis(System.currentTimeMillis()-1000*60*60*24*10);
			int year=startCalendar.get(Calendar.YEAR);
			int month=startCalendar.get(Calendar.MONTH);
			int day=startCalendar.get(Calendar.DAY_OF_MONTH);
			startCalendar.set(year, month, day, 0, 0, 0);
			endCalendar = Calendar.getInstance(Tools.getTimeZone());
			tvStartTime.setText(DateFormat.format(Consts.DATE_FORMAT, startCalendar));
			tvEndTime.setText(DateFormat.format(Consts.DATE_FORMAT, endCalendar));
			tvPages = (TextView) findViewById(R.id.tv_pages);
			ibFirst = (ImageButton) findViewById(R.id.ib_to_first);
			ibLast = (ImageButton) findViewById(R.id.ib_last);
			ibNext = (ImageButton) findViewById(R.id.ib_next);
			ibEnd = (ImageButton) findViewById(R.id.ib_to_end);
			cbAll = (CheckBox) findViewById(R.id.cb_select_all);
			cbAllPage=(CheckBox) findViewById(R.id.cb_select_all_page);
			tvCurrent = (TextView) findViewById(R.id.tv_current_page);
			if (MyApp.getApp().isTtsOk()) {
				TTS.stop();
				TTS.speak(getResources().getString(R.string.specimen_input_notice));
			}
			tvQRCode=(TextView) findViewById(R.id.tv_show_qr);
			ibType=(ImageButton) findViewById(R.id.ib_type);
			
			findViewById(R.id.tv_inhibit_rate).setVisibility(View.GONE);
		} catch (NotFoundException e) {
			ExceptionHandler.handleException(e);
		}
	}

	private void setAdapter() {
		datas = new LinkedList<EnzymeData>(); // 根据使用情况
		allDatas = new ArrayList<EnzymeData>();
		dataAdapter = new DataAdapter(this, datas);
		lvData.setAdapter(dataAdapter);
	}

	private void setListeners() {
		btQuery.setOnClickListener(this);
		btUpload.setOnClickListener(this);
		btInput.setOnClickListener(this);
		btOutput.setOnClickListener(this);
		btPrint.setOnClickListener(this);
		tvStartTime.setOnClickListener(this);
		tvEndTime.setOnClickListener(this);
		ibFirst.setOnClickListener(this);
		ibLast.setOnClickListener(this);
		ibNext.setOnClickListener(this);
		ibEnd.setOnClickListener(this);
		ibType.setOnClickListener(this);
		lvData.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				operatePosition = position;
				showResult(datas.get(position));
			}
		});
		lvData.setOnItemLongClickListener(this);
		cbAll.setOnCheckedChangeListener(this);
		cbAllPage.setOnCheckedChangeListener(this);
		tvQRCode.setOnClickListener(this);
	}

	private int operatePosition;

	private void showResult(EnzymeData data) {
		Intent intent = new Intent(this, e.b.c.a.f.class);
//		intent.putExtra(Consts.KEY_ID, data.getId()); //进去了查询完整数据。
		intent.putExtra(Consts.KEY_DATA_FROM_DB, true);
		intent.putExtra(Consts.KEY_DATA, data);
		startActivityForResult(intent, Consts.REQUEST_TO_RESULT);
		overrideAnim();
		// 斟酌使用对话框还是新界面。
//		new EnzymeSpecimenDialog(this, data,null).show();

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		switch (requestCode) {
		case Consts.REQUEST_TO_RESULT:
			if (resultCode == Consts.DELETE_DATA_OK) {
				datas.remove(operatePosition);
				dataAdapter.notifyDataSetChanged();
			}else if(resultCode==Consts.EDIT_DATA_OK) {
				EnzymeData data =  (EnzymeData) intent.getSerializableExtra(Consts.KEY_DATA);
				datas.remove(operatePosition);
				datas.add(operatePosition, data);
				dataAdapter.notifyDataSetChanged();
			}
			break;
		}
	}

	int size;
	private TeaPickerView speciesPickerView;

	@Override
	public void onClick(View v) {
		List<Species> lsSpecies;
		switch (v.getId()) {
		case R.id.bt_upload:
			// Toast.makeText(this, "进行数据上传逻辑", Toast.LENGTH_SHORT).show();
			if (null == datas || datas.size() <= 0) {
				// ToastUtil.showText( "没有数据上传", Toast.LENGTH_SHORT).show();
				ToastUtil.showText(R.string.no_data_to_upload, Toast.LENGTH_SHORT);
				return;
			}
			
			List<IData> uploads=new ArrayList<IData>();
			if(cbAllPage.isChecked()) {
				uploads.addAll(allDatas);
			}else {
				for(EnzymeData d:datas) {	//只上传当前页面选定的还是上传所有页面选定的？
					if(d.isChecked())
						uploads.add(d);
				}
			}
			btUpload.setEnabled(false);


//			NetModel netMode = NetModel.getModel();

//			netMode.uploadClient(uploads, new ICallback() {
			HttpModel.get().send( uploads, new ICallback() {
				@Override
				public void onSuccess(Object obj) {
					ToastUtil.showText(R.string.data_upload_succed, Toast.LENGTH_SHORT);
					if (null != btUpload)
						btUpload.setEnabled(true);
					if(null==qrDialog) {
						qrDialog=new QRCodeDialog(EnzymeDataActivity.this, null, Tools.getQRURL());
					}
					qrDialog.show();
					
				}
				@Override
				public void onFailed(Object obj) {
					ToastUtil.showText(getString(R.string.data_upload_failed) + "," + (String) obj, Toast.LENGTH_SHORT);
					if (null != btUpload)
						btUpload.setEnabled(true);
				}
			});
			break;
		case R.id.bt_query:
			type = etType.getText().toString();
			String sql="select * from "+Database.EnzymeData.TABLE_NAME+" where "+Database.EnzymeData.Columns.TIME + " > "+
					startCalendar.getTimeInMillis()+" and "+Database.EnzymeData.Columns.TIME+" < "+endCalendar.getTimeInMillis();
			if(!TextUtils.isEmpty(type)) {
				sql+=" and "+Database.EnzymeData.Columns.SPECIMEN+" = \'"+type+"\'";
			}
			String sourceUnit = etTargetUnit.getText().toString();
			if(!TextUtils.isEmpty(sourceUnit)) {
				sql+=" and "+Database.EnzymeData.Columns.SOURCE_ORG+" = \'"+sourceUnit+"\'";
			}
			if(!TextUtils.isEmpty(result)) {
				sql+=" and "+Database.EnzymeData.Columns.RESULT+" = \'"+result+"\'";
			}
			sql+=";";		
			
			query(sql);
			
			
			break;
		case R.id.tv_data_start_time:
			new DatePickerDialog(this, new OnDateSetListener() {
				@Override
				public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
					startCalendar.set(Calendar.YEAR, year);
					startCalendar.set(Calendar.MONTH, monthOfYear);
					startCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
					tvStartTime.setText(DateFormat.format(Consts.DATE_FORMAT, startCalendar));
				}
			}, startCalendar.get(Calendar.YEAR), startCalendar.get(Calendar.MONTH), startCalendar.get(Calendar.DAY_OF_MONTH)).show();
			break;
		case R.id.tv_data_end_time:
			new DatePickerDialog(this, new OnDateSetListener() {
				@Override
				public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
					endCalendar.set(Calendar.YEAR, year);
					endCalendar.set(Calendar.MONTH, monthOfYear);
					endCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
					tvEndTime.setText(DateFormat.format(Consts.DATE_FORMAT, endCalendar));
				}
			}, endCalendar.get(Calendar.YEAR), endCalendar.get(Calendar.MONTH), endCalendar.get(Calendar.DAY_OF_MONTH))
					.show();
			break;
		case R.id.ib_to_first:
			if (page <= 1)
				return;
			datas.clear();
			page = 1;
			if (size > TPP)
				datas.addAll(allDatas.subList(0, TPP));
			else
				datas.addAll(allDatas.subList(0, size));
			changeCb(datas);
			dataAdapter.notifyDataSetChanged();
			tvCurrent.setText("1");
			break;
		case R.id.ib_last:
			if (page <= 1)
				return;
			datas.clear();
			page--;
			datas.addAll(allDatas.subList((page - 1) * TPP, page * TPP));
			changeCb(datas);
			dataAdapter.notifyDataSetChanged();
			tvCurrent.setText("" + page);
			break;

		case R.id.ib_next:
			if (page >= (size - 1) / TPP + 1)
				return;
			datas.clear();
			page++;
			datas.addAll(allDatas.subList((page - 1) * TPP, page * TPP > size ? size : page * TPP));
			changeCb(datas);
			dataAdapter.notifyDataSetChanged();
			tvCurrent.setText("" + page);
			break;
		case R.id.ib_to_end:
			if (page >= (size - 1) / TPP + 1)
				return;
			datas.clear();
			page = (size - 1) / TPP + 1;
			datas.addAll(allDatas.subList(size / TPP * TPP, size));
			changeCb(datas);
			dataAdapter.notifyDataSetChanged();
			tvCurrent.setText("" + ((size - 1) / TPP + 1));
			break;
		case R.id.bt_print:
			final List<IData>prints=new ArrayList<>();
			if(cbAllPage.isChecked()) {
				prints.addAll(allDatas);
			}else {
				for(EnzymeData d:datas) {
					if(d.isChecked())
						prints.add(d);
				}
			}
			if(prints.size()==0) {
				ToastUtil.showText("请选择需要打印的通道", Toast.LENGTH_SHORT);
				return;
			}
			final String[] items = {"打印常规结果","打印承诺达标合格证（农产品生产者）","打印承诺达标合格证（农产品收购单位/个人）"};
			AlertDialog.Builder listDialog=new Builder(this);
			listDialog.setTitle("请选择打印类型");
			listDialog.setItems(items, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					btPrint.setEnabled(false);
					if(which==0) {
						PrinterJPW.print(prints, new ICallback() { // 此函数内部会开启新的线程以适配打印机的速度以防打印机缓存爆掉。
							@Override
							public void onSuccess(Object obj) {
								btPrint.setEnabled(true);
							}

							@Override
							public void onFailed(Object obj) {
								btPrint.setEnabled(true);
							}
						});
					}else if (which==1){
						PrinterJPW.printCert(prints, new ICallback() { // 此函数内部会开启新的线程以适配打印机的速度以防打印机缓存爆掉。
							@Override
							public void onSuccess(Object obj) {
								btPrint.setEnabled(true);
							}

							@Override
							public void onFailed(Object obj) {
								btPrint.setEnabled(true);
							}
						});
					}else {
						PrinterJPW.printCertPerson(prints, new ICallback() { // 此函数内部会开启新的线程以适配打印机的速度以防打印机缓存爆掉。
							@Override
							public void onSuccess(Object obj) {
								btPrint.setEnabled(true);
							}

							@Override
							public void onFailed(Object obj) {
								btPrint.setEnabled(true);
							}
						});
					}
				}
			});
			listDialog.show();
			break;
		case R.id.bt_output:
			List<EnzymeData> outputDatas=null;
			if(cbAllPage.isChecked()) {
				outputDatas=allDatas;
			}else {
				outputDatas=new ArrayList<EnzymeData>();
				for(EnzymeData d:allDatas) {
					if(d.isChecked())
						outputDatas.add(d);
				}
			}
			if (null == outputDatas || outputDatas.size() <= 0) {
				ToastUtil.showText(R.string.no_data_to_output, Toast.LENGTH_SHORT);
				return;
			}
			if (UsbReceiver.udiskExist) {
				final ProDialog dialog = new ProDialog(null, this, R.string.alert,
						R.string.do_not_pull_out_usb_storage);
				dialog.show();
				btOutput.setEnabled(false);
				FileModel.saveEnzymeUdisk(outputDatas, new ICallback() {
					@Override
					public void onSuccess(Object obj) {
						ToastUtil.showText(R.string.data_export_success, Toast.LENGTH_SHORT);
						dialog.cancel();
						btOutput.setEnabled(true);
					}

					@Override
					public void onFailed(Object obj) {
						LogUtil.e("save failed:"+obj);
						ToastUtil.showText(R.string.data_export_faild, Toast.LENGTH_SHORT);
						dialog.dismiss();
						btOutput.setEnabled(true);
					}
				});
			} else {
				ToastUtil.showText(R.string.please_connect_usb_storage_first, Toast.LENGTH_SHORT);
			}
			
			break;
		case R.id.tv_show_qr:
			if(null==qrDialog) {
				qrDialog=new QRCodeDialog(EnzymeDataActivity.this, null, Tools.getQRURL());
			}
			qrDialog.show();
			break;
		case R.id.ib_type:
			lsSpecies=MyApp.getApp().getLsSpecies();
			if (null == speciesPickerView) {
				List<String> kinds = new ArrayList<>();
				Map<String, List<String>> map = new HashMap<>();
				for (Species sp : lsSpecies) {
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
				speciesPickerView = new TeaPickerView(this, data);
				speciesPickerView.setScreenH(3).setDiscolourHook(true).setRadius(25).setContentLine(true).setRadius(25)
						.build();
				// 选择器点击事件
				speciesPickerView.setOnPickerClickListener(new OnPickerClickListener() {
					@Override
					public void OnPickerClick(PickerData pickerData) {
						Toast.makeText(EnzymeDataActivity.this, pickerData.getFirstText() + ":" + pickerData.getSecondText() + ","
								+ pickerData.getThirdText(), Toast.LENGTH_SHORT).show();
						type=pickerData.getSecondText();
						etType.setText(type);
						speciesPickerView.dismiss();// 关闭选择器
					}
				});
			}
			speciesPickerView.showAsDropDown(v, 0, 0);
			break;
			default:
				super.onClick(v);
		}
	}
	
	private void changeCb(List<EnzymeData> datas) {
		int n=0;
		for(EnzymeData d:datas) {
			if(cbAllPage.isChecked()) {
				d.setChecked(true);
				n++;
			}else {
				if(d.isChecked())
					n++;
			}
		}
		if(n>=datas.size()) {
			cbAll.setChecked(true);
		}else if(n==0) {
			cbAll.setChecked(false);
		}
	}
	
	
	
	private String result;
	void setSpinner() {
		final String[] results= {"","阴性","阳性"};
		ArrayAdapter<String> resultAdapter = new ArrayAdapter<String>(this, R.layout.my_spinner, results);
		resultAdapter.setDropDownViewResource(R.layout.my_spinner_dropdown);
		// 绑定 Adapter到控件
		spResult.setAdapter(resultAdapter);
		spResult.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				try {
					result=results[pos];
				} catch (Exception e) {
					ExceptionHandler.handleException(e);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});

	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * ContextMenu的菜单项：编辑
	 */
	private static final int MENU_ITEM_EDIT = 1;
	/**
	 * ContextMenu的菜单项：删除
	 */
	private static final int MENU_ITEM_DELETE = 2;
	/**
	 * ContextMenu的菜单项：删除全部数据(目前的实现方式时删除整个表，以使id从新开始计数);
	 */
	private static final int MENU_ITEM_DELETE_TABLE = 3;
	/**
	 * 执行编辑或删除操作时的操作数据
	 */

	private EnzymeData actionData;

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		// 获取操作AdapterView时的ContextMenuInfo
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
		int actionPosition = info.position;
		// 获取对应的学生记录
		actionData = datas.get(actionPosition);
		// 创建菜单项
		if (MyApp.getApp().getLanguage()) { // 中文
			menu.add(Menu.NONE, MENU_ITEM_EDIT, Menu.NONE, "编辑ID号为 " + actionData.getId() + " 的记录");
			menu.add(Menu.NONE, MENU_ITEM_DELETE, Menu.NONE, "删除ID号为  " + actionData.getId() + " 的记录");
		} else { // 英文
			menu.add(Menu.NONE, MENU_ITEM_EDIT, Menu.NONE,
					getString(R.string.edit_the_record_that_id_is) + actionData.getId());
			menu.add(Menu.NONE, MENU_ITEM_DELETE, Menu.NONE,
					getString(R.string.delete_the_record_that_id_is) + actionData.getId());
		}
		menu.add(Menu.NONE, MENU_ITEM_DELETE_TABLE, Menu.NONE, getString(R.string.delete_all_data));
		menu.setHeaderTitle(getString(R.string.Select_appropriate_action));
		super.onCreateContextMenu(menu, v, menuInfo);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_ITEM_EDIT:
			// 激活RecordFormActivity
			// Intent intent = new Intent(this, RecordEditActivity.class);
			// intent.putExtra("id", actionData.getId());
			// startActivity(intent);
			Toast.makeText(this, R.string.not_allowed_to_edit, Toast.LENGTH_SHORT).show();
			new MyDialog(this, new MyDialog.BackListener() {
				@Override
				public void back() {
					// toSet();
				}

			}, R.string.notice, R.string.no_right_to_modify_detect_result).show();
			;
			break;

		case MENU_ITEM_DELETE:
			// 弹出删除对话框
			new MyDialog(this, new MyDialog.BackListener() {
				@Override
				public void back() {
					try {
						XDao.getDb().delete(actionData);
						datas.remove(actionData);
						allDatas.remove(actionData);
						size = allDatas.size();

						dataAdapter.notifyDataSetChanged();
					} catch (DbException e) {
						e.printStackTrace();
						// 删除失败
						ToastUtil.showText(R.string.delete_record_failed, Toast.LENGTH_SHORT);
					}
				}

			}, R.string.notice, R.string.ensure_to_delete).show();
			break;
		case MENU_ITEM_DELETE_TABLE:
			new MyDialog(this, new MyDialog.BackListener() {
				@Override
				public void back() {
					PswDialog d = new PswDialog(EnzymeDataActivity.this, new PswDialog.OnCustomDialogListener() {
						@Override
						public void back(String str) {
							if(null!=str&&("0000".equals(str)||str.contains("scsw"))) {
								try {
									XDao.getDb().dropTable(actionData.getClass());
									datas.clear();
									allDatas.clear();
									size = allDatas.size();
									dataAdapter.notifyDataSetChanged();
								} catch (DbException e) {
									e.printStackTrace();
									// 删除失败
									ToastUtil.showText(R.string.delete_record_failed, Toast.LENGTH_SHORT);
								}
								
							}
						}
					}, getString(R.string.administrator),getString(R.string.ensure));
					d.show();
					d.setPswHint("默认管理密码：0000");
					
					
					
				}
			}, R.string.notice, R.string.ensure_to_delete).show();
			;

			break;
		}
		return super.onContextItemSelected(item);
	}

	
	
	@SuppressLint("StaticFieldLeak")
	private void query(final String sql) {
		btQuery.setEnabled(false);
		new AsyncTask<Void, Integer, Boolean>() {
			@Override
			protected Boolean doInBackground(Void... params) {
				allDatas.clear();
				
				XDao.queryEnzyme(sql, allDatas);
				return true;
			}

			@Override
			protected void onPostExecute(Boolean result) {
				try {
					btQuery.setEnabled(true);
					size = allDatas.size();
					if (MyApp.getApp().getLanguage())//有空了换xml描述。
						tvPages.setText("共" + ((size - 1) / TPP + 1) + "页");
					else
						tvPages.setText("Total pages " + ((size - 1) / TPP + 1));

					datas.clear();
					// LogUtil.d("size="+size+",TPP="+TPP);
					if (size > TPP)
						datas.addAll(allDatas.subList(0, TPP));
					else
						datas.addAll(allDatas.subList(0, size));
					dataAdapter.notifyDataSetChanged();
					tvCurrent.setText("" + page);
				} catch (Exception e) {
					ExceptionHandler.handleException(e);
				}
			}

		}.executeOnExecutor( AsyncTask.THREAD_POOL_EXECUTOR );
		
		
	}
	

	private void overrideAnim() {
		overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
	}


	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		switch(buttonView.getId()) {
		case R.id.cb_select_all:
			for (EnzymeData d : datas)
				d.setChecked(isChecked);
			dataAdapter.notifyDataSetChanged();
			break;
		case R.id.cb_select_all_page:
			if(isChecked) {	//仅针对选定情况作出处理。
				for (EnzymeData d : allDatas)
					d.setChecked(isChecked);
				dataAdapter.notifyDataSetChanged();
			}
			break;
		}
		
	}
}
