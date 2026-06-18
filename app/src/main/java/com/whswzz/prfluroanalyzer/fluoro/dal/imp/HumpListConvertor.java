//package cn.whhas.fluoroanalyzer.dal.imp;
//
//import android.database.Cursor;
//import cn.whhas.fluoroanalyzer.dal.Database;
//import cn.whhas.fluoroanalyzer.entity.HumpList;
//import top.jemen.utils.ExceptionHandler;
//
//import org.xutils.db.converter.ColumnConverter;
//import org.xutils.db.sqlite.ColumnDbType;
//
//import java.io.ByteArrayInputStream;
//import java.io.ByteArrayOutputStream;
//import java.io.IOException;
//import java.io.ObjectInputStream;
//import java.io.ObjectOutputStream;
//
//public class HumpListConvertor implements ColumnConverter<HumpList> {
//
//    @Override
//    public HumpList getFieldValue(Cursor c, int i) {
//        HumpList peaks = null;
//        ObjectInputStream objIn = null;
//        try {
//            byte bs[] = c.getBlob(c.getColumnIndex(Database.Data.Columns.));
//            ByteArrayInputStream arrayIn = new ByteArrayInputStream(bs);
//            objIn = new ObjectInputStream(arrayIn);
//            peaks=(HumpList) objIn.readObject();
//        } catch (Exception e) {
//            ExceptionHandler.handleException(e);
//        }finally {
//            try {
//                if(null!=objIn)
//                    objIn.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        return peaks;
//    }
//
//    @Override
//    public Object fieldValue2DbValue(HumpList peaks) {
//        byte[] bs = new byte[0];
//        ByteArrayOutputStream bout = null;
//        ObjectOutputStream objOut = null;
//        try {
//           bout = new ByteArrayOutputStream();
//           objOut = new ObjectOutputStream(bout);
//            objOut.writeObject(peaks);
//            objOut.flush();
//            bs=bout.toByteArray();
//            objOut.close();
//            bout.close();
//        }catch (Exception e) {
//            ExceptionHandler.handleException(e);
//        }finally {
//            if(null!=objOut){
//                try {
//                    objOut.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            if(null!=bout){
//                try {
//                    bout.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//            }
//        }
//        return bs;
//    }
//
//    @Override
//    public ColumnDbType getColumnDbType() {
//        return ColumnDbType.BLOB;
//    }
//}
