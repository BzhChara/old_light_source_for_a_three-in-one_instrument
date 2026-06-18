package com.zkzk.pra.dal;

import java.util.List;

import com.zkzk.pra.entity.Data;

public interface IRecordDao {
	/**
	 * ����ѧ����¼
	 * 
	 * @param Record
	 */
	long insert(Data record);

	/**
	 * 
	 * @param whereClause
	 *            WHERE语句，例如：_id=?，不限值则可以输入Ϊnull
	 * @param whereArgs
	 *            WHERE的参ֵ
	 * @return 查询到的数据
	 */
	List<Data> query(String whereClause, String[] whereArgs);
	boolean query(List<Data> datas,String whereClause, String[] whereArgs);
	Data query(long id);
	int delete(long id);
	boolean deleteAll();
	int update(Data data);
	int deleteByTime(long time);
	
	void setUpload(long id,boolean uploaded);
}
