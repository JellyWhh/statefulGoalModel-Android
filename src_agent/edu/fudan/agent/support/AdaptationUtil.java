/**
 * 
 */
package edu.fudan.agent.support;

import java.util.ArrayList;

/**
 * @author whh
 *
 */
public class AdaptationUtil {
	
	private String goalModelName;
	private String elementName;
	/**
	 * 调用服务的次数，就是agent为goalModel#element尝试了几次服务调用了
	 */
	private int triedTimes;

	private ArrayList<String> alreadyTriedList;
	
	public AdaptationUtil(String goalModelName, String elementName){
		this.goalModelName = goalModelName;
		this.elementName = elementName;
		this.alreadyTriedList = new ArrayList<>();
	}
	
	
	public String getGoalModelName() {
		return goalModelName;
	}

	public String getElementName() {
		return elementName;
	}

	public int getTriedTimes() {
		return triedTimes;
	}

	public void setTriedTimes(int triedTimes) {
		this.triedTimes = triedTimes;
	}

	public ArrayList<String> getAlreadyTriedList() {
		return alreadyTriedList;
	}
}
