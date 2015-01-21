package com.knoll.cztools.kiwi.model;

import static com.knoll.cztools.kiwi.model.Constants.DS30_2_LT_RULE_MAPPING;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import com.knoll.cztools.cdl.parser.CDLParser;
import com.knoll.cztools.cdl.parser.ParseException;
import com.knoll.cztools.cdl.parser.TokenMgrError;

public class RuleRec extends EntityRec {

	private RuleRec mParentRuleRec = null;
	private List<RuleRec> mChildRuleRecs = Collections.<RuleRec>emptyList();
	private boolean mIsFolderType = false;
	private String mName;
	private Boolean mIsValid = null;
	
	public RuleRec(Row row) {
		super(row);
		mIsFolderType = false;
	}
	
	public RuleRec(String folderName) {
		super(null);
		mName = folderName;
		mIsFolderType = true;
	}	
	
	public String getName() {
		if(mName == null) {
			mName = (String)getColValueInternal("NAME");
		}
		return mName;
	}
	
	public String getText() {
		return (String)getColValueInternal("RULE_TEXT");
	}

	public boolean includeForExtraction() {
		String includeForLoad = (String)getColValueInternal("INCLUDE_FOR_LOAD");
		return "Y".equalsIgnoreCase(includeForLoad.trim());
	}
	
	private Object getColValueInternal(String colName) {
		int idx = getDS30Handler().getCurrentTabColumnHeaders().indexOf(DS30_2_LT_RULE_MAPPING.get(colName));
		Object colValue = cellValue(getRow().getCell(idx)); 
		return colValue;
	}
	
	public List<RuleRec> getChildren() {
		if(mChildRuleRecs == Collections.<RuleRec>emptyList()) {
			mChildRuleRecs = new ArrayList<RuleRec>();
		}
		
		return mChildRuleRecs;
	}	
	
	public RuleRec getParent() {
		return mParentRuleRec;
	}	
	
	public void setParent(RuleRec parentRuleRec) {
		mParentRuleRec = parentRuleRec;
	}
	
	public boolean isFolder() {
		return mIsFolderType;
	}
	
	public boolean isValid() {
		if(mIsValid == null) {
			String text = getText();
			try {
				CDLParser.getInstance().parseStatements(new StringReader(text));
				mIsValid = Boolean.TRUE;
			} catch (ParseException pe) {
				mIsValid = Boolean.FALSE;
			} catch (TokenMgrError te) {
				mIsValid = Boolean.FALSE;
			}
		}
		
		return mIsValid;
	}
	
	@Override
	public String toString() {
		return getName();
	}	
}
