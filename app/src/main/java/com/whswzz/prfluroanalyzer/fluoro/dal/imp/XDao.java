package com.whswzz.prfluroanalyzer.fluoro.dal.imp;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;
import top.jemen.interfaces.ICallback;
import top.jemen.utils.ExceptionHandler;
import top.jemen.utils.LogUtil;

import com.whswzz.prfluroanalyzer.consts.Consts;
import com.whswzz.prfluroanalyzer.entity.IData;
import com.whswzz.prfluroanalyzer.enzyme.entity.EnzymeData;
import com.whswzz.prfluroanalyzer.fluoro.dal.Database;
import com.whswzz.prfluroanalyzer.fluoro.entity.FluData;
import com.whswzz.prfluroanalyzer.fluoro.entity.Hump;
import com.whswzz.prfluroanalyzer.fluoro.entity.ValueList;
import com.whswzz.prfluroanalyzer.photometer.entity.PhotometerData;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.xutils.DbManager;
import org.xutils.x;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.DbException;

public class XDao {
    public static final String NAME = "praio.db";
    private static DbManager.DaoConfig daoConfig;

    public static DbManager.DaoConfig getDaoConfig() {
        if (daoConfig == null) {
            daoConfig = new DbManager.DaoConfig().setDbName(NAME)
                    .setDbVersion(2)
                    .setAllowTransaction(true)  //有助于提高性能
//                    .setDbDir(new File("/sdcard/"))
                    .setDbOpenListener(new DbManager.DbOpenListener() {
                        @Override
                        public void onDbOpened(DbManager db) {
                            // 开启WAL, 对写入加速提升巨大
                            db.getDatabase().enableWriteAheadLogging();
                        }
                    })
                    .setDbUpgradeListener(new DbManager.DbUpgradeListener() {
                        @Override
                        public void onUpgrade(DbManager db, int oldVersion, int newVersion) {
                            for (int i = oldVersion; i < newVersion; i++) {
                                switch (i) {
                                    case 1://1-->2;
                                        try {

                                            db.addColumn(FluData.class,Database.CollaurumData.Columns.USER_PHONE);
                                            db.addColumn(FluData.class,Database.EnzymeData.Columns.USER_PHONE);
                                            db.addColumn(FluData.class,Database.PhotometerData.Columns.USER_PHONE);
                                            db.saveOrUpdate(db.findAll(FluData.class));//当前表中有这条isId则更新数据，没有则添加
                                        } catch (DbException e) {
                                            e.printStackTrace();
                                        }
                                        break;
                                }

                            }
                        }
                    });
        }

        return daoConfig;
    }

    public static DbManager getDb() {
        DbManager dbm = null;
        try {
			dbm= x.getDb(getDaoConfig());
		} catch (DbException e) {
			e.printStackTrace();
		}
        return dbm;
    }

    //插入teacher到数据库
    public static boolean insertToDataBase(FluData data) {
        DbManager db = getDb();

        try {
            db.save(data);
//            db.saveOrUpdate(data);
            LogUtil.d("执行插入数据库操作");
//                db.close();// 同一个库是单实例的, 尽量不要调用这个方法, 会自动释放.
            return true;
        } catch (DbException e) {
            e.printStackTrace();
        } finally {
        }
        return false;
    }

    //根据number查询teacher
    public static FluData findById(int id) {
        FluData data = null;
        DbManager db = getDb();
        try {
            data = db.selector(FluData.class).where("_id", "=", id).findFirst();
        } catch (DbException e) {
            e.printStackTrace();
        } finally {
//            try {
//                //                db.close();// 同一个库是单实例的, 尽量不要调用这个方法, 会自动释放.
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        }
        return data;
    }

