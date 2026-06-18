package com.zkzk.pra.dal.imp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.StreamCorruptedException;
import java.util.LinkedList;
import java.util.List;

import com.whswzz.prfluroanalyzer.app.MyApp;
import com.whswzz.prfluroanalyzer.entity.Organization;
import com.whswzz.prfluroanalyzer.entity.Source;
import com.zkzk.pra.dal.IRecordDao;
import com.zkzk.pra.db.DBOpenHelper;
import com.zkzk.pra.db.Database;
import com.zkzk.pra.entity.Data;
import com.zkzk.pra.entity.Location;
import com.zkzk.pra.entity.User;
import com.zkzk.pra.utils.ExceptionHandler;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DataSetObservable;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import android.widget.Toast;
import top.jemen.utils.LogUtil;

/**
 * 数据库相关的操作。因没有特别需求，没有使用事物。
 * 
 * @author Administrator
 *
 */
public class RecordDaoImpl implements IRecordDao {
	private Context context;
	private static RecordDaoImpl dao;
	DBOpenHelper dbOpenHelper;
	SQLiteDatabase db;

	private RecordDaoImpl() {
		super();
		context = MyApp.getApp();
		dbOpenHelper = new DBOpenHelper(context);
	}

	public static RecordDaoImpl getDao() {
		if (null == dao) {
			dao = new RecordDaoImpl();
		}
		return dao;
	}

	@Override
	public synchronized long insert(Data data) {
		long id = -1;
		try {
			if (null == db || !db.isOpen())
				db = dbOpenHelper.getWritableDatabase();
			String table = Database.Data.TABLE_NAME;
			String nullColumnHack = Database.Data.Columns.ID;
			ContentValues values = new ContentValues();
			values.put(Database.Data.Columns.SN, data.getSn());
			values.put(Database.Data.Columns.TIME, data.getTime());
			values.put(Database.Data.Columns.PROJ, data.getProj());
			values.put(Database.Data.Columns.SPECIMEN, data.getSpecimen());
			values.put(Database.Data.Columns.CHANNEL_NUM, data.getChannel());
			if(null!=data.getSource()) {
				values.put(Database.Data.Columns.SOURCE_UNIT, data.getSource().getUnit());
				values.put(Database.Data.Columns.SOURCE_ADDR, data.getSource().getAddr());
				values.put(Database.Data.Columns.SOURCE_CONTACT, data.getSource().getContact());
				values.put(Database.Data.Columns.SOURCE_PHONE, data.getSource().getPhone());
				values.put(Database.Data.Columns.SOURCE_ORG_CODE, data.getSourceCode());
				values.put(Database.Data.Columns.SOURCE_ORG_TYPE, data.getSourceOrgType());
			}
			
			values.put(Database.Data.Columns.ABSORBANCY, data.getAbsorbancy());
			values.put(Database.Data.Columns.INHIBITION_RATIO, data.getInhibitionRatio());
			values.put(Database.Data.Columns.LIMIT, data.getLimit());
			values.put(Database.Data.Columns.RESULT, data.getResult());
			values.put(Database.Data.Columns.UPLOADED, data.isUploaded());
			
			 Organization user = data.getUser();
			if(null!=user) {
				values.put(Database.Data.Columns.USER_NAME, data.getUser().getName());
				values.put(Database.Data.Columns.USER_ADDR, data.getUser().getAddr());
				values.put(Database.Data.Columns.USER_CONTACT, data.getUser().getContact());
				values.put(Database.Data.Columns.USER_PHONE, data.getUser().getPhone());
				values.put(Database.Data.Columns.USER_OPERATOR, user.getOperator());
				values.put(Database.Data.Columns.USER_CODE, user.getCode());
				values.put(Database.Data.Columns.USER_TOKEN, user.getToken());
			}
			Location loc = data.getLocation();
			if (null != loc) {
				values.put(Database.Data.Columns.LATITUDE, loc.getLatitude());
				values.put(Database.Data.Columns.LONGITUDE, loc.getLongitude());
				values.put(Database.Data.Columns.DESCRIBE, loc.getDescribe());
			}
			id = db.insert(table, nullColumnHack, values);
			db.close();
			db = null;
		} catch (Exception e) {
			ExceptionHandler.handleException(e);
		} finally {
			if (null != db && db.isOpen()) {
				db.close();
				db = null;
			}
		}
		return id;
	}

