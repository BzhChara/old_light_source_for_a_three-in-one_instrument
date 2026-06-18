package com.whswzz.prfluroanalyzer.photometer.data;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.example.pickerviewlibrary.picker.TeaPickerView;
import com.example.pickerviewlibrary.picker.entity.PickerData;
import com.example.pickerviewlibrary.picker.listener.OnPickerClickListener;
import com.google.gson.Gson;
import com.whswzz.prfluroanalyzer.app.MyApp;
import com.whswzz.prfluroanalyzer.base.BaseActivity;
import com.whswzz.prfluroanalyzer.consts.Consts;
import com.whswzz.prfluroanalyzer.entity.IData;
import com.whswzz.prfluroanalyzer.entity.Species;
import com.whswzz.prfluroanalyzer.fluoro.dal.Database;
import com.whswzz.prfluroanalyzer.fluoro.dal.imp.XDao;
import com.whswzz.prfluroanalyzer.photometer.entity.PhotometerData;
import com.whswzz.prfluroanalyzer.utils.PrinterJPW;
import com.zkzk.pra.R;
import com.zkzk.pra.model.imp.FileModel;
import com.zkzk.pra.model.imp.NetModel;
import com.zkzk.pra.receiver.UsbReceiver;
import com.zkzk.pra.ui.ProDialog;
import com.zkzk.pra.utils.ListUtil;
import com.zkzk.pra.utils.ToastUtil;
import com.zkzk.pra.utils.Tools;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
//import android.webkit.WebView.PrivateAccess;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import top.jemen.interfaces.ICallback;
import top.jemen.ui.DateDialogFragment;
import top.jemen.ui.MyDialogFragment;
import top.jemen.ui.OnCustomDialogListener;
import top.jemen.utils.ExceptionHandler;
import top.jemen.utils.LogUtil;

/**
 * 数据量大的话最后动态加载,有空的时候改一下。
 * 
 * @author JemenChen
 */
public class PhotometerDataActivity extends BaseActivity implements OnItemLongClickListener, OnCheckedChangeListener, OnClickListener {
	private Button btQuery, btUpload, btOutput,btPrint;
	private TextView tvStartTime, tvEndTime;
	private Calendar startCalendar, endCalendar;
	private EditText etType;
	private TextView tvPages, tvCurrent, tvTotal;
	private ImageButton ibFirst, ibLast, ibNext, ibEnd,ibType;
	private CheckBox cbAll,cbAllPage;
	
	List<String> projs;
	private String proj, type;
	private long startTime, endTime;
	private ListView lvData;
	private List<PhotometerData> datas;
	private List<PhotometerData> allDatas;
	private PhotometerDataAdapter dataAdapter;

	private static final int TPP = 10;// 每页显示的数据条数
	private int page = 1;
	private Gson gson;
	
	private TextView tvProj;
	
