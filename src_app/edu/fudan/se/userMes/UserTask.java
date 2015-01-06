/**
 * 
 */
package edu.fudan.se.userMes;

/**
 * 用户要做的任务
 * 
 * @author whh
 * 
 */
public class UserTask {

	private String goalModelName;
	private String elementName;
	private String description;
	private boolean isDone; // 用户是否做过了

	public UserTask(String goalModelName, String elementName, boolean isDone) {
		this.goalModelName = goalModelName;
		this.elementName = elementName;
		this.isDone = isDone;
	}

	public boolean isDone() {
		return isDone;
	}

	public void setDone(boolean isDone) {
		this.isDone = isDone;
	}

	public String getGoalModelName() {
		return goalModelName;
	}

	public void setGoalModelName(String goalModelName) {
		this.goalModelName = goalModelName;
	}

	public String getElementName() {
		return elementName;
	}

	public void setElementName(String elementName) {
		this.elementName = elementName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
