package com.whswzz.prfluroanalyzer.entity.ah;

import java.util.ArrayList;
import java.util.List;
public class ProductData
{
    private int total;

    private List<Product> data;

    public void setTotal(int total){
        this.total = total;
    }
    public int getTotal(){
        return this.total;
    }
    public void setData(List<Product> data){
        this.data = data;
    }
    public List<Product> getData(){
        return this.data;
    }
}
