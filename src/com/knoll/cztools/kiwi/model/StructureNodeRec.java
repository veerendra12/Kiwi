package com.knoll.cztools.kiwi.model;

import static com.knoll.cztools.kiwi.model.Constants.DS30_2_LT_RULE_MAPPING;
import static com.knoll.cztools.kiwi.model.Constants.DS30_2_LT_STRUCT_MAPPING;
import static com.knoll.cztools.kiwi.model.Constants.LT_STRUCTURE_COLUMNS;
import static com.knoll.cztools.kiwi.model.Constants.NodeType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

public class StructureNodeRec extends EntityRec {

	private Integer mTreeSeq = null;
	private Integer mPlanLevel = null;
	private String mName = null;
	private String mParent = null;
	private String mOrigSysRef = null;
	private StructureNodeRec mParentNodeRec = null;
	private List<StructureNodeRec> mChildNodeRecs = Collections.<StructureNodeRec>emptyList();

	public StructureNodeRec(Row row) {
		super(row);
	}
	
	public StructureNodeRec getParent() {
		return mParentNodeRec;
	}
	
	public void setParent(StructureNodeRec parentRec) {
		mParentNodeRec = parentRec;
	}
	
	public List<StructureNodeRec> getChildren() {
		if(mChildNodeRecs == Collections.<StructureNodeRec>emptyList()) {
			mChildNodeRecs = new ArrayList<StructureNodeRec>();
		}
		
		return mChildNodeRecs;
	}
	
	public List<StructureNodeRec> getChildrenUnModifiable() {
		return mChildNodeRecs;
	}
	
	public List<String[]> getPropertyValues() {
		List<String> colNames = getDS30Handler().getCurrentTabColumnHeaders();
		String propColName = DS30_2_LT_STRUCT_MAPPING.get("PS_PROPERTY_NAME");
		List<String[]> propVals = new ArrayList<String[]>();
		for(int i = colNames.indexOf(propColName); i <= colNames.lastIndexOf(propColName); i = i + 2) {
			String[] propValPair = new String[2];
			propValPair[0] = cellValue(getRow().getCell(i));
			propValPair[1] = cellValue(getRow().getCell(i+1));
			if(propValPair[0] != null && !propValPair[0].equals(""))
				propVals.add(propValPair);
		}
		
		return propVals;
	}

	public Integer getTreeSeq() {
		return mTreeSeq;
	}
	
	public void setTreeSeq(int treeSeq) {
		mTreeSeq = treeSeq;
	}

	public Integer getPlanLevel() {
		if(mPlanLevel == null) {
			String planLevel = (String)getColValueInternal("PLAN_LEVEL");
			if(planLevel != null) {
				if(Utilities.isInteger(planLevel)) {
					mPlanLevel = Integer.parseInt(Utilities.integerForm(planLevel));
				}
			} 
		}
		return mPlanLevel;
	}
	
	public void setPlanLevel(int planLevel) {
		mPlanLevel = planLevel;
	}		
	
	public String getName() {
		if(mName == null) {
			mName = (String)getColValueInternal("NAME");
		}
		return mName;
	}

	public String getDescription() {
		return (String)getColValueInternal("DESCRIPTION");
	}

	public String getMinimum() {
		String colValue = (String)getColValueInternal("MINIMUM");
		if(colValue != null && colValue.contains("/")) {
			colValue = colValue.substring(0, colValue.indexOf("/"));
		}
		return colValue;
	}
	
	public String getMaximum() {
		String colValue = (String)getColValueInternal("MAXIMUM");
		if(colValue != null && colValue.contains("/")) {
			colValue = colValue.substring(colValue.indexOf("/") + 1);
		}
		return colValue;
	}
	
	public String getParentName() {
		if(mParent == null) {
			mParent = (String)getColValueInternal("PARENT");
		}
		return mParent;
	}
	
	public void setParent(String parentName) {
		mParent = parentName;
	}
	
	public void setName(String name) {
		mName = name;
	}
	
	public String getOrigSysRef() {
		return mOrigSysRef;
	}
	
	public void setOrigSysRef(String origSysRef) {
		mOrigSysRef = origSysRef;
	}
	
	public NodeType getNodeType() {
		String nodeType = (String)getColValueInternal("NODE_TYPE");
		return NodeType.forDS30Format(nodeType);
	}		
	
	private Object getColValueInternal(String colName) {
		int idx = getDS30Handler().getCurrentTabColumnHeaders().indexOf(DS30_2_LT_STRUCT_MAPPING.get(colName));
		Object colValue = cellValue(getRow().getCell(idx)); 
		return colValue;
	}
	
	public Object getColValue(final String colName) {
		if(colName.equals("NAME")) {
			return getName();
		}
		else if(colName.equals("DESCRIPTION")) {
			return getDescription();
		}
		else if(colName.equals("PARENT")) {
			return getParent();
		}
		else if(colName.equals("NODE_TYPE")) {
			return getNodeType().getLoadToolFormat();
		}
		else if(colName.equals("MINIMUM")) {
			return getMinimum();
		}
		else if(colName.equals("MAXIMUM")) {
			return getMaximum();
		}
		else if(colName.equals("PLAN_LEVEL")) {
			return getPlanLevel();
		}
		else if(colName.equals("TREE_SEQ")) {
			return getTreeSeq();
		}
		else if(colName.equals("ORIG_SYS_REF")) {
			return getOrigSysRef();
		}
		return null;
	}
	
	public String serialize() {
		StringBuilder sb = new StringBuilder("");
		 for(String col : LT_STRUCTURE_COLUMNS) {
			 if(!sb.toString().equals("")) {
				 sb.append(",");
			 }			
			 String colValue = Utilities.formatToString(getColValue(col));
			 colValue = colValue != null ? colValue : "";
			 sb.append(colValue);
		 }
		return sb.toString();
	}
	
	public static String cellValue(Cell cell) {
		return (cell != null ? cell.toString() : null);
	}
	
	@Override
	public String toString() {
		return getName();
	}	
}
