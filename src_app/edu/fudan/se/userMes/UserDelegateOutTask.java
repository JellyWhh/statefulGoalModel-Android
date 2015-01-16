/**
 * 
 */
package edu.fudan.se.userMes;

import edu.fudan.se.goalmodel.RequestData;

/**
 * 执行到一个需要委托出去的任务后，需要用户选择委托去向agent，这个任务是<code>UserDelegateOutTask</code>，它是
 * <code>UserTask</code>的子类
 * 
 * @author whh
 * 
 */
public class UserDelegateOutTask extends UserTask {
	
	/**
	 * 委托出去的任务中可能需要携带一些数据
	 */
	private RequestData requestData;

	public UserDelegateOutTask(String time, String goalModelName,
			String elementName, boolean isDone) {
		super(time, goalModelName, elementName, isDone);
	}

	public RequestData getRequestData() {
		return requestData;
	}

	public void setRequestData(RequestData requestData) {
		this.requestData = requestData;
	}

}
