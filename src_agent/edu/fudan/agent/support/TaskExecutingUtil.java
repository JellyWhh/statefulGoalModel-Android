/**
 * 
 */
package edu.fudan.agent.support;

import edu.fudan.se.goalmodel.RequestData;

/**
 * agent用来存储一个goalModelName#elementName所需要的各种数据
 * 
 * @author whh
 * 
 */
public class TaskExecutingUtil {

	private String abstractServiceName;
	private String taskDescription;	//任务的描述，如果这个任务
	private RequestData needRequestData;
	private RequestData retRequestData;

	public TaskExecutingUtil(String abstractServiceName, String taskDescription,
			RequestData needRequestData,RequestData retRequestData) {
		this.abstractServiceName = abstractServiceName;
		this.taskDescription = taskDescription;
		this.needRequestData = needRequestData;
		this.retRequestData = retRequestData;
	}

	public String getAbstractServiceName() {
		return abstractServiceName;
	}

	public void setAbstractServiceName(String abstractServiceName) {
		this.abstractServiceName = abstractServiceName;
	}

	public String getTaskDescription() {
		return taskDescription;
	}

	public void setTaskDescription(String taskDescription) {
		this.taskDescription = taskDescription;
	}

	public RequestData getNeedRequestData() {
		return needRequestData;
	}

	public void setNeedRequestData(RequestData needRequestData) {
		this.needRequestData = needRequestData;
	}

	public RequestData getRetRequestData() {
		return retRequestData;
	}

	public void setRetRequestData(RequestData retRequestData) {
		this.retRequestData = retRequestData;
	}

	
}
