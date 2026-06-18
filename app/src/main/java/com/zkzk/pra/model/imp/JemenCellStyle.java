package com.zkzk.pra.model.imp;

import org.apache.poi.hssf.record.ExtendedFormatRecord;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class JemenCellStyle  extends HSSFCellStyle{
	private  JemenCellStyle(short index, ExtendedFormatRecord rec, HSSFWorkbook workbook) {
		super(index, rec, workbook);
	}

	
	
}
