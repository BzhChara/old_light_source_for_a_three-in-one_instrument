package top.jemen.ui.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.renderscript.Type;

public class NV21ToBitmap {
    
    private RenderScript rs;
    private ScriptIntrinsicYuvToRGB yuvToRgbIntrinsic;
    private Type.Builder yuvType, rgbaType;

	public NV21ToBitmap(Context context) {
        rs = RenderScript.create(context);
        yuvToRgbIntrinsic = ScriptIntrinsicYuvToRGB.create(rs, Element.U8_4(rs));
        yuvType = new Type.Builder(rs, Element.U8(rs));
        rgbaType = new Type.Builder(rs, Element.RGBA_8888(rs));
    }
    
	public Bitmap nv21ToBitmap(byte[] nv21, int width, int height) {
    	yuvType.setX(nv21.length);
    	Allocation in = Allocation.createTyped(rs, yuvType.create(), Allocation.USAGE_SCRIPT);
    	in.copyFrom(nv21);

		rgbaType.setX(width).setY(height);
		Allocation out = Allocation.createTyped(rs, rgbaType.create(), Allocation.USAGE_SCRIPT);
		yuvToRgbIntrinsic.setInput(in);
		yuvToRgbIntrinsic.forEach(out);

		Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		out.copyTo(bmp);
    
		return bmp;
    }
}    
