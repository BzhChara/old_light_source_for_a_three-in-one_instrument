/*Copyright:Jemen Chen
 * ���ݿⴴ����������ص���
 */
package com.zkzk.pra.db;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import top.jemen.utils.LogUtil;
import android.database.sqlite.SQLiteOpenHelper;

public class DBOpenHelper extends SQLiteOpenHelper {
	//为了速度查询时并未动态获取顺序，所以列的顺序不可变。
	String createDatas = "CREATE TABLE " + Database.Data.TABLE_NAME + " ("
			+ Database.Data.Columns.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ Database.Data.Columns.SN + " VARCHAR(20) , "
			+ Database.Data.Columns.TIME + " INTEGER, "
			+ Database.Data.Columns.PROJ+ " TEXT,"
			+ Database.Data.Columns.SPECIMEN+ " TEXT,"
			+ Database.Data.Columns.CHANNEL_NUM+ " TEXT,"
			+ Database.Data.Columns.SOURCE_UNIT+ " TEXT,"
			+ Database.Data.Columns.SOURCE_ADDR+ " TEXT,"
			+ Database.Data.Columns.SOURCE_CONTACT+ " TEXT,"
			+ Database.Data.Columns.SOURCE_PHONE+ " TEXT,"
			+ Database.Data.Columns.ABSORBANCY+ "  Decimal (5,3) default 0,"
			+ Database.Data.Columns.INHIBITION_RATIO+ " Decimal (5,3) default 0,"
			+ Database.Data.Columns.LIMIT+ " TEXT,"
			+ Database.Data.Columns.RESULT+ " TEXT,"
			+ Database.Data.Columns.UPLOADED+ " BIT,"
			+Database.Data.Columns.USER_NAME+" TEXT,"
			+Database.Data.Columns.USER_ADDR+" TEXT,"
			+Database.Data.Columns.USER_CONTACT+" TEXT,"
			+Database.Data.Columns.USER_PHONE+" TEXT,"
			
			+Database.Data.Columns.USER_OPERATOR+" TEXT,"
			+Database.Data.Columns.LATITUDE+" Decimal (9,6) default 30.485267,"
			+Database.Data.Columns.LONGITUDE+" Decimal (9,6) default 114.276161,"
			+Database.Data.Columns.DESCRIBE+" TEXT,"
			+Database.Data.Columns.USER_CODE+" TEXT,"
			+Database.Data.Columns.USER_TOKEN+" TEXT,"
			+Database.Data.Columns.SOURCE_ORG_CODE+" TEXT,"
			+Database.Data.Columns.SOURCE_ORG_TYPE+" INTEGER"
			+ ")";
	public DBOpenHelper(Context context) {
		super(context, "pra.db", null, 1);
	}


	@Override
	public void onCreate(SQLiteDatabase db) {
		LogUtil.d("创建数据库");
		// 创建该数据库的语句
		db.execSQL(createDatas); // execute sql
	 	//创建索引 
	 	db.execSQL("create index if not exists ia on "+Database.Data.TABLE_NAME+"("
	 			+ Database.Data.Columns.ID + " , "
	 			+ Database.Data.Columns.PROJ + " , "
				+ Database.Data.Columns.TIME + ") ");
	}

	//数据库升级版本
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		LogUtil.d("数据库升级版本,oldVersion="+oldVersion+",newVersion="+newVersion);
		switch(oldVersion){		//jemen:数据库升级
		case 1:

			break;
		case 2:
			
			break;
		case 3:
			
			break;
		}
		
	}
	
	//数据库降版本
	@Override
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		LogUtil.d("数据库降版本,oldVersion="+oldVersion+",newVersion="+newVersion);
//		super.onDowngrade(db, oldVersion, newVersion);
	}
	
	
	public boolean reCreateDatas() {
		try {
			SQLiteDatabase db = getReadableDatabase();
			db.execSQL("DROP TABLE "+Database.Data.TABLE_NAME);
			db.execSQL(createDatas); // execute sql
			db.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

}
