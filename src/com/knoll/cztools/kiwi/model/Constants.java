package com.knoll.cztools.kiwi.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class Constants {
	
	public static enum TabType {
		STRUCTURE,
		RULES,
		NONE
	}
	
	public static enum Actions {
		EXTRACT_STRUCTURE("Extract CZ Structure"),
		EXTRACT_PROPERTIES("Extract CZ Properties"),
		EXTRACT_PROPERTY_VALUES("Extract CZ Property Values"),
		EXTRACT_RULES("Extract CZ Rules"),
		VALIDATE("Validate");
		
		private String mLabel;
		
		private Actions(String label) {
			mLabel = label;
		}
		
		public String label() {
			return mLabel;
		}
		
		public static Actions getValueOf(String action) {
			for(Actions a : values()) {
				if(a.label().equals(action)) return a;
			}
			
			return null;
		}
	}
	
	public static enum NodeType {
		COMPONENT("Component", "COMPONENT", true),
		OPTION_FEATURE("Option Feature", "OPTION FEATURE", true),
		INTEGER_FEATURE("Integer Feature", "INTEGER FEATURE", true),
		DECIMAL_FEATURE("Decimal Feature", "DECIMAL FEATURE", true),
		BOOLEAN_FEATURE("Boolean Feature", "BOOLEAN FEATURE", true),
		TEXT_FEATURE("Text Feature", "TEXT FEATURE", true),
		TOTAL("Total", "TOTAL", true),
		RESOURCE("Resource", "RESOURCE", true),
		OPTION("Option", "OPTION", true),
		ATO_MODEL("ATO Model", "BOM MODEL", false),
		PTO_MODEL("PTO Model", "BOM MODEL", false),
		ATO_OC("ATO Option Class", null, false),
		PTO_OC("PTO Option Class", null, false),
		ATO_ITEM("ATO Item", null, false),
		PURCHASED_ITEM("Purchased Item", null, false),
		;
		
		private String mDS30Format;
		private String mLoadToolFormat; 
		private boolean mIsNonBOM;
		
		private NodeType(String ds30Format, String loadToolFormat, boolean isNonBOM) {
			mDS30Format = ds30Format;
			mLoadToolFormat = loadToolFormat;
			mIsNonBOM = isNonBOM;
		}
		
		public String getDS30Format() {
			return mDS30Format;
		}

		public String getLoadToolFormat() {
			return mLoadToolFormat;
		}		
		
		public boolean isNonBOM() {
			return mIsNonBOM;
		}
		
		public static NodeType forDS30Format(String ds30Format) {
			for(NodeType nodeType : values()) {
				if(nodeType.getDS30Format().equals(ds30Format)) {
					return nodeType;
				}
			}
			
			return null;
		}
	}	
	
	static List<String> LT_STRUCTURE_COLUMNS = Arrays.asList("NAME","DESCRIPTION","NODE_TYPE",
                                                             "MINIMUM","MAXIMUM","INITIAL_VALUE",
                                                             "PARENT","TREE_SEQ","PLAN_LEVEL",
                                                             "EFFECTIVE_FROM","EFFECTIVE_UNTIL",
                                                             "ORIG_SYS_REF","BATCH_ID");

    static List<String> LT_PROPERTY_VAL_COLUMNS = Arrays.asList("PS_NODE_NAME","PS_PROPERTY_NAME","MODEL_NAME",
                                                                "DATA_VALUE","PARENT","BATCH_ID");	

    static List<String> LT_RULE_COLUMNS = Arrays.asList("MODEL_NAME", "DESC_TEXT","NAME", "RULE_TEXT", 
                                                        "RULE_FOLDER", "VIOLATION_MESSAGE", "UNSATISFIED_MESSAGE", 
                                                        "DISABLED_FLAG", "EFFECTIVE_USAGE_MASK", "EFFECTIVE_FROM", 
                                                        "EFFECTIVE_UNTIL", "NOTES", "EFFECTIVITY_SET_NAME", "ORIG_SYS_REF","BATCH_ID");		
	
	public static final Map<String, String> DS30_2_LT_STRUCT_MAPPING;
	public static final Map<String, String> DS30_2_LT_RULE_MAPPING;
	
	static {
		Map<String, String> map = new LinkedHashMap<String, String>();
		map.put("NAME", "Item");
		map.put("DESCRIPTION", "Description");
		map.put("NODE_TYPE", "Item\nType");
		map.put("PARENT", "Parent Item");
		map.put("PLAN_LEVEL", "Level");
		map.put("MINIMUM", "Min / Max Selections");
		map.put("MAXIMUM", "Min / Max Selections");
		map.put("PS_PROPERTY_NAME", "Element / Property");
		map.put("DATA_VALUE", "Value");
		DS30_2_LT_STRUCT_MAPPING = Collections.<String, String>unmodifiableMap(map);
		
		map = new LinkedHashMap<String, String>();
		map.put("NAME", "Rule Name");
		map.put("RULE_TEXT", "Rule Text");
		map.put("INCLUDE_FOR_LOAD", "Include");		
		DS30_2_LT_RULE_MAPPING = Collections.<String, String>unmodifiableMap(map);
	}
}