	private List<Species> lsProjs=MyApp.getApp().getLsProjs();
	private List<Species> lsSpecies=MyApp.getApp().getLsSpecies();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_flu_data);
		init();
		setAdapter();
		setListeners();
		registerForContextMenu(lvData);
		String sql="select * from "+Database.PhotometerData.TABLE_NAME+" where "+Database.PhotometerData.Columns.TIME + " > "+
				startCalendar.getTimeInMillis()+" and "+Database.PhotometerData.Columns.TIME+" < "+endCalendar.getTimeInMillis()
				+" order by "+Database.PhotometerData.Columns.TIME+" DESC LIMIT 50;";
		query(sql);
	}

	
	
	
	private void init() {
		btQuery = (Button) findViewById(R.id.bt_query);
		btUpload = (Button) findViewById(R.id.bt_upload);
		btOutput = (Button) findViewById(R.id.bt_output);
		tvProj=(TextView) findViewById(R.id.tv_proj);
		
		
		ibType=(ImageButton) findViewById(R.id.ib_type);
		etType = (EditText) findViewById(R.id.et_type);
		etType.clearFocus();
		lvData = (ListView) findViewById(R.id.lv_data);
		tvStartTime = (TextView) findViewById(R.id.tv_data_start_time);
		tvEndTime = (TextView) findViewById(R.id.tv_data_end_time);
		startCalendar = Calendar.getInstance(Tools.getTimeZone());
		int month=startCalendar.get(Calendar.MONTH);
		if(month>=2) {
			startCalendar.set(Calendar.MONTH, month-2);
		}else {
			startCalendar.set(Calendar.YEAR, startCalendar.get(Calendar.YEAR)-1);
			startCalendar.set(Calendar.MONTH, month+10);
		}
		startCalendar.set(Calendar.HOUR_OF_DAY, 0);
		startCalendar.set(Calendar.MINUTE, 0);
		endCalendar = Calendar.getInstance(Tools.getTimeZone());

		tvStartTime.setText(DateFormat.format(Consts.DATE_FORMAT, startCalendar));
		tvEndTime.setText(DateFormat.format(Consts.DATE_FORMAT, endCalendar));
		tvPages = (TextView) findViewById(R.id.tv_pages);
		ibFirst = (ImageButton) findViewById(R.id.ib_to_first);
		ibLast = (ImageButton) findViewById(R.id.ib_last);
		ibNext = (ImageButton) findViewById(R.id.ib_next);
		ibEnd = (ImageButton) findViewById(R.id.ib_to_end);
		tvCurrent = (TextView) findViewById(R.id.tv_current_page);
		
		
		tvTotal=(TextView) findViewById(R.id.tv_total);
		
//		tvTotal.setText(getString(R.string.total_num_)+MyApp.getApp().getDataNum());
		
		cbAll = (CheckBox) findViewById(R.id.cb_select_all);
		cbAllPage=(CheckBox) findViewById(R.id.cb_select_all_page);
		btPrint=(Button) findViewById(R.id.bt_print);
		
		TextView tvInhibition=(TextView) findViewById(R.id.tv_inhibit_rate);
		tvInhibition.setText("吸光度");
	}

	private void setAdapter() {
		datas = new LinkedList<PhotometerData>(); // 根据使用情况
		allDatas = new ArrayList<PhotometerData>();
		dataAdapter = new PhotometerDataAdapter(this, datas);
		lvData.setAdapter(dataAdapter);
	}

	private void setListeners() {
		btQuery.setOnClickListener(this);
		btUpload.setOnClickListener(this);
		btPrint.setOnClickListener(this);
		tvProj.setOnClickListener(this);
		ibType.setOnClickListener(this);
		
		if (!Tools.isSMDK()) { // 迅为板不适用
			btOutput.setOnClickListener(this);
		}else {
			btOutput.setVisibility(View.GONE);
		}
		tvStartTime.setOnClickListener(this);
		tvEndTime.setOnClickListener(this);

		ibFirst.setOnClickListener(this);
		ibLast.setOnClickListener(this);
		ibNext.setOnClickListener(this);
		ibEnd.setOnClickListener(this);
		lvData.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				operatePosition = position;
				showResult(datas.get(position));
			}
		});
		
		lvData.setOnItemLongClickListener(this);
		cbAll.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				for (PhotometerData d : datas)
					d.setChecked(isChecked);
				dataAdapter.notifyDataSetChanged();
			}
		});
		cbAllPage.setOnCheckedChangeListener(this);
	}

	private int operatePosition;
	
	
	private ArrayAdapter<String> typeAdapter;

	private void showResult(PhotometerData data) {
		Intent intent = new Intent(this, e.b.c.a.f.class);
//		intent.putExtra(Consts.KEY_ID, data.getId()); //进去了查询完整数据。
		intent.putExtra(Consts.KEY_DATA_FROM_DB, true);
		intent.putExtra(Consts.KEY_DATA, data);
//		LogUtil.d("fa data=" + data.hashCode());
		startActivityForResult(intent, Consts.REQUEST_TO_RESULT);
		overrideAnim();
	}

	
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		switch (resultCode) {
		case Consts.DELETE_DATA_OK:
			datas.remove(operatePosition);
			break;
		case Consts.EDIT_DATA_OK:
			PhotometerData data = (PhotometerData) intent.getSerializableExtra(Consts.KEY_DATA);
//			LogUtil.d("返回data=" + data.hashCode());  //这俩不是同一个对象
			datas.remove(operatePosition);
			datas.add(operatePosition, data);
			
//			MyApp.getApp().updateData(data);
			break;
		}

		dataAdapter.notifyDataSetChanged();
	}

	int size;
	private TeaPickerView teaPickerView,speciesPickerView;

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_upload:
			// Toast.makeText(this, "进行数据上传逻辑", Toast.LENGTH_SHORT).show();
			if (null == allDatas || allDatas.size() <= 0) {
				// ToastUtil.showText( "没有数据上传", Toast.LENGTH_SHORT).show();
				ToastUtil.showText(R.string.no_data_to_upload, Toast.LENGTH_SHORT);
				return;
			}
			if (null == gson)
				gson = new Gson();
			// LogUtil.d("datas="+gson.toJson(allDatas)); //预计将使用json格式.
			btUpload.setEnabled(false);
			NetModel netMode = NetModel.getModel();
			List<PhotometerData> checks=null;
			if(cbAllPage.isChecked()) {
				checks=allDatas;
			}else {
				checks=new ArrayList<PhotometerData>();
				for(PhotometerData d:datas) {
					if(d.isChecked())
						checks.add(d);
				}
			}
			
