package com.knoll.cztools.kiwi.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Level;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import static com.knoll.cztools.kiwi.model.Constants.*;

public class DS30Handler {
	
	private static final DS30Handler sDS30Handler = new DS30Handler();
	
	private File mSourceFile;
	private List<String> mTabNames;
	private Workbook mWb;
	private String mCurrentTab;
	List<String> mHeaderColumns;
	
	private StructureNodeRec mRootStrucNodeRec = null;
	
	
	private DS30Handler() {}
	
	public static DS30Handler getInstance() {
		return sDS30Handler;
	}
	

	public void setSourceFile(File sourceFile) throws Exception {
		mSourceFile = sourceFile;
		mWb = new XSSFWorkbook(new FileInputStream(mSourceFile));
		List<String> sheets = new ArrayList<String>();
		for(int i = 0; i < mWb.getNumberOfSheets(); i++) {
			sheets.add(mWb.getSheetName(i));
		}
		mTabNames = sheets;
	}
	
	private List<String> getRowContent(Row row) {
        short minColIx = row.getFirstCellNum();
        short maxColIx = row.getLastCellNum();
        List<String> cellContents = new ArrayList<String>();
        for(short colIx=minColIx; colIx<maxColIx; colIx++) {
		   Cell cell = row.getCell(colIx);
		   cellContents.add(cell.toString());
		}
        
        return cellContents;
	}
	
	
	public TabType getTabType(String tabName) {
		System.out.println(tabName);
		Sheet sheet = mWb.getSheet(tabName);
		boolean mismatch = false;
		
		Row row = sheet.getRow(1);
		if(row != null) {
			List<String> cellContents = getRowContent(row);
			Collection<String> structColumns = DS30_2_LT_STRUCT_MAPPING.values();
	        for(String colName : structColumns) {
	        	if(!cellContents.contains(colName)) {
	        		mismatch = true;
	        		break;
	        	}
	        }
		}
		if(!mismatch) return TabType.STRUCTURE;
		
		row = sheet.getRow(0);
		if(row != null) {
			List<String> cellContents = getRowContent(row);
	        Collection<String> ruleColumns = DS30_2_LT_RULE_MAPPING.values();
			
	        mismatch = false;	        
	        for(String colName : ruleColumns) {
	        	if(!cellContents.contains(colName)) {
	        		mismatch = true;
	        		break;
	        	}
	        }
		}
        
        if(!mismatch) return TabType.RULES;
        
        return TabType.NONE;
        
	}
	
	private File getTargetFile(String targetSuffix) {
		String sourceFileName = mSourceFile.getName();
		int idx = sourceFileName.lastIndexOf('.');
		String tgtFileName = sourceFileName.substring(0, idx) + "_" + targetSuffix + ".csv";
		return new File(mDestFolder, tgtFileName);
	}
	
	public List<String> getSheets() {
		return mTabNames;
	}

	public RuleRec buildRules(String modelName, String sheetName) {
		Sheet sheet = mWb.getSheet(sheetName);
		RuleRec rootRuleRec = new RuleRec("Rules");
		RuleRec parentRuleRec = rootRuleRec;
		
		for (Row row : sheet) {
			if(row.getRowNum() == 0) {
				 continue;
			}
			
			String val = Utilities.cellValue(row.getCell(0));
			
			if(val != null && val.startsWith("Rule Folder:")) {
				String folderName = val.substring("Rule Folder:".length()).trim();
				RuleRec folderRec = new RuleRec(folderName);
				folderRec.setParent(rootRuleRec);
				rootRuleRec.getChildren().add(folderRec);
				parentRuleRec = folderRec;
			}
			else {
				RuleRec ruleRec = new RuleRec(row);
				
				if(ruleRec.getText() == null || ruleRec.getText().equals("")) continue;
				
				ruleRec.setParent(parentRuleRec);
				parentRuleRec.getChildren().add(ruleRec);
				ruleRec.getName();
			}
		}
		
		return rootRuleRec;
	}
	
	public void setCurrentTab(String tabName) {
		mCurrentTab = tabName;
		TabType tabType = getTabType(tabName);
		if(TabType.STRUCTURE.equals(tabType)) {
			mHeaderColumns = getColumnNames(mWb.getSheet(tabName).getRow(1));
		}
		else if(TabType.RULES.equals(tabType)) {
			mHeaderColumns = getColumnNames(mWb.getSheet(tabName).getRow(0));
		}
	}
	
