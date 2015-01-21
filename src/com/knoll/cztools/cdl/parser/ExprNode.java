package com.knoll.cztools.cdl.parser;


import com.sun.java.util.collections.Collections;
import com.sun.java.util.collections.HashMap;
import com.sun.java.util.collections.Iterator;
import com.sun.java.util.collections.Set;

import java.math.BigDecimal;

public class ExprNode extends SimpleNode 
{
 ExprNode m_prevNode;
 private String m_name;
 private int m_line;
 private int m_column;
 private Set m_possibleTypes;
 private boolean m_infix;

 //cz_expression_nodes fields
 private final static int NUM_ATTRS  = 19;
 private final static String TEMPLATE_ID       = "template_id";
 private final static String TEMPLATE_TOKEN = "template_token";
 private final static String TEXT_VALUE        = "data_value";
 private final static String NUM_VALUE         = "data_num_value";
 private final static String PS_NODE_ID        = "ps_node_id";
 private final static String EXPLOSION_ID      = "model_ref_expl_id";
 private final static String NODE_DEPTH        = "display_node_depth";
 private final static String ARGUMENT_NAME     = "argument_name";
 private final static String PROPERTY_ID       = "property_id";
 private final static String EXPR_TYPE         = "expr_type"; 
 private final static String DATA_TYPE         = "data_type";
 private final static String PARAM_INDEX       = "param_index";
 private final static String PARAM_SIGNATURE_ID = "param_signature_id";
 private final static String COLLECTION_FLAG   = "collection_flag";
 private final static String PARENT_ID         = "expr_parent_id";
 private final static String RULE_ID           = "rule_id";
 private final static String CHILD_SEQ         = "seq_nbr";
 private final static String GLOBAL_SEQ        = "token_list_seq";
 private final static String SOURCE_OFFSET     = "source_offset";
 private final static String SOURCE_LENGTH     = "source_length";
 private final static String MUTABLE_FLAG      = "mutable_flag";
 private final static String RULE_TYPE      = "rule_type";
 private final static String CLASS_NAME      = "class_name";
 private final static String ARG_CLASS_NAME      = "arg_class_name";
 private final static String EVENT_SCOPE      = "event_scope";
 private final static String ARG_SIGN_ID      = "arg_sig_id";
 private final static String EVENT_ARG_INDEX      = "event_arg_index";
 private final static String  COMMAND_NAME= "command_name";
 public final static String  CONTRIBUTE  = "ContributesTo";
 public final static String  ADD               = "AddsTo";
 public final static String  SUBTRACT          = "SubtractsFrom";
 private final static String  TEMPLATE_APPLICATION  = "template_application";
 private final static String  RELATIVE_NODE_PATH  = "RelativeNodePath";
 private static HashMap s_ambiguousSysProperties;
 private HashMap m_attributes;

 //data types
 final static int DATA_TYPE_VOID               = 0;
 final static int DATA_TYPE_INTEGER            = 1;
 final static int DATA_TYPE_DECIMAL            = 2;
 final static int DATA_TYPE_BOOLEAN            = 3;
 final static int DATA_TYPE_TEXT               = 4;
 final static int DATA_TYPE_NODE               = 5;
 final static int DATA_TYPE_VARIANT            = 6;
 final static int DATA_TYPE_RETURN_NODE        = 26; // return type for StateNodes
 final static int DATA_TYPE_SIGNATURE          = -99; //just temporary until we figure out which signature
 final static String TRUE = "true";
 final static String FALSE = "false";
 final static String INTEGER_TRUE = "1";
 final static String INTEGER_FALSE = "0";
 
