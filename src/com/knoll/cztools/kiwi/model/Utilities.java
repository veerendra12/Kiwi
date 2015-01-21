package com.knoll.cztools.kiwi.model;

import org.apache.poi.ss.usermodel.Cell;

public final class Utilities {
	
	public static String encloseInQuotesIfNeeded(String value) {
		boolean encloseInQuotes = false;
		if(value != null) { 
			encloseInQuotes |= value.contains("\"");
			value = value.replaceAll("\"", "\"\"");
//			value = value.replaceAll("\n", " ");
			encloseInQuotes |= value.contains(",");
			encloseInQuotes |= value.contains("\n");
			
			value = encloseInQuotes ? "\"" + value + "\"" : value;
		}

		return value; 
	}
	
	public static boolean isInteger(String str) {
		return (str != null) ? (str.matches("[+-]?\\d+(\\.0+)?")) : false;
	}
	
	public static String integerForm(String intVal) {
		return intVal.contains(".") ? intVal.substring(0, intVal.lastIndexOf(".")) : intVal;
	}
	
	public static String cellValue(Cell cell) {
		return (cell != null ? cell.toString() : null);
	}
	
	public static String formatToString(Object val) {
		if(val == null) return null;
		
		if(val instanceof String) {
			String strVal = (String)val;
			if(isInteger(strVal)) {
				strVal = integerForm(strVal);
			}
			else {
					strVal = encloseInQuotesIfNeeded(strVal);
			}
			return strVal;
		}
		else
			return val.toString();
	}	
}