	public List<String> getCurrentTabColumnHeaders() {
		return mHeaderColumns;
	}
	
	public void extractRules(String modelName, String sheetName) throws Exception {
		PrintWriter destFileW = new PrintWriter(getTargetFile("Rules"));
		
		Sheet sheet = mWb.getSheet(sheetName);

		Row hdrRow = sheet.getRow(0);
		List<String> colNames = getColumnNames(hdrRow);
		log(colNames.toString(), Level.INFO);

		String headerRow = StringUtils.join(LT_RULE_COLUMNS, ",");
		destFileW.println(headerRow);			
		
		for (Row row : sheet) {
			if(row.getRowNum() == 0) {
				 continue;
			}
			 
			RuleRec ruleRec = new RuleRec(row);
			 
			if(ruleRec.getText() == null || ruleRec.getText().equals("")) continue;
			
			if(ruleRec.includeForExtraction())
				serializeRules(modelName, ruleRec, colNames, destFileW);
		}
		
		 destFileW.flush();
		 destFileW.close();
		 log("Done...", Level.INFO);
	}

	private static void serializeRules(String modelName, RuleRec ruleRec, List<String> colNames, PrintWriter writer) {
		StringBuilder sb = new StringBuilder();
		for(String colName : LT_RULE_COLUMNS) {
			if(!sb.toString().equals(""))
				sb.append(",");
			 
			if(colName.equals("NAME")) {
				sb.append(Utilities.formatToString(ruleRec.getName()));
			}
			else if(colName.equals("RULE_TEXT")) {
				sb.append(Utilities.formatToString(ruleRec.getText()));
			}
			else if(colName.equals("MODEL_NAME")) {
				sb.append(Utilities.formatToString(modelName));
			}
		}		
		
		writer.println(sb.toString());
		log(sb.toString(), Level.INFO);
	}
	
	private File mDestFolder;
	
	public void setDestinationFolder(File destFolder) {
		mDestFolder = destFolder;
	}
	
	public StructureNodeRec buildStructureTree(String modelName, String sheetName) throws Exception {
		StructureNodeRec rootStrucNodeRec = null;
		
		Sheet sheet = mWb.getSheet(sheetName);
		Row headerRow = sheet.getRow(1);
		List<String> colNames = getRowContent(headerRow);
		log(colNames.toString(), Level.INFO);
		
		StructureNodeRec currNodeRec = null;
		StructureNodeRec prevNodeRec = null;
		Map<String, Integer> parentToChildCountMap = new LinkedHashMap<String, Integer>();
		Map<String, StructureNodeRec> nodeKeyToNodeRecMap = new LinkedHashMap<String, StructureNodeRec>();
		int levelOffset = 0;
		String origModelName = null;		
		for (Row row : sheet) {
			if(row.getRowNum() <= 1) {
				 continue;
			}
			 
			currNodeRec = new StructureNodeRec(row);
			
			String name = currNodeRec.getName(); 
			
			if(name == null || name.equals("")) continue;

			 NodeType nodeType = null;
			 String parentName = null;
			 if(name.trim().startsWith("***See Tab")) {
				 String refTabName = name.substring(name.indexOf("\"") + 1, name.lastIndexOf("\""));
				 log("refTabName: " + refTabName, Level.DEBUG);
				 Sheet refTab = mWb.getSheet(refTabName);
				 if(refTab == null) {
					 String msg = "Could not find the expected tab: " + refTab; 
					 log(msg, Level.ERROR);
					 throw new RuntimeException(msg);
				 }
				 
				 parentName = prevNodeRec.getName();
				 
				 for(Row refRow : refTab) {
					 currNodeRec = new StructureNodeRec(refRow);
					 name = currNodeRec.getName();
					 if(refRow.getRowNum() <= 1 || name == null || name == "") {
						 continue;
					 }
					 nodeType = currNodeRec.getNodeType();
					 
					 if(nodeType.isNonBOM()) {
						 currNodeRec.setPlanLevel(prevNodeRec.getPlanLevel() + 1);
						 currNodeRec.setParent(prevNodeRec.getName());
						 String parentKey = currNodeRec.getPlanLevel() + ":" + currNodeRec.getParent();
						 currNodeRec.setTreeSeq(getTreeSeq(parentToChildCountMap, parentKey));						 
					 }
				 }
				 
				 continue;
			 }
			 else {
				 nodeType = currNodeRec.getNodeType();
				 parentName = currNodeRec.getParentName();
				 boolean isRootNode = (parentName == null || parentName.equals(""));
				 Integer planLevel = currNodeRec.getPlanLevel();
				 
				 if(isRootNode && (planLevel==1)) {
					 levelOffset = -1;
				 }
				 
				 if(planLevel != null) currNodeRec.setPlanLevel(planLevel + levelOffset);
				 planLevel = currNodeRec.getPlanLevel();
				 
				 if(isRootNode && modelName != null) {
					 if(!nodeType.isNonBOM()) {
						 if(!isValidBOMModelName(modelName))
							 throw new Exception("Invalid BOM model name: " + modelName);
						 String origSysRef = "OPTIONAL:";
						 origSysRef += modelName.substring(modelName.lastIndexOf('(') + 1, modelName.lastIndexOf(' '));
						 origSysRef += ':';
						 origSysRef += modelName.substring(modelName.lastIndexOf(' ') + 1, modelName.lastIndexOf(')'));
						 currNodeRec.setOrigSysRef(origSysRef);
					 }
					 origModelName = currNodeRec.getName();
					 currNodeRec.setName(modelName);
				 }
				 else if(planLevel == 1 && modelName != null && origModelName.equals(currNodeRec.getParentName())) { // Children of root
					 currNodeRec.setParent(modelName);
				 }
				 
				 if(nodeType != null && ( nodeType.isNonBOM() || (isRootNode)) ) {
					 String parentKey = planLevel + ":" + parentName;
					 currNodeRec.setTreeSeq(getTreeSeq(parentToChildCountMap, parentKey));
				 }
				 
				 String currentNodeKey = (currNodeRec.getPlanLevel()) + ":" + currNodeRec.getName();
				 nodeKeyToNodeRecMap.put(currentNodeKey, currNodeRec);
				 if(isRootNode) {
					 rootStrucNodeRec = currNodeRec;
				 }
				 else {
					 String parentNodeKey = (currNodeRec.getPlanLevel() - 1) + ":" + currNodeRec.getParentName();
					 StructureNodeRec parentNodeRec = nodeKeyToNodeRecMap.get(parentNodeKey);
					 currNodeRec.setParent(parentNodeRec);
					 parentNodeRec.getChildren().add(currNodeRec);
					 
				 }
			 }
			 
			 prevNodeRec = currNodeRec;
		 }
		
		return rootStrucNodeRec;
	}
	