 //expr and AST types
 //following should be declared final but this doesn't work because of a 1.1.8 compiler bug and because our script compiles
 //a directory at a time
 private static HashMap s_exprTypes;  //constructed in initializer block, never grows afterwards, read only afterwards
 public static final int EXPR_OPERATOR     = 200;
 public static final int EXPR_LITERAL      = 201;
 public static final int EXPR_PSNODE       = 205;
 public static final int EXPR_REFNODE      = 206;
 public static final int EXPR_PROP         = 207;
 public static final int EXPR_SYS_PROP     = 210;
 public static final int EXPR_ARGUMENT     = 221;
 public static final int EXPR_TEMPLATE     = 222;  
 public static final int EXPR_FOR_ALL      = 223;
 public static final int EXPR_ITERATOR     = 224;
 public static final int EXPR_WHERE        = 225;
 public static final int EXPR_COMPATIBLE   = 226;
 public static final int EXPR_JAVA_METHOD  = 216;
 public static final int EXPR_EVENT_PARAM  = 217;
 public static final int EXPR_SYS_PARAM    = 218;
 public static final int EXPR_CALL         = 219;
 public static final int  EXPR_FOR_DISTINCT = 231 ;
 public static final int  EXPR_PS_NODE_BY_NAME = 232 ;
 
 static{
   s_exprTypes = new HashMap(20);
   s_exprTypes.put( new Integer(CDLParserTreeConstants.JJTEXPR_OPERATOR),    new Integer(EXPR_OPERATOR));
   s_exprTypes.put( new Integer(CDLParserTreeConstants.JJTEXPR_FOR_ALL),     new Integer(EXPR_FOR_ALL));
   s_exprTypes.put( new Integer(CDLParserTreeConstants.JJTEXPR_WHERE),       new Integer(EXPR_WHERE));
   s_exprTypes.put( new Integer(CDLParserTreeConstants.JJTEXPR_ITERATOR),    new Integer(EXPR_ITERATOR));
   s_exprTypes.put( new Integer(CDLParserTreeConstants.JJTEXPR_LITERAL),     new Integer(EXPR_LITERAL));
   s_exprTypes.put( new Integer(CDLParserTreeConstants.JJTEXPR_REFNODE),     new Integer(EXPR_REFNODE));
   s_exprTypes.put( new Integer(CDLParserTreeConstants.JJTEXPR_PROP),        new Integer(EXPR_PROP));
   s_exprTypes.put( new Integer(CDLParserTreeConstants.JJTEXPR_SYS_PROP),    new Integer(EXPR_SYS_PROP));
   s_exprTypes.put( new Integer(CDLParserTreeConstants.JJTEXPR_PSNODE),      new Integer(EXPR_PSNODE));
   s_exprTypes.put( new Integer(CDLParserTreeConstants.JJTEXPR_ARGUMENT),    new Integer(EXPR_ARGUMENT));
   s_exprTypes.put( new Integer(CDLParserTreeConstants.JJTEXPR_TEMPLATE),    new Integer(EXPR_TEMPLATE));
   s_exprTypes.put( new Integer(CDLParserTreeConstants.JJTEXPR_COMPATIBLE),  new Integer(EXPR_COMPATIBLE));
   s_exprTypes.put( new Integer(CDLParserTreeConstants.JJTEXPR_CALL),  new Integer((EXPR_CALL)));
   s_exprTypes.put( new Integer(CDLParserTreeConstants.JJTEXPR_EVENT),  new Integer((CDLParserTreeConstants.JJTEXPR_EVENT)));
   s_exprTypes.put( new Integer(CDLParserTreeConstants.JJTEXPR_METHOD),  new Integer((CDLParserTreeConstants.JJTEXPR_METHOD)));
   s_exprTypes.put( new Integer(CDLParserTreeConstants.JJTEXPR_EVENTSCOPE),  new Integer((CDLParserTreeConstants.JJTEXPR_EVENTSCOPE)));
   s_exprTypes.put( new Integer(CDLParserTreeConstants.JJTEXPR_EVENTARGUMENT),  new Integer((CDLParserTreeConstants.JJTEXPR_EVENTARGUMENT)));
   s_exprTypes.put( new Integer(CDLParserTreeConstants.JJTEXPR_SYSTEMARGUMENT),  new Integer((CDLParserTreeConstants.JJTEXPR_SYSTEMARGUMENT)));
   s_exprTypes.put( new Integer(CDLParserTreeConstants.JJTEXPR_DISTINCT ),  new Integer((EXPR_FOR_DISTINCT  )));
   s_exprTypes.put( new Integer(CDLParserTreeConstants.JJTEXPR_EVENTARGUMENT),  new Integer((CDLParserTreeConstants.JJTEXPR_EVENTARGUMENT)));
   //above is last put for this hashMap. It never grows after being initialized int this block

   s_ambiguousSysProperties = new HashMap();
   s_ambiguousSysProperties.put("value", "Value");
   s_ambiguousSysProperties.put("selection", "Selection");
   s_ambiguousSysProperties.put("quantity", "Quantity");
   s_ambiguousSysProperties.put("options", "Options");
 }
 