    //根据number查询teacher
    public static List<FluData> findAllData() {
        List<FluData> datas = null;
        DbManager db = getDb();
        try {
            datas = db.selector(FluData.class)
                    .orderBy(Database.CollaurumData.Columns.TIME, true)
                    .findAll();
        } catch (DbException e) {
            ExceptionHandler.handleException(e);
        }
        return datas;
    }
    public static List<FluData> query(String sql){
    	return query(sql,null);
    }

    @SuppressLint("Range")
    public static List<FluData> query(String sql, List<FluData> datas) {
        if(null==datas)
        	datas = new LinkedList<>();
        DbManager db = getDb();
        try {
            Cursor c = db.execQuery(sql);
            if (c.moveToFirst()) {
                ObjectInputStream objIn = null;
                for (; !c.isAfterLast(); c.moveToNext()) {
                    try {
                        FluData data = new FluData();
                        data.setId(c.getInt(c.getColumnIndex(Database.CollaurumData.Columns.ID)));
                        data.setSn(c.getString(c.getColumnIndex(Database.CollaurumData.Columns.SN)));
                        data.setTime(c.getLong(c.getColumnIndex(Database.CollaurumData.Columns.TIME)));
                        data.setName(c.getString(c.getColumnIndex(Database.CollaurumData.Columns.SPECIMEN)));
                        data.setProj(c.getString(c.getColumnIndex(Database.CollaurumData.Columns.PROJ)));
                        data.setLimit(c.getFloat(c.getColumnIndex(Database.CollaurumData.Columns.LIMIT)));
                        data.setResult(c.getString(c.getColumnIndex(Database.CollaurumData.Columns.RESULT)));
                        data.setSourceOrg(c.getString(c.getColumnIndex(Database.CollaurumData.Columns.SOURCE_ORG)));
                        data.setSourceAddr(c.getString(c.getColumnIndex(Database.CollaurumData.Columns.SOURCE_ADDR)));
                        data.setWorkOrg(c.getString(c.getColumnIndex(Database.CollaurumData.Columns.USER_ORG)));
                        
                        byte bs[] = c.getBlob(c.getColumnIndex(Database.CollaurumData.Columns.VALUES));
                        ByteArrayInputStream arrayIn = new ByteArrayInputStream(bs);
                        objIn = new ObjectInputStream(arrayIn);
                        data.setValues((ValueList) objIn.readObject());

                        bs = c.getBlob(c.getColumnIndex(Database.CollaurumData.Columns.HUMP));
                        arrayIn = new ByteArrayInputStream(bs);
                        objIn = new ObjectInputStream(arrayIn);
                        data.setHump((Hump) objIn.readObject());
                        
                        data.setOperator(c.getString(c.getColumnIndex(Database.CollaurumData.Columns.OPERATOR)));
                        data.setUpLoded(c.getShort(c.getColumnIndex(Database.CollaurumData.Columns.UPLOADED)) == 1);
                        datas.add(data);
                    } catch (Exception e) {
                        ExceptionHandler.handleException(e);
                    } finally {
                        if (null != objIn)
                            try {
                                objIn.close();
                            } catch (IOException e) {
                                ExceptionHandler.handleException(e);
                            }
                    }

                }
            }


        } catch (DbException e) {
            e.printStackTrace();
        }

        return datas;
    }
    
    
    @SuppressLint("Range")
    public static List<FluData> queryQuick(String sql, List<FluData> datas) {
        if(null==datas)
        	datas = new LinkedList<>();
        DbManager db = getDb();
        LogUtil.d("执行查询语句："+sql);
        try {
            Cursor c = db.execQuery(sql);
            if (c.moveToFirst()) {
                ObjectInputStream objIn = null;
                for (; !c.isAfterLast(); c.moveToNext()) {
                    try {
                    	FluData data = new FluData();
                        data.setId(c.getInt(c.getColumnIndex(Database.CollaurumData.Columns.ID)));
                        data.setChannel(c.getString(c.getColumnIndex(Database.CollaurumData.Columns.CHANNEL)));
                        data.setSn(c.getString(c.getColumnIndex(Database.CollaurumData.Columns.SN)));
                        data.setTime(c.getLong(c.getColumnIndex(Database.CollaurumData.Columns.TIME)));
                        data.setName(c.getString(c.getColumnIndex(Database.CollaurumData.Columns.SPECIMEN)));
                        data.setProj(c.getString(c.getColumnIndex(Database.CollaurumData.Columns.PROJ)));
                        data.setLimit(c.getFloat(c.getColumnIndex(Database.CollaurumData.Columns.LIMIT)));
                        data.setResult(c.getString(c.getColumnIndex(Database.CollaurumData.Columns.RESULT)));
                        data.setSourceOrg(c.getString(c.getColumnIndex(Database.CollaurumData.Columns.SOURCE_ORG)));
                        data.setSourceAddr(c.getString(c.getColumnIndex(Database.CollaurumData.Columns.SOURCE_ADDR)));
                        data.setSourceOrgCode(c.getString(c.getColumnIndex(Database.CollaurumData.Columns.SOURCE_ORG_CODE)));
                        data.setSourceOrgType(c.getInt(c.getColumnIndex(Database.CollaurumData.Columns.SOURCE_ORG_TYPE)));
                        
                        data.setWorkOrg(c.getString(c.getColumnIndex(Database.CollaurumData.Columns.USER_ORG)));
                        
                        data.setUsrCode(c.getString(c.getColumnIndex(Database.CollaurumData.Columns.CODE)));
                        data.setUserContact(c.getString(c.getColumnIndex(Database.CollaurumData.Columns.USER_CONTACT)));
                        data.setUserPhone(c.getString(c.getColumnIndex(Database.CollaurumData.Columns.USER_PHONE)));
                        data.setUserAddr(c.getString(c.getColumnIndex(Database.CollaurumData.Columns.USER_ADDR)));
                        data.setUserOrg(c.getString(c.getColumnIndex(Database.CollaurumData.Columns.USER_ORG)));

                        data.setToken(c.getString(c.getColumnIndex(Database.CollaurumData.Columns.TOKEN)));
//                        byte bs[] = c.getBlob(c.getColumnIndex(Database.Data.Columns.VALUES));
//                        ByteArrayInputStream arrayIn = new ByteArrayInputStream(bs);
//                        objIn = new ObjectInputStream(arrayIn);
//                        data.setValues((ValueList) objIn.readObject());
//
//                        bs = c.getBlob(c.getColumnIndex(Database.Data.Columns.HUMP));
//                        arrayIn = new ByteArrayInputStream(bs);
//                        objIn = new ObjectInputStream(arrayIn);
//                        data.setHump((Hump) objIn.readObject());
                        
                        data.setOperator(c.getString(c.getColumnIndex(Database.CollaurumData.Columns.OPERATOR)));
                        data.setUpLoded(c.getShort(c.getColumnIndex(Database.CollaurumData.Columns.UPLOADED)) == 1);
                        data.setT(c.getFloat(c.getColumnIndex(Database.CollaurumData.Columns.T)));
                        data.setC(c.getFloat(c.getColumnIndex(Database.CollaurumData.Columns.C)));
                        
                        datas.add(data);
                        
                    } catch (Exception e) {
                        ExceptionHandler.handleException(e);
                    } finally {
                        if (null != objIn)
                            try {
                                objIn.close();
                            } catch (IOException e) {
                                ExceptionHandler.handleException(e);
                            }
                    }

                }//for循环结束
                LogUtil.d("查到的数据数量："+datas.size());
            }else {
            	LogUtil.d("movetofirst 返回false");
            }


        } catch (DbException e) {
            e.printStackTrace();
        }

        return datas;
    }

    


