package com.zkzk.pra.model.imp;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.apache.poi.hssf.model.InternalWorkbook;
import org.apache.poi.hssf.record.ExtendedFormatRecord;
import org.apache.poi.hssf.record.FontRecord;
import org.apache.poi.hssf.record.FormatRecord;
import org.apache.poi.hssf.record.PaletteRecord;
import org.apache.poi.hssf.record.StyleRecord;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.hssf.util.HSSFColor.HSSFColorPredefined;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;

/**
* Jemen: It's really quite complex to repair the poi jar,so I used lots of reflex.
* 还未成功，HSSFCell.setCellStyle(CellStyle style)  里边竟然强制把接口转为其实例化的HSSFCellStyle，悲剧。
* @see org.apache.poi.hssf.usermodel.HSSFWorkbook#createCellStyle()
* @see org.apache.poi.hssf.usermodel.HSSFWorkbook#getCellStyleAt(int)
* @see org.apache.poi.hssf.usermodel.HSSFCell#setCellStyle(MyCellStyle)
*/
public final class MyCellStyle extends HSSFCellStyle {
 private final ExtendedFormatRecord _format;
 private final short                _index;
 private final InternalWorkbook     _workbook;

 
private static InternalWorkbook getWorkbook(HSSFWorkbook workbook) {
	  InternalWorkbook iwb = null;
	  try {
		  Class<? extends HSSFWorkbook> clz= HSSFWorkbook.class;
		  Method method=clz.getDeclaredMethod("getWorkbook");
		  method.setAccessible(true);
		  iwb = (InternalWorkbook) method.invoke(workbook);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return iwb;
}
 

 /** Creates new MyCellStyle why would you want to do this?? */
 public static MyCellStyle getInstance(  HSSFWorkbook workbook)
 {
	 
	 
	 
     return new MyCellStyle((short)(workbook.getNumCellStyles() - 1),getExtendedFormatRecord(), getWorkbook(workbook));
 }
 

 
 public MyCellStyle(short index, ExtendedFormatRecord rec, InternalWorkbook workbook)
 {
	 super(index,rec,workbook);
	 
	 if(rec==null) {
		 	ExtendedFormatRecord retval = new ExtendedFormatRecord();
	        retval.setFontIndex(( short ) 0);
	        retval.setFormatIndex(( short ) 0x0);
	        retval.setCellOptions(( short ) 0x1);
	        retval.setAlignmentOptions(( short ) 0x20);
	        retval.setIndentionOptions(( short ) 0);
	        retval.setBorderOptions(( short ) 0);
	        retval.setPaletteOptions(( short ) 0);
	        retval.setAdtlPaletteOptions(( short ) 0);
	        retval.setFillPaletteOptions(( short ) 0x20c0);
	        retval.setTopBorderPaletteIdx((short) 0x08);
	        retval.setBottomBorderPaletteIdx((short) 0x08);
	        retval.setLeftBorderPaletteIdx((short) 0x08);
	        retval.setRightBorderPaletteIdx((short) 0x08);
		 rec=retval;
	 }
     _workbook = workbook;
     _index = index;
     _format     = rec;
     
 }
 
 
 
 private static ExtendedFormatRecord getExtendedFormatRecord() {
	 ExtendedFormatRecord retval = new ExtendedFormatRecord();
     retval.setFontIndex(( short ) 0);
     retval.setFormatIndex(( short ) 0x0);
     retval.setCellOptions(( short ) 0x1);
     retval.setAlignmentOptions(( short ) 0x20);
     retval.setIndentionOptions(( short ) 0);
     retval.setBorderOptions(( short ) 0);
     retval.setPaletteOptions(( short ) 0);
     retval.setAdtlPaletteOptions(( short ) 0);
     retval.setFillPaletteOptions(( short ) 0x20c0);
     retval.setTopBorderPaletteIdx((short) 0x08);
     retval.setBottomBorderPaletteIdx((short) 0x08);
     retval.setLeftBorderPaletteIdx((short) 0x08);
     retval.setRightBorderPaletteIdx((short) 0x08);
	 return retval;
	 
 }

 /**
  * get the index within the HSSFWorkbook (sequence within the collection of ExtnededFormat objects)
  * @return unique index number of the underlying record this style represents (probably you don't care
  *  unless you're comparing which one is which)
  */
 @Override
 public short getIndex() {
     return _index;
 }

 /**
  * Return the parent style for this cell style.
  * In most cases this will be null, but in a few
  *  cases there'll be a fully defined parent.
  */
 public MyCellStyle getParentStyle() {
     short parentIndex = _format.getParentIndex();
     // parentIndex equal 0xFFF indicates no inheritance from a cell style XF (See 2.4.353 XF)
     if(parentIndex == 0 || parentIndex == 0xFFF) {
         return null;
     }
     return new MyCellStyle(
             parentIndex,
             _workbook.getExFormatAt(parentIndex),
             _workbook
     );
 }

 /**
  * set the data format (must be a valid format)
  * @see org.apache.poi.hssf.usermodel.HSSFDataFormat
  */
 @Override
 public void setDataFormat(short fmt)
 {
     _format.setFormatIndex(fmt);
 }

 /**
  * get the index of the format
  * @see org.apache.poi.hssf.usermodel.HSSFDataFormat
  */
 @Override
 public short getDataFormat()
 {
     return _format.getFormatIndex();
 }

 // we keep the cached data in ThreadLocal members in order to
 // avoid multi-threading issues when different workbooks are accessed in 
 // multiple threads at the same time
 private static final ThreadLocal<Short> lastDateFormat = new ThreadLocal<Short>() {
     @Override
     protected Short initialValue() {
         return Short.MIN_VALUE;
     }
 };
 private static final ThreadLocal<List<FormatRecord>> lastFormats = new ThreadLocal<List<FormatRecord>>();
 private static final ThreadLocal<String> getDataFormatStringCache = new ThreadLocal<String>();

 /**
  * Get the contents of the format string, by looking up
  *  the DataFormat against the bound workbook
  * @see org.apache.poi.hssf.usermodel.HSSFDataFormat
  * @return the format string or "General" if not found
  */
 @Override
 public String getDataFormatString() {
     if (getDataFormatStringCache.get() != null) {
         if (lastDateFormat.get() == getDataFormat() && _workbook.getFormats().equals(lastFormats.get())) {
             return getDataFormatStringCache.get();
         }
     }

     lastFormats.set(_workbook.getFormats());
     lastDateFormat.set(getDataFormat());

     getDataFormatStringCache.set(getDataFormatString(_workbook));

     return getDataFormatStringCache.get();
 }

 /**
  * Get the contents of the format string, by looking up
  *  the DataFormat against the supplied workbook
  * @see org.apache.poi.hssf.usermodel.HSSFDataFormat
  *
  * @return the format string or "General" if not found
  */
 public String getDataFormatString(org.apache.poi.ss.usermodel.Workbook workbook) {
     HSSFDataFormat format =getHSSFDataFormat( getWorkbook((HSSFWorkbook)workbook) );
     int idx = getDataFormat();
     return idx == -1 ? "General" : format.getFormat(getDataFormat());
 }
 
 private static HSSFDataFormat getHSSFDataFormat(InternalWorkbook workbook) {
	 try {
		Class<HSSFDataFormat> clz=HSSFDataFormat.class;
		Constructor<HSSFDataFormat> con=clz.getDeclaredConstructor(InternalWorkbook.class);
		con.setAccessible(true);
		return (HSSFDataFormat) con.newInstance(workbook);
	} catch (Exception e) {
		e.printStackTrace();
		return null;
	}
 }
 
 
 /**
  * Get the contents of the format string, by looking up
  *  the DataFormat against the supplied low level workbook
  * @see org.apache.poi.hssf.usermodel.HSSFDataFormat
  */
 public String getDataFormatString(org.apache.poi.hssf.model.InternalWorkbook workbook) {
     HSSFDataFormat format = getHSSFDataFormat( workbook );

     return format.getFormat(getDataFormat());
 }

 /**
  * set the font for this style
  * @param font  a font object created or retrieved from the HSSFWorkbook object
  * @see org.apache.poi.hssf.usermodel.HSSFWorkbook#createFont()
  * @see org.apache.poi.hssf.usermodel.HSSFWorkbook#getFontAt(short)
  */
 @Override
 public void setFont(Font font) {
     setFont((HSSFFont)font);
 }
 public void setFont(HSSFFont font) {
     _format.setIndentNotParentFont(true);
     short fontindex = font.getIndex();
     _format.setFontIndex(fontindex);
 }

 /**
  * gets the index of the font for this style
  * @see org.apache.poi.hssf.usermodel.HSSFWorkbook#getFontAt(short)
  */
 @Override
 public short getFontIndex()
 {
     return _format.getFontIndex();
 }

 /**
  * gets the font for this style
  * @param parentWorkbook The HSSFWorkbook that this style belongs to
  * @see org.apache.poi.hssf.usermodel.MyCellStyle#getFontIndex()
  * @see org.apache.poi.hssf.usermodel.HSSFWorkbook#getFontAt(short)
  */
 public HSSFFont getFont(org.apache.poi.ss.usermodel.Workbook parentWorkbook) {
     return ((HSSFWorkbook) parentWorkbook).getFontAt(getFontIndex());
 }

 /**
  * set the cell's using this style to be hidden
  * @param hidden - whether the cell using this style should be hidden
  */
 @Override
 public void setHidden(boolean hidden)
 {
     _format.setIndentNotParentCellOptions(true);
     _format.setHidden(hidden);
 }

 /**
  * get whether the cell's using this style are to be hidden
  * @return hidden - whether the cell using this style should be hidden
  */
 @Override
 public boolean getHidden()
 {
     return _format.isHidden();
 }

 /**
  * set the cell's using this style to be locked
  * @param locked - whether the cell using this style should be locked
  */
 @Override
 public void setLocked(boolean locked)
 {
     _format.setIndentNotParentCellOptions(true);
     _format.setLocked(locked);
 }

 /**
  * get whether the cell's using this style are to be locked
  * @return hidden - whether the cell using this style should be locked
  */
 @Override
 public boolean getLocked()
 {
     return _format.isLocked();
 }

 /**
  * Turn on or off "Quote Prefix" or "123 Prefix" for the style,
  *  which is used to tell Excel that the thing which looks like
  *  a number or a formula shouldn't be treated as on.
  */
 @Override
 public void setQuotePrefixed(boolean quotePrefix) {
     _format.set123Prefix(quotePrefix);
 }
 
 /**
  * Is "Quote Prefix" or "123 Prefix" enabled for the cell?
  */
 @Override
 public boolean getQuotePrefixed() {
     return _format.get123Prefix();
 }
 
 /**
  * set the type of horizontal alignment for the cell
  * @param align - the type of alignment
  */
 @Override
 public void setAlignment(HorizontalAlignment align)
 {
     _format.setIndentNotParentAlignment(true);
     _format.setAlignment(align.getCode());
 }

 /**
  * get the type of horizontal alignment for the cell
  * @return align - the type of alignment
  * @deprecated POI 3.15 beta 3. Use {@link #getAlignmentEnum()} instead.
  */
 @Deprecated
 @Override
 public short getAlignment()
 {
     return _format.getAlignment();
 }
 /**
  * get the type of horizontal alignment for the cell
  * @return align - the type of alignment
  */
 @Override
 public HorizontalAlignment getAlignmentEnum()
 {
     return HorizontalAlignment.forInt(_format.getAlignment());
 }

 /**
  * set whether the text should be wrapped
  * @param wrapped  wrap text or not
  */
 @Override
 public void setWrapText(boolean wrapped)
 {
     _format.setIndentNotParentAlignment(true);
     _format.setWrapText(wrapped);
 }

 /**
  * get whether the text should be wrapped
  * @return wrap text or not
  */
 @Override
 public boolean getWrapText()
 {
     return _format.getWrapText();
 }

 /**
  * set the type of vertical alignment for the cell
  * @param align the type of alignment
  */
 @Override
 public void setVerticalAlignment(VerticalAlignment align)
 {
     _format.setVerticalAlignment(align.getCode());
 }

 /**
  * get the type of vertical alignment for the cell
  * @return align the type of alignment
  * @see VerticalAlignment
  * @deprecated POI 3.15 beta 3. Use {@link #getVerticalAlignmentEnum()} instead.
  */
 @Deprecated
 @Override
 public short getVerticalAlignment()
 {
     return _format.getVerticalAlignment();
 }
/**
 * get the type of vertical alignment for the cell
 * @return align the type of alignment
 */
@Override
public VerticalAlignment getVerticalAlignmentEnum()
{
    return VerticalAlignment.forInt(_format.getVerticalAlignment());
}

 /**
  * set the degree of rotation for the text in the cell
  *
  * Note: HSSF uses values from -90 to 90 degrees, whereas XSSF 
  * uses values from 0 to 180 degrees. The implementations of this method will map between these two value-ranges 
  * accordingly, however the corresponding getter is returning values in the range mandated by the current type
  * of Excel file-format that this CellStyle is applied to.
  *
  * @param rotation degrees (between -90 and 90 degrees, of 0xff for vertical)
  */
 @Override
 public void setRotation(short rotation)
 {
   if (rotation == 0xff) {
       // Special cases for vertically aligned text
   } 
   else if ((rotation < 0)&&(rotation >= -90)) {
     //Take care of the funny 4th quadrant issue
     //The 4th quadrant (-1 to -90) is stored as (91 to 180)
     rotation = (short)(90 - rotation);
   }
   else if (rotation > 90 && rotation <= 180) {
       // stay compatible with the range used by XSSF, map from ]90..180] to ]0..-90]
       // we actually don't need to do anything here as the internal value is stored in [0-180] anyway!
   }
   else if ((rotation < -90)  || (rotation > 90)) {
     //Do not allow an incorrect rotation to be set
     throw new IllegalArgumentException("The rotation must be between -90 and 90 degrees, or 0xff");
   }
   _format.setRotation(rotation);
 }

 /**
  * get the degree of rotation for the text in the cell
  * @return rotation degrees (between -90 and 90 degrees, or 0xff for vertical)
  */
 @Override
 public short getRotation()
 {
   short rotation = _format.getRotation();
   if (rotation == 0xff) {
      // Vertical aligned special case
      return rotation;
   }
   if (rotation > 90) {
     //This is actually the 4th quadrant
     rotation = (short)(90-rotation);
   }
   return rotation;
 }

 /**
  * set the number of spaces to indent the text in the cell
  * @param indent - number of spaces
  */
 @Override
 public void setIndention(short indent)
 {
     _format.setIndent(indent);
 }

 /**
  * get the number of spaces to indent the text in the cell
  * @return indent - number of spaces
  */
 @Override
 public short getIndention()
 {
     return _format.getIndent();
 }
 
 /**
  * set the type of border to use for the left border of the cell
  * @param border type
  * @since POI 3.15
  */
 @Override
 public void setBorderLeft(BorderStyle border)
 {
     _format.setIndentNotParentBorder(true);
     _format.setBorderLeft(border.getCode());
 }

 /**
  * get the type of border to use for the left border of the cell
  * @return border type
  * @deprecated POI 3.15. Will return a BorderStyle enum in the future. Use {@link #getBorderLeftEnum()}.
  */
 @Deprecated
 @Override
 public short getBorderLeft()
 {
     return _format.getBorderLeft();
 }
 /**
  * get the type of border to use for the left border of the cell
  * @return border type
  * @since POI 3.15
  */
 @Override
 public BorderStyle getBorderLeftEnum()
 {
     return BorderStyle.valueOf(_format.getBorderLeft());
 }
 
 /**
  * set the type of border to use for the right border of the cell
  * @param border type
  * @since POI 3.15
  */
 @Override
 public void setBorderRight(BorderStyle border)
 {
     _format.setIndentNotParentBorder(true);
     _format.setBorderRight(border.getCode());
 }

 /**
  * get the type of border to use for the right border of the cell
  * @return border type
  * @deprecated POI 3.15. Will return a BorderStyle enum in the future. Use {@link #getBorderRightEnum()}.
  */
 @Deprecated
 @Override
 public short getBorderRight()
 {
     return _format.getBorderRight();
 }
 /**
  * get the type of border to use for the right border of the cell
  * @return border type
  * @since POI 3.15
  */
 @Override
 public BorderStyle getBorderRightEnum()
 {
     return BorderStyle.valueOf(_format.getBorderRight());
 }
 
 /**
  * set the type of border to use for the top border of the cell
  * @param border type
  * @since POI 3.15
  */
 @Override
 public void setBorderTop(BorderStyle border)
 {
     _format.setIndentNotParentBorder(true);
     _format.setBorderTop(border.getCode());
 }

 /**
  * get the type of border to use for the top border of the cell
  * @return border type
  * @deprecated POI 3.15. Will return a BorderStyle enum in the future. Use {@link #getBorderTopEnum()}.
  */
 @Deprecated
 @Override
 public short getBorderTop()
 {
     return _format.getBorderTop();
 }
 /**
  * get the type of border to use for the top border of the cell
  * @return border type
  * @since 3.15
  */
 @Override
 public BorderStyle getBorderTopEnum()
 {
     return BorderStyle.valueOf(_format.getBorderTop());
 }
 
 /**
  * set the type of border to use for the bottom border of the cell
  * @param border type
  * @since 3.15 beta 2
  */
 @Override
 public void setBorderBottom(BorderStyle border)
 {
     _format.setIndentNotParentBorder(true);
     _format.setBorderBottom(border.getCode());
 }

 /**
  * get the type of border to use for the bottom border of the cell
  * @return border type
  * @deprecated POI 3.15. Will return a BorderStyle enum in the future. Use {@link #getBorderBottomEnum()}.
  */
 @Deprecated
 @Override
 public short getBorderBottom()
 {
     return _format.getBorderBottom();
 }
 /**
  * get the type of border to use for the bottom border of the cell
  * @return border type
  * @since 3.15
  */
 @Override
 public BorderStyle getBorderBottomEnum()
 {
     return BorderStyle.valueOf(_format.getBorderBottom());
 }

 /**
  * set the color to use for the left border
  * @param color The index of the color definition
  */
 @Override
 public void setLeftBorderColor(short color)
 {
     _format.setLeftBorderPaletteIdx(color);
 }

 /**
  * get the color to use for the left border
  * @see org.apache.poi.hssf.usermodel.HSSFPalette#getColor(short)
  * @return The index of the color definition
  */
 @Override
 public short getLeftBorderColor()
 {
     return _format.getLeftBorderPaletteIdx();
 }

 /**
  * set the color to use for the right border
  * @param color The index of the color definition
  */
 @Override
 public void setRightBorderColor(short color)
 {
     _format.setRightBorderPaletteIdx(color);
 }

 /**
  * get the color to use for the left border
  * @see org.apache.poi.hssf.usermodel.HSSFPalette#getColor(short)
  * @return The index of the color definition
  */
 @Override
 public short getRightBorderColor()
 {
     return _format.getRightBorderPaletteIdx();
 }

 /**
  * set the color to use for the top border
  * @param color The index of the color definition
  */
 @Override
 public void setTopBorderColor(short color)
 {
     _format.setTopBorderPaletteIdx(color);
 }

 /**
  * get the color to use for the top border
  * @see org.apache.poi.hssf.usermodel.HSSFPalette#getColor(short)
  * @return The index of the color definition
  */
 @Override
 public short getTopBorderColor()
 {
     return _format.getTopBorderPaletteIdx();
 }

 /**
  * set the color to use for the bottom border
  * @param color The index of the color definition
  */
 @Override
 public void setBottomBorderColor(short color)
 {
     _format.setBottomBorderPaletteIdx(color);
 }

 /**
  * get the color to use for the left border
  * @see org.apache.poi.hssf.usermodel.HSSFPalette#getColor(short)
  * @return The index of the color definition
  */
 @Override
 public short getBottomBorderColor()
 {
     return _format.getBottomBorderPaletteIdx();
 }
 
 /**
  * setting to one fills the cell with the foreground color... No idea about
  * other values
  *
  * @param fp  fill pattern (set to {@link FillPatternType#SOLID_FOREGROUND} to fill w/foreground color)
  */
 @Override
 public void setFillPattern(FillPatternType fp)
 {
     _format.setAdtlFillPattern(fp.getCode());
 }

 /**
  * get the fill pattern
  * @return fill pattern
  * @deprecated POI 3.15 beta 3. This method will return {@link FillPatternType} in the future. Use {@link #setFillPattern(FillPatternType)} instead. 
  */
 @Deprecated
 @Override
 public short getFillPattern()
 {
     return getFillPatternEnum().getCode();
 }
 
 /**
  * get the fill pattern
  * @return fill pattern
  */
 @Override
 public FillPatternType getFillPatternEnum()
 {
     return FillPatternType.forInt(_format.getAdtlFillPattern());
 }

 /**
  * Checks if the background and foreground fills are set correctly when one
  * or the other is set to the default color.
  * <p>Works like the logic table below:</p>
  * <p>BACKGROUND   FOREGROUND</p>
  * <p>NONE         AUTOMATIC</p>
  * <p>0x41         0x40</p>
  * <p>NONE         RED/ANYTHING</p>
  * <p>0x40         0xSOMETHING</p>
  */
 private void checkDefaultBackgroundFills() {
     final short autoIdx = HSSFColorPredefined.AUTOMATIC.getIndex();
     if (_format.getFillForeground() == autoIdx) {
         //JMH: Why +1, hell why not. I guess it made some sense to someone at the time. Doesnt
         //to me now.... But experience has shown that when the fore is set to AUTOMATIC then the
         //background needs to be incremented......
         if (_format.getFillBackground() != autoIdx+1) {
             setFillBackgroundColor((short)(autoIdx+1));
         }
     } else if (_format.getFillBackground() == autoIdx+1) {
         //Now if the forground changes to a non-AUTOMATIC color the background resets itself!!!
         if (_format.getFillForeground() != autoIdx) {
             setFillBackgroundColor(autoIdx);
         }
     }
 }

 /**
  * set the background fill color.
  * <p>
  * For example:
  * <pre>
  * cs.setFillPattern(MyCellStyle.FINE_DOTS );
  * cs.setFillBackgroundColor(new HSSFColor.RED().getIndex());
  * </pre>
  * optionally a Foreground and background fill can be applied:
  * <i>Note: Ensure Foreground color is set prior to background</i>
  * <pre>
  * cs.setFillPattern(MyCellStyle.FINE_DOTS );
  * cs.setFillForegroundColor(new HSSFColor.BLUE().getIndex());
  * cs.setFillBackgroundColor(new HSSFColor.RED().getIndex());
  * </pre>
  * or, for the special case of SOLID_FILL:
  * <pre>
  * cs.setFillPattern(MyCellStyle.SOLID_FOREGROUND );
  * cs.setFillForegroundColor(new HSSFColor.RED().getIndex());
  * </pre>
  * It is necessary to set the fill style in order
  * for the color to be shown in the cell.
  *
  * @param bg  color
  */
 @Override
 public void setFillBackgroundColor(short bg)
 {
     _format.setFillBackground(bg);
     checkDefaultBackgroundFills();
 }

 /**
  * Get the background fill color.
  * Note - many cells are actually filled with a foreground
  *  fill, not a background fill - see {@link #getFillForegroundColor()}
  * @see org.apache.poi.hssf.usermodel.HSSFPalette#getColor(short)
  * @return fill color
  */
 @Override
 public short getFillBackgroundColor() {
     final short autoIndex = HSSFColorPredefined.AUTOMATIC.getIndex();
     short result = _format.getFillBackground();
     //JMH: Do this ridiculous conversion, and let MyCellStyle
     //internally migrate back and forth
     if (result == autoIndex+1) {
         return autoIndex;
     }
     return result;
 }
 
 private HSSFPalette getHSSFPalette(PaletteRecord  obj) {
	 try {
		Class<HSSFPalette> clz=HSSFPalette.class;
		 Constructor<HSSFPalette> con = clz.getDeclaredConstructor(PaletteRecord.class);
		 con.setAccessible(true);// 值为true则指示反射的对象在使用时应该取消Java语言访问检查。
		 return (HSSFPalette) con.newInstance(obj);
	} catch (Exception e) {
		e.printStackTrace();
		return null;
	}
 }
 
 @Override
 public HSSFColor getFillBackgroundColorColor() {
    HSSFPalette pallette = getHSSFPalette(
          _workbook.getCustomPalette()
    );
    return pallette.getColor(
          getFillBackgroundColor()
    );
 }

 /**
  * set the foreground fill color
  * <i>Note: Ensure Foreground color is set prior to background color.</i>
  * @param bg  color
  */
 @Override
 public void setFillForegroundColor(short bg)
 {
     _format.setFillForeground(bg);
     checkDefaultBackgroundFills();
 }

 /**
  * Get the foreground fill color.
  * Many cells are filled with this, instead of a
  *  background color ({@link #getFillBackgroundColor()})
  * @see org.apache.poi.hssf.usermodel.HSSFPalette#getColor(short)
  * @return fill color
  */
 @Override
 public short getFillForegroundColor()
 {
     return _format.getFillForeground();
 }

 
 @Override
 public HSSFColor getFillForegroundColorColor() {
    HSSFPalette pallette = getHSSFPalette(
          _workbook.getCustomPalette()
    );
    return pallette.getColor(
          getFillForegroundColor()
    );
 }

 /**
  * Gets the name of the user defined style.
  * Returns null for built in styles, and
  *  styles where no name has been defined
  */
 public String getUserStyleName() {
     StyleRecord sr = _workbook.getStyleRecord(_index);
     if(sr == null) {
         return null;
     }
     if(sr.isBuiltin()) {
         return null;
     }
     return sr.getName();
 }

 /**
  * Sets the name of the user defined style.
  * Will complain if you try this on a built in style.
  */
 public void setUserStyleName(String styleName) {
     StyleRecord sr = _workbook.getStyleRecord(_index);
     if(sr == null) {
         sr = _workbook.createStyleRecord(_index);
     }
     // All Style records start as "builtin", but generally
     //  only 20 and below really need to be
     if(sr.isBuiltin() && _index <= 20) {
         throw new IllegalArgumentException("Unable to set user specified style names for built in styles!");
     }
     sr.setName(styleName);
 }

 /**
  * Controls if the Cell should be auto-sized
  *  to shrink to fit if the text is too long
  */
 @Override
 public void setShrinkToFit(boolean shrinkToFit) {
     _format.setShrinkToFit(shrinkToFit);
 }
 /**
  * Should the Cell be auto-sized by Excel to shrink
  *  it to fit if this text is too long?
  */
 @Override
 public boolean getShrinkToFit() {
     return _format.getShrinkToFit();
 }
 
 /**
  * Get the reading order, for RTL/LTR ordering of
  *  the text.
  * <p>0 means Context (Default), 1 means Left To Right,
  *  and 2 means Right to Left</p>
  *
  * @return order - the reading order (0,1,2)
  */
 public short getReadingOrder() {
     return _format.getReadingOrder();
 }
 /**
  * Sets the reading order, for RTL/LTR ordering of
  *  the text.
  * <p>0 means Context (Default), 1 means Left To Right,
  *  and 2 means Right to Left</p>
  *
  * @param order - the reading order (0,1,2)
  */
 public void setReadingOrder(short order) {
     _format.setReadingOrder(order);
 }
 
 /**
  * Verifies that this style belongs to the supplied Workbook.
  * Will throw an exception if it belongs to a different one.
  * This is normally called when trying to assign a style to a
  *  cell, to ensure the cell and the style are from the same
  *  workbook (if they're not, it won't work)
  * @throws IllegalArgumentException if there's a workbook mis-match
  */
 public void verifyBelongsToWorkbook(HSSFWorkbook wb) {
     if(getWorkbook(wb) != _workbook) {
         throw new IllegalArgumentException("This Style does not belong to the supplied Workbook. Are you trying to assign a style from one workbook to the cell of a differnt workbook?");
     }
 }

 /**
  * Clones all the style information from another
  *  MyCellStyle, onto this one. This
  *  MyCellStyle will then have all the same
  *  properties as the source, but the two may
  *  be edited independently.
  * Any stylings on this MyCellStyle will be lost!
  *
  * The source MyCellStyle could be from another
  *  HSSFWorkbook if you like. This allows you to
  *  copy styles from one HSSFWorkbook to another.
  */
 @Override
 public void cloneStyleFrom(CellStyle source) {
     if(source instanceof MyCellStyle) {
         this.cloneStyleFrom((MyCellStyle)source);
     } else {
         throw new IllegalArgumentException("Can only clone from one MyCellStyle to another, not between MyCellStyle and XSSFCellStyle");
     }
 }
 public void cloneStyleFrom(MyCellStyle source) {
     // First we need to clone the extended format
     //  record
     _format.cloneStyleFrom(source._format);

     // Handle matching things if we cross workbooks
     if(_workbook != source._workbook) {

         lastDateFormat.set(Short.MIN_VALUE);
         lastFormats.set(null);
         getDataFormatStringCache.set(null);
        
         // Then we need to clone the format string,
         //  and update the format record for this
         short fmt = (short)_workbook.createFormat(source.getDataFormatString() );
         setDataFormat(fmt);

         // Finally we need to clone the font,
         //  and update the format record for this
         FontRecord fr = _workbook.createNewFont();
         fr.cloneStyleFrom(
                 source._workbook.getFontRecordAt(
                         source.getFontIndex()
                 )
         );

         HSSFFont font = getHSSFFont(
                 (short)_workbook.getFontIndex(fr), fr
         );
         setFont(font);
     }
 }

 
 private HSSFFont getHSSFFont(short x,FontRecord fr) {
	try{
		 Class<HSSFFont> clz=HSSFFont.class;
		 Constructor<HSSFFont> con=clz.getDeclaredConstructor(Short.class,FontRecord.class);
		 con.setAccessible(true);
		 return (HSSFFont) con.newInstance(x);
	}catch(Exception e) {
		e.printStackTrace();
		return null;
	}
 }

 @Override
 public int hashCode() {
     final int prime = 31;
     int result = 1;
     result = prime * result + ((_format == null) ? 0 : _format.hashCode());
     result = prime * result + _index;
     return result;
 }

 @Override
 public boolean equals(Object obj) {
     if (this == obj) {
         return true;
     }
     if (obj == null) {
         return false;
     }
     if (obj instanceof MyCellStyle) {
         final MyCellStyle other = (MyCellStyle) obj;
         if (_format == null) {
             if (other._format != null) {
                 return false;
             }
         } else if (!_format.equals(other._format)) {
             return false;
         }
         if (_index != other._index) {
             return false;
         }
         return true;
     }
     return false;
 }
 
}
