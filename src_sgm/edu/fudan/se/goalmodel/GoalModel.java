/**
 * 
 */
package edu.fudan.se.goalmodel;

import java.io.Serializable;
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
public class GoalModel implements Serializable {

	private static final long serialVersionUID = 1L;

	private String name; // goal model的名字
	private String description; // goal model的描述
	private String state = "INITIAL"; // 整个goal
										// model的state，取值有INITIAL,STARTED,STOPED,SUSPENDED,RESUMED

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

//	/**
//	 * start这个goal model里面的所有element machines
//	 */
//	public void start() {
//		Log.logDebug("goal model:" + this.getName(), "start()", "init.");
//		if (this.elementMachines != null && this.elementMachines.size() != 0) {
//			for (ElementMachine elementMachine : this.elementMachines) {
//				Thread thread = new Thread(elementMachine);
//				thread.start();
//			}
//			// 然后给root goal发送激活消息
//			SGMMessage msg = new SGMMessage("TOROOT", "UI",
//					this.rootGoal.getName(), "ACTIVATE");
//			if (this.rootGoal.getMsgPool().offer(msg)) {
//				Log.logMessage(msg, true);
//				Log.logDebug(
//						"goal model:" + this.getName(),
//						"start()",
//						"UI thread send a ACTIVATE msg to "
//								+ this.rootGoal.getName() + " succeed!");
//			} else {
//				Log.logMessage(msg, false);
//				Log.logError(
//						"goal model:" + this.getName(),
//						"start()",
//						"UI thread send a ACTIVATE msg to "
//								+ this.rootGoal.getName() + " error!");
//			}
//		} else {
//			Log.logError("goal model:" + this.getName(), "start()",
//					"elementMachines is null or its size is 0!");
//		}
//		this.state = "STARTED";
//	}

//	/**
//	 * stop这个goal model，只需要给这个goal model里面的root goal发送STOP消息即可
//	 */
//	public void stop() {
//		Log.logDebug("goal model:" + this.getName(), "stop()", "init.");
//		if (this.rootGoal != null) {
//			SGMMessage msg = new SGMMessage("TOROOT", "UI",
//					this.rootGoal.getName(), "STOP");
//			if (this.rootGoal.getMsgPool().offer(msg)) {
//				Log.logMessage(msg, true);
//				Log.logDebug(
//						"goal model:" + this.getName(),
//						"stop()",
//						"UI thread send a STOP msg to "
//								+ this.rootGoal.getName() + " succeed!");
//			} else {
//				Log.logMessage(msg, false);
//				Log.logError(
//						"goal model:" + this.getName(),
//						"stop()",
//						"UI thread send a STOP msg to "
//								+ this.rootGoal.getName() + " error!");
//			}
//
//		} else {
//			Log.logError("goal model:" + this.getName(), "stop()",
//					"rootGoal is null!");
//		}
//		this.state = "STOPED";
//	}

	/**
	 * suspend这个goal model，只需要给这个goal model里面的root goal发送SUSPEND消息即可
	 */
	public void suspend() {
		Log.logDebug("goal model:" + this.getName(), "suspend()", "init.");
		// TODO
		this.state = "SUSPENDED";
	}

	/**
	 * resume这个goal model，只需要给这个goal model里面的root goal发送RESUME消息即可
	 */
	public void resume() {
		Log.logDebug("goal model:" + this.getName(), "resume()", "init.");
		// TODO
		this.state = "RESUMED";
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

	/**
	 * 重新把所有ElementMachine的状态设置为initial
	 */
	public void reset() {
		Log.logDebug("goal model:" + this.getName(), "reset()", "init.");
		if (this.elementMachines != null && this.elementMachines.size() != 0) {
			for (ElementMachine elementMachine : this.elementMachines) {
				elementMachine.resetMachine();
			}
		} else {
			Log.logError("goal model:" + this.getName(), "reset()",
					"elementMachines is null or its size is 0!");
		}
		this.state = "INITIAL";
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

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}
}