//			netMode.uploadClient(checks, new ICallback() {
//				@Override
//				public void onSuccess(Object obj) {
//					ToastUtil.showText(R.string.data_upload_succed, Toast.LENGTH_SHORT);
//					if (null != btUpload)
//						btUpload.setEnabled(true);
//				}
//				@Override
//				public void onFailed(Object obj) {
//					ToastUtil.showText(getString(R.string.data_upload_failed) + "," + (String) obj, Toast.LENGTH_SHORT);
//					if (null != btUpload)
//						btUpload.setEnabled(true);
//				}
//			});
			break;
		case R.id.bt_query:
			type = etType.getText().toString();
			String sql="select * from "+Database.PhotometerData.TABLE_NAME+" where "+Database.PhotometerData.Columns.TIME + " > "+
					startCalendar.getTimeInMillis()+" and "+Database.PhotometerData.Columns.TIME+" < "+endCalendar.getTimeInMillis();
			if(!TextUtils.isEmpty(type)) {
				sql+=" and "+Database.PhotometerData.Columns.SPECIMEN+" = \'"+type+"\'";
			}
			if(!TextUtils.isEmpty(proj)) {
				sql+=" and "+Database.PhotometerData.Columns.PROJ+" = \'"+proj+"\'";
			}
			
			sql+=";";		
			
			query(sql);
			break;
		case R.id.bt_output: // 导出到Upan
			if (null == allDatas || allDatas.size() <= 0) {
				ToastUtil.showText("没有数据需要导出", Toast.LENGTH_SHORT);
				return;
			}
			if(cbAllPage.isChecked()) {
				checks=allDatas;
			}else {
				checks=new ArrayList<PhotometerData>();
				for(PhotometerData d:datas) {
					if(d.isChecked())
						checks.add(d);
				}
			}
			
			if (UsbReceiver.udiskExist) {
				final ProDialog dialog = new ProDialog(null, this, R.string.alert,
						R.string.do_not_pull_out_usb_storage);
				dialog.show();
				btOutput.setEnabled(false);
				FileModel.savePhotometerUdisk(allDatas, new ICallback() {
					@Override
					public void onSuccess(Object obj) {
						ToastUtil.showText(R.string.data_export_success, Toast.LENGTH_SHORT);
						dialog.cancel();
						btOutput.setEnabled(true);
					}

					@Override
					public void onFailed(Object obj) {
						ToastUtil.showText(R.string.data_export_faild, Toast.LENGTH_SHORT);
						dialog.dismiss();
						btOutput.setEnabled(true);
					}
				});
			} else {
				ToastUtil.showText(R.string.please_connect_usb_storage_first, Toast.LENGTH_SHORT);
			}
			break;
		case R.id.tv_data_start_time:
			new DateDialogFragment(startCalendar, new OnDateSetListener() {
				@Override
				public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
					startCalendar.set(Calendar.YEAR, year);
					startCalendar.set(Calendar.MONTH, month);
					startCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
					tvStartTime.setText(DateFormat.format(Consts.DATE_FORMAT, startCalendar));
				}
			}).show(getFragmentManager(), "date_end");
//			new DatePickerDialog(this, new OnDateSetListener() {
//				@Override
//				public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
//					startCalendar.set(Calendar.YEAR, year);
//					startCalendar.set(Calendar.MONTH, monthOfYear);
//					startCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
//					tvStartTime.setText(DateFormat.format(Consts.DATE_FORMAT, startCalendar));
//				}
//			}, startCalendar.get(Calendar.YEAR),startCalendar.get(Calendar.MONTH),startCalendar.get(Calendar.DAY_OF_MONTH)).show();
			break;
		case R.id.tv_data_end_time:
			new DateDialogFragment(endCalendar, new OnDateSetListener() {
				@Override
				public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
					endCalendar.set(Calendar.YEAR, year);
					endCalendar.set(Calendar.MONTH, month);
					endCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
					tvEndTime.setText(DateFormat.format(Consts.DATE_FORMAT, endCalendar));
				}
			}).show(getFragmentManager(), "date_end");
//			new DatePickerDialog(this, new OnDateSetListener() {
//				@Override
//				public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
//					endCalendar.set(Calendar.YEAR, year);
//					endCalendar.set(Calendar.MONTH, monthOfYear);
//					endCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
//					tvEndTime.setText(DateFormat.format(Consts.DATE_FORMAT, endCalendar));
//				}
//			}, endCalendar.get(Calendar.YEAR), endCalendar.get(Calendar.MONTH), endCalendar.get(Calendar.DAY_OF_MONTH))
//					.show();
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
			dataAdapter.notifyDataSetChanged();
			tvCurrent.setText("1");
			break;
		case R.id.ib_last:
			if (page <= 1)
				return;
			datas.clear();
			page--;
			datas.addAll(allDatas.subList((page - 1) * TPP, page * TPP));
			dataAdapter.notifyDataSetChanged();
			tvCurrent.setText("" + page);
			break;

		case R.id.ib_next:
			if (page >= (size - 1) / TPP + 1)
				return;
			datas.clear();
			page++;
			datas.addAll(allDatas.subList((page - 1) * TPP, page * TPP > size ? size : page * TPP));
			dataAdapter.notifyDataSetChanged();
			tvCurrent.setText("" + page);
			break;
		case R.id.ib_to_end:
			if (page >= (size - 1) / TPP + 1)
				return;
			datas.clear();
			page = (size - 1) / TPP + 1;
			datas.addAll(allDatas.subList((page - 1) * TPP, size));
			dataAdapter.notifyDataSetChanged();
			tvCurrent.setText("" + ((size - 1) / TPP + 1));
			break;
		case R.id.bt_print:
			final List<IData> prints=new ArrayList<>();
			if(cbAllPage.isChecked()) {
				prints.addAll(allDatas);
			}else {
				for(PhotometerData d:datas) {
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
		case R.id.tv_proj:
			if (lsProjs == null) {
				return;
			}
			if (null == teaPickerView) {
				List<String> kinds = new ArrayList<>();
				Map<String, List<String>> map = new HashMap<>();
				for (Species sp : lsProjs) {
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
				teaPickerView = new TeaPickerView(this, data);
				teaPickerView.setScreenH(3).setDiscolourHook(true).setRadius(25).setContentLine(true).setRadius(25)
						.build();
				// 选择器点击事件
				teaPickerView.setOnPickerClickListener(new OnPickerClickListener() {
					@Override
					public void OnPickerClick(PickerData pickerData) {
						Toast.makeText(PhotometerDataActivity.this, pickerData.getFirstText() + ":" + pickerData.getSecondText() + ","
								+ pickerData.getThirdText(), Toast.LENGTH_SHORT).show();
						proj=pickerData.getSecondText();
						tvProj.setText(proj);
						teaPickerView.dismiss();// 关闭选择器
					}
				});
			}
			LogUtil.d("teaPicker="+teaPickerView+",  v="+v);
			teaPickerView.showAsDropDown(v, 0, 0);
			break;
		case R.id.ib_type:
			if (lsSpecies == null) {
				return;
			}
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
						Toast.makeText(PhotometerDataActivity.this, pickerData.getFirstText() + ":" + pickerData.getSecondText() + ","
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

			
	

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		
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

	private PhotometerData actionData;

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		// 获取操作AdapterView时的ContextMenuInfo
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
		int actionPosition = info.position;
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
			Toast.makeText(this, R.string.not_allowed_to_edit, Toast.LENGTH_SHORT).show();
//			new MyDialog(this, new OnCustomDialogListener() {
//				@Override
//				public void back() {
//					// toSet();
//				}
//
//				@Override
//				public void cancel() {
//					// TODO Auto-generated method stub
//
//				}
//			}, R.string.notice, R.string.no_right_to_modify_detect_result).show();

			new MyDialogFragment(new OnCustomDialogListener() {
				@Override
				public void back() {
					// toSet();
				}
				@Override
				public void cancel() {
				}
			}, R.string.notice, R.string.no_right_to_modify_detect_result).show(getFragmentManager(), "date_delete_all");
			
			
			break;

		case MENU_ITEM_DELETE:
			// 弹出删除对话框
			
			new MyDialogFragment(new OnCustomDialogListener() {
				@Override
				public void back() {
					int affectedRows = XDao.deletePhotometerData(actionData.getId());

					// 提示操作结果
					if (affectedRows > 0) {
						// 删除成功
						// records.clear();
						// records.addAll(dao.query(null, null));
						datas.remove(actionData);
						allDatas.remove(actionData);
						size = allDatas.size();
						dataAdapter.notifyDataSetChanged();
					} else {
						// 删除失败
						ToastUtil.showText(R.string.delete_record_failed, Toast.LENGTH_SHORT);
					}
				}

				@Override
				public void cancel() {
					// TODO Auto-generated method stub

				}
			}, R.string.notice, R.string.ensure_to_delete).show(getFragmentManager(), "date_delete_one");
			
			
			break;
		case MENU_ITEM_DELETE_TABLE:
			new MyDialogFragment(new OnCustomDialogListener() {
				@Override
				public void back() {
					if (XDao.deleteAll(PhotometerData.class)) {
						datas.clear();
						allDatas.clear();
						size = allDatas.size();
						dataAdapter.notifyDataSetChanged();
					} else {
						// 删除失败
						ToastUtil.showText(R.string.delete_record_failed, Toast.LENGTH_SHORT);
					}
				}
			}, R.string.notice, R.string.ensure_to_delete).show(getFragmentManager(), "date_delete_table");

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
				XDao.queryPhotometer(sql, allDatas);
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
			for (PhotometerData d : datas)
				d.setChecked(isChecked);
			dataAdapter.notifyDataSetChanged();
			break;
		case R.id.cb_select_all_page:
//			if(isChecked) {	//仅针对选定情况作出处理。
				for (PhotometerData d : allDatas)
					d.setChecked(isChecked);
				dataAdapter.notifyDataSetChanged();
//			}
			break;
		}
		
	}
}