 public ExprNode(int astType) {
   super(astType);
 }  

 public ExprNode(CDLParser parser, int astType) {
   super(parser, astType);
   m_attributes = new HashMap(NUM_ATTRS);
   m_prevNode = null;
   Integer exprType = (Integer)s_exprTypes.get(new Integer(astType));
   if(exprType == null) {
     throw new RuntimeException("Invalid Expression Type: " + astType);
   }
   setExprType(exprType.intValue());
   if(exprType.intValue() == EXPR_PSNODE) setMutableFlag(true);
   setGlobalSequenceNumber(parser.getNextSequence());
   // setRuleId(parser.getModelAdapter().getRuleId());
   // setRuleType(parser.getModelAdapter().getRuleType());
   if(exprType.intValue() == CDLParserTreeConstants.JJTEXPR_METHOD ) {
     // setClassName(parser.getModelAdapter().getClassName());
   }
   setLocationInSource(parser.getToken(1).beginLine, parser.getToken(1).beginColumn);
   // setSourcePosition(parser.getToken(1).offset, parser.getToken(1).image.length());
   m_infix = true;
 }

 public boolean isRoot() {
   return (jjtGetParent() == null);
 }
   
 public boolean isLastChild() {
   Node parent = jjtGetParent();
   return (parent == null ||
           this.equals(parent.jjtGetChild(parent.jjtGetNumChildren() - 1)));
 }

 public String toString()
 {
   StringBuffer buf = new StringBuffer();
   String name = CDLParserTreeConstants.jjtNodeName[id];
   buf.append(name + ":" + getName());

   if(!m_attributes.isEmpty()) {
     buf.append("  (");
     Set keys = m_attributes.keySet();
     int i = 0;
     for(Iterator iter = keys.iterator(); iter.hasNext();) {
       String field = (String)iter.next();
       buf.append(field);
       buf.append("=");
       buf.append(m_attributes.get(field));
       if(i < keys.size()-1) buf.append(", ");
       i++;
     }
     buf.append(")");
   }
   
   return buf.toString();
 }

 void setExprType(int exprType)
 {
   m_attributes.put(EXPR_TYPE, new Integer(exprType));
 }

 public int getExprType()
 {
   return ((Integer)m_attributes.get(EXPR_TYPE)).intValue();
 }

 void setAstType(int astType)
 {
   id = astType;
   setExprType(((Integer)s_exprTypes.get(new Integer(id))).intValue());
 }

 int getASTType()
 {
   return id;
 }

 void setName(String name)
 {
   m_name = name;
 }

 public String getName()
 {
   return m_name;
 }

 void setCommandName(String commandName) 
 {
    m_attributes.put(COMMAND_NAME, commandName);
 }

 public String getCommandName() 
 {
    return (String)m_attributes.get(COMMAND_NAME);
 }
 
 String getDisplayName()
 {
   String name = m_name;
   if (getExprType() == EXPR_PSNODE)  {
     for(int i = 0; i < jjtGetNumChildren(); i++){
       name += "." + ((ExprNode)jjtGetChild(i)).getName();
     }
   }
   return name;
 }
 
 void setTemplateId(Integer id)
 {
   m_attributes.put(TEMPLATE_ID, id);
 }

 public Integer getTemplateId()
 {
   return (Integer)m_attributes.get(TEMPLATE_ID);
 }

/**
* TODO. The logic should be data driven (seed data)
* For now hard code the sysProperties here
*/
 boolean isAmbiguousSysOrUserProperty() {
   int exprType =  getExprType();
   if(exprType == EXPR_PROP) return false; //always non-Ambiguous and clear
   if(exprType != EXPR_SYS_PROP) return false; // not even a sysProp
   return s_ambiguousSysProperties.containsKey(getName().toLowerCase() );
 }
 