	public void extractStructure(String modelName, String sheetName) throws Exception {
		if(mRootStrucNodeRec == null) {
			mRootStrucNodeRec = buildStructureTree(modelName, sheetName);
		}
		
		PrintWriter destFileW = new PrintWriter(getTargetFile("Structure"));
		Sheet sheet = mWb.getSheet(sheetName);
		List<String> colNames = getColumnNames(sheet.getRow(1));
		String headerRow = StringUtils.join(LT_STRUCTURE_COLUMNS, ",");
		destFileW.println(headerRow);		
		processStructureNodeRec(mRootStrucNodeRec, colNames, destFileW);
		
		
		destFileW.flush();
		destFileW.close();
		log("Done...", Level.INFO);		
	}
	
	private static void processStructureNodeRec(StructureNodeRec nodeRec, List<String> colNames, PrintWriter writer) {
		if(nodeRec.getParent() != null && !nodeRec.getNodeType().isNonBOM()) {
			return;
		}
		processNodeRec(nodeRec, colNames, writer);
		 for(StructureNodeRec childNodeRec : nodeRec.getChildrenUnModifiable()) {
			 processStructureNodeRec(childNodeRec, colNames, writer);
		 }
	}
	
	public static List<String> getColumnNames(Row headerRow) {
		short minColIx = headerRow.getFirstCellNum();
		short maxColIx = headerRow.getLastCellNum();
		List<String> colNames = new ArrayList<String>();
		for(short colIx=minColIx; colIx<maxColIx; colIx++) {
			Cell cell = headerRow.getCell(colIx);
			colNames.add(cell.toString());
		}
		
		return colNames;
	}
	