    public static long getCount() {
        long dataNum = 0;
        Cursor cursor = null;
        try {
            DbManager db = getDb();

//            String createIndex = "create index if not exists ia on " +Database.Data.TABLE_NAME + "("
//                    + Database.Data.Columns.ID + "," + Database.Data.Columns.TIME + "," + Database.Data.Columns.PROJ
//                    + "," + Database.Data.Columns.SPECIMEN + ")";
//            db.execQuery(createIndex);// 创建索引
            StringBuilder sbSql = new StringBuilder("SELECT COUNT(*) FROM datas");

            cursor = db.execQuery(sbSql.toString());
            if (cursor != null && cursor.moveToFirst()) {
                dataNum = cursor.getLong(0);// 获取数据库中总数据条数
                Log.d("jemen", "dataNum=" + dataNum + ",count=" + cursor.getCount());
            }

        } catch (Exception e) {
            ExceptionHandler.handleException(e);
        } finally {
            if (null != cursor) {
                cursor.close();
            }
        }
        return dataNum;
    }


    public static int deleteCollarurumData(long id) {
        try {
            DbManager db = getDb();
            WhereBuilder builder = WhereBuilder.b(Database.CollaurumData.Columns.ID, "=", id);
            return db.delete(FluData.class, builder);
        } catch (DbException e) {
            ExceptionHandler.handleException(e);
        }
        return -1;
    }

