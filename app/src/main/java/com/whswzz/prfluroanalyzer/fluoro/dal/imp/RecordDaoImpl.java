//package com.whswzz.prfluroanalyzer.fluoro.dal.imp;
//
//import java.io.ByteArrayInputStream;
//import java.io.ByteArrayOutputStream;
//import java.io.IOException;
//import java.io.ObjectInput;
//import java.io.ObjectInputStream;
//import java.io.ObjectOutput;
//import java.io.ObjectOutputStream;
//import java.io.OptionalDataException;
//import java.io.StreamCorruptedException;
//import java.util.ArrayList;
//import java.util.LinkedList;
//import java.util.List;
//
//import com.whswzz.prfluroanalyzer.app.MyApp;
//import com.whswzz.prfluroanalyzer.fluoro.dal.DBOpenHelper;
//import com.whswzz.prfluroanalyzer.fluoro.dal.Database;
//import com.whswzz.prfluroanalyzer.fluoro.dal.IRecordDao;
//import com.whswzz.prfluroanalyzer.fluoro.entity.FluData;
//import com.whswzz.prfluroanalyzer.fluoro.entity.ValueList;
//
//import android.content.ContentValues;
//import android.content.Context;
//import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
//import android.database.sqlite.SQLiteException;
//import android.util.Log;
//import android.widget.Toast;
//import top.jemen.utils.ExceptionHandler;
//
//
///**
// * 数据库相关的操作。因没有特别需求，没有使用事物。
// * 
// * @author Administrator
// *
// */
//public class RecordDaoImpl implements IRecordDao {
//	private static RecordDaoImpl dao;
//	private Context context;
//	private DBOpenHelper dbOpenHelper;
//	private static SQLiteDatabase db = null;
//
//	private RecordDaoImpl(Context context) {
//		super();
//		this.context = context;
//	}
//
//	public static synchronized RecordDaoImpl getDao(MyApp app) {
//		if (null == dao) {
//			if(null==app)app=MyApp.getApp();
//			dao = new RecordDaoImpl(app);
//		}
//		return dao;
//	}
//
//	private DBOpenHelper getHelper() {
//		if (null == dbOpenHelper) {
//			dbOpenHelper = DBOpenHelper.getHelper(context);
//		}
//		return dbOpenHelper;
//	}
//
//	public synchronized void closeDb() {
//		if (null != db && db.isOpen()) {
//			db.close();
//		}
//	}
//
//	@Override
//	public synchronized long insert(FluData data) {
//		long id = -1;
//		if (null == data)
//			return id;
//
//		if (null == db || !db.isOpen()) {
//			try {
//				dbOpenHelper = getHelper();
//				db = dbOpenHelper.getReadableDatabase(); // 3850
//			} catch (SQLiteException e) {
//				ExceptionHandler.handleException(e);
//				return -1;
//			}
//		}
//		ObjectOutputStream objOut = null;
//		try {
//			String table = Database.CollaurumData.TABLE_NAME;
//			String nullColumnHack = Database.CollaurumData.Columns.ID;
//			ContentValues values = new ContentValues();
//			values.put(Database.CollaurumData.Columns.SN, data.getSn());
//			values.put(Database.CollaurumData.Columns.TIME, data.getTime());
//			values.put(Database.CollaurumData.Columns.SPECIMEN, data.getSpecimen());
//			values.put(Database.CollaurumData.Columns.PROJ, data.getProj());
//			values.put(Database.CollaurumData.Columns.LIMIT, data.getLimit());
//			values.put(Database.CollaurumData.Columns.RESULT, data.getResult());
//			values.put(Database.CollaurumData.Columns.SOURCE_ORG, data.getSourceOrg());
//			values.put(Database.CollaurumData.Columns.USER_ORG, data.getWorkOrg());
//			values.put(Database.CollaurumData.Columns.OPERATOR, data.getOperator());
//			ByteArrayOutputStream bout = new ByteArrayOutputStream();
//			objOut = new ObjectOutputStream(bout);
//			objOut.writeObject(data.getValues());
//			objOut.flush();
//			objOut.close(); // 放到finally中去关闭
//			values.put(Database.CollaurumData.Columns.VALUES, bout.toByteArray());
//			// bout.close();
//			id = db.insert(table, nullColumnHack, values);
//		} catch (IOException e) {
//			ExceptionHandler.handleException(e);
//		} finally {
//			if (null != objOut)
//				try {
//					objOut.close();
//				} catch (IOException e) {
//					ExceptionHandler.handleException(e);
//				}
////			 if (null != db) {
////				 db.close();//暂不关闭了。
////				 db=null;
////			 }
//		}
//		return id;
//
//	}
//
//	public synchronized long getCount(String where, String[] args) {
//		long dataNum = 0;
//		Cursor cursor=null;
//		try {
//			if (null == db || !db.isOpen()) {
//				dbOpenHelper = getHelper();
//				db = dbOpenHelper.getReadableDatabase();
//			}
//
//			String createIndex = "create index if not exists ia on " + Database.CollaurumData.TABLE_NAME + "("
//					+ Database.CollaurumData.Columns.ID + "," + Database.CollaurumData.Columns.TIME + "," + Database.CollaurumData.Columns.PROJ
//					+ "," + Database.CollaurumData.Columns.SPECIMEN + ")";
//			db.execSQL(createIndex);// 创建索引
//
//			// "select count(*) from gl_sample where upload = 'true' and time >= '" + start
//			// + "' and time <= '" + end + "'";
//			StringBuilder sbSql = new StringBuilder("SELECT COUNT(*) FROM ").append(Database.CollaurumData.TABLE_NAME);
//			if (null != where && null != args) {
//				sbSql.append(" where ").append(where);
//			}
//			cursor = db.rawQuery(sbSql.toString(), args);// 查总数
//			if (cursor != null && cursor.moveToFirst()) {
//				dataNum = cursor.getLong(0);// 获取数据库中总数据条数
//				Log.d("jemen", "dataNum=" + dataNum + ",count=" + cursor.getCount());
//			}
//			
//			// db.close();	//暂不关闭了。
//			// db = null;
//		} catch (Exception e) {
//			ExceptionHandler.handleException(e);
//		}finally {
//			if(null!=cursor) {
//				cursor.close();
//			}
//		}
//		return dataNum;
//	}
//
//	public synchronized void deletFirst() {
//		try {
//			if (null == db || !db.isOpen()) {
//				dbOpenHelper = getHelper();
//				db = dbOpenHelper.getReadableDatabase();
//			}
//			String sql = "select * from " + Database.CollaurumData.TABLE_NAME + " limit 1 ";// 执行该句仅会查出一条
//			Cursor cursor = db.rawQuery(sql, null);
//			if (cursor.moveToFirst()) {
//				int id=cursor.getInt(0);
//				cursor.close();
//				id = delete(id);
//				String table = Database.CollaurumData.TABLE_NAME;
//				String whereClause = Database.CollaurumData.Columns.ID + "=?";
//				String[] whereArgs = { id + "" };
//				db.delete(table, whereClause, whereArgs);
//			}
//			// db.close();
//		} catch (Exception e) {
//			ExceptionHandler.handleException(e);
//		}
//	}
//
//	@SuppressWarnings("unchecked")
//	@Override
//	public synchronized List<FluData> query(String whereClause, String[] whereArgs) {
//		List<FluData> datas = new LinkedList<FluData>();
//		if (null == db || !db.isOpen()) {
//			try {
//				DBOpenHelper dbOpenHelper = getHelper();
//				db = dbOpenHelper.getReadableDatabase();
//			} catch (SQLiteException e) {
//				ExceptionHandler.handleException(e);
//				// Toast.makeText(context, "打开数据库失败", Toast.LENGTH_SHORT).show();//本函数通常在子线程调用，
//			}
//		}
//		String table = Database.CollaurumData.TABLE_NAME;
//		String[] columns = { Database.CollaurumData.Columns.ID, // 0
//				Database.CollaurumData.Columns.SN, Database.CollaurumData.Columns.TIME, Database.CollaurumData.Columns.SPECIMEN,
//				Database.CollaurumData.Columns.PROJ, Database.CollaurumData.Columns.LIMIT, Database.CollaurumData.Columns.RESULT,
//				Database.CollaurumData.Columns.SOURCE_ORG, Database.CollaurumData.Columns.USER_ORG, Database.CollaurumData.Columns.OPERATOR,
//				Database.CollaurumData.Columns.VALUES };
//		String selection = whereClause; // WHERE�Ӿ䣬���磺_id=?
//		String[] selectionArgs = whereArgs;
//		String groupBy = null;
//		String having = null;
//		String orderBy = Database.CollaurumData.Columns.ID + " DESC";
//		Cursor c = db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
//		// 4.2. ������ѯ���
//		ObjectInputStream objIn = null;
//		if (c.moveToFirst()) {
//			for (; !c.isAfterLast(); c.moveToNext()) {
//				FluData data = new FluData();
//				data.setId(c.getInt(0));
//				data.setSn(c.getString(1));
//				data.setTime(c.getLong(2));
//				data.setName(c.getString(3));
//				data.setProj(c.getString(4));
//				data.setLimit(c.getFloat(5));
//				data.setResult(c.getString(6));
//				data.setSourceOrg(c.getString(7));
//				data.setWorkOrg(c.getString(8));
//				data.setOperator(c.getString(9));
//				try {
//					byte bs[] = c.getBlob(c.getColumnIndex(Database.CollaurumData.Columns.VALUES));
//					ByteArrayInputStream arrayIn = new ByteArrayInputStream(bs);
//					objIn = new ObjectInputStream(arrayIn);
//					data.setValues((ValueList) objIn.readObject());
//				} catch (Exception e) {
//					ExceptionHandler.handleException(e);
//				} finally {
//					if (null != objIn)
//						try {
//							objIn.close();
//						} catch (IOException e) {
//							ExceptionHandler.handleException(e);
//						}
//				}
//				datas.add(data);
//			}
//		}
//		c.close();
////		 db.close();
//		 
//		return datas;
//	}
//
//	@Override
//	public synchronized int delete(long id) {
//		int affectedRows = 0;
//		if (null == db || !db.isOpen()) {
//			try {
//				DBOpenHelper dbOpenHelper = getHelper();
//				db = dbOpenHelper.getWritableDatabase();
//			} catch (SQLiteException e) {
//				ExceptionHandler.handleException(e);
//				Toast.makeText(context, "打开数据库失败", Toast.LENGTH_SHORT).show();
//			}
//		}
//		String table = Database.CollaurumData.TABLE_NAME;
//		String whereClause = Database.CollaurumData.Columns.ID + "=?";
//		String[] whereArgs = { id + "" };
//		affectedRows = db.delete(table, whereClause, whereArgs);
//		// db.close();
//		return affectedRows;
//	}
//
//	@Override
//	public synchronized int deleteByTime(long time) {
//		int affectedRows = 0;
//		DBOpenHelper dbOpenHelper = getHelper();
//		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
//		String table = Database.CollaurumData.TABLE_NAME;
//		String whereClause = Database.CollaurumData.Columns.TIME + "=?";
//		String[] whereArgs = { time + "" };
//		affectedRows = db.delete(table, whereClause, whereArgs);
//		// db.close();
//		return affectedRows;
//	}
//
//	@Override
//	public synchronized int update(FluData data) {
//		int affectedRows = 0;
//		if (null == db || !db.isOpen()) {
//			try {
//				DBOpenHelper dbOpenHelper = getHelper();
//				db = dbOpenHelper.getWritableDatabase();
//			} catch (SQLiteException e) {
//				ExceptionHandler.handleException(e);
//				Toast.makeText(context, "打开数据库失败", Toast.LENGTH_SHORT).show();
//			}
//		}
//		String table = Database.CollaurumData.TABLE_NAME;
//		String whereClause = Database.CollaurumData.Columns.TIME + "=?";
//		String[] whereArgs = { data.getTime() + "" };
//		ContentValues values = new ContentValues();
//
//		values.put(Database.CollaurumData.Columns.SN, data.getSn());
//		values.put(Database.CollaurumData.Columns.TIME, data.getTime());
//		values.put(Database.CollaurumData.Columns.SPECIMEN, data.getSpecimen());
//		values.put(Database.CollaurumData.Columns.PROJ, data.getProj());
//		values.put(Database.CollaurumData.Columns.LIMIT, data.getLimit());
//		values.put(Database.CollaurumData.Columns.RESULT, data.getResult());
//		values.put(Database.CollaurumData.Columns.SOURCE_ORG, data.getSourceOrg());
//		values.put(Database.CollaurumData.Columns.USER_ORG, data.getWorkOrg());
//		values.put(Database.CollaurumData.Columns.OPERATOR, data.getOperator());
//		try {
//			ByteArrayOutputStream bout = new ByteArrayOutputStream();
//			ObjectOutputStream objOut = new ObjectOutputStream(bout);
//			objOut.writeObject(data.getValues());
//			objOut.flush();
//			values.put(Database.CollaurumData.Columns.VALUES, bout.toByteArray());
//			objOut.close();
//			bout.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		affectedRows = db.update(table, values, whereClause, whereArgs);
//
//		// db.close();
//		return affectedRows;
//	}
//
//	@SuppressWarnings("unchecked")
//	@Override
//	public synchronized boolean query(List<FluData> datas, String whereClause, String[] whereArgs) {
//		datas.clear();
//		if (null == db || !db.isOpen()) {
//			try {
//				DBOpenHelper dbOpenHelper = getHelper();
//				db = dbOpenHelper.getWritableDatabase();
//			} catch (SQLiteException e) {
//				ExceptionHandler.handleException(e);
//				Toast.makeText(context, "打开数据库失败", Toast.LENGTH_SHORT).show();
//			}
//		}
//		String table = Database.CollaurumData.TABLE_NAME;
//		String[] columns = { Database.CollaurumData.Columns.ID, // 0
//				Database.CollaurumData.Columns.SN, Database.CollaurumData.Columns.TIME, Database.CollaurumData.Columns.SPECIMEN,
//				Database.CollaurumData.Columns.PROJ, Database.CollaurumData.Columns.LIMIT, Database.CollaurumData.Columns.RESULT,
//				Database.CollaurumData.Columns.SOURCE_ORG, Database.CollaurumData.Columns.USER_ORG, Database.CollaurumData.Columns.OPERATOR,
//				// Database.Data.Columns.VALUES //在批量查询时候可以不查询原始数据以提高精度。
//		};
//		String[] columnAll = { "*" };
//		String selection = whereClause; // WHERE�Ӿ䣬���磺_id=?
//		String[] selectionArgs = whereArgs;
//		String groupBy = null;
//		String having = null;
//		String orderBy = Database.CollaurumData.Columns.ID + " DESC";
//		Cursor c = db.query(table, columnAll, selection, selectionArgs, groupBy, having, orderBy);
//		if (c.moveToFirst()) {
//			for (; !c.isAfterLast(); c.moveToNext()) {
//				FluData data = new FluData();
//				data.setId(c.getInt(0));
//				data.setSn(c.getString(1));
//				data.setTime(c.getLong(2));
//				data.setName(c.getString(3));
//				data.setProj(c.getString(4));
//				data.setLimit(c.getFloat(5));
//				data.setResult(c.getString(6));
//				data.setSourceOrg(c.getString(7));
//				data.setWorkOrg(c.getString(8));
//				data.setOperator(c.getString(9));
//				
////				if (Params.DEBUG) {
////					ObjectInputStream objIn = null; // 默认批量查询时不再读取原始数据以提高查询的速度
////					try {
////						byte bs[] = c.getBlob(10);
////						if (null != bs) {
////							ByteArrayInputStream arrayIn = new ByteArrayInputStream(bs);
////							objIn = new ObjectInputStream(arrayIn);
////							data.setValues((List<Value>) objIn.readObject());
////							data.setSensitivity(objIn.readInt());// 6-12日之前的数据没有灵敏度参数
////						}
////					} catch (Exception e) { // 早期保存的数据没有原始电流电压，会爆出异常。
////						// ExceptionHandler.handleException(e);
////					} finally {
////						if (null != objIn)
////							try {
////								objIn.close();
////							} catch (IOException e) {
////								ExceptionHandler.handleException(e);
////							}
////					}
////				}
//
//
//				datas.add(data);
//			}
//		}
//		c.close();
//		// db.close();
//		return false;
//	}
//
//	/**
//	 * 仅根据id查出一条数据，包含该数据的原始电流电压值。
//	 * 
//	 * @param id
//	 * @return
//	 */
//	public synchronized FluData query(long id) {
//		if (null == db || !db.isOpen()) {
//			try {
//				DBOpenHelper dbOpenHelper = getHelper();
//				db = dbOpenHelper.getWritableDatabase();
//			} catch (SQLiteException e) {
//				ExceptionHandler.handleException(e);
//				Toast.makeText(context, "打开数据库失败", Toast.LENGTH_SHORT).show();
//			}
//		}
//		String table = Database.CollaurumData.TABLE_NAME;
//		String[] columnAll = { "*" };
//		String selection = Database.CollaurumData.Columns.ID + "=?"; //
//		String[] selectionArgs = { "" + id };
//		String groupBy = null;
//		String having = null;
//		String orderBy = Database.CollaurumData.Columns.ID + " DESC";
//		Cursor c = db.query(table, columnAll, selection, selectionArgs, groupBy, having, orderBy);
//
//		FluData data = null;
//		if (c.moveToFirst()) {
//			for (; !c.isAfterLast(); c.moveToNext()) {
//				data = new FluData();
//				data.setId(c.getInt(0));
//				data.setSn(c.getString(1));
//				data.setTime(c.getLong(2));
//				data.setName(c.getString(3));
//				data.setProj(c.getString(4));
//				data.setLimit(c.getFloat(5));
//				data.setResult(c.getString(6));
//				data.setSourceOrg(c.getString(7));
//				data.setWorkOrg(c.getString(8));
//				data.setOperator(c.getString(9));
//				ObjectInputStream objIn = null;
//				try {
//					byte bs[] = c.getBlob(c.getColumnIndex(Database.CollaurumData.Columns.VALUES));
//					if (null != bs) {
//						ByteArrayInputStream arrayIn = new ByteArrayInputStream(bs);
//						objIn = new ObjectInputStream(arrayIn);
//						data.setValues((ValueList) objIn.readObject());
//					}
//				} catch (Exception e) { // 早期保存的数据没有原始电流电压，会爆出异常。
//					// ExceptionHandler.handleException(e);
//				} finally {
//					if (null != objIn)
//						try {
//							objIn.close();
//						} catch (IOException e) {
//							ExceptionHandler.handleException(e);
//						}
//				}
//				// Log.v(Consts.TAG, "" + data);
//			}
//		}
//		c.close();
//		// db.close();
//		return data;
//	}
//
//	@Override
//	public synchronized boolean deleteAll() {
//		try {
//			DBOpenHelper dbOpenHelper = getHelper();
//			// SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
//			// String table = Database.Data.TABLE_NAME;
//			// db.delete(table,null, null);
//			//// String drop="DROP TABLE "+Database.Data.TABLE_NAME; //
//			//// db.execSQL(drop);
//			//
//			// db.execSQL("VACUUM");// VACUUM 命令清除未使用的空间。;l
//			// db.close();
//			dbOpenHelper.reCreateDatas();
//			return true;
//		} catch (Exception e) {
//			e.printStackTrace();
//			return false;
//		}
//	}
//	
//	public synchronized void close() {
//		if(null!=db) {
//			db.close();
//			db=null;
//		}
//	}
//
//}