 void setTextValue(String value) throws ParseException {
   setName(value);
   if(getExprType() == ExprNode.EXPR_LITERAL) {
       Integer dataType = getDataType();
       if(dataType != null) {
         switch ( dataType.intValue() ) {
           case DATA_TYPE_BOOLEAN:
             if(INTEGER_TRUE.equals(value) || TRUE.equalsIgnoreCase(value)) {
               value =  INTEGER_TRUE;
             } else if(INTEGER_FALSE.equals(value) ||  FALSE.equalsIgnoreCase(value)) {
               value =  INTEGER_FALSE;
             } else {
               //TODO: FND Message
               String msg = value + " is invalid as a Boolean literal. Valid Boolean literals are True or 1 or False or 0";
               throw new ParseException(msg, null); 
             //throw new RuntimeException(dataType +  " is invalid as a Boolean literal. Valid Boolean literal are True or 1 or False or 0");
             }
             break;
         }
       }
     }
   m_attributes.put(TEXT_VALUE, value);
 }

 void setNumValue(String value)
 {
   double numVal;
   if(value.equalsIgnoreCase("PI")) numVal = Math.PI;
   else if(value.equalsIgnoreCase("E")) numVal = Math.E;
   else numVal = new BigDecimal(value).doubleValue();
   m_attributes.put(NUM_VALUE, new Double(numVal));
   setName(value);
 }

 String getValue()
 {
   String val = (String)m_attributes.get(TEXT_VALUE);
   if(val == null) val = ((Double)m_attributes.get(NUM_VALUE)).toString();
   return val;
 }

 Object getValueAsObject()
 {
   String val = (String)m_attributes.get(TEXT_VALUE);
   if(val == null) return m_attributes.get(NUM_VALUE);
   return val;
 }
 
 int getRuleType() {
   Integer ruleType = (Integer)m_attributes.get(RULE_TYPE);
   return ruleType != null ? ruleType.intValue() : -1;
 }
 
 void setRuleType(Integer ruleType) {
   m_attributes.put(RULE_TYPE, ruleType);
 }
 
 void setArgument(String arg)
 {
   String internedArg = arg.intern();
   m_attributes.put(ARGUMENT_NAME, internedArg);
   setName(internedArg);
 }

 String getArgumentName()
 {
   return (String)m_attributes.get(ARGUMENT_NAME);
 }
 
 void setPsNodeId(long id)
 {
   m_attributes.put(PS_NODE_ID, new Long(id));
 }

 void setExplosionId(long id)
 {
   m_attributes.put(EXPLOSION_ID, new Long(id));
 }

 public Long getPsNodeId()
 {
   return (Long)m_attributes.get(PS_NODE_ID);
 }

 Long getExplosionId(int id)
 {
   return (Long)m_attributes.get(EXPLOSION_ID);
 }

 String getJavaDataType() {
   String argClassName = getArgClassName();
   if(argClassName != null) return argClassName;
   switch(getDataType().intValue()) {
     case ExprNode.DATA_TYPE_BOOLEAN:
       return "boolean";
     case ExprNode.DATA_TYPE_DECIMAL:
       return "double";
     case ExprNode.DATA_TYPE_INTEGER:
       return "int";
     case ExprNode.DATA_TYPE_NODE:
       return "oracle.apps.cz.cio.IRuntimeNode";
     case ExprNode.DATA_TYPE_TEXT:
      return "java.lang.String";
   }
   return null;
 }
 
 void setDisplayNodeDepth(int depth)
 {
   m_attributes.put(NODE_DEPTH, new Integer(depth));
 }

 void setToken(String token)
 {
   m_attributes.put(TEMPLATE_TOKEN, token);
 }

 String getToken()
 {
   return (String)m_attributes.get(TEMPLATE_TOKEN);
 }
 
 void setDataType(int type) throws ParseException {
   m_attributes.put(DATA_TYPE, new Integer(type));
   if(getExprType() == ExprNode.EXPR_LITERAL && type == DATA_TYPE_BOOLEAN) {
     String value = getValue();
     setTextValue(value);
   }
 }

