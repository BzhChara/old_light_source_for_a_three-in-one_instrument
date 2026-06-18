package com.whswzz.prfluroanalyzer.fluoro.dal.imp;

import android.database.Cursor;
import top.jemen.utils.ExceptionHandler;

import org.xutils.db.converter.ColumnConverter;
import org.xutils.db.sqlite.ColumnDbType;

import com.whswzz.prfluroanalyzer.fluoro.dal.Database;
import com.whswzz.prfluroanalyzer.fluoro.entity.Hump;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class HumpConvertor implements ColumnConverter<Hump> {

    @Override
    public Hump getFieldValue(Cursor c, int i) {
    	Hump hump = null;
        ObjectInputStream objIn = null;
        try {
            byte bs[] = c.getBlob(c.getColumnIndex(Database.CollaurumData.Columns.HUMP));
            ByteArrayInputStream arrayIn = new ByteArrayInputStream(bs);
            objIn = new ObjectInputStream(arrayIn);
            hump=(Hump) objIn.readObject();
        } catch (Exception e) {
            ExceptionHandler.handleException(e);
        }finally {
            try {
                if(null!=objIn)
                    objIn.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return hump;
    }

    @Override
    public Object fieldValue2DbValue(Hump hump) {
        byte[] bs = new byte[0];
        ByteArrayOutputStream bout = null;
        ObjectOutputStream objOut = null;
        try {
           bout = new ByteArrayOutputStream();
           objOut = new ObjectOutputStream(bout);
            objOut.writeObject(hump);
            objOut.flush();
            bs=bout.toByteArray();
            objOut.close();
            bout.close();
        }catch (Exception e) {
            ExceptionHandler.handleException(e);
        }finally {
            if(null!=objOut){
                try {
                    objOut.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if(null!=bout){
                try {
                    bout.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
        return bs;
    }

    @Override
    public ColumnDbType getColumnDbType() {
        return ColumnDbType.BLOB;
    }
}
