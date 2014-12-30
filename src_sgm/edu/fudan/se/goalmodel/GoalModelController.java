/**
 * 
 */
package edu.fudan.se.goalmodel;

import edu.fudan.se.goalmachine.ElementMachine;
import edu.fudan.se.goalmachine.SGMMessage;
import edu.fudan.se.goalmachine.TaskMachine;
import edu.fudan.se.log.Log;

/**
 * 对一个<code>GoalModel</code>执行各种控制命令
 * 
 * @author whh
 * 
 */
public class GoalModelController {

	/**
	 * start这个goal model里面的所有element machines
	 * 
	 * @param goalModel
	 *            要start的goal model
	 */
	public void start(GoalModel goalModel) {
		Log.logDebug("GoalModelController:" + goalModel.getName(), "start()",
				"init.");
		if (goalModel.getElementMachines() != null
				&& goalModel.getElementMachines().size() != 0) {
			for (ElementMachine elementMachine : goalModel.getElementMachines()) {
				Thread thread = new Thread(elementMachine);
				thread.start();
			}
			// 然后给root goal发送激活消息
			sendMesToRoot(goalModel, "ACTIVATE");
		} else {
			Log.logError("GoalModelController:" + goalModel.getName(),
					"start()", "elementMachines is null or its size is 0!");
		}
	}

	/**
	 * stop这个goal model，只需要给这个goal model里面的root goal发送STOP消息即可
	 * 
	 * @param goalModel
	 *            要stop的goal model
	 */
	public void stop(GoalModel goalModel) {
		Log.logDebug("GoalModelController:" + goalModel.getName(), "stop()",
				"init.");
		sendMesToRoot(goalModel, "STOP");
	}

	/**
	 * suspend这个goal model，只需要给这个goal model里面的root goal发送SUSPEND消息即可
	 * 
	 * @param goalModel
	 *            要suspend的goal model
	 */
	public void suspend(GoalModel goalModel) {
		Log.logDebug("GoalModelController:" + goalModel.getName(), "suspend()",
				"init.");
		sendMesToRoot(goalModel, "SUSPEND");
	}

	/**
	 * resume这个goal model，只需要给这个goal model里面的root goal发送RESUME消息即可
	 * 
	 * @param goalModel
	 *            要resume的goal model
	 */
	public void resume(GoalModel goalModel) {
		Log.logDebug("GoalModelController:" + goalModel.getName(), "resume()",
				"init.");
		sendMesToRoot(goalModel, "RESUME");
	}

	/**
	 * 重新把所有ElementMachine的状态设置为initial
	 * 
	 * @param goalModel
	 *            要reset的goal model
	 */
	public void reset(GoalModel goalModel) {
		Log.logDebug("GoalModelController:" + goalModel.getName(), "reset()",
				"init.");
		if (goalModel.getElementMachines() != null
				&& goalModel.getElementMachines().size() != 0) {
			for (ElementMachine elementMachine : goalModel.getElementMachines()) {
				elementMachine.resetMachine();
			}
		} else {
			Log.logError("GoalModelController:" + goalModel.getName(),
					"reset()", "elementMachines is null or its size is 0!");
		}
	}
	
	/**
	 * 给一个task machine发送END消息，这个是在用户完成了某个需要他参与的任务后，在UI上点击这个task后面的end按钮时触发的操作
	 * 
	 * @param taskMachine
	 *            用户完成的task
	 */
	public void endTaskMachine(TaskMachine taskMachine) {
		Log.logDebug("GoalModelController:" + taskMachine.getName(), "endTaskMachine()",
				"init.");
		SGMMessage msg = new SGMMessage("TOTASK", "UI", taskMachine.getName(),
				"END");
		if (taskMachine.getMsgPool().offer(msg)) {
			Log.logMessage(msg, true);
			Log.logDebug("GoalModelController:" + taskMachine.getName(), "endTaskMachine()",
					"UI thread send a END msg to " + taskMachine.getName()
							+ " succeed!");
		} else {
			Log.logMessage(msg, false);
			Log.logError("GoalModelController:" + taskMachine.getName(), "endTaskMachine()",
					"UI thread send a END msg to " + taskMachine.getName()
							+ " error!");
		}

	}
	

	/**
	 * 发送一条消息给goal model中的root goal
	 * 
	 * @param goalModel
	 *            消息接收方的goal model
	 * @param mes
	 *            要发送的消息
	 */
	private void sendMesToRoot(GoalModel goalModel, String mes) {
		Log.logDebug("GoalModelController:" + goalModel.getName(),
				"sendMesToRoot()", "init.");
		if (goalModel.getRootGoal() != null) {
			SGMMessage msg = new SGMMessage("TOROOT", "UI", goalModel
					.getRootGoal().getName(), mes);
			if (goalModel.getRootGoal().getMsgPool().offer(msg)) {
				Log.logMessage(msg, true);
				Log.logDebug("GoalModelController:" + goalModel.getName(),
						"sendMesToRoot()", "UI thread send a " + mes
								+ " msg to "
								+ goalModel.getRootGoal().getName()
								+ " succeed!");
			} else {
				Log.logMessage(msg, false);
				Log.logError("GoalModelController:" + goalModel.getName(),
						"sendMesToRoot()", "UI thread send a " + mes
								+ " msg to "
								+ goalModel.getRootGoal().getName() + " error!");
			}

		} else {
			Log.logError("GoalModelController:" + goalModel.getName(),
					"sendMesToRoot()", "rootGoal is null!");
		}
	}

}
