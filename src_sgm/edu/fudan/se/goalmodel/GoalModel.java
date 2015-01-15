/**
 * 
 */
package edu.fudan.se.goalmodel;

import java.util.ArrayList;

import edu.fudan.se.goalmachine.ElementMachine;
import edu.fudan.se.goalmachine.GoalMachine;

/**
 * 表示一个完整的goal model，用来把里面的<code>ElementMachine</code>组织起来
 * 
 * @author whh
 * 
 */
public class GoalModel {
	

	private String name; // goal model的名字
	private String description; // goal model的描述

	private ArrayList<ElementMachine> elementMachines; // goal
														// model里面所有的ElementMachine

	private GoalMachine rootGoal; // goal model的root goal，在初始化goal
										// model的时候要设置，并且也要把它加到elementMachines中去。
	private GoalModelManager goalModelManager;
	
	public GoalModel(){
		this.elementMachines = new ArrayList<>();
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

}
