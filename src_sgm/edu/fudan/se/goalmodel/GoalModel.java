/**
 * 
 */
package edu.fudan.se.goalmodel;

import java.util.ArrayList;

import edu.fudan.se.goalmachine.ElementMachine;
import edu.fudan.se.goalmachine.SGMMessage;
import edu.fudan.se.goalmachine.TaskMachine;
import edu.fudan.se.log.Log;

/**
 * 表示一个完成的goal model，用来把里面的<code>ElementMachine</code>组织起来
 * 
 * @author whh
 * 
 */
public class GoalModel{


	private String name; // goal model的名字
	private String description; // goal model的描述

	private ArrayList<ElementMachine> elementMachines; // goal
														// model里面所有的ElementMachine

	private ElementMachine rootGoal; // goal model的root goal，在初始化goal
										// model的时候要设置，并且也要把它加到elementMachines中去。

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


	/**
	 * 给一个task machine发送END消息，这个是在用户完成了某个需要他参与的任务后，在UI上点击这个task后面的end按钮时触发的操作
	 * 
	 * @param taskMachine
	 *            用户完成的task
	 */
	public void endTaskMachine(TaskMachine taskMachine) {
		Log.logDebug("goal model:" + this.getName(), "endTaskMachine()",
				"init.");
		SGMMessage msg = new SGMMessage("TOTASK", "UI", taskMachine.getName(),
				"END");
		if (taskMachine.getMsgPool().offer(msg)) {
			Log.logMessage(msg, true);
			Log.logDebug("goal model:" + this.getName(), "endTaskMachine()",
					"UI thread send a END msg to " + taskMachine.getName()
							+ " succeed!");
		} else {
			Log.logMessage(msg, false);
			Log.logError("goal model:" + this.getName(), "endTaskMachine()",
					"UI thread send a END msg to " + taskMachine.getName()
							+ " error!");
		}

	}


	private void sortElementMachines() {

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
	}

	public ArrayList<ElementMachine> getElementMachines() {
		return this.elementMachines;
	}

	public ElementMachine getRootGoal() {
		return rootGoal;
	}

	public void setRootGoal(ElementMachine rootGoal) {
		this.rootGoal = rootGoal;
	}
	
}
