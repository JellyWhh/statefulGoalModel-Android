package edu.fudan.se.goalmodel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import edu.fudan.se.goalmachine.ElementMachine;
import edu.fudan.se.goalmachine.SGMMessage;
import edu.fudan.se.goalmachine.TaskMachine;
import edu.fudan.se.log.Log;

public class GoalModelManager implements Runnable {

	private BlockingQueue<SGMMessage> msgPool; // 消息池

	private List<GoalModel> goalModelList; // 所有目标模型列表

	public GoalModelManager() {
		this.msgPool = new LinkedBlockingQueue<SGMMessage>();
		goalModelList = new ArrayList<GoalModel>();
	}

	public void addGoalModel(GoalModel gm) {
		this.goalModelList.add(gm);
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

		while (true) {
			// 处理消息
			SGMMessage msg = this.getMsgPool().peek();

			// 处理Agent发来的外部事件
			if (msg.getHeader().equals("EXTERNAL_EVENT"))
				handleExternalEvent(msg);

			// 处理Task发来的请求事件
			if (msg.getHeader().equals("TASK_REQUEST"))
				handleTaskRequest(msg);

			try {
				Thread.sleep(2 * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * 处理外部事件，也就是发送给GoalModel的各种命令消息
	 * 
	 * @param msg 要发送的消息内容
	 */
	private void handleExternalEvent(SGMMessage msg) {
		Log.logDebug("GoalModelManager", "handleExternalEvent()", "init");
		GoalModel targetGoalModel = null;
		TaskMachine tm = null;
		String receiver = msg.getReceiver();
		String targetTaskName = receiver.split("@")[0];
		String targetGoalModelName = receiver.split("@")[1];

		for (GoalModel gm : goalModelList) {
			if (gm.getName().equals(targetGoalModelName)) {
				targetGoalModel = gm;
				for (ElementMachine em : gm.getElementMachines()) {
					if (em.getName().equals(targetTaskName))
						tm = (TaskMachine) em;
				}
				break;
			}
		}
		if (targetGoalModel != null) {
			switch (msg.getBody()) {
			case "START":
				start(targetGoalModel);
				break;
			case "STOP":
				stop(targetGoalModel);
				break;
			case "SUSPEND":
				suspend(targetGoalModel);
				break;
			case "RESUME":
				resume(targetGoalModel);
				break;
			case "RESET":
				reset(targetGoalModel);
				break;
			default:
				endTaskMachine(tm, msg.getBody());
			}
		} else {
			Log.logError("GoalModelManager:" + targetGoalModelName,
					"treat EXTERNAL_EVENT", "goal model is null!");
		}
	}

	private void handleTaskRequest(SGMMessage msg) {
		Log.logDebug("GoalModelManager", "handleTaskRequest()", "init");
	}

	/**
	 * start这个goal model里面的所有element machines
	 * 
	 * @param goalModel
	 *            要start的goal model
	 */
	private void start(GoalModel goalModel) {
		Log.logDebug("GoalModelManager:" + goalModel.getName(), "start()",
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
			Log.logError("GoalModelManager:" + goalModel.getName(), "start()",
					"elementMachines is null or its size is 0!");
		}
	}

	/**
	 * stop这个goal model，只需要给这个goal model里面的root goal发送STOP消息即可
	 * 
	 * @param goalModel
	 *            要stop的goal model
	 */
	private void stop(GoalModel goalModel) {
		Log.logDebug("GoalModelManager:" + goalModel.getName(), "stop()",
				"init.");
		sendMesToRoot(goalModel, "STOP");
	}

	/**
	 * suspend这个goal model，只需要给这个goal model里面的root goal发送SUSPEND消息即可
	 * 
	 * @param goalModel
	 *            要suspend的goal model
	 */
	private void suspend(GoalModel goalModel) {
		Log.logDebug("GoalModelManager:" + goalModel.getName(), "suspend()",
				"init.");
		sendMesToRoot(goalModel, "SUSPEND");
	}

	/**
	 * resume这个goal model，只需要给这个goal model里面的root goal发送RESUME消息即可
	 * 
	 * @param goalModel
	 *            要resume的goal model
	 */
	private void resume(GoalModel goalModel) {
		Log.logDebug("GoalModelManager:" + goalModel.getName(), "resume()",
				"init.");
		sendMesToRoot(goalModel, "RESUME");
	}

	/**
	 * 重新把所有ElementMachine的状态设置为initial
	 * 
	 * @param goalModel
	 *            要reset的goal model
	 */
	private void reset(GoalModel goalModel) {
		Log.logDebug("GoalModelManager:" + goalModel.getName(), "reset()",
				"init.");
		if (goalModel.getElementMachines() != null
				&& goalModel.getElementMachines().size() != 0) {
			for (ElementMachine elementMachine : goalModel.getElementMachines()) {
				elementMachine.resetMachine();
			}
		} else {
			Log.logError("GoalModelManager:" + goalModel.getName(), "reset()",
					"elementMachines is null or its size is 0!");
		}
	}

	/**
	 * 给一个task machine发送END消息，这个是在用户完成了某个需要他参与的任务后，在UI上点击这个task后面的end按钮时触发的操作
	 * 
	 * @param taskMachine
	 *            用户完成的task
	 * @param mes
	 *            发送的消息内容，END为完成了，QUIT为没有完成
	 */
	private void endTaskMachine(TaskMachine taskMachine, String mes) {
		Log.logDebug("GoalModelManager:" + taskMachine.getName(),
				"endTaskMachine()", "init.");
		SGMMessage msg = new SGMMessage("TOTASK", "UI", taskMachine.getName(),
				mes);
		if (taskMachine.getMsgPool().offer(msg)) {
			Log.logMessage(msg, true);
			Log.logDebug("GoalModelManager:" + taskMachine.getName(),
					"endTaskMachine()", "UI thread send a " + mes + " msg to "
							+ taskMachine.getName() + " succeed!");
		} else {
			Log.logMessage(msg, false);
			Log.logError("GoalModelManager:" + taskMachine.getName(),
					"endTaskMachine()", "UI thread send a " + mes + " msg to "
							+ taskMachine.getName() + " error!");
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
		Log.logDebug("GoalModelManager:" + goalModel.getName(),
				"sendMesToRoot()", "init.");
		if (goalModel.getRootGoal() != null) {
			SGMMessage msg = new SGMMessage("TOROOT", "UI", goalModel
					.getRootGoal().getName(), mes);
			if (goalModel.getRootGoal().getMsgPool().offer(msg)) {
				Log.logMessage(msg, true);
				Log.logDebug("GoalModelManager:" + goalModel.getName(),
						"sendMesToRoot()", "UI thread send a " + mes
								+ " msg to "
								+ goalModel.getRootGoal().getName()
								+ " succeed!");
			} else {
				Log.logMessage(msg, false);
				Log.logError("GoalModelManager:" + goalModel.getName(),
						"sendMesToRoot()", "UI thread send a " + mes
								+ " msg to "
								+ goalModel.getRootGoal().getName() + " error!");
			}

		} else {
			Log.logError("GoalModelManager:" + goalModel.getName(),
					"sendMesToRoot()", "rootGoal is null!");
		}
	}

	public BlockingQueue<SGMMessage> getMsgPool() {
		return msgPool;
	}

	public void setMsgPool(BlockingQueue<SGMMessage> msgPool) {
		this.msgPool = msgPool;
	}

}
