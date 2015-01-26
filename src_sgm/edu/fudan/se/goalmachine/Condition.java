/**
 * 
 */
package edu.fudan.se.goalmachine;

import java.util.Hashtable;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

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
	private boolean waitable; // 这个条件是否可以通过等待，重新改变是否满足的状态，只有pre condition需要设置

	private String valueType;

	private String leftValueDes;
	private String operator;
	private String rightValue;

	private String description;

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
			String operator, String rightValue, boolean waitable) {
		this.type = type;
		this.valueType = valueType;
		this.leftValueDes = leftValueDes;
		this.operator = operator;
		this.rightValue = rightValue;
		this.waitable = waitable;
	}

	public String getType() {
		return type;
	}

	public boolean isSatisfied() {
		return satisfied;
	}

	public void setSatisfied(boolean satisfied) {
		this.satisfied = satisfied;
	}

	public boolean isWaitable() {
		return waitable;
	}

	public Hashtable<String, IContext> getContextHashtable() {
		return contextHashtable;
	}

	public void setContextHashtable(Hashtable<String, IContext> contextHashtable) {
		this.contextHashtable = contextHashtable;
	}

	public String getRightValue() {
		return rightValue;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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

		case "Double":
			double leftValuedouble = (double) contextManager.getValue();
			switch (this.operator) {
			case "BIGGERTHAN":
				this.satisfied = (leftValuedouble > Double
						.parseDouble(this.rightValue));
				break;
			case "EQUAL":
				this.satisfied = (leftValuedouble == Double
						.parseDouble(this.rightValue));
				break;
			case "SMALLERTHAN":
				this.satisfied = (leftValuedouble < Double
						.parseDouble(this.rightValue));
				break;
			}
			break;

		case "Boolean":
			boolean leftValueBoolean = (boolean) contextManager.getValue();
			switch (this.operator) {
			case "EQUAL":
				this.satisfied = (leftValueBoolean == Boolean
						.parseBoolean(this.rightValue));
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
//		case "UI":// 弹出对话框让用户确认的
//
//			this.satisfied = isUserSelectYes(ContextManager.applicationContext,
//					description);
//
//			break;
		}

		System.out.println("---------check condition done!-------isSatisfied: "
				+ this.isSatisfied());
	}

//	private boolean isUserSelectYes(Context mContext, String description) {
//
//		// 弹出一个让用户确认的通知
//		// 新任务广播
//		Intent broadcast_nda = new Intent();
//		broadcast_nda.setAction("jade.task.NOTIFICATION");
//		broadcast_nda.putExtra("Content", "You have to confirm something!");
//		mContext.sendBroadcast(broadcast_nda);
//
//		final String[] isYes = new String[1];
//		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
//		builder.setTitle("Confirm");
//		builder.setIcon(android.R.drawable.ic_dialog_info);
//		builder.setMessage(description);
//
//		builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				isYes[0] = "Yes";
//			}
//		});
//
//		builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
//
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				isYes[0] = "No";
//			}
//		});
//
//		AlertDialog dialog = builder.create();
//		dialog.setCanceledOnTouchOutside(false);// 使除了dialog以外的地方不能被点击
//		dialog.show();
//
//		System.out.println("Debug!!!!!!--isYes[0]:" + isYes[0]);
//
//		if (isYes[0].equals("Yes")) {
//			return true;
//		} else {
//			return false;
//		}
//
//	}

}
