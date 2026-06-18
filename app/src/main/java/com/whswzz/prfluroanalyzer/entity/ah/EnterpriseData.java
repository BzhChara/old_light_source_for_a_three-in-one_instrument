package com.whswzz.prfluroanalyzer.entity.ah;

import java.util.List;

public class EnterpriseData
{
    private int total;

    private List<Enterprise> data;

    public void setTotal(int total){
        this.total = total;
    }
    public int getTotal(){
        return this.total;
    }
    public void setData(List<Enterprise> data){
        this.data = data;
    }
    public List<Enterprise> getData(){
        return this.data;
    }
	@Override
	public String toString() {
		return "Data [total=" + total + ", data=" + data + "]";
	}
    
    
    
}