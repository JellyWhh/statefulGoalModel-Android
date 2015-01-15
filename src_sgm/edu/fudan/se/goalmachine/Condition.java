/**
 * 
 */
package edu.fudan.se.goalmachine;

import java.util.Hashtable;

import edu.fudan.se.contextmanager.ContextManager;
import edu.fudan.se.contextmanager.IContext;

/**
 * 状态机中需要检查的条件Condition,具体类型有：CONTEXT,PRE,POST,COMMITMENT,INVARIANT
 * 
 * @author whh
 * 
 */
public class Condition {

	private String type; // 条件类型，具体有CONTEXT,PRE,POST,COMMITMENT,INVARIANT
	private boolean satisfied = true; // 条件是否被满足，true为被满足
	private boolean canRepairing; // 标志这个条件是否可通过主动做一些事来修复，使之满足，只针对PreCondition

	private String valueType;
	private String leftValueDes;
	private String operator;
	private String rightValue;

	private Hashtable<String, IContext> contextHashtable;

	public Condition(String type, String valueType, String leftValueDes,
			String operator, String rightValue) {
		this.type = type;
		this.valueType = valueType;
		this.leftValueDes = leftValueDes;
		this.operator = operator;
		this.rightValue = rightValue;
	}

	public Condition(String type, String valueType, String leftValueDes,
			String operator, String rightValue, boolean canRepairing) {
		this.type = type;
		this.valueType = valueType;
		this.leftValueDes = leftValueDes;
		this.operator = operator;
		this.rightValue = rightValue;
		this.canRepairing = canRepairing;
	}

	public String getType() {
		return type;
	}

	public boolean isSatisfied() {
		return satisfied;
	}

	public boolean isCanRepairing() {
		return canRepairing;
	}

	public Hashtable<String, IContext> getContextHashtable() {
		return contextHashtable;
	}

	public void setContextHashtable(Hashtable<String, IContext> contextHashtable) {
		this.contextHashtable = contextHashtable;
	}

	public void check() {
		System.out.println("---------check condition start!-------type: "
				+ this.type + ", valueType: " + this.valueType
				+ ", leftValueDes: " + this.leftValueDes);

		ContextManager contextManager = new ContextManager(
				this.contextHashtable.get(this.leftValueDes));

		switch (this.valueType) {
		case "Int":
			int leftValueInt = (int) contextManager.getValue();
			switch (this.operator) {
			case "BIGGERTHAN":
				this.satisfied = (leftValueInt > Integer
						.parseInt(this.rightValue));
				break;
			case "EQUAL":
				this.satisfied = (leftValueInt == Integer
						.parseInt(this.rightValue));
				break;
			case "SMALLERTHAN":
				this.satisfied = (leftValueInt < Integer
						.parseInt(this.rightValue));
				break;
			}
			break;

		case "String":
			String leftValueString = (String) contextManager.getValue();
			switch (this.operator) {
			case "EQUAL":
				this.satisfied = (leftValueString.equals(this.rightValue));
				break;
			case "NOTEQUAL":
				this.satisfied = (!leftValueString.equals(this.rightValue));
				break;
			}
			break;
		case "Time":

			break;
		}

		System.out.println("---------check condition done!-------isSatisfied: "
				+ this.isSatisfied());
	}
}
