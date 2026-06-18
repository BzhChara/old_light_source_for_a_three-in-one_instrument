package com.whswzz.prfluroanalyzer.consts;

import java.text.SimpleDateFormat;

import android.os.Handler;
import android.os.Looper;

import com.google.gson.Gson;

public class Consts {
	public static final String TAG="jemen";
	public static final int MSG_READ_DATA=2932932;
	public static final int MSG_READ_END=2927432;
	public static final String LOGIN_SUCCEED="com.zkzk.ecas.utils.Consts.LOGIN_SUCCEED";
	public static final String IMAGE_DOWNLOAD="com.zkzk.ecas.utils.Consts.IMAGE_DOWNLOAD";

	public static final String KEY_WIFI="key_wifi";
	public static final String KEY_LANGUAGE="key_language";
	public static final String KEY_SPECIMEN="KEY_SPECIMEN";
	public static final String KEY_SN="KEY_SN";//2.样品编号：SN序列号
	public static final String KEY_PROJ="KEY_PROJ";// proj;//5.检测项目：铅和镉，有机磷农残
	public static final String KEY_LIMIT="KEY+LIMIT";//6.检测限值：
	public static final String KEY_CUSTOMER_ORG="KEY_CUSTOMER_ORG";//8.送检单位：
	public static final String KEY_WORK_ORG="KEY_WORK_ORG";//9.检测单位
	public static final String KEY_OPERATOR="KEY_OPERATOR";//operator;//10.检测人员：
	public static final String KEY_PRINT_SET="KEY_PRINT_SET";
	public static final String KEY_PRINT_MSG="465465	QWERJPKO";
	public static final int REQUST_OK=1;
	public static final int DELETE_DATA_OK=2992;
	public static final int REQUEST_TO_DETECT_SET=782897;
	public static final int REQUEST_TO_RESULT=9216239;
	public static final int MSG_DOWNLOAD_PROGRESS = 41010;
	public static final String YMDHM_FORMAT="yyyy-MM-dd k:mm";//用android。os中的DateFormat将不能显示24小时制，需加aa
	public static final String YMDHMS_FORMAT="yy-MM-dd-k-mm-ss";//用android。os中的DateFormat将不能显示24小时制，需加aa
	public static final SimpleDateFormat mydhmsFromat=new SimpleDateFormat(YMDHMS_FORMAT);
	public static final String DATE_FORMAT="yyyy-MM-dd";
	public static final String TIME_FORMAT="hh:mm";
	public static final String KEY_DATA="KEY_DATA";
	public static final String PROJS_FILE_CN="PROJS_FILE_CN";
	public static final String PROJS_FILE_EN="PROJS_FILE_EN";
	public static final String PROJS_FILE_FLU="PROJS_FILE_FLU";
	
	public static final int KEY_TAG_TYPE=0x7f0b0068;//adapter中添加多个监听时候使用.
	public static final int KEY_TAG_ID=0x7f0b0541;
	
	public static final String ACTION_BROADCAST_PROJS_CHANGED="COM.ZKZK.ECAS.String ACTION_BROADCAST_PROJS_CHANGED";
	
	
	
	//串口读取数据handler中的一些what参数.
	public static final int SERIAL_ERROR=6545462;
	public static final int SERIAL_OPEN_ERROR=654913;
	public static final int SERIAL_IS_USING=641211;
	public static final int TIME_OUT=782799782;
	public static final int CIRCUIT_ERROR=-787558251;
	public static final int SENSITIVITY_CHANGED=-8512423;
	public static final int DETECT_TIME=4645131;
	
	public static final String KEY_V="KEY_V";
	public static final String KEY_I="KEY_I";
	
	public static final String KEY_DATA_FROM_DB="KEY_DATA_FROM_DB289762";
	
	public static final int KEY_ADD_OK=5461251;
	
	public static final String KEY_SAVE_SPECTRUM_VIEW="KEY_SAVE_SPECTRUM_VIEW";
	
	public static final String PR="Pesticide residue";
	public static final String Cr="Cr";
	
	public static final String JEMEN_BATTERY_CHANGED="JEMEN_BATTERY_CHANGED";
	
	public final static int MSG_SHOW_VERSION = 1;
	public final static int MSG_INSTALL_APK = 2;
	public final static int MSG_ERROR = 3;
	public static final int INSTALL_RESULT = 49816312;
	
