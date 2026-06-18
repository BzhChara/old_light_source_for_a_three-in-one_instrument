package top.jemen.utils;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.EncodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.ResultPoint;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

//import org.opencv.android.Utils;
//import org.opencv.core.Mat;
//import org.opencv.core.MatOfPoint;
//import org.opencv.core.MatOfPoint2f;
//import org.opencv.core.Point;
//import org.opencv.core.Rect;
//import org.opencv.core.RotatedRect;
//import org.opencv.core.Size;
//import org.opencv.imgcodecs.Imgcodecs;
//import org.opencv.imgproc.CLAHE;
//import org.opencv.imgproc.Imgproc;

public class QRCodeUtil {
	/**
	 * 生成自定义二维码,jemen增加了去除白边的一些代码
	 *
	 * @param content
	 *            字符串内容
	 * @param width
	 *            二维码宽度
	 * @param height
	 *            二维码高度
	 * @param character_set
	 *            编码方式（一般使用UTF-8）
	 * @param error_correction_level
	 *            容错率 L：7% M：15% Q：25% H：35%
	 * @param margin
	 *            空白边距（二维码与边框的空白区域）
	 * @param color_black
	 *            黑色色块
	 * @param color_white
	 *            白色色块
	 * @param logoBitmap
	 *            logo图片（传null时不添加logo）
	 * @param logoPercent
	 *            logo所占百分比
	 * @param bitmap_black
	 *            用来代替黑色色块的图片（传null时不代替）
	 * @return
	 */
	public static Bitmap createQRCodeBitmapCutted(String content, int width, int height, String character_set,
			String error_correction_level, String margin, int color_black, int color_white, Bitmap logoBitmap,
			float logoPercent, Bitmap bitmap_black) {
		// 字符串内容判空
		if (TextUtils.isEmpty(content)) {
			return null;
		}
		// 宽和高>=0
		if (width < 0 || height < 0) {
			return null;
		}
		try {
			/** 1.设置二维码相关配置,生成BitMatrix(位矩阵)对象 */
			Hashtable<EncodeHintType, String> hints = new Hashtable<>();
			// 字符转码格式设置
			if (!TextUtils.isEmpty(character_set)) {
				hints.put(EncodeHintType.CHARACTER_SET, character_set);
			}
			// 容错率设置
			if (!TextUtils.isEmpty(error_correction_level)) {
				hints.put(EncodeHintType.ERROR_CORRECTION, error_correction_level);
			}
			// 空白边距设置
			if(!TextUtils.isEmpty(margin)) {
				hints.put(EncodeHintType.MARGIN, margin);
			}
			/** 2.将配置参数传入到QRCodeWriter的encode方法生成BitMatrix(位矩阵)对象 */
			BitMatrix bitMatrix = new QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints);

			/** 3.创建像素数组,并根据BitMatrix(位矩阵)对象为数组元素赋颜色值 */
			if (bitmap_black != null) {
				// 从当前位图按一定的比例创建一个新的位图
				bitmap_black = Bitmap.createScaledBitmap(bitmap_black, width, height, false);
			}
			
			int w=bitMatrix.getWidth(),h=bitMatrix.getHeight();
			int l = 0,t = 0,r = 0,b = 0;
			boolean bl = false,bt = false,br = false,bb = false;
			out:for(int i=0;i<w;i++) {
				for(int j=0;j<h;j++) {
					if(!bl) {
						if(bitMatrix.get(i, j)) {
							l=i;
							bl=true;
						}
					}
					if(!br) {
						if(bitMatrix.get(w-1-i, j)) {
							r=w-1-i;
							br=true;
						}
					}
					
					if(!bt) {
						if(bitMatrix.get(i, j)) {
							t=j;
							bt=true;
						}
					}
					if(!bb) {
						if(bitMatrix.get(i, h-1-j)) {
							b=h-1-j;
							bb=true;
						}
					}
					
					if(bl&&bt&&br&&bb) {
						break out;
					}
					
				}
			}
//			LogUtil.d("l,t,r,b:"+l+","+t+","+r+","+b);
			
			
			w=r-l+1;
			h=b-t+1;
			int n=w*h;
			if(n<=0) {
				return null;
			}
			int[] pixels = new int[n];
			for (int y = 0; y <h; y++) {
				for (int x = 0; x <w; x++) {
					// bitMatrix.get(x,y)方法返回true是黑色色块，false是白色色块
					if (bitMatrix.get(l+x, t+y)) {// 黑色色块像素设置
						if (bitmap_black != null) {// 图片不为null，则将黑色色块换为新位图的像素。
							pixels[y * w + x] = bitmap_black.getPixel(x, y);
						} else {
							pixels[y * w + x] = color_black;
						}
					} else {
						pixels[y * w + x] = color_white;// 白色色块像素设置
					}
				}
			}

			/** 4.创建Bitmap对象,根据像素数组设置Bitmap每个像素点的颜色值,并返回Bitmap对象 */
			Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
			bitmap.setPixels(pixels, 0,w, 0, 0, w,h);

			/** 5.为二维码添加logo图标 */
			if (logoBitmap != null) {
				return addLogo(bitmap, logoBitmap, logoPercent);
			}
			return bitmap;
		} catch (WriterException e) {
			e.printStackTrace();
			return null;
		}
	}

	
	
	
	
	
	
	/**
	 * 向二维码中间添加logo图片(图片合成)
	 *
	 * @param srcBitmap
	 *            原图片（生成的简单二维码图片）
	 * @param logoBitmap
	 *            logo图片
	 * @param logoPercent
	 *            百分比 (用于调整logo图片在原图片中的显示大小, 取值范围[0,1] )
	 *            原图片是二维码时,建议使用0.2F,百分比过大可能导致二维码扫描失败。
	 * @return
	 */
	@Nullable
	private static Bitmap addLogo(@Nullable Bitmap srcBitmap, @Nullable Bitmap logoBitmap, float logoPercent) {
		if (srcBitmap == null) {
			return null;
		}
		if (logoBitmap == null) {
			return srcBitmap;
		}
		// 传值不合法时使用0.2F
		if (logoPercent < 0F || logoPercent > 1F) {
			logoPercent = 0.2F;
		}

		/** 1. 获取原图片和Logo图片各自的宽、高值 */
		int srcWidth = srcBitmap.getWidth();
		int srcHeight = srcBitmap.getHeight();
		int logoWidth = logoBitmap.getWidth();
		int logoHeight = logoBitmap.getHeight();

		/** 2. 计算画布缩放的宽高比 */
		float scaleWidth = srcWidth * logoPercent / logoWidth;
		float scaleHeight = srcHeight * logoPercent / logoHeight;

		/** 3. 使用Canvas绘制,合成图片 */
		Bitmap bitmap = Bitmap.createBitmap(srcWidth, srcHeight, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		canvas.drawBitmap(srcBitmap, 0, 0, null);
		canvas.scale(scaleWidth, scaleHeight, srcWidth / 2, srcHeight / 2);
		canvas.drawBitmap(logoBitmap, srcWidth / 2 - logoWidth / 2, srcHeight / 2 - logoHeight / 2, null);

		return bitmap;
	}

	
	/**
	 * 默认参数生成简单二维码
	 * @param content                字符串内容
	 * @param width                  二维码宽度
	 * @param height                 二维码高度
	 * @return BitMap
	 */
	public static Bitmap getCodeBitmap(String content, int width,int height) {
		int d =width<height?width:height;
		if(d<30) {
			d=30;
		}
		return 	createQRCodeBitmap(content, d, d,"UTF-8","Q", "0", Color.BLACK, Color.WHITE,null,0,null);
	 }

	
	/**
	 * 默认参数生成简单二维码
	 * @param content                字符串内容
	 * @param width                  二维码宽度
	 * @param height                 二维码高度
	 * @return BitMap
	 */
	public static Bitmap logoCode(String content, int width,int height,Bitmap logo) {
		int d =width<height?width:height;
		return 	createQRCodeBitmap(content, d, d,"UTF-8","Q", "1", Color.BLACK, Color.WHITE,
				logo,logo==null?0:0.2f,null);
	 }
	
	
	
	
	
	
	/**
	 * 生成自定义二维码
	 *
	 * @param content
	 *            字符串内容
	 * @param width
	 *            二维码宽度
	 * @param height
	 *            二维码高度
	 * @param character_set
	 *            编码方式（一般使用UTF-8）
	 * @param error_correction_level
	 *            容错率 L：7% M：15% Q：25% H：35%
	 * @param margin
	 *            空白边距（二维码与边框的空白区域）
	 * @param color_black
	 *            黑色色块
	 * @param color_white
	 *            白色色块
	 * @param logoBitmap
	 *            logo图片（传null时不添加logo）
	 * @param logoPercent
	 *            logo所占百分比
	 * @param bitmap_black
	 *            用来代替黑色色块的图片（传null时不代替）
	 * @return
	 */
	public static Bitmap createQRCodeBitmap(String content, int width, int height, String character_set,
			String error_correction_level, String margin, int color_black, int color_white, Bitmap logoBitmap,
			float logoPercent, Bitmap bitmap_black) {
		// 字符串内容判空
		if (TextUtils.isEmpty(content)) {
			return null;
		}
		// 宽和高>=0
		if (width < 0 || height < 0) {
			return null;
		}
		try {
			/** 1.设置二维码相关配置,生成BitMatrix(位矩阵)对象 */
			Hashtable<EncodeHintType, String> hints = new Hashtable<>();
			// 字符转码格式设置
			if (!TextUtils.isEmpty(character_set)) {
				hints.put(EncodeHintType.CHARACTER_SET, character_set);
			}
			// 容错率设置
			if (!TextUtils.isEmpty(error_correction_level)) {
				hints.put(EncodeHintType.ERROR_CORRECTION, error_correction_level);
			}
			// 空白边距设置
			if (!TextUtils.isEmpty(margin)) {
				hints.put(EncodeHintType.MARGIN, margin);
			}
			/** 2.将配置参数传入到QRCodeWriter的encode方法生成BitMatrix(位矩阵)对象 */
			BitMatrix bitMatrix = new QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints);

			/** 3.创建像素数组,并根据BitMatrix(位矩阵)对象为数组元素赋颜色值 */
			if (bitmap_black != null) {
				// 从当前位图按一定的比例创建一个新的位图
				bitmap_black = Bitmap.createScaledBitmap(bitmap_black, width, height, false);
			}
			int[] pixels = new int[width * height];
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					// bitMatrix.get(x,y)方法返回true是黑色色块，false是白色色块
					if (bitMatrix.get(x, y)) {// 黑色色块像素设置
						if (bitmap_black != null) {// 图片不为null，则将黑色色块换为新位图的像素。
							pixels[y * width + x] = bitmap_black.getPixel(x, y);
						} else {
							pixels[y * width + x] = color_black;
						}
					} else {
						pixels[y * width + x] = color_white;// 白色色块像素设置
					}
				}
			}

			/** 4.创建Bitmap对象,根据像素数组设置Bitmap每个像素点的颜色值,并返回Bitmap对象 */
			Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
			bitmap.setPixels(pixels, 0, width, 0, 0, width, height);

			/** 5.为二维码添加logo图标 */
			if (logoBitmap != null) {
				return addLogo(bitmap, logoBitmap, logoPercent);
			}
			return bitmap;
		} catch (WriterException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	 /**
     * 识别二维码
     */
	public static String simpleDecode(Bitmap bMap) {
	    String contents = null;

	    int[] intArray = new int[bMap.getWidth()*bMap.getHeight()];  
	    //copy pixel data from the Bitmap into the 'intArray' array  
	    bMap.getPixels(intArray, 0, bMap.getWidth(), 0, 0, bMap.getWidth(), bMap.getHeight());  

	    LuminanceSource source = new RGBLuminanceSource(bMap.getWidth(), bMap.getHeight(), intArray);
	    
	    BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

	    Reader reader = new MultiFormatReader();// use this otherwise ChecksumException
	    
//	    Map<DecodeHintType, Object> hints = new HashMap<DecodeHintType, Object>();
//        hints.put(DecodeHintType.CHARACTER_SET, "UTF-8");   //可以传给decode函数作第二个参数。
	    try {
	        Result result = reader.decode(bitmap);
	        contents = result.getText(); 
	        //byte[] rawBytes = result.getRawBytes(); 
	        //BarcodeFormat format = result.getBarcodeFormat(); 
	        //ResultPoint[] points = result.getResultPoints();
	    } catch (NotFoundException e) { e.printStackTrace(); } 
	    catch (ChecksumException e) { e.printStackTrace(); }
	    catch (FormatException e) { e.printStackTrace(); } 
	    return contents;
	}
//	
//	 /**
//     * 复杂图片二维码解析
//     *
//     * @param file
//     * @return
//     */
//    public static String complexDecode(Bitmap bMap) {
//        String tempFilePath = null;
//        try {
//            //第一次解析：直接解析
//            String codeDataByFirst = simpleDecode(bMap);
//            if (codeDataByFirst != null) {
//                return codeDataByFirst;
//            }
//            //第二次解析：定位图中二维码，截图放大
//            Bitmap bmp2=piz(bMap);
//            String codeDataBySecond = simpleDecode(bmp2);
//            if (codeDataBySecond != null) {
//                return codeDataBySecond;
//            }
//            
//            //第三次解析：将截图后二维码二值化
//            Bitmap bmp3 = binarization(bmp2);
//            String codeDataByThird = simpleDecode(bmp3);
//            if (codeDataByThird != null) {
//                return codeDataByThird;
//            }
//            
//            
//            //第四次解析: 进行限制对比度的自适应直方图均衡化处理
//            Bitmap bmp4=limitContrast(bmp3);
//            String codeDataByFourth = simpleDecode(bmp4);
//            if (codeDataByFourth != null) {
////                System.out.println("QRCodeUtil -> complexDecode() fileName:{} state:{} result:{}"+file.getName()+Boolean.TRUE+codeDataByFourth);
//                return codeDataByFourth;
//            }
//        } finally {
////            file.deleteOnExit();
////            if (tempFilePath != null){
////                file = new File(tempFilePath);
////                file.deleteOnExit();
////            }
//        }
//        return null;
//    }
//    
//    
//    /**
//     * 定位 - > 截取 -> 放大
//     * @param filePath
//     * @param tempFilePath
//     * @return 
//     */
//    private static Bitmap piz(Bitmap bmp) {
//        Mat srcGray = new Mat();
//        Mat src =new Mat();
//        Utils.bitmapToMat(bmp, src);
//        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
//        List<MatOfPoint> markContours = new ArrayList<MatOfPoint>();
//        //图片太小就放大
//        if (src.width() * src.height() < 90000) {
//            Imgproc.resize(src, src, new Size(800, 600));
//        }
//        // 彩色图转灰度图
//        Imgproc.cvtColor(src, srcGray, Imgproc.COLOR_RGB2GRAY);
//        
//        // 对图像进行平滑处理
//        Imgproc.GaussianBlur(srcGray, srcGray, new Size(3, 3), 0);
//        Imgproc.Canny(srcGray, srcGray, 112, 255);
//        Mat hierarchy = new Mat();
//        Imgproc.findContours(srcGray, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_NONE);
// 
//        for (int i = 0; i < contours.size(); i++) {
//            MatOfPoint2f newMtx = new MatOfPoint2f(contours.get(i).toArray());
//            RotatedRect rotRect = Imgproc.minAreaRect(newMtx);
//            double w = rotRect.size.width;
//            double h = rotRect.size.height;
//            double rate = Math.max(w, h) / Math.min(w, h);
//            // 长短轴比小于1.3，总面积大于60
//            if (rate < 1.3 && w < srcGray.cols() / 4 && h < srcGray.rows() / 4 && Imgproc.contourArea(contours.get(i)) > 60) {
//                // 计算层数，二维码角框有五层轮廓（有说六层），这里不计自己这一层，有4个以上子轮廓则标记这一点
//                double[] ds = hierarchy.get(0, i);
//                if (ds != null && ds.length > 3) {
//                    int count = 0;
//                    if (ds[3] == -1) {
//                        //最外层轮廓排除
//                        continue;
//                    }
//                    // 计算所有子轮廓数量
//                    while ((int) ds[2] != -1) {
//                        ++count;
//                        ds = hierarchy.get(0, (int) ds[2]);
//                    }
//                    if (count >= 4) {
//                        markContours.add(contours.get(i));
//                    }
//                }
//            }
//        }
//        
//        /*
//         * 二维码有三个角轮廓，正常需要定位三个角才能确定坐标，本工具当识别到两个点的时候也将二维码定位出来；
//         * 当识别到两个及两个以上点时，取两个点中间点，往四周扩散截取 当小于两个点时，直接返回
//         */
//        if (markContours.size() == 0) {
//            return null;
//        } else if (markContours.size() == 1) {
//            Mat mat= capture(markContours.get(0), src );
//            Bitmap bmp1=Bitmap.createBitmap(mat.width(), mat.height(), Config.ARGB_8888);
//            Utils.matToBitmap(mat, bmp1);
//            return bmp1;
//        } else {
//            List<MatOfPoint> threePointList = new ArrayList<>();
//            threePointList.add(markContours.get(0));
//            threePointList.add(markContours.get(1));
//            Mat mat=capture(threePointList, src);
//            Bitmap bmp1=Bitmap.createBitmap(mat.width(), mat.height(), Config.ARGB_8888);
//            Utils.matToBitmap(mat, bmp1);
//            return bmp1;
//        }
//    }
//    
//   
//    /**
//     * 默认放大倍数
//     */
//    private final static int TIMES = 4;
//    
//    /**
//     * 当只识别到二维码的两个定位点时，根据两个点的中点进行定位
//     * @param threePointList
//     * @param src
//     * @return 
//     */
//    private static Mat capture(List<MatOfPoint> threePointList, Mat src) {
//        try {
//            Point p1 = centerCal(threePointList.get(0));
//            Point p2 = centerCal(threePointList.get(1));
//            Point centerPoint = new Point((p1.x + p2.x) / 2, (p1.y + p2.y) / 2);
//            double width = Math.abs(p1.x - p2.x) + Math.abs(p1.y - p2.y) + 50;
//            // 设置截取规则
//            Rect roiArea = new Rect((int) (centerPoint.x - width) > 0 ? (int) (centerPoint.x - width) : 0,
//                    (int) (centerPoint.y - width) > 0 ? (int) (centerPoint.y - width) : 0, (int) (2 * width),
//                    (int) (2 * width));
//            Mat dstRoi = new Mat(src, roiArea);
//            // 放大图片
//            Imgproc.resize(dstRoi, dstRoi, new Size(TIMES * width, TIMES * width));
//            return dstRoi;
//        }catch (Exception e){
//            // 设置截取规则
//            Rect roiArea = new Rect(0,0,src.width(),800);
//            Mat dstRoi = new Mat(src, roiArea);
//            // 放大图片
//            Imgproc.resize(dstRoi, dstRoi, new Size(TIMES * src.width(), TIMES * src.height()));
//            return dstRoi;
//        }
//    }
//
//    
//    /**
//     * 针对对比度不高的图片，只能识别到一个角的，直接以该点为中心截取
//     * @param matOfPoint
//     * @param src
//     * @param tempFilePath
//     */
//    private static Mat capture(MatOfPoint matOfPoint, Mat src) {
//        Point centerPoint = centerCal(matOfPoint);
//        int width = 200;
//        Rect roiArea = new Rect((int) (centerPoint.x - width) > 0 ? (int) (centerPoint.x - width) : 0,
//                (int) (centerPoint.y - width) > 0 ? (int) (centerPoint.y - width) : 0, (int) (2 * width),
//                (int) (2 * width));
//        // 截取二维码
//        Mat dstRoi = new Mat(src, roiArea);
//        // 放大图片
//        Imgproc.resize(dstRoi, dstRoi, new Size(TIMES * width, TIMES * width));
//        return dstRoi;
//    }
//    /**
//     * 获取轮廓的中心坐标
//     * @param matOfPoint
//     * @return
//     */
//    private static Point centerCal(MatOfPoint matOfPoint) {
//        double centerx = 0, centery = 0;
//        MatOfPoint2f mat2f = new MatOfPoint2f(matOfPoint.toArray());
//        RotatedRect rect = Imgproc.minAreaRect(mat2f);
//        Point vertices[] = new Point[4];
//        rect.points(vertices);
//        centerx = ((vertices[0].x + vertices[1].x) / 2 + (vertices[2].x + vertices[3].x) / 2) / 2;
//        centery = ((vertices[0].y + vertices[1].y) / 2 + (vertices[2].y + vertices[3].y) / 2) / 2;
//        Point point = new Point(centerx, centery);
//        return point;
//    }
// 
//    /**
//     * 二值化图像
//     * @param filePath 图像地址
//     */
//    private static Bitmap binarization(Bitmap bmp){
//        Mat mat = new Mat();
//        Utils.bitmapToMat(bmp, mat);
//        Bitmap bmp1=Bitmap.createBitmap(mat.width(), mat.height(), Config.ARGB_8888);
//        try {
//            // 彩色图转灰度图
//            Imgproc.cvtColor(mat, mat, Imgproc.COLOR_RGB2GRAY);
//            // 对图像进行平滑处理
//            Imgproc.blur(mat, mat, new Size(3, 3));
//            // 中值去噪
//            Imgproc.medianBlur(mat, mat, 5);
//            // 这里定义一个新的Mat对象，主要是为了保留原图，未下次处理做准备
//            Mat mat2 = new Mat();
//            // 根据OTSU算法进行二值化
//            Imgproc.threshold(mat, mat2, 205, 255, Imgproc.THRESH_OTSU);
////            // 生成二值化后的图像
////            Imgcodecs.imwrite(filePath, mat2);
//            Utils.matToBitmap(mat, bmp1);
//            return bmp1;
//        }catch (Exception e){
//            System.out.println("未识别到二维码");
//            return bmp;
//        }
//    }
// 
//    /**
//     * 图像进行限制对比度的自适应直方图均衡化处理
//     * @param filePath
//     * @return 
//     */
//    public static Bitmap limitContrast(Bitmap bmp){
//        try {
//        	 Mat mat = new Mat();
//             Utils.bitmapToMat(bmp, mat);
//            CLAHE clahe = Imgproc.createCLAHE(2, new Size(8, 8));
//            clahe.apply(mat, mat);
////            Imgcodecs.imwrite(filePath, mat);
//            Bitmap bmp1=Bitmap.createBitmap(mat.width(), mat.height(), Config.ARGB_8888);
//            Utils.matToBitmap(mat, bmp1);
//            return bmp1;
//        }catch (Exception e){
//            System.out.println("未识别到二维码");
//            return bmp;
//        }
//    }
//    
}
