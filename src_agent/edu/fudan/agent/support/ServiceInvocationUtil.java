/**
 * 
 */
package edu.fudan.agent.support;

import java.util.ArrayList;

import edu.fudan.se.goalmachine.message.SGMMessage;

/**
 * @author whh
 * 
 */
public class ServiceInvocationUtil {

	private String goalModelName;
	private String elementName;
	/**
	 * 调用服务的次数，就是agent为goalModel-element尝试了几次服务调用了
	 */
	private int invocationTimes;

	private ArrayList<String> alreadyTriedServiceNameList;

	private SGMMessage sgmMessage; // 调用服务时element
									// machine发来的msg信息，里面有隐含的content部分，所以要记录下来

	public ServiceInvocationUtil(String goalModelName, String elementName,
			SGMMessage sgmMessage) {
		this.goalModelName = goalModelName;
		this.elementName = elementName;
		this.sgmMessage = sgmMessage;
		this.alreadyTriedServiceNameList = new ArrayList<>();
	}

	public String getGoalModelName() {
		return goalModelName;
	}

	public String getElementName() {
		return elementName;
	}

	public int getInvocationTimes() {
		return invocationTimes;
	}

	public void setInvocationTimes(int invocationTimes) {
		this.invocationTimes = invocationTimes;
	}

	public ArrayList<String> getAlreadyTriedServiceNameList() {
		return alreadyTriedServiceNameList;
	}

	public SGMMessage getSgmMessage() {
		return sgmMessage;
	}

}