 boolean convertibleToInt() {
   Object value = m_attributes.get(NUM_VALUE);
   if(value == null) return false;
   if(value instanceof Integer) return true;
   Double d = (Double)value;
   return d.intValue() == d.doubleValue();
 }
 
 Integer getDataType()
 {
   return (Integer)m_attributes.get(DATA_TYPE);
 }

 //this is for arguments whose type we cant determine until we see the usage
 void setPossibleDataTypes(Set types)
 {
   m_possibleTypes = types;
 }

 Set getPossibleDataTypes()
 {
   return m_possibleTypes != null ? m_possibleTypes : Collections.EMPTY_SET;
 }

 Integer getReturnsCollection() {
   return (Integer)m_attributes.get(COLLECTION_FLAG);
 }
 
 void setReturnsCollection(boolean returnsCollection)
 {
   m_attributes.put(COLLECTION_FLAG, new Integer(returnsCollection ? 1 : 0));
 }

 void setParamIndex(int index)
 {
   m_attributes.put(PARAM_INDEX, new Integer(index));
 }

 int getParamIndex()
 {
   Integer index = (Integer)m_attributes.get(PARAM_INDEX);
   return index == null ? -1 : index.intValue();
 }

 void setParamSignatureId(int id)
 {
   m_attributes.put(PARAM_SIGNATURE_ID, new Integer(id));
 }
 
 int getParamSignatureId()
 {
   Integer id = (Integer)m_attributes.get(PARAM_SIGNATURE_ID);
   return id == null ? -1 : id.intValue();
 }

 void setRuleId(int id)
 {
   m_attributes.put(RULE_ID, new Integer(id));
 }

 Integer getRuleId()
 {
   return (Integer)m_attributes.get(RULE_ID);
 }

 void setChildSequenceNumber(int num)
 {
   m_attributes.put(CHILD_SEQ, new Integer(num));
 }

 Integer getChildSequenceNumber()
 {
   return (Integer)m_attributes.get(CHILD_SEQ);
 }

 void setGlobalSequenceNumber(int num)
 {
   m_attributes.put(GLOBAL_SEQ, new Integer(num));
 }

 Integer getGlobalSequenceNumber()
 {
   return (Integer)m_attributes.get(GLOBAL_SEQ);
 }

 void setPrevNode(ExprNode node)
 {
   m_prevNode = node;
 }

 boolean isChained()
 {
   return m_prevNode != null;
 }

 int getPrevIndex()
 {
   if(!isChained()) throw new RuntimeException("Node is not chained");
   return m_prevNode.getParamIndex();
 }

 void setPropertyId(int id)
 {
   m_attributes.put(PROPERTY_ID, new Integer(id));
 }

 void setParentId(long id)
 {
   m_attributes.put(PARENT_ID, new Long(id));
 }

 void setLocationInSource(int line, int column)
 {
   m_line = line;
   m_column = column;
 }

 int getSourceLine()
 {
   return m_line;
 }

 int getSourceColumn()
 {
   return m_column;
 }

 void setSourcePosition(int offset, int length)
 {
   m_attributes.put(SOURCE_OFFSET, new Integer(offset));
   m_attributes.put(SOURCE_LENGTH, new Integer(length));
 }

 int getSourceOffset()
 {
   Integer offset = (Integer)m_attributes.get(SOURCE_OFFSET);
   return offset == null ? -1 : offset.intValue();
 }

 int getSourceLength()
 {
   Integer length = (Integer)m_attributes.get(SOURCE_LENGTH);
   return length == null ? -1 : length.intValue();
 }

 void setMutableFlag(boolean flag)
 {
   if(flag) m_attributes.put(MUTABLE_FLAG, "1");
   else m_attributes.put(MUTABLE_FLAG, "0");
 }

 boolean getMutableFlag()
 {
   String mutable = (String)m_attributes.get(MUTABLE_FLAG);
   if(mutable == null || mutable.equals("0")) return false;
   return true;
 }

 boolean isInfixOperator()
 {
   return m_infix;
 }

 void setInfixOperatorFlag(boolean infix)
 {
   m_infix = infix;
 }

