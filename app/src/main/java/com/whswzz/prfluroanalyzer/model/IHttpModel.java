package com.whswzz.prfluroanalyzer.model;

import com.whswzz.prfluroanalyzer.entity.IData;

import java.util.List;

import top.jemen.interfaces.ICallback;

public interface IHttpModel {
     void send(final Object data, final ICallback callback) ;
}
