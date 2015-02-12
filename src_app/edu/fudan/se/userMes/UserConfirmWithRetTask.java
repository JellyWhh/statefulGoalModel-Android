/**
 * 
 */
package edu.fudan.se.userMes;

import edu.fudan.se.goalmodel.RequestData;

/**
 * @author whh
 *
 */
public class UserConfirmWithRetTask  extends UserTask {
	
	private RequestData needRequestData;

	public UserConfirmWithRetTask(String time, String fromAgentName,
			String goalModelName, String elementName) {
		super(time, fromAgentName, goalModelName, elementName);
	}

	public RequestData getNeedRequestData() {
		return needRequestData;
	}

	public void setNeedRequestData(RequestData needRequestData) {
		this.needRequestData = needRequestData;
	}


}