	@SuppressLint("Range")
	@SuppressWarnings("unchecked")
	@Override
	public synchronized List<Data> query(String whereClause, String[] whereArgs) {
		// Log.i("tedu", "RecordDaoImpl.query() start.");
		List<Data> datas = new LinkedList<Data>();
		try {
			if (null == db || !db.isOpen())
				db = dbOpenHelper.getReadableDatabase();
			String table = Database.Data.TABLE_NAME;
			String[] columns = { Database.Data.Columns.ID, // 0
					Database.Data.Columns.SN, Database.Data.Columns.TIME, Database.Data.Columns.SPECIMEN,
					Database.Data.Columns.PROJ, Database.Data.Columns.SOURCE_UNIT, Database.Data.Columns.CHANNEL_NUM,
					Database.Data.Columns.ABSORBANCY, Database.Data.Columns.INHIBITION_RATIO,
					Database.Data.Columns.LIMIT, Database.Data.Columns.RESULT, Database.Data.Columns.UPLOADED,
					Database.Data.Columns.USER_NAME, Database.Data.Columns.USER_OPERATOR,
					Database.Data.Columns.USER_CODE,Database.Data.Columns.USER_TOKEN };
			String[] all = { "*" };
			String selection = whereClause; // WHERE�Ӿ䣬���磺_id=?
			String[] selectionArgs = whereArgs;
			String groupBy = null;
			String having = null;
			String orderBy = Database.Data.Columns.ID + " DESC";
			Cursor c = db.query(table, all, selection, selectionArgs, groupBy, having, orderBy);
			ObjectInputStream objIn = null;
			if (c.moveToFirst()) {
				for (; !c.isAfterLast(); c.moveToNext()) {
					Data data = new Data();
					data.setId(c.getInt(0));// 为尽可能提高效率，此处写死，故不可改变建表SQL的语句顺序。
					data.setSn(c.getString(c.getColumnIndex(Database.Data.Columns.SN)));
					data.setTime(c.getLong(c.getColumnIndex(Database.Data.Columns.TIME)));
					data.setProj(c.getString(c.getColumnIndex(Database.Data.Columns.PROJ)));
					data.setSpecimen(c.getString(4));
					data.setChannel(c.getString(5));
					data.setSource(new Source(c.getString(6)));
					data.setAbsorbancy(c.getFloat(7));
					data.setInhibitionRatio(c.getFloat(8));
					data.setLimit(c.getString(9));
					data.setResult(c.getString(10));
					data.setUploaded(c.getInt(11) == 1);
					
					Source source=new Source(c.getString(c.getColumnIndex(Database.Data.Columns.SOURCE_UNIT)));
					source.setAddr(c.getString(c.getColumnIndex(Database.Data.Columns.SOURCE_ADDR)));
					source.setContact(c.getString(c.getColumnIndex(Database.Data.Columns.SOURCE_CONTACT)));
					source.setPhone(c.getString(c.getColumnIndex(Database.Data.Columns.SOURCE_PHONE)));
					source.setCode(c.getString(c.getColumnIndex(Database.Data.Columns.SOURCE_ORG_CODE)));
					source.setType(c.getInt(c.getColumnIndex(Database.Data.Columns.SOURCE_ORG_TYPE)));
					data.setSource(source);
					
					Organization user=new Organization(c.getString(12));
					user.setOperator(c.getString(12));
					user.setAddr(c.getString(c.getColumnIndex(Database.Data.Columns.USER_ADDR)));
					user.setCode(c.getString(c.getColumnIndex(Database.Data.Columns.USER_CODE)));
					user.setToken(c.getString(c.getColumnIndex(Database.Data.Columns.USER_TOKEN)));
					
					data.setUser(user);
					datas.add(data);
				}
			}

			c.close();
			db.close();
			db = null;
		} catch (Exception e) {
			ExceptionHandler.handleException(e);
			e.printStackTrace();
		}
		return datas;
	}

