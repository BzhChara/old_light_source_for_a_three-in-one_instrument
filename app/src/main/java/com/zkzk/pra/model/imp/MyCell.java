package com.zkzk.pra.model.imp;

import org.apache.poi.hssf.record.CellValueRecordInterface;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellType;

public class MyCell extends HSSFCell{

	public MyCell(HSSFWorkbook book, HSSFSheet sheet, CellValueRecordInterface cval) {
		super(book, sheet, cval);
		// TODO Auto-generated constructor stub
	}

	public MyCell(HSSFWorkbook book, HSSFSheet sheet, int row, short col, CellType type) {
		super(book, sheet, row, col, type);
		// TODO Auto-generated constructor stub
	}

	public MyCell(HSSFWorkbook book, HSSFSheet sheet, int row, short col) {
		super(book, sheet, row, col);
		// TODO Auto-generated constructor stub
	}

		
}
