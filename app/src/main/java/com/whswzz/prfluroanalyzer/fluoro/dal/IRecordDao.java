package com.whswzz.prfluroanalyzer.fluoro.dal;


import java.util.List;

import com.whswzz.prfluroanalyzer.fluoro.entity.FluData;


public interface IRecordDao {
	/**
	 * 
	 * @param Record
	 */
	long insert(FluData record);

	/**
	 * 
	 * @param whereClause
	 *            WHERE语句，例如：_id=?，不限值则可以输入Ϊnull
	 * @param whereArgs
	 *            WHERE的参ֵ
	 * @return 查询到的数据
	 */
	List<FluData> query(String whereClause, String[] whereArgs);
	boolean query(List<FluData> datas, String whereClause, String[] whereArgs);
	FluData query(long id);
	int delete(long id);
	boolean deleteAll();
	int update(FluData data);
	int deleteByTime(long time);
	
	public long getCount(String where, String[] args);
}
