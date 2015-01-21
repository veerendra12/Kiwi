package com.knoll.cztools.kiwi.model;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

abstract public class EntityRec {
	private Row mRow;

	public EntityRec(Row row) {
		mRow = row;
	}
	
	public Row getRow() {
		return mRow;
	}
	
	public static String cellValue(Cell cell) {
		return (cell != null ? cell.toString() : null);
	}
	
	public DS30Handler getDS30Handler() {
		return DS30Handler.getInstance();
	}
}
