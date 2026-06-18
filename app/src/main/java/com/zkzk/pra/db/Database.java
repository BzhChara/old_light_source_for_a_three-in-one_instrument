/*Copyright:Jemen Chen
 * ���ݿ�����ݱ���������������
 */
package com.zkzk.pra.db;

public class Database {

	public static final class Data {

		public static final String TABLE_NAME = "datas";

		public static final class Columns {

			public static final String ID = "_id";
			
			public static final String SN = "_sn";
			
			public static final String TIME= "_time";
			
			public static final String PROJ= "_proj";
			
			public static final String SPECIMEN= "_specimen";

			public static final String CHANNEL_NUM="_channel_num";//通道号
			
			public static final String SOURCE_UNIT="_sourcedUnit";//样品来源单位
			public static final String SOURCE_ADDR="_sourcedAddr";//样品来源地
			public static final String SOURCE_CONTACT="_sourcedContact";//样品来源地联系人
			public static final String SOURCE_PHONE="_sourcedPhone";//样品来源地联系电话
			public static final String SOURCE_ORG_CODE="SOURCE_ORG_CODE";//样品来源地联系电话
			public static final String SOURCE_ORG_TYPE="SOURCE_ORG_TYPE";//样品来源地联系电话
			
			
			public static final String ABSORBANCY= "_absorbancy";//吸光度
			public static final String INHIBITION_RATIO="_inhibitionRatio";
			
			public static final String LIMIT= "_limit";
			
			public static final String RESULT= "_result";
			
			public static final String UPLOADED="_uploaded";
			
			public static final String USER_NAME="_userName";
			public static final String USER_ADDR="_userAddr";
			public static final String USER_CONTACT="_userContact";
			public static final String USER_PHONE="_userPhone";
			
			public static final String USER_OPERATOR="_operator";
			
			public static final String LATITUDE="_latitude";
			
			public static final String LONGITUDE="_longitude";
			
			public static final String DESCRIBE="_describe";
			public static final String USER_CODE = "_code";
			public static final String USER_TOKEN = "_token";
		}

	}

}
