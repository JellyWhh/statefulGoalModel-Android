/**
 * 
 */
package edu.fudan.se.pool;

import jade.core.AID;
import jade.util.leap.Serializable;

/**
 * @author zjh
 *
 */
public class Message implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private AID toAid;// 1
	private String toRole = null;// 2

	// 1，2两项是委托方必须要设置其中之一的，一开始委托的时候，只需要设置上方标示2就可以了，
	// 如果后续交流涉及到和上次委托方有关，则1是必须设置的，否则，应当将toAid设置为空。

	private AID fromAid;


	private String content = null;// 捎带闲话。
	private String fromGoal = null;// 委托方的Goal
	private String confirm = null;// 返回被委托方接受委托与否。

	public AID getFromAID() {
		return fromAid;
	}

	public void setFromAID(AID fromAID) {
		this.fromAid = fromAID;
	}
	
	

	
	@Override
	public String toString() {
		
		String toAidStr = toAid == null ? "NULL": toAid.getName();
		String fromAidStr = fromAid == null ? "NULL": fromAid.getName();
		
		return "toAid:" + toAidStr + "#toRole:" + toRole + "#content:"
				+ content + "#fromGoal:" + fromGoal + "#confirm:" + confirm +"#fromAid:"+fromAidStr;
	}

	public AID getToAid() {
		return toAid;
	}

	public void setToAid(AID toAid) {
		this.toAid = toAid;
	}

	public String getToRole() {
		return toRole;
	}

	public void setToRole(String toRole) {
		this.toRole = toRole;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getFromGoal() {
		return fromGoal;
	}

	public void setFromGoal(String fromGoal) {
		this.fromGoal = fromGoal;
	}

	public String getConfirm() {
		return confirm;
	}

	public void setConfirm(String confirm) {
		this.confirm = confirm;
	}
}