	@Override
	public synchronized int delete(long id) {
		int affectedRows = 0;
		try {
			if (null == db || !db.isOpen())
				db = dbOpenHelper.getReadableDatabase();
			String table = Database.Data.TABLE_NAME;
			String whereClause = Database.Data.Columns.ID + "=?";
			String[] whereArgs = { id + "" };
			affectedRows = db.delete(table, whereClause, whereArgs);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != db) {
				db.close();
				db = null;
			}
		}
		return affectedRows;
	}

	@Override
	public synchronized int deleteByTime(long time) {
		int affectedRows = -1;
		try {
			if (null == db || !db.isOpen())
				db = dbOpenHelper.getReadableDatabase();
			String table = Database.Data.TABLE_NAME;
			String whereClause = Database.Data.Columns.TIME + "=?";
			String[] whereArgs = { time + "" };
			affectedRows = db.delete(table, whereClause, whereArgs);
		} catch (Exception e) {
			ExceptionHandler.handleException(e);
		} finally {
			if (null != db) {
				db.close();
				db = null;
			}

		}
		return affectedRows;
	}

	@Override
	public synchronized int update(Data data) {
		int affectedRows = 0;

		try {
			if (null == db || !db.isOpen())
				db = dbOpenHelper.getWritableDatabase();
			db.beginTransaction();// 开启事务
			String table = Database.Data.TABLE_NAME;
			String whereClause = Database.Data.Columns.ID + "=?";
			String[] whereArgs = { data.getId() + "" };
			ContentValues values = new ContentValues();
			values.put(Database.Data.Columns.SN, data.getSn());
			values.put(Database.Data.Columns.TIME, data.getTime());
			values.put(Database.Data.Columns.PROJ, data.getProj());
			values.put(Database.Data.Columns.SPECIMEN, data.getSpecimen());
			values.put(Database.Data.Columns.CHANNEL_NUM, data.getChannel());
			values.put(Database.Data.Columns.SOURCE_UNIT, data.getSource().getUnit());
			values.put(Database.Data.Columns.ABSORBANCY, data.getAbsorbancy());
			values.put(Database.Data.Columns.INHIBITION_RATIO, data.getInhibitionRatio());
			values.put(Database.Data.Columns.LIMIT, data.getLimit());
			values.put(Database.Data.Columns.RESULT, data.getResult());
			values.put(Database.Data.Columns.UPLOADED, data.isUploaded());
			if(null!=data.getSource()) {
				values.put(Database.Data.Columns.SOURCE_UNIT, data.getSource().getUnit());
				values.put(Database.Data.Columns.SOURCE_ADDR, data.getSource().getAddr());
				values.put(Database.Data.Columns.SOURCE_CONTACT, data.getSource().getContact());
				values.put(Database.Data.Columns.SOURCE_PHONE, data.getSource().getPhone());
				values.put(Database.Data.Columns.SOURCE_ORG_CODE, data.getSourceCode());
				values.put(Database.Data.Columns.SOURCE_ORG_TYPE, data.getSourceOrgType());
			}
			
			if(null!=data.getUser()) {
				values.put(Database.Data.Columns.USER_NAME, data.getUser().getName());
				values.put(Database.Data.Columns.USER_OPERATOR, data.getUser().getOperator());
				values.put(Database.Data.Columns.USER_ADDR, data.getUser().getAddr());
				values.put(Database.Data.Columns.USER_CONTACT, data.getUser().getContact());
				values.put(Database.Data.Columns.USER_PHONE, data.getUser().getPhone());
				values.put(Database.Data.Columns.USER_CODE, data.getUserCode());
				values.put(Database.Data.Columns.USER_TOKEN, data.getUserToken());
			}
			
			
			Location loc = data.getLocation();
			if (null != loc) {
				values.put(Database.Data.Columns.LATITUDE, loc.getLatitude());
				values.put(Database.Data.Columns.LONGITUDE, loc.getLongitude());
				values.put(Database.Data.Columns.DESCRIBE, loc.getDescribe());
				
			}
			affectedRows = db.update(table, values, whereClause, whereArgs);
			db.setTransactionSuccessful();
		} catch (Exception e) {
			ExceptionHandler.handleException(e);
		} finally {
			db.endTransaction();
			db.close();
			db = null;
		}

		return affectedRows;
	}

	public void setUpload(long id,boolean uploaded) {
		if (null == db || !db.isOpen())
			db = dbOpenHelper.getWritableDatabase();
		db.execSQL("UPDATE "+Database.Data.TABLE_NAME+" SET "+Database.Data.Columns.UPLOADED+"="
			+(uploaded?1:0)+" WHERE "+Database.Data.Columns.ID+"=="+id);
		
	}
	
	@SuppressLint("Range")
	@SuppressWarnings("unchecked")
	@Override
	public synchronized boolean query(List<Data> datas, String whereClause, String[] whereArgs) {
		datas.clear();
		try {
			if (null == db || !db.isOpen())
				db = dbOpenHelper.getWritableDatabase();
			String table = Database.Data.TABLE_NAME;
			String[] columns = { Database.Data.Columns.ID, // 0
					Database.Data.Columns.ID, // 0
					Database.Data.Columns.SN, Database.Data.Columns.TIME, Database.Data.Columns.SPECIMEN,
					Database.Data.Columns.PROJ, Database.Data.Columns.SOURCE_UNIT, Database.Data.Columns.CHANNEL_NUM, // 通道号
					Database.Data.Columns.ABSORBANCY, // 吸光度
					Database.Data.Columns.INHIBITION_RATIO, // 抑制率
					Database.Data.Columns.LIMIT, Database.Data.Columns.RESULT, // 结果
					Database.Data.Columns.UPLOADED, Database.Data.Columns.USER_NAME, Database.Data.Columns.USER_OPERATOR,
					Database.Data.Columns.LATITUDE, Database.Data.Columns.LONGITUDE, 
					Database.Data.Columns.DESCRIBE ,
					Database.Data.Columns.USER_CODE,Database.Data.Columns.USER_TOKEN 	
			};
			String[] columnAll = { "*" };
			String selection = whereClause; //
			String[] selectionArgs = whereArgs;
			String groupBy = null;
			String having = null;
			String orderBy = Database.Data.Columns.ID + " DESC";
			Cursor c = db.query(table, columnAll, selection, selectionArgs, groupBy, having, orderBy);
			if (c.moveToFirst()) {
				for (; !c.isAfterLast(); c.moveToNext()) {
					Data data = new Data();
					data.setId(c.getInt(0));// 为尽可能提高效率，此处写死，故不可改变建表SQL的语句顺序。
					data.setSn(c.getString(1));
					data.setTime(c.getLong(2));
					data.setProj(c.getString(3));
					data.setSpecimen(c.getString(4));
					data.setChannel(c.getString(5));
					
					data.setSource(new Source(c.getString(c.getColumnIndex(Database.Data.Columns.SOURCE_UNIT))));
					data.setAbsorbancy(c.getFloat(c.getColumnIndex(Database.Data.Columns.ABSORBANCY)));
					data.setInhibitionRatio(c.getFloat(c.getColumnIndex(Database.Data.Columns.INHIBITION_RATIO)));
					data.setLimit(c.getString(c.getColumnIndex(Database.Data.Columns.LIMIT)));
					data.setResult(c.getString(c.getColumnIndex(Database.Data.Columns.RESULT)));
					data.setUploaded(c.getInt(c.getColumnIndex(Database.Data.Columns.UPLOADED)) == 1);
					Source source=new Source(c.getString(c.getColumnIndex(Database.Data.Columns.SOURCE_UNIT)));
					source.setAddr(c.getString(c.getColumnIndex(Database.Data.Columns.SOURCE_ADDR)));
					source.setContact(c.getString(c.getColumnIndex(Database.Data.Columns.SOURCE_CONTACT)));
					source.setPhone(c.getString(c.getColumnIndex(Database.Data.Columns.SOURCE_PHONE)));
					source.setCode(c.getString(c.getColumnIndex(Database.Data.Columns.SOURCE_ORG_CODE)));
					source.setType(c.getInt(c.getColumnIndex(Database.Data.Columns.SOURCE_ORG_TYPE)));
					data.setSource(source);
					
					Organization user=new Organization(c.getString(c.getColumnIndex(Database.Data.Columns.USER_NAME)));
					user.setOperator(c.getString(c.getColumnIndex(Database.Data.Columns.USER_OPERATOR)));
					user.setPhone(c.getString(c.getColumnIndex(Database.Data.Columns.USER_PHONE)));
					user.setCode(c.getString(c.getColumnIndex(Database.Data.Columns.USER_CODE)));
					user.setToken(c.getString(c.getColumnIndex(Database.Data.Columns.USER_TOKEN)));
					user.setAddr(c.getString(c.getColumnIndex(Database.Data.Columns.USER_ADDR)));
					user.setContact(c.getString(c.getColumnIndex(Database.Data.Columns.USER_CONTACT)));

					data.setUser(user);
					datas.add(data);
				}
			}else {
				LogUtil.d("movetofirst faild!!!");
			}
			c.close();
		} catch (Exception e) {
			ExceptionHandler.handleException(e);
		} finally {
			if (null != db) {
				db.close();
				db = null;
			}
		}
		return false;
	}

	/**
	 * 仅根据id查出一条数据，包含该数据的原始电流电压值。
	 * 
	 * @param id
	 * @return
	 */
	public synchronized Data query(long id) {
		try {
			if (null == db || !db.isOpen())
				db = dbOpenHelper.getWritableDatabase();
			String table = Database.Data.TABLE_NAME;
			String[] columnAll = { "*" };
			String selection = Database.Data.Columns.ID + "=?"; //
			String[] selectionArgs = { "" + id };
			String groupBy = null;
			String having = null;
			String orderBy = Database.Data.Columns.ID + " DESC";
			Cursor c = db.query(table, columnAll, selection, selectionArgs, groupBy, having, orderBy);

			Data data = null;
			if (c.moveToFirst()) {
				for (; !c.isAfterLast(); c.moveToNext()) {
					data = new Data();
					data.setId(c.getInt(0));// 为尽可能提高效率，此处写死，故不可改变建表SQL的语句顺序。
					data.setSn(c.getString(1));
					data.setTime(c.getLong(2));
					data.setProj(c.getString(3));
					data.setSpecimen(c.getString(4));
					data.setChannel(c.getString(5));
					data.setSource(new Source(c.getString(c.getColumnIndex(Database.Data.Columns.SOURCE_UNIT))));
					data.setAbsorbancy(c.getFloat(c.getColumnIndex(Database.Data.Columns.ABSORBANCY)));
					data.setInhibitionRatio(c.getFloat(c.getColumnIndex(Database.Data.Columns.INHIBITION_RATIO)));
					data.setLimit(c.getString(c.getColumnIndex(Database.Data.Columns.LIMIT)));
					data.setResult(c.getString(c.getColumnIndex(Database.Data.Columns.RESULT)));
					data.setUploaded(c.getInt(c.getColumnIndex(Database.Data.Columns.UPLOADED)) == 1);
					
					Source source=new Source(c.getString(c.getColumnIndex(Database.Data.Columns.SOURCE_UNIT)));
					source.setAddr(c.getString(c.getColumnIndex(Database.Data.Columns.SOURCE_ADDR)));
					source.setContact(c.getString(c.getColumnIndex(Database.Data.Columns.SOURCE_CONTACT)));
					source.setPhone(c.getString(c.getColumnIndex(Database.Data.Columns.SOURCE_PHONE)));
					source.setCode(c.getString(c.getColumnIndex(Database.Data.Columns.SOURCE_ORG_CODE)));
					source.setType(c.getInt(c.getColumnIndex(Database.Data.Columns.SOURCE_ORG_TYPE)));
					data.setSource(source);
					
					Organization user=new Organization(c.getString(c.getColumnIndex(Database.Data.Columns.USER_NAME)));
					user.setOperator(c.getString(c.getColumnIndex(Database.Data.Columns.USER_OPERATOR)));
					user.setPhone(c.getString(c.getColumnIndex(Database.Data.Columns.USER_PHONE)));
					user.setCode(c.getString(c.getColumnIndex(Database.Data.Columns.USER_CODE)));
					user.setToken(c.getString(c.getColumnIndex(Database.Data.Columns.USER_TOKEN)));
					data.setUser(user);
				}
			}
			c.close();
			return data;
		} catch (Exception e) {
			ExceptionHandler.handleException(e);
			if (null != db) {
				db.close();
				db = null;
			}
		}
		return null;
	}

	@Override
	public synchronized boolean deleteAll() {
		try {
			DBOpenHelper dbOpenHelper = new DBOpenHelper(context);

			// SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
			// String table = Database.Data.TABLE_NAME;
			// db.delete(table,null, null);
			//// String drop="DROP TABLE "+Database.Data.TABLE_NAME; //
			//// db.execSQL(drop);
			//
			// db.execSQL("VACUUM");// VACUUM 命令清除未使用的空间。;l
			// db.close();

			dbOpenHelper.reCreateDatas();//从2018-8-30已经开始了改用此函数.
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public synchronized boolean deletFirst() {
		try {
			if (null == db || !db.isOpen())
				db = dbOpenHelper.getWritableDatabase();
			String sql = "select * from " + Database.Data.TABLE_NAME + " limit 1 ";// 执行该句仅会查出一条
			Cursor cursor = db.rawQuery(sql, null);
			if (cursor.moveToFirst()) {
				String table = Database.Data.TABLE_NAME;
				String whereClause = Database.Data.Columns.ID + "=?";
				String[] whereArgs = { cursor.getInt(0) + "" };
				db.delete(table, whereClause, whereArgs);
			}
			cursor.close();
			return true;
		} catch (Exception e) {
			ExceptionHandler.handleException(e);
		} finally {
			db.close();
			db = null;
		}
		return false;
	}

	/**
	 * 统计数据的量，超过10万条将删除第一条。
	 */
	public synchronized long countData() {
		long dataNum = -1;
		try {
			if (null == db || !db.isOpen())
				db = dbOpenHelper.getReadableDatabase();
			String createIndex = "create index if not exists ia on " + Database.Data.TABLE_NAME + "("
					+ Database.Data.Columns.ID + "," + Database.Data.Columns.TIME + "," + Database.Data.Columns.PROJ
					+ "," + Database.Data.Columns.SPECIMEN + ")";
			db.execSQL(createIndex);// 创建索引

			Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + Database.Data.TABLE_NAME, null);// 查总数
			if (cursor != null && cursor.moveToFirst()) {
				dataNum = cursor.getLong(0);// 获取数据库中总数据条数
				Log.d("jemen", "dataNum=" + dataNum + ",count=" + cursor.getCount());
			}
			if (dataNum > 100000) {
				String sql = "select * from " + Database.Data.TABLE_NAME + " limit 1 ";
				Cursor cursor2 = db.rawQuery(sql, null);
				if (cursor2.moveToFirst()) {
					Log.d("jemen", "count，the first id=" + cursor2.getInt(0) + ",count=" + cursor2.getCount());
					String table = Database.Data.TABLE_NAME;
					String whereClause = Database.Data.Columns.ID + "=?";
					String[] whereArgs = { cursor2.getInt(0) + "" };
					db.delete(table, whereClause, whereArgs);
					dataNum--;
				}
				cursor2.close();
			}
			cursor.close();
			db.close();
		} catch (Exception e) {
			ExceptionHandler.handleException(e);
		} finally {
			if (null != db) {
				db.close();
				db = null;
			}
		}
		return dataNum;
	}

}
