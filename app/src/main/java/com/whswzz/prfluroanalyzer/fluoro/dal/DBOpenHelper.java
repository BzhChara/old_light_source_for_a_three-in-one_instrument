/*Copyright:Jemen Chen
 * ���ݿⴴ����������ص���
 */
package com.whswzz.prfluroanalyzer.fluoro.dal;


import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import org.xutils.common.util.LogUtil;

public class DBOpenHelper extends SQLiteOpenHelper {
	private static DBOpenHelper helper;
	
	String createDatas = "CREATE TABLE " + Database.CollaurumData.TABLE_NAME + " ("
			+ Database.CollaurumData.Columns.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ Database.CollaurumData.Columns.SN + " VARCHAR(20) , "
			+ Database.CollaurumData.Columns.TIME + " INTEGER, "
			+ Database.CollaurumData.Columns.SPECIMEN+ " TEXT,"
			+ Database.CollaurumData.Columns.PROJ+ " TEXT,"
			+ Database.CollaurumData.Columns.LIMIT+ "  Decimal (12,4) default 0,"
			+ Database.CollaurumData.Columns.RESULT+ " TEXT,"
			+ Database.CollaurumData.Columns.SOURCE_ORG+ " TEXT,"
			+ Database.CollaurumData.Columns.USER_ORG+ " TEXT,"
			+ Database.CollaurumData.Columns.OPERATOR+ " TEXT,"
			+ Database.CollaurumData.Columns.VALUES+ " BLOB"
			+ ")";
	String createIndex="create index if not exists ia on "+Database.CollaurumData.TABLE_NAME+"("
 			+ Database.CollaurumData.Columns.ID + " , "
 			+ Database.CollaurumData.Columns.TIME + " , "
 			+ Database.CollaurumData.Columns.PROJ + " , "
			+ Database.CollaurumData.Columns.SPECIMEN + ") ";


	private DBOpenHelper(Context context) {
		super(context, "records.db", null, 2);
	}
	
	public static DBOpenHelper getHelper(Context context) {
		if(helper==null) {
			helper=new DBOpenHelper(context);
		}
		return helper;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		LogUtil.d("创建数据库");
		// 创建该数据库的语句
		db.execSQL(createDatas); // execute sql
		//创建索引 
	 	db.execSQL(createIndex);

	}

	//数据库升级版本
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		LogUtil.d("数据库升级版本,oldVersion="+oldVersion+",newVersion="+newVersion);
		switch(oldVersion){		//jemen:数据库升级
		case 1:
			db.execSQL("alter table "+Database.CollaurumData.TABLE_NAME+" add column "+Database.CollaurumData.Columns.VALUES+" BLOB");
			//创建索引 
		 	db.execSQL(createIndex);
			break;
		case 2://啥都不用做
			
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
			db.execSQL("DROP TABLE "+Database.CollaurumData.TABLE_NAME);
			db.execSQL(createDatas); // execute sql
			db.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

}