	public void extractPropertyValues(String modelName, String sheetName) throws Exception {
		PrintWriter destFileW = new PrintWriter(getTargetFile("PropertyValues"));		
		
		Sheet sheet = mWb.getSheet(sheetName);
		
		Row hdrRow = sheet.getRow(1);
		List<String> colNames = getColumnNames(hdrRow);
		log(colNames.toString(), Level.INFO);
		 

		 String headerRow = StringUtils.join(LT_PROPERTY_VAL_COLUMNS, ",");
		 destFileW.println(headerRow);		 
		 
		 Map<String, String> nodeToParentMap = new LinkedHashMap<String, String>();
		 Map<String, Integer> parentToChildCountMap = new LinkedHashMap<String, Integer>();
		 int levelOffset = 0;
		 StructureNodeRec currNodeRec = null;
		 StructureNodeRec prevNodeRec = null;
		 for (Row row : sheet) {
			 if(row.getRowNum() <= 1) {
				 continue;
			 }
			 
			 currNodeRec = new StructureNodeRec(row);
			 
			 String name = currNodeRec.getName();
			 if(name == null || name.equals("")) continue;
			 
			 
			 String parentName;
			 NodeType nodeType;
			 
			 if(name.trim().startsWith("***See Tab")) {
				 String refTabName = name.substring(name.indexOf("\"") + 1, name.lastIndexOf("\""));
				 log("refTabName: " + refTabName, Level.DEBUG);
				 Sheet refTab = mWb.getSheet(refTabName);
				 if(refTab == null) {
					 String msg = "Could not find the expected tab: " + refTab; 
					 log(msg, Level.ERROR);
					 throw new RuntimeException(msg);
				 }
				 
				 parentName = prevNodeRec.getName();
				 List<String> refTabColNames = getColumnNames(refTab.getRow(1));
				 for(Row refRow : refTab) {
					 currNodeRec = new StructureNodeRec(refRow);
					 name = currNodeRec.getName();
					 if(refRow.getRowNum() <= 1 || name == null || name == "") {
						 continue;
					 }
					 nodeType = currNodeRec.getNodeType();
					 currNodeRec.setParent(prevNodeRec.getName());					 
					 
					 if(nodeType.isNonBOM()) {
						 serializeNodePropValues(modelName, currNodeRec, refTabColNames, destFileW);
					 }
				 }
				 
				 continue;
			 }
			 
			 else {
				 nodeType = currNodeRec.getNodeType();
				 parentName = currNodeRec.getParentName();
				 boolean isRootNode = (parentName == null || parentName.equals(""));
				 if(isRootNode) {
					 // modelName = currNodeRec.getName();
				 }
				 if(nodeType != null && nodeType.isNonBOM()) {
					 serializeNodePropValues(modelName, currNodeRec, colNames, destFileW);
				 }				 
			 }
			 
			 prevNodeRec = currNodeRec;
		 }
		 
		 destFileW.flush();
		 destFileW.close();		 
	}
	
	private static void serializeNodePropValues(String modelName, StructureNodeRec nodeRec, List<String> colNames, PrintWriter writer) {
		List<String[]> psPropVals = nodeRec.getPropertyValues();
		 StringBuilder sb = new StringBuilder();
		 
		 for(String[] psNodePropVals : psPropVals) {
			 sb.append(Utilities.formatToString(nodeRec.getName()));
			 sb.append(",");
			 sb.append(Utilities.formatToString(psNodePropVals[0]));
			 sb.append(",");
			 sb.append(Utilities.formatToString(modelName));
			 sb.append(",");
			 sb.append(Utilities.formatToString(psNodePropVals[1]));
			 sb.append(",");
			 sb.append(Utilities.formatToString(nodeRec.getParentName()));
			 
			 writer.println(sb.toString());
			 log(sb.toString(), Level.INFO);
			 sb = new StringBuilder();
		 }		
	}
	
	private static int getTreeSeq(Map<String, Integer> parentToChildCountMap, String parentKey) {
		 Integer childCount = parentToChildCountMap.get(parentKey);
		 childCount = (childCount != null) ? ++childCount : 1;
		 parentToChildCountMap.put(parentKey, childCount);
		 
		 return childCount;
	}
	
	private static void processNodeRec(StructureNodeRec nodeRec, List<String> colNames, PrintWriter writer) {
		 String rowRec = nodeRec.serialize();
		 writer.println(rowRec);
		 log(rowRec, Level.DEBUG);		
	}
	
	private static void log(String msg, Level logLevel) {
		System.out.println(msg);
//		logger.log(logLevel, msg);
	}
	
	private static boolean isValidBOMModelName(String modelName) {
		Pattern pattern = Pattern.compile(".+\\(\\d{2} \\d{3,}+\\)");
		Matcher matcher = pattern.matcher(modelName);
		return matcher.matches();
	}
}
	