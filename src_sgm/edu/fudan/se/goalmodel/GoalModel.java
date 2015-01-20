/**
 * 
 */
package edu.fudan.se.goalmodel;

import java.util.ArrayList;
import java.util.Hashtable;

import edu.fudan.se.goalmachine.ElementMachine;
import edu.fudan.se.goalmachine.GoalMachine;
import edu.fudan.se.goalmachine.message.MesBody_Mes2Manager;

/**
 * 表示一个完整的goal model，用来把里面的<code>ElementMachine</code>组织起来
 * 
 * @author whh
 * 
 */
public class GoalModel {

	private String name; // goal model的名字
	private String description; // goal model的描述

	/**
	 * goal model里面所有的ElementMachine
	 */
	private ArrayList<ElementMachine> elementMachines;

	/**
	 * goal model的root goal，在初始化goal model的时候要设置，并且也要把它加到elementMachines中去。
	 */
	private GoalMachine rootGoal;

	private GoalModelManager goalModelManager;

	/**
	 * 对requestDate进行赋值时查询的表，manager在收到service回复时查询这个表 。key是element name
	 */
	private Hashtable<String, RequestData> assignmentHashtable;

	/**
	 * 参数表，manager在收到element请求服务时查询的表，如果有，说明element调用的服务需要传入参数，那么就把byte[]
	 * 字节流附加在信息中传递过去。key是element name,value是assignmentHashtable中的key
	 */
	private Hashtable<String, String> parameterHashtable;
	
	/**
	 * 将设备事件（定时器、点击按钮等）映射到goal machine的“外部事件”
	 */
	private Hashtable<MesBody_Mes2Manager, ExternalEvent> deviceEventMapToExternalEventTable;

	public GoalModel() {
		this.elementMachines = new ArrayList<>();
		this.assignmentHashtable = new Hashtable<>();
		this.parameterHashtable = new Hashtable<>();
		this.deviceEventMapToExternalEventTable = new Hashtable<>();
	}

	/**
	 * 构造方法
	 * 
	 * @param name
	 *            goal model的名字，唯一
	 */
	public GoalModel(String name) {
		this.name = name;
		this.elementMachines = new ArrayList<>();
		this.assignmentHashtable = new Hashtable<>();
		this.parameterHashtable = new Hashtable<>();
		this.deviceEventMapToExternalEventTable = new Hashtable<>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * 为goal model添加一个element machine
	 * 
	 * @param elementMachine
	 *            要添加的element machine
	 */
	public void addElementMachine(ElementMachine elementMachine) {
		this.elementMachines.add(elementMachine);
		elementMachine.setGoalModel(this);
	}

	public ArrayList<ElementMachine> getElementMachines() {
		return this.elementMachines;
	}

	public GoalMachine getRootGoal() {
		return rootGoal;
	}

	public void setRootGoal(GoalMachine rootGoal) {
		this.rootGoal = rootGoal;
	}

	public GoalModelManager getGoalModelManager() {
		return goalModelManager;
	}

	public void setGoalModelManager(GoalModelManager goalModelManager) {
		this.goalModelManager = goalModelManager;
	}

	public Hashtable<String, RequestData> getAssignmentHashtable() {
		return assignmentHashtable;
	}

	public Hashtable<String, String> getParameterHashtable() {
		return parameterHashtable;
	}

	public Hashtable<MesBody_Mes2Manager, ExternalEvent> getDeviceEventMapToExternalEventTable() {
		return deviceEventMapToExternalEventTable;
	}

}
