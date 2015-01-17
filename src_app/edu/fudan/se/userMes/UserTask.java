/**
 * 
 */
package edu.fudan.se.userMes;

import edu.fudan.se.goalmodel.RequestData;

/**
 * 用户要做的任务
 * 
 * @author whh
 * 
 */
public class UserTask {

	private String time;
	private String goalModelName;
	private String elementName;
	private String description;
	private boolean isDone; // 用户是否做过了
	
	/**
	 * 有些user task可能需要request data
	 */
	private RequestData requestData;
	
	public UserTask(String time,String goalModelName, String elementName, boolean isDone) {
		this.time = time;
		this.goalModelName = goalModelName;
		this.elementName = elementName;
		this.isDone = isDone;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
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

	public RequestData getRequestData() {
		return requestData;
	}

	public void setRequestData(RequestData requestData) {
		this.requestData = requestData;
	}
	
}
