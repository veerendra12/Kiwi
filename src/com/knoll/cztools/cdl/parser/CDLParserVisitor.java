package com.knoll.cztools.cdl.parser;

public interface CDLParserVisitor
{
  public Object visit(SimpleNode node, Object data) throws ParseException;
}