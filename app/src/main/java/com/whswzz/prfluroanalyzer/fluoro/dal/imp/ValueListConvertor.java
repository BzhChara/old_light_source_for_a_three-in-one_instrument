package com.whswzz.prfluroanalyzer.fluoro.dal.imp;

import android.database.Cursor;
import top.jemen.utils.ExceptionHandler;

import org.xutils.db.converter.ColumnConverter;
import org.xutils.db.sqlite.ColumnDbType;

import com.whswzz.prfluroanalyzer.fluoro.dal.Database;
import com.whswzz.prfluroanalyzer.fluoro.entity.ValueList;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

public class ValueListConvertor implements ColumnConverter<ValueList> {

    @Override
    public ValueList getFieldValue(Cursor c, int i) {
        ValueList values = null;
        ObjectInputStream objIn = null;
        try {
            byte bs[] = c.getBlob(c.getColumnIndex(Database.CollaurumData.Columns.VALUES));
            ByteArrayInputStream arrayIn = new ByteArrayInputStream(bs);
            objIn = new ObjectInputStream(arrayIn);
            values=(ValueList) objIn.readObject();
        } catch (Exception e) {
            ExceptionHandler.handleException(e);
        }finally {
            try {
                objIn.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return values;
    }

    @Override
    public Object fieldValue2DbValue(ValueList values) {
        byte[] bs = new byte[0];
        try {
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            ObjectOutputStream objOut = new ObjectOutputStream(bout);
            objOut.writeObject(values);
            objOut.flush();
            bs=bout.toByteArray();
            objOut.close();
            bout.close();
        }catch (Exception e) {
            ExceptionHandler.handleException(e);
        }
        return bs;
    }

    @Override
    public ColumnDbType getColumnDbType() {
        return ColumnDbType.BLOB;
    }
}
