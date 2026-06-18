package com.whswzz.prfluroanalyzer.fluoro;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;

import com.huawei.hms.hmsscankit.ScanUtil;
import com.huawei.hms.ml.scan.HmsScan;
import com.huawei.hms.ml.scan.HmsScanAnalyzerOptions;
import com.whswzz.prfluroanalyzer.app.MyApp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import top.jemen.utils.LogUtil;

public class Tool {
    // 阈值0-255

    /**
     * 图像二值化处理
     *r 二值化的分割比例，介于0到1之间
     */
    public static Bitmap binarization(Bitmap bmp,double d)  {
        // 获取当前图片的高,宽,ARGB
        int w =bmp.getWidth();
        int h= bmp.getHeight();
        int arr[][] = new int[w][h];


        List<Integer> is=new ArrayList<>();
        // 获取图片每一像素点的灰度值
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                // getRGB()返回默认的RGB颜色模型(十进制)
                arr[i][j] = getImageGray(bmp.getPixel(i, j));// 该点的灰度值
                if(i%3==0&&j%3==0){
                    is.add(arr[i][j]);
                }
            }
        }
        Collections.sort(is);
        int YZ=is.get((int) (is.size()*d));
        LogUtil.d("阈值为："+YZ);
//        int t1=is.get(is.size()/6);
//        int t2=is.get(is.size()/2);

        // 和预先设置的阈值大小进行比较，大的就显示为255即白色，小的就显示为0即黑色
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                int v=getGray(arr, i, j, w, h);
//                if ( v>t2) {
//                    bmp.setPixel(i,j,Color.WHITE);
//                } else if(v<t1) {
//                    bmp.setPixel(i,j,Color.BLACK);
//                }
                if(v>YZ){
                    bmp.setPixel(i,j,Color.WHITE);
                }else{
                    bmp.setPixel(i,j,Color.BLACK);
                }
            }

        }
       return bmp;
    }

    /**
     * 图像的灰度处理
     * 利用浮点算法：Gray = R*0.3 + G*0.59 + B*0.11;
     *
     * @param rgb 该点的RGB值
     * @return 返回处理后的灰度值
     */
    private static int getImageGray(int rgb) {
        String argb = Integer.toHexString(rgb);// 将十进制的颜色值转为十六进制
        // argb分别代表透明,红,绿,蓝 分别占16进制2位
        int r = Integer.parseInt(argb.substring(2, 4), 16);// 后面参数为使用进制
        int g = Integer.parseInt(argb.substring(4, 6), 16);
        int b = Integer.parseInt(argb.substring(6, 8), 16);
        int gray = (int) (r*0.3 + g*0.59 + b*0.11);
        return gray;
    }

    /**
     * 自己加周围8个灰度值再除以9，算出其相对灰度值
     *
     * @param gray
     * @param x 要计算灰度的点的横坐标
     * @param y 要计算灰度的点的纵坐标
     * @param w 图像的宽度
     * @param h 图像的高度
     * @return
     */
    public static int getGray(int gray[][], int x, int y, int w, int h) {
        int rs = gray[x][y] + (x == 0 ? 255 : gray[x - 1][y]) + (x == 0 || y == 0 ? 255 : gray[x - 1][y - 1])
                + (x == 0 || y == h - 1 ? 255 : gray[x - 1][y + 1]) + (y == 0 ? 255 : gray[x][y - 1])
                + (y == h - 1 ? 255 : gray[x][y + 1]) + (x == w - 1 ? 255 : gray[x + 1][y])
                + (x == w - 1 || y == 0 ? 255 : gray[x + 1][y - 1])
                + (x == w - 1 || y == h - 1 ? 255 : gray[x + 1][y + 1]);
        return rs / 9;
    }

    /**
     * 二值化后的图像的开运算：先腐蚀再膨胀（用于去除图像的小黑点）
     *
     */
    public static Bitmap opening(Bitmap bmp0)  {
        // 获取当前图片的高,宽,ARGB
        int w = bmp0.getWidth();
        int h =bmp0.getHeight();
        int arr[][] = new int[w][h];
        // 获取图片每一像素点的灰度值
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                // getRGB()返回默认的RGB颜色模型(十进制)
                arr[i][j] = getImageGray(bmp0.getPixel(i, j));// 该点的灰度值
            }
        }

        Bitmap bmp1=Bitmap.createBitmap(w,h,bmp0.getConfig());
        // 临时存储腐蚀后的各个点的亮度
        int temp[][] = new int[w][h];
        // 1.先进行腐蚀操作
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                /*
                 * 为0表示改点和周围8个点都是黑，则该点腐蚀操作后为黑
                 * 由于公司图片态模糊，完全达到9个点全为黑的点太少，最后效果很差，故改为了小于30
                 * （写30的原因是，当只有一个点为白，即总共255，调用getGray方法后得到255/9 = 28）
                 */
                if (getGray(arr, i, j, w, h) < 100) {
                    temp[i][j] = 0;
                } else{
                    temp[i][j] = 255;
                }
            }
        }

        // 2.再进行膨胀操作
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                bmp1.setPixel(i,j,Color.WHITE);
            }
        }
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                // 为0表示改点和周围8个点都是黑，则该点腐蚀操作后为黑
                if (temp[i][j] == 0) {
                    bmp1.setPixel(i, j, Color.BLACK);
                    if(i > 0) {
                        bmp1.setPixel(i-1, j, Color.BLACK);
                    }
                    if (j > 0) {
                        bmp1.setPixel(i, j-1, Color.BLACK);
                    }
                    if (i > 0 && j > 0) {
                        bmp1.setPixel(i-1, j-1, Color.BLACK);
                    }
                    if (j < h-1) {
                        bmp1.setPixel(i, j+1, Color.BLACK);
                    }
                    if (i < w-1) {
                        bmp1.setPixel(i+1, j, Color.BLACK);
                    }
                    if (i < w-1 && j > 0) {
                        bmp1.setPixel(i+1, j-1, Color.BLACK);
                    }
                    if (i < w-1 && j < h-1) {
                        bmp1.setPixel(i+1, j+1, Color.BLACK);
                    }
                    if (i > 0 && j < h-1) {
                        bmp1.setPixel(i-1, j+1, Color.BLACK);
                    }
                }else{
                    
                }
            }
        }

       return bmp1;
    }


    public static Bitmap bitMapScale(Bitmap bitmap,float scale) {
        Matrix matrix = new Matrix();
        matrix.postScale(scale,scale); //长和宽放大缩小的比例
        Bitmap resizeBmp = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
        return resizeBmp;
    }


    /**
     * 华为的比zxing加openCV优化要强得多
     * @param bmp
     * @return
     */
    public static String HWQRDecode(Bitmap bmp){
        HmsScan[] result = ScanUtil.decodeWithBitmap(MyApp.getApp(), bmp, new HmsScanAnalyzerOptions.Creator().setHmsScanTypes(0).setPhotoMode(false).create());
        if(null!=result&&result.length>0){
            return result[0].getOriginalValue();
        }
        return null;
    }

}