    public static int deletePhotometerData(long id) {
        try {
            DbManager db = getDb();
            WhereBuilder builder = WhereBuilder.b(Database.PhotometerData.Columns.ID, "=", id);
            return db.delete(PhotometerData.class, builder);
        } catch (DbException e) {
            ExceptionHandler.handleException(e);
        }
        return -1;
    }
    public static int deleteEnzymeData(long id) {
        try {
            DbManager db = getDb();
            WhereBuilder builder = WhereBuilder.b(Database.EnzymeData.Columns.ID, "=", id);
            return db.delete(EnzymeData.class, builder);
        } catch (DbException e) {
            ExceptionHandler.handleException(e);
        }
        return -1;
    }


    public static boolean deleteAll(Class cls) {
        try {
            DbManager db = getDb();
            db.delete(cls);

            return true;
        } catch (DbException e) {
            ExceptionHandler.handleException(e);
        }
        return false;
    }

	@SuppressLint("Range")
    public static List<EnzymeData> queryEnzyme(String sql, List<EnzymeData> datas) {
		 if(null==datas)
	        	datas = new LinkedList<>();
	        DbManager db = getDb();
	        LogUtil.d("执行查询语句："+sql);
	        try {
	            Cursor c = db.execQuery(sql);
	            if (c.moveToFirst()) {
	                ObjectInputStream objIn = null;
	                for (; !c.isAfterLast(); c.moveToNext()) {
	                    try {
	                    	EnzymeData data = new EnzymeData();
	                        data.setId(c.getInt(c.getColumnIndex(Database.EnzymeData.Columns.ID)));
	                        data.setChannel(c.getString(c.getColumnIndex(Database.EnzymeData.Columns.CHANNEL)));
	                        
	                        data.setSn(c.getString(c.getColumnIndex(Database.EnzymeData.Columns.SN)));
	                       
	                        data.setTime(c.getLong(c.getColumnIndex(Database.EnzymeData.Columns.TIME)));
	                        data.setName(c.getString(c.getColumnIndex(Database.EnzymeData.Columns.SPECIMEN)));
	                        data.setProj(c.getString(c.getColumnIndex(Database.EnzymeData.Columns.PROJ)));
	                        data.setLimit(c.getFloat(c.getColumnIndex(Database.EnzymeData.Columns.LIMIT)));
	                        data.setResult(c.getString(c.getColumnIndex(Database.EnzymeData.Columns.RESULT)));
	                        data.setSourceOrg(c.getString(c.getColumnIndex(Database.EnzymeData.Columns.SOURCE_ORG)));
	                        data.setSourceAddr(c.getString(c.getColumnIndex(Database.EnzymeData.Columns.SOURCE_ADDR)));
	                        data.setSourceOrgCode(c.getString(c.getColumnIndex(Database.EnzymeData.Columns.SOURCE_ORG_CODE)));
	                        data.setSourceOrgType(c.getInt(c.getColumnIndex(Database.EnzymeData.Columns.SOURCE_ORG_TYPE)));

                            data.setUserContact(c.getString(c.getColumnIndex(Database.EnzymeData.Columns.USER_CONTACT)));
	                        data.setUserPhone(c.getString(c.getColumnIndex(Database.EnzymeData.Columns.USER_PHONE)));
	                        data.setUserAddr(c.getString(c.getColumnIndex(Database.EnzymeData.Columns.USER_ADDR)));
                            data.setUserOrg(c.getString(c.getColumnIndex(Database.EnzymeData.Columns.USER_ORG)));
                            

	                        data.setWorkOrg(c.getString(c.getColumnIndex(Database.EnzymeData.Columns.USER_ORG)));
	                        data.setCode(c.getString(c.getColumnIndex(Database.EnzymeData.Columns.CODE)));
	                        data.setToken(c.getString(c.getColumnIndex(Database.EnzymeData.Columns.TOKEN)));
	                        
	                        
	                        
//	                        byte bs[] = c.getBlob(c.getColumnIndex(Database.Data.Columns.VALUES));
//	                        ByteArrayInputStream arrayIn = new ByteArrayInputStream(bs);
//	                        objIn = new ObjectInputStream(arrayIn);
//	                        data.setValues((ValueList) objIn.readObject());
	//
//	                        bs = c.getBlob(c.getColumnIndex(Database.Data.Columns.HUMP));
//	                        arrayIn = new ByteArrayInputStream(bs);
//	                        objIn = new ObjectInputStream(arrayIn);
//	                        data.setHump((Hump) objIn.readObject());
	                        
	                        data.setOperator(c.getString(c.getColumnIndex(Database.CollaurumData.Columns.OPERATOR)));
	                        data.setUpLoded(c.getShort(c.getColumnIndex(Database.CollaurumData.Columns.UPLOADED)) == 1);
	                        
	                        datas.add(data);
	                        
	                    } catch (Exception e) {
	                        ExceptionHandler.handleException(e);
	                    } finally {
	                        if (null != objIn)
	                            try {
	                                objIn.close();
	                            } catch (IOException e) {
	                                ExceptionHandler.handleException(e);
	                            }
	                    }

	                }//for循环结束
	                LogUtil.d("查到的数据数量："+datas.size());
	            }else {
	            	LogUtil.d("movetofirst 返回false");
	            }


	        } catch (DbException e) {
	            e.printStackTrace();
	        }

	        return datas;
	}
	
	
	
