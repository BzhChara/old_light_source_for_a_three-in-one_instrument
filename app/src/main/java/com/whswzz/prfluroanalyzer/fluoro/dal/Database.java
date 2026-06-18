/*Copyright:Jemen Chen
 * ���ݿ�����ݱ���������������
 */
package com.whswzz.prfluroanalyzer.fluoro.dal;

public class Database {

	public static final class CollaurumData {

		public static final String TABLE_NAME = "collaurumDatas";

		public static final class Columns {

			public static final String ID = "_id";
			
			public static final String SN = "_sn";
			
			public static final String CHANNEL = "_channel";
			
			public static final String TIME= "_time";
			
			public static final String PROJ= "_proj";
			
			public static final String SPECIMEN= "_specimen";

			public static final String LIMIT= "_limit";
			
			public static final String RESULT= "_result";
			
			public static final String SOURCE_ORG= "_customer";
			public static final String SOURCE_ORG_CODE= "SOURCE_ORG_CODE";
			public static final String SOURCE_ORG_TYPE= "SOURCE_ORG_TYPE";
			
			public static final String SOURCE_ADDR="_source_addr";
			
			public static final String SOURCE_CONTACT="_source_contact";
			
			public static final String SOURCE_PHONE="_sourece_phone";
			
			
			
			public static final String USER_ORG= "_workOrg";
			public static final String USER_ADDR="_user_addr";
			public static final String USER_CONTACT="_user_contact";
			public static final String USER_PHONE="_user_phone";
			public static final String OPERATOR= "_operator";
			
			
			public static final String VALUES= "_values";	
			public static final String HUMP= "_hump";	
			public static final String T= "_T";	
			public static final String C= "_C";	
			
			public static final String PEAKS= "_peaks";	//电流电压对数组

			public static final String COUNTRY= "_country";	//电流电压对数组
			public static final String PROVINCE= "_province";	//电流电压对数组
			public static final String CITY= "_city";	//电流电压对数组
			public static final String DEVICE_ID= "_deviceId";

			public static final String UPLOADED="_uploaded";

			public static final String CODE = "_code";
			public static final String TOKEN = "_token";
		}

	}
	public static final class EnzymeData {
		
		public static final String TABLE_NAME = "enzymeDatas";
		
		public static final class Columns {
			
			public static final String ID = "_id";
			
			public static final String SN = "_sn";
			public static final String CHANNEL = "_channel";
			public static final String CODE = "_code";
			public static final String TOKEN = "_token";
			
			public static final String TIME= "_time";
			
			public static final String PROJ= "_proj";
			
			public static final String SPECIMEN= "_specimen";
			
			public static final String LIMIT= "_limit";
			
			public static final String RESULT= "_result";
			
			public static final String SOURCE_ORG= "_customer";
			
			public static final String SOURCE_ADDR="_source_addr";
			
			public static final String SOURCE_CONTACT="_source_contact";
			
			public static final String SOURCE_PHONE="_sourece_phone";
			public static final String SOURCE_ORG_CODE= "SOURCE_ORG_CODE";
			public static final String SOURCE_ORG_TYPE= "SOURCE_ORG_TYPE";
			
			
			public static final String USER_ORG= "_workOrg";
			public static final String USER_ADDR="_user_addr";
			public static final String USER_CONTACT="_user_contact";
			public static final String USER_PHONE="_user_phone";
			public static final String OPERATOR= "_operator";
			
			
			public static final String VALUES= "_values";	
			public static final String HUMP= "_hump";	
			public static final String T= "_T";	
			public static final String C= "_C";	
			
			public static final String PEAKS= "_peaks";	//电流电压对数组
			
			public static final String COUNTRY= "_country";	//电流电压对数组
			public static final String PROVINCE= "_province";	//电流电压对数组
			public static final String CITY= "_city";	//电流电压对数组
			public static final String DEVICE_ID= "_deviceId";
			
			public static final String UPLOADED="_uploaded";
			public static final String TEMP="_temp";
			
			
		}
		
	}
	
	
	
	public static final class PhotometerData {

		public static final String TABLE_NAME = "PhotometerDatas";

		public static final class Columns {

			public static final String ID = "_id";
			
			public static final String SN = "_sn";
			
			public static final String CHANNEL = "_channel";
			
			public static final String TIME= "_time";
			
			public static final String PROJ= "_proj";
			
			public static final String SPECIMEN= "_specimen";

			public static final String LIMIT= "_limit";
			
			public static final String RESULT= "_result";
			
			public static final String SOURCE_ORG= "_customer";
			public static final String SOURCE_ORG_CODE= "SOURCE_ORG_CODE";
			public static final String SOURCE_ORG_TYPE= "SOURCE_ORG_TYPE";
			
			public static final String SOURCE_ADDR="_source_addr";
			
			public static final String SOURCE_CONTACT="_source_contact";
			
			public static final String SOURCE_PHONE="_sourece_phone";
			
			
			
			public static final String USER_ORG= "_workOrg";
			public static final String USER_ADDR="_user_addr";
			public static final String USER_CONTACT="_user_contact";
			public static final String USER_PHONE="_user_phone";
			public static final String OPERATOR= "_operator";
			
			
			public static final String VALUES= "_values";	
			public static final String HUMP= "_hump";	
			public static final String T= "_T";	
			public static final String C= "_C";	
			
			public static final String PEAKS= "_peaks";	//电流电压对数组

			public static final String COUNTRY= "_country";	//电流电压对数组
			public static final String PROVINCE= "_province";	//电流电压对数组
			public static final String CITY= "_city";	//电流电压对数组
			public static final String DEVICE_ID= "_deviceId";

			public static final String UPLOADED="_uploaded";

			public static final String CODE = "_code";
			public static final String TOKEN = "_token";

			public static final String ABSORBANCY = "_absorbancy";

			public static final String UNIT = "_unit";
		}

	}

}