	public static final String CALIBRATE="CALIBRATE";
	
	public static final String K="k";
	public static final String INCR="INCRE";
	
	
	public static final String VALUES="VALUES";
	public static final String SENSITIVITY="SENSITIVITY";
	public static final int TTS_INIT_SUCCESS = 82237;
	public static final String VOICE_GUIDE = "VOICE_GUIDE";
	public static final String FIRST_ON="FIRST_ON";
	public static final int NOBLOCK=00004000;//	以非阻塞的方式打开串口
	public static final int BLOCK=00000000;//	以非阻塞的方式打开串口
	
	
	
	
/************************************************农残仪开发时增加*************/
	public static final String KEY_CITYCODE="CITYCODE"; 
	
	
	public static final int ABSORBANCY=842102;
	public static final String KEY_DA0="KEY_DA0";
	public static final String KEY_REF="KEY_REF";
	public static final String ACTION_DA0_CHANGED="ACTION_CONTRAST_CHANGED";
	
	
	public static final String KEY_LATITUDE="LATITUDE";
	public static final String KEY_LONGITUDE="LONGITUDE";
	public static final String KEY_DESCRIBE="DESCRIBE";
	public static final String KEY_ID = "KEY_ID";
	public static final String VOLUME = "VOLUME";
	
	
	
	public static final float ALL=0.998F;
	public static final String KEY_MAP_TAG = "KEY_MAP_TAG";
	public static final String ADDR = "ADDR";
	public static final String LOC_TIME = "LOC_TIME";
	public static final String DEBUG = "kpojo0";
	public static final String RELEASED = "jo0asfe";
	
	
	
	public static final String K_Peek = "23dsfS";
	public static final String B_Peek = "uidfS";
	public static final String K_Peek2 = "34gK";
	public static final String B_Peek2 = "DE4s";
	public static final String KIT_CODE = "kphoi";
	public static final String KEY_TOOGLE_DEDUCT = "j9gihuho9i";
	public static final String B = "0u9";
	public static final int EDIT_DATA_OK = 4987321;
	public static final SimpleDateFormat SDFM = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
	public static final SimpleDateFormat SDFMND = new SimpleDateFormat("yyyy年MM月dd日");
	public static final SimpleDateFormat STM = new SimpleDateFormat("kk:mm:ss.SSS");
	public static final SimpleDateFormat SDM = new SimpleDateFormat("yyyy-MM-dd");
	public static final SimpleDateFormat MDKM = new SimpleDateFormat("MM-dd kk:mm");
	public static final String SPECIES_FN = "239874798";
	public static final String LIMITS_FN = "7948615QW2ER";
	public static final String TC_LIMITS_FN = "32R4154";
	public static final String PROJS_FN = "WEQR0I9J45";
	public static final String BATCHMAP_FN = "BATCHMAP_FN";
	public static final String PHOTOMETER_FN = "PHOTOMETER_FN";
	public static final String SOURCES_FILE_NAME = "0U92OJR";
	public static final String ORGANIZATIONS_FILE_NAME = "234saf";
	public static final String SUCCESS = "SUCCESS";
	public static final int TEMP_CHANGE = 987615;
	public static final int REACT_TIME = 984;
	public static final int COLORATE_TIME = 8741;
	
	public static final String BORDERS_FN = "849152A";
	public static final String USER_INDEX = "0u9qwer";
	public static final SimpleDateFormat SNDF=new SimpleDateFormat("yyMMddkkmmss");
	public static final SimpleDateFormat SNDFms=new SimpleDateFormat("yyMMddkkmmssSSS");
	public static final String KEY_UPLOAD_URL = "07989h8u";
	public static final String KEY_LIGHT = "23-I0KPO";
    public static final Gson GSON =new Gson() ;
    public static final String PROJ_INDEX ="PROJ_INDEX" ;
    public static final String KEY_SHOW_ABNORMAL ="4ASDF65" ;
	public static final String KEY_BASE_ABSORBANCE = "qwe456s";


	public static Handler handler = new Handler(Looper.getMainLooper());
	public static final String GB_MAP_FN = "DFJOK4548";


	public static final String KEY_SPECTRUM_K = "KEY_SPECTRUM_K";
	public static final String KEY_SPECTRUM_B = "KEY_SPECTRUM_B";



	
}
