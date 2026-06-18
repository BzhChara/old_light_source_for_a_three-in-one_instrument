//package com.zkzk.pra.fragment;
//
//import org.xutils.x;
//import org.xutils.view.annotation.ViewInject;
//
//import com.whswzz.prfluroanalyzer.app.MyApp;
//import com.zkzk.pra.R;
//import com.zkzk.pra.consts.Consts;
//import com.zkzk.pra.utils.ExceptionHandler;
//
//import android.app.Fragment;
//import android.content.SharedPreferences;
//import android.content.SharedPreferences.Editor;
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.Toast;
//
//public class UserSetFragment extends Fragment {
//	private View root;
//	@ViewInject(R.id.et_set_org)
//	private EditText etOrg;
//	@ViewInject(R.id.et_set_operator)
//	private EditText etOperator;
//	@ViewInject(R.id.bt_user_ok)
//	private Button btOk;
//
//	private String org, operator;
//
//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//		root = inflater.inflate(R.layout.fragment_user, null);
//		x.view().inject(this, root);
//		init();
//		setListeners();
//		return root;
//	}
//
//	private void setListeners() {
//		btOk.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				onOkClicked();
//			}
//		});
//	}
//
//	private void init() {
//		SharedPreferences pref = MyApp.getApp().getPref();
//		org = pref.getString(Consts.KEY_WORK_ORG, "");
//		operator = pref.getString(Consts.KEY_OPERATOR, "");
//		if(!"".equals(org))
//			etOrg.setText(org);
//		if(!"".equals(operator))
//			etOperator.setText(operator);
//	}
//
//	/**
//	 */
//	public void onOkClicked() {
//		try {
//			org = etOrg.getText().toString();
//			operator = etOperator.getText().toString();
//			Editor editer = MyApp.getApp().getPref().edit();
//		
//			if (null != org && !"".equals(org)) {
//				editer.putString(Consts.KEY_WORK_ORG, org);
//			}
//			if (null != operator && !"".equals(operator))
//				editer.putString(Consts.KEY_OPERATOR, operator);
//
//			if (editer.commit()) {
//				Toast.makeText(getActivity(), "用户信息设置成功", Toast.LENGTH_SHORT).show();
//			}
//		} catch (Exception e) {
//			ExceptionHandler.handleException(e);
//		}
//	}
//
//	@Override
//	public void onStop() {
//		super.onStop();
//	}
//}
