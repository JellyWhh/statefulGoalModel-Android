/**
 * 
 */
package edu.fudan.se.userMes;

/**
 * 让用户确认某些事情的task
 * @author whh
 *
 */
public class UserConfirmTask extends UserTask {

	/**
	 * @param time
	 * @param fromAgentName
	 * @param goalModelName
	 * @param elementName
	 */
	public UserConfirmTask(String time, String fromAgentName,
			String goalModelName, String elementName) {
		super(time, fromAgentName, goalModelName, elementName);
	}

}