 /**
  * This method removes the first <numChildren> children of this node
  * We did it this way instead of just a jjtRemoveFirst() for performance
  * reasons i.e. the children list is an array and removing from the beginning
  * is expensive
  */
 public void jjtRemoveFirstChildren(int numChildren)
 {
   if(children == null) return;
   if (jjtGetNumChildren() <= numChildren) {
     children = new Node[0];
   } else {
     Node c[] = new Node[jjtGetNumChildren()-numChildren];
     System.arraycopy(children, numChildren, c, 0, c.length);
     children = c;
   }
 }

 public ExprNode getParent()
 {
   return (ExprNode)jjtGetParent();
 }

 /**
  * clears all attrbitues and removes all children
  * retains ruleId, and global and child sequence numbers
  */
 void clear()
 {
   int ruleId = getRuleId().intValue();
   int childSeq = getChildSequenceNumber().intValue();
   int globalSeq = getGlobalSequenceNumber().intValue();
   m_attributes.clear();
   m_name = null;
   setRuleId(ruleId);
   setChildSequenceNumber(childSeq);
   setGlobalSequenceNumber(globalSeq);
   children = null;
 }

 void setClassName(String className) {
   m_attributes.put(CLASS_NAME, className);
 }

 String getClassName() {
   return (String)m_attributes.get(CLASS_NAME);
 }

 void setEventScope(Object scope) {
   m_attributes.put(EVENT_SCOPE, scope);
 }



 public Object getArgSigId() 
 {
   return m_attributes.get(ARG_SIGN_ID);
 }
 void setArgSigId(Object className) {
   m_attributes.put(ARG_SIGN_ID, className);
 }
 
 public Object getEventScope() 
 {
   return m_attributes.get(EVENT_SCOPE);
 }

 void setTemplateApplicationFlag(boolean tempApp) {
    m_attributes.put(TEMPLATE_APPLICATION, tempApp ? Boolean.TRUE : Boolean.FALSE);
 }

 boolean isTemplateApplication() {
   Boolean value = (Boolean)m_attributes.get(TEMPLATE_APPLICATION);
   return value != null ? value.booleanValue() : false;
 }
 
 void setArgClassName(String className) {
   m_attributes.put(ARG_CLASS_NAME, className);
 }


 public Object getEventArgIndex() 
 {
   return m_attributes.get(EVENT_ARG_INDEX);
 }
 void setEventArgIndex(Object id) {
   m_attributes.put(EVENT_ARG_INDEX, id);
 }
 

 String getArgClassName() {
   return (String)m_attributes.get(ARG_CLASS_NAME);
 } 
boolean isCXRule() {
 return getRuleType() == 300;
}

 /**
  * Writes the node and all its descendants out to the database
  */
 void write()
 {
   if("DISTINCT".equals(getName())) return;
   //SKM:Int2Long: Changed recordId to long: GM issue

    long recordId = -1; // parser.getModelAdapter().createRecord(m_attributes);
    for(int i=0; i<jjtGetNumChildren(); i++){
     ExprNode child = (ExprNode)jjtGetChild(i);
     child.setParentId(recordId);
     child.write();
   }
 }

 static boolean isPsNodeType(int type) {
   switch(type) {
     case DATA_TYPE_BOOLEAN:
     case DATA_TYPE_INTEGER:
     case DATA_TYPE_TEXT:
     case DATA_TYPE_DECIMAL:
     case DATA_TYPE_VARIANT:
     case DATA_TYPE_VOID :
       return false;
     case DATA_TYPE_NODE:
     case EXPR_PSNODE:
     case DATA_TYPE_RETURN_NODE:
       return true;
   } 
   return false;
 }
 
 boolean hasSysPropertyForChildPsNode() {
   int numChild = jjtGetNumChildren();
   for(int num = 0; num<numChild; num++ ) {
     ExprNode child = ((ExprNode)jjtGetChild(num));
     Integer chType =  child.getDataType();
     if(chType != null && isPsNodeType(chType.intValue()) && child.getExprType() == EXPR_SYS_PROP ) {
       return true;
     }
   }
   return false;
 }

 public String getRelativeNodePath() {
   return (String)m_attributes.get(RELATIVE_NODE_PATH);
 }
 
 public void setRelativeNodePath(String relPath) {
   m_attributes.put(RELATIVE_NODE_PATH, relPath);    
 }
}