	public static List<PhotometerData> queryPhotometer(String sql, List<PhotometerData> datas) {
		 if(null==datas)
	        	datas = new LinkedList<>();
	        DbManager db = getDb();
	        LogUtil.d("执行查询语句："+sql);
	        try {
	            Cursor c = db.execQuery(sql);
	            if (c.moveToFirst()) {
	                ObjectInputStream objIn = null;
	                for (; !c.isAfterLast(); c.moveToNext()) {
	                    try {
	                    	PhotometerData data = new PhotometerData();
	                        data.setId(c.getInt(c.getColumnIndex(Database.PhotometerData.Columns.ID)));
	                        data.setChannel(c.getString(c.getColumnIndex(Database.PhotometerData.Columns.CHANNEL)));
	                        
	                        data.setSn(c.getString(c.getColumnIndex(Database.PhotometerData.Columns.SN)));
	                       
	                        data.setTime(c.getLong(c.getColumnIndex(Database.PhotometerData.Columns.TIME)));
	                        data.setName(c.getString(c.getColumnIndex(Database.PhotometerData.Columns.SPECIMEN)));
	                        data.setProj(c.getString(c.getColumnIndex(Database.PhotometerData.Columns.PROJ)));
	                        data.setLimit(c.getFloat(c.getColumnIndex(Database.PhotometerData.Columns.LIMIT)));
	                        data.setAbsorbancy(c.getFloat(c.getColumnIndex(Database.PhotometerData.Columns.ABSORBANCY)));
	                        data.setResult(c.getString(c.getColumnIndex(Database.PhotometerData.Columns.RESULT)));
	                        data.setSourceOrg(c.getString(c.getColumnIndex(Database.PhotometerData.Columns.SOURCE_ORG)));
	                        data.setSourceAddr(c.getString(c.getColumnIndex(Database.PhotometerData.Columns.SOURCE_ADDR)));
	                        data.setSourceOrgCode(c.getString(c.getColumnIndex(Database.PhotometerData.Columns.SOURCE_ORG_CODE)));
	                        data.setSourceOrgType(c.getInt(c.getColumnIndex(Database.PhotometerData.Columns.SOURCE_ORG_TYPE)));
	                        
	                        data.setWorkOrg(c.getString(c.getColumnIndex(Database.PhotometerData.Columns.USER_ORG)));
	                        data.setUserCode(c.getString(c.getColumnIndex(Database.PhotometerData.Columns.CODE)));
	                        data.setToken(c.getString(c.getColumnIndex(Database.PhotometerData.Columns.TOKEN)));
	                        
	                        
	                        
//	                        byte bs[] = c.getBlob(c.getColumnIndex(Database.Data.Columns.VALUES));
//	                        ByteArrayInputStream arrayIn = new ByteArrayInputStream(bs);
//	                        objIn = new ObjectInputStream(arrayIn);
//	                        data.setValues((ValueList) objIn.readObject());
	//
//	                        bs = c.getBlob(c.getColumnIndex(Database.Data.Columns.HUMP));
//	                        arrayIn = new ByteArrayInputStream(bs);
//	                        objIn = new ObjectInputStream(arrayIn);
//	                        data.setHump((Hump) objIn.readObject());
	                        
	                        data.setOperator(c.getString(c.getColumnIndex(Database.PhotometerData.Columns.OPERATOR)));
	                        data.setUpLoded(c.getShort(c.getColumnIndex(Database.PhotometerData.Columns.UPLOADED)) == 1);
                            data.setUserPhone(c.getString(c.getColumnIndex(Database.PhotometerData.Columns.USER_PHONE)));
                            data.setUserContact(c.getString(c.getColumnIndex(Database.PhotometerData.Columns.USER_CONTACT)));
	                        
	                        datas.add(data);
	                        
	                    } catch (Exception e) {
	                        ExceptionHandler.handleException(e);
	                    } finally {
	                        if (null != objIn)
	                            try {
	                                objIn.close();
	                            } catch (IOException e) {
	                                ExceptionHandler.handleException(e);
	                            }
	                    }

	                }//for循环结束
	                LogUtil.d("查到的数据数量："+datas.size());
	            }else {
	            	LogUtil.d("movetofirst 返回false");
	            }


	        } catch (DbException e) {
	            e.printStackTrace();
	        }

	        return datas;
	}
	
	
	public static void save(final Object datas, final ICallback iCallback) {
		if(null==datas) {
			iCallback.onFailed("没有数据");
			return;
		}
		AsyncTask<Void, Void, String> at=new AsyncTask<Void, Void, String>() {
			@Override
			protected String doInBackground(Void... params) {
					try {
						getDb().save(datas);
						return Consts.SUCCESS;
					} catch (DbException e) {
						e.printStackTrace();
						return "数据库插入异常";
					}
			}
			@Override
			protected void onPostExecute(String result) {
				if(null==iCallback) {
					return;
				}
				if(Consts.SUCCESS.equals(result)) {
					iCallback.onSuccess(result);
				}else {
					iCallback.onFailed(result);
				}
			}
		};
		at.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}
}
