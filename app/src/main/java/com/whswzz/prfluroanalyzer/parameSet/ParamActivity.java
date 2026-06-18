package com.whswzz.prfluroanalyzer.parameSet;

import java.util.LinkedList;
import java.util.List;

import com.whswzz.prfluroanalyzer.base.BaseActivity;
import com.whswzz.prfluroanalyzer.settings.fragment.DatauploadFragment;
import com.zkzk.pra.R;
import com.zkzk.pra.utils.ExceptionHandler;

import android.app.Fragment;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import top.jemen.utils.LogUtil;

public class ParamActivity extends BaseActivity {
    private RadioGroup rgParam;
    private Fragment sourceFragment, usersFragment, speciesFragment, projsFragment, tcLimitsFragment,
            limitsFragment, functionFragment,gbFragment;//normalFragment,wifiFragment,selfInfoFragment,userSetFragment,ethFragment;


    DatauploadFragment dataUploadFragment;

    private Drawable rightDrawable;
    private List<RadioButton> rbs;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_param);
        getWindow().setBackgroundDrawable(null);
        /**Jemen:该飞思卡尔的芯片主频较低并且界面背景图片太大，为减少界面切换的时间，不得已延迟加载一些内容。**/
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                init();
                getFragmentManager().beginTransaction().replace(R.id.fl_set, speciesFragment).commit();
                setListeners();
            }
        }, 100);
    }


    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    private void init() {
        try {
            rgParam = (RadioGroup) findViewById(R.id.rg_setup);
            sourceFragment = new SourcesFragment();
            usersFragment = new UsersFragment();
            speciesFragment = new SpeciesFragment();
            projsFragment = new ProjsFragment();
            limitsFragment = new LimitsFragment();
            tcLimitsFragment = new TCLimitsFragment();
            functionFragment = new FunctionFragment();
            gbFragment = new GBFragment();

            rightDrawable = getResources().getDrawable(R.drawable.arrow);
            rightDrawable.setBounds(0, 0, rightDrawable.getMinimumWidth(), rightDrawable.getMinimumHeight());
            rbs = new LinkedList<>();
            for (int i = 0; i < rgParam.getChildCount(); i++) {
                if (rgParam.getChildAt(i) instanceof RadioButton) {
                    rbs.add((RadioButton) rgParam.getChildAt(i));
                }
            }


        } catch (Exception e) {
            ExceptionHandler.handleException(e);
        }
    }

    private void setListeners() {
        rgParam.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            RadioButton rb;

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                LogUtil.d("checkChanged:" + checkedId);
                for (int i = 0; i < rbs.size(); i++) {    //for wipe the arrow right
                    rb = rbs.get(i);
                    Drawable[] ds = rb.getCompoundDrawables();
                    if (rb.getId() == checkedId) {

                        rb.setCompoundDrawables(ds[0], null, rightDrawable, null);
                    } else {
                        rb.setCompoundDrawables(ds[0], null, null, null);
                    }
                    rb.setPadding(80, 0, 60, 0);
                }
                switch (checkedId) {
                    case R.id.rb_users:
                        getFragmentManager().beginTransaction().replace(R.id.fl_set, usersFragment).commit();
                        break;
                    case R.id.rb_projs:
                        getFragmentManager().beginTransaction().replace(R.id.fl_set, projsFragment).commit();
                        break;
                    case R.id.rb_species:
                        getFragmentManager().beginTransaction().replace(R.id.fl_set, speciesFragment).commit();

                        break;
                    case R.id.rb_sources:
                        getFragmentManager().beginTransaction().replace(R.id.fl_set, sourceFragment).commit();
                        break;
                    case R.id.rb_limits:
                        getFragmentManager().beginTransaction().replace(R.id.fl_set, limitsFragment).commit();
                        break;
                    case R.id.rb_tc_limits:
                        getFragmentManager().beginTransaction().replace(R.id.fl_set, tcLimitsFragment).commit();
                        break;
                    case R.id.rb_function:
                        getFragmentManager().beginTransaction().replace(R.id.fl_set, functionFragment).commit();
                        break;
                    case R.id.rb_guobiao:
                        getFragmentManager().beginTransaction().replace(R.id.fl_set, gbFragment).commit();
                        break;

                }

//                rb.setPadding(80, 0, 15, 0);
            }

        });


    }


}
