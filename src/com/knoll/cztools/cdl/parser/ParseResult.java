package com.knoll.cztools.cdl.parser;

public class ParseResult {

	private boolean mIsAccumulatorStatement;
	private boolean mIsCompatibilityStatement;
	private boolean mIsDefaultOrSearchOnlyStatement;
	private String mDefaultOrSearchOperator;

	ParseResult() {
	}

	void setIsAccumulatorStatement(boolean b) {
		mIsAccumulatorStatement = b;
	}

	/**
	 * Returns a boolean indicating whether the Rule statement is of an
	 * accumulator type.
	 *
	 * @return <code>true</code> if this Rule statement is of an accumulator
	 *         type, <code>false</code> otherwise
	 */
	public boolean isAccumulatorStatement() {
		return mIsAccumulatorStatement;
	}

	void setIsCompatibilityStatement(boolean b) {
		mIsCompatibilityStatement = b;
	}

	/**
	 * Returns a boolean indicating whether the statement is a property based
	 * compatibility rule.
	 *
	 * @return <code>true</code> if this statement is a property based
	 *         compatibility rule, <code>false</code> otherwise
	 */
	public boolean isCompatibilityStatement() {
		return mIsCompatibilityStatement;
	}

	void setIsDefaultOrSearchOnlyStatement(boolean b) {
		mIsDefaultOrSearchOnlyStatement = b;
	}

	/**
	 * Returns a boolean indicating whether this Rule statement can be used only
	 * as a Default or Search Decision.
	 * 
	 * @return <code>true</code> if the Rule statement can be used only as a
	 *         Default or Search Decision, <code>false</code> otherwise
	 */
	public boolean isDefaultOrSearchOnlyStatement() {
		return mIsDefaultOrSearchOnlyStatement;
	}

	void setDefaultOrSearchOperator(String operator) {
		mDefaultOrSearchOperator = operator;
	}

	/**
	 * Returns the name of the Default or Search operator, if the Rule statement
	 * contains operator(s) that can be used only in Default or Search Decisions
	 * 
	 * @see ParseResult#isDefaultOrSearchOnlyStatement()
	 *      isDefaultOrSearchOnlyStatement
	 * 
	 * @exception RuntimeException
	 *                if his method is called when the Rule statement is not a
	 *                Default or Search statement, i.e. if
	 *                isDefaultOrSearchOnlyStatement() returns false.
	 * 
	 * @return name of the Default or Search operator
	 */
	public String getDefaultOrSearchOperator() {
		if (!isDefaultOrSearchOnlyStatement())
			throw new RuntimeException(
					"Statement not of type Default or Search");
		return mDefaultOrSearchOperator;
	}
}
