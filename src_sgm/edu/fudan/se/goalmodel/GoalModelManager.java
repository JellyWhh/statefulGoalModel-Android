package edu.fudan.se.goalmodel;

import jade.core.MicroRuntime;
import jade.wrapper.ControllerException;
import jade.wrapper.StaleProxyException;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.baidu.location.f;

import edu.fudan.se.agent.AideAgentInterface;
import edu.fudan.se.goalmachine.ElementMachine;
import edu.fudan.se.goalmachine.GoalMachine;
import edu.fudan.se.goalmachine.message.MesBody_Mes2Manager;
import edu.fudan.se.goalmachine.message.MesBody_Mes2Machine;
import edu.fudan.se.goalmachine.message.MesHeader_Mes2Manger;
import edu.fudan.se.goalmachine.message.SGMMessage;
import edu.fudan.se.goalmachine.message.SGMMessage.Messager;
import edu.fudan.se.goalmachine.TaskMachine;
import edu.fudan.se.log.Log;

public class GoalModelManager implements Runnable {

	private BlockingQueue<SGMMessage> msgPool; // 消息池

	/**
	 * 所有目标模型列表，key是goal model name
	 */
	private Hashtable<String, GoalModel> goalModelList;
	private String agentNickname;

	public GoalModelManager() {
		this.msgPool = new LinkedBlockingQueue<SGMMessage>();
		this.goalModelList = new Hashtable<>();
	}

	@Override
	public void run() {

		while (true) {
			// 处理消息
			SGMMessage msg = this.getMsgPool().peek(); // peek是拿出来看看，但是没有从消息队列中remove

			if (msg != null) {

				msg = this.getMsgPool().poll(); // poll是remove

				switch ((MesHeader_Mes2Manger) msg.getHeader()) {

				case LOCAL_AGENT_MESSAGE: // 本地agent发来的消息，也就是通过UI操作发给agent然后agent又转发的消息
					handleLocalAgentMessage(msg);
					break;

				case EXTERNAL_AGENT_MESSAGE: // 外部agent发来的消息，也是就外部agent发来消息然后本地agent转发的消息
					handleExternalAgentMessage(msg);
					break;

				case ELEMENT_MESSAGE: // 本地element machine发来的消息
					handleElementMessage(msg);
					break;

				default:
					break;
				}

			}

			try {
				Thread.sleep(2 * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * 本地agent发来的消息，也就是通过UI操作发给agent然后agent又转发的消息
	 * 
	 * @param msg
	 *            消息内容
	 */
	private void handleLocalAgentMessage(SGMMessage msg) {
		Log.logDebug("GoalModelManager", "handleLocalAgentMessage()", "init");

		Messager receiver = msg.getReceiver();
		String targetTaskName = receiver.getElementName();
		String targetGoalModelName = receiver.getGoalModelName();

		GoalModel targetGoalModel = goalModelList.get(targetGoalModelName);
		ElementMachine targetElementMachine = getEMFromGoalModelByName(
				targetGoalModel, targetTaskName);

		if (targetGoalModel != null) {
			switch ((MesBody_Mes2Manager) msg.getBody()) {
			case StartGM:
				start(targetGoalModel, msg);
				break;
			case StopGM:
				stop(targetGoalModel, msg);
				break;
			case SuspendGM:
				suspend(targetGoalModel, msg);
				break;
			case ResumeGM:
				resume(targetGoalModel, msg);
				break;
			case ResetGM:
				reset(targetGoalModel);
				break;
			case EndTE:
			case QuitTE:
				endTaskMachine((TaskMachine) targetElementMachine, msg);
				break;
			case ServiceExecutingDone:
				// 看返回的信息中是否需要给某些数据赋值
				if (msg.getContent() != null) {
					targetGoalModel.getAssignmentHashtable()
							.get(targetTaskName)
							.setContent(msg.getContent().getContent());
				}

				endTaskMachine((TaskMachine) targetElementMachine, msg);
				break;
			case ServiceExecutingFailed:
				endTaskMachine((TaskMachine) targetElementMachine, msg);
				break;
			case QuitGM:
				endGoalMachine((GoalMachine) targetElementMachine, msg);
				break;
			default:
			}
		} else {
			Log.logError("GoalModelManager:" + targetGoalModelName,
					"treat EXTERNAL_EVENT", "goal model is null!");
		}
	}

	/**
	 * 外部agent发来的消息，也是就外部agent发来消息然后本地agent转发的消息
	 * 
	 * @param msg
	 *            消息内容
	 */
	private void handleExternalAgentMessage(SGMMessage msg) {
		Log.logDebug("GoalModelManager", "handleExternalAgentMessage()", "init");
		if (msg != null) {

			String agentFrom = msg.getSender().getAgentName();
			String delegateGoalModelFrom = msg.getSender().getGoalModelName();

			Messager receiver = msg.getReceiver();
			String targetGoalName = receiver.getElementName();
			String targetGoalModelName = receiver.getGoalModelName();

			GoalModel targetGoalModel = goalModelList.get(targetGoalModelName);
			GoalMachine targerGoalMachine = (GoalMachine) getEMFromGoalModelByName(
					targetGoalModel, targetGoalName);
			switch ((MesBody_Mes2Manager) msg.getBody()) {
			case DelegatedAchieved:
				// 如果有携带的数据，那么对本goal model的数据进行赋值，也就是对DELEGATERETURN条目的数据进行赋值
				if (msg.getContent() != null) {
					targetGoalModel.getAssignmentHashtable().get("DELEGATERETURN")
							.setContent(msg.getContent().getContent());
				}
				endGoalMachine(targerGoalMachine, msg);
				break;
			case DelegatedFailed:
				endGoalMachine(targerGoalMachine, msg);
				break;

			case StartGM:
				targetGoalModel.getRootGoal().setDelegated(true);
				targetGoalModel.getRootGoal().setAgentFrom(agentFrom);
				targetGoalModel.getRootGoal().setDelegateGoalModelFrom(
						delegateGoalModelFrom);

				// 如果有携带的数据，那么对本goal model的数据进行赋值，也就是对DELEGATEIN条目的数据进行赋值
				if (msg.getContent() != null) {
					targetGoalModel.getAssignmentHashtable().get("DELEGATEIN")
							.setContent(msg.getContent().getContent());
				}

				start(targetGoalModel, msg);
				break;

			default:
				break;
			}
		}

	}

	/**
	 * 处理委托、endTask、本地服务调用，是element machine通过maneger发送给agent的消息
	 * 
	 * @param msg
	 */
	private void handleElementMessage(SGMMessage msg) {
		Log.logDebug("GoalModelManager", "handleElementMessage()", "init");

		if (msg != null) {
			switch ((MesBody_Mes2Manager) msg.getBody()) {
			// 都是把消息直接转发给agent，由agent根据消息body部分进行处理
			case RequestPersonIA: // 需要用户反馈是否完成task的消息
			case NoDelegatedAchieved: // 告诉主人自己完成了任务
			case NoDelegatedFailed: // 告诉主人自己没有完成任务
				getAideAgentInterface().handleMesFromManager(msg);
				break;

			case DelegatedAchieved: // 告诉委托方agent完成了任务

				// 查看这个任务完成的时候是否需要把数据也传递回去
				String fromElementName = goalModelList
						.get(msg.getSender().getGoalModelName())
						.getParameterHashtable().get("DELEGATEOUT");
				if (fromElementName != null) {
					RequestData requestData = goalModelList
							.get(msg.getSender().getGoalModelName())
							.getAssignmentHashtable().get(fromElementName);
					msg.setContent(requestData);
				}

				getAideAgentInterface().sendMesToExternalAgent(msg);
				break;
			case DelegatedFailed: // 告诉委托方agent没有完成任务
				getAideAgentInterface().sendMesToExternalAgent(msg);
				break;

			case DelegateOut: // 告诉agent这个是要委托出去的任务
				// 委托出去的时候也可能需要把数据传递出去，要看这个委托出去的任务是否需要数据
			case RequestService: // 需要调用服务，要查询参数表
				// 先从数据传输路径表中看这个element是否需要别的element赋值过的数据
				String requestElementName = goalModelList
						.get(msg.getSender().getGoalModelName())
						.getParameterHashtable()
						.get(msg.getSender().getElementName());
				if (requestElementName != null) {
					// 如果需要，就从数据赋值表里拿出这个它需要的数据
					RequestData requestData = goalModelList
							.get(msg.getSender().getGoalModelName())
							.getAssignmentHashtable().get(requestElementName);
					msg.setContent(requestData);
				}

				getAideAgentInterface().handleMesFromManager(msg);
				break;

			default:
				break;
			}
		}
	}

	/**
	 * start这个goal model里面的所有element machines
	 * 
	 * @param goalModel
	 *            要start的goal model
	 */

	private void start(GoalModel goalModel, SGMMessage msg) {
		Log.logDebug("GoalModelManager:" + goalModel.getName(), "start()",
				"init.");
		if (goalModel.getElementMachines() != null
				&& goalModel.getElementMachines().size() != 0) {
			for (ElementMachine elementMachine : goalModel.getElementMachines()) {
				Thread thread = new Thread(elementMachine);
				thread.start();
			}
			// 然后给root goal发送激活消息
			msg.setBody(MesBody_Mes2Machine.ACTIVATE);
			sendMesToRoot(goalModel, msg);
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
	private void stop(GoalModel goalModel, SGMMessage msg) {
		Log.logDebug("GoalModelManager:" + goalModel.getName(), "stop()",
				"init.");
		msg.setBody(MesBody_Mes2Machine.STOP);
		sendMesToRoot(goalModel, msg);
	}

	/**
	 * suspend这个goal model，只需要给这个goal model里面的root goal发送SUSPEND消息即可
	 * 
	 * @param goalModel
	 *            要suspend的goal model
	 */
	private void suspend(GoalModel goalModel, SGMMessage msg) {
		Log.logDebug("GoalModelManager:" + goalModel.getName(), "suspend()",
				"init.");
		msg.setBody(MesBody_Mes2Machine.SUSPEND);
		sendMesToRoot(goalModel, msg);
	}

	/**
	 * resume这个goal model，只需要给这个goal model里面的root goal发送RESUME消息即可
	 * 
	 * @param goalModel
	 *            要resume的goal model
	 */
	private void resume(GoalModel goalModel, SGMMessage msg) {
		Log.logDebug("GoalModelManager:" + goalModel.getName(), "resume()",
				"init.");
		msg.setBody(MesBody_Mes2Machine.RESUME);
		sendMesToRoot(goalModel, msg);
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
	 * 给一个task
	 * machine发送END或者QUIT消息，这个是在用户完成了某个需要他参与的任务后，在UI上点击这个task后面的end按钮时触发的操作
	 * 
	 * @param taskMachine
	 *            用户完成的task
	 * @param mes
	 *            发送的消息内容，END为完成了，QUIT为没有完成
	 */
	private void endTaskMachine(TaskMachine taskMachine, SGMMessage msg) {
		Log.logDebug("GoalModelManager:" + taskMachine.getName(),
				"endTaskMachine()", "init.");
		// SGMMessage msg = new SGMMessage("TOTASK", "UI",
		// taskMachine.getName(),
		// mes);

		if (msg.getBody().equals(MesBody_Mes2Manager.EndTE)
				|| msg.getBody().equals(
						MesBody_Mes2Manager.ServiceExecutingDone)) {
			msg.setBody(MesBody_Mes2Machine.TASK_DONE);
		} else if (msg.getBody().equals(MesBody_Mes2Manager.QuitTE)
				|| msg.getBody().equals(
						MesBody_Mes2Manager.ServiceExecutingFailed)) {
			msg.setBody(MesBody_Mes2Machine.TASK_FAILED);
		}
		if (taskMachine.getMsgPool().offer(msg)) {
			Log.logMessage(msg, true);
			Log.logDebug("GoalModelManager:" + taskMachine.getName(),
					"endTaskMachine()", "UI thread send a " + msg.getBody()
							+ " msg to " + taskMachine.getName() + " succeed!");
		} else {
			Log.logMessage(msg, false);
			Log.logError("GoalModelManager:" + taskMachine.getName(),
					"endTaskMachine()", "UI thread send a " + msg.getBody()
							+ " msg to " + taskMachine.getName() + " error!");
		}

	}

	/**
	 * 给一个goal machine发送是否完成的消息，这个goal
	 * machine是委托出去做的，现在是收到了外部agent发回来的反馈，根据反馈结果来设置goal machine是否完成
	 * 
	 * @param goalMachine
	 *            之前委托出去的goal machine
	 * @param msg
	 *            消息
	 */
	private void endGoalMachine(GoalMachine goalMachine, SGMMessage msg) {
		Log.logDebug("GoalModelManager:" + goalMachine.getName(),
				"endGoalMachine()", "init.");
		if (msg.getBody().equals(MesBody_Mes2Manager.DelegatedAchieved)) {
			msg.setBody(MesBody_Mes2Machine.ACHIEVEDDONE);
		} else if (msg.getBody().equals(MesBody_Mes2Manager.DelegatedFailed)
				|| msg.getBody().equals(MesBody_Mes2Manager.QuitGM)) {
			msg.setBody(MesBody_Mes2Machine.FAILED);
		}

		if (goalMachine.getMsgPool().offer(msg)) {
			Log.logMessage(msg, true);
			Log.logDebug("GoalModelManager:" + goalMachine.getName(),
					"endGoalMachine()",
					"External agent or UI send a " + msg.getBody() + " msg to "
							+ goalMachine.getName() + " succeed!");
		} else {
			Log.logMessage(msg, false);
			Log.logError("GoalModelManager:" + goalMachine.getName(),
					"endGoalMachine()",
					"External agent send a " + msg.getBody() + " msg to "
							+ goalMachine.getName() + " error!");
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
	private void sendMesToRoot(GoalModel goalModel, SGMMessage msg) {
		Log.logDebug("GoalModelManager:" + goalModel.getName(),
				"sendMesToRoot()", "init.");
		if (goalModel.getRootGoal() != null) {
			// SGMMessage msg = new SGMMessage("TOROOT", "UI", goalModel
			// .getRootGoal().getName(), mes);
			if (goalModel.getRootGoal().getMsgPool().offer(msg)) {
				Log.logMessage(msg, true);
				Log.logDebug("GoalModelManager:" + goalModel.getName(),
						"sendMesToRoot()", "UI thread send a " + msg.getBody()
								+ " msg to "
								+ goalModel.getRootGoal().getName()
								+ " succeed!");
			} else {
				Log.logMessage(msg, false);
				Log.logError("GoalModelManager:" + goalModel.getName(),
						"sendMesToRoot()", "UI thread send a " + msg.getBody()
								+ " msg to "
								+ goalModel.getRootGoal().getName() + " error!");
			}

		} else {
			Log.logError("GoalModelManager:" + goalModel.getName(),
					"sendMesToRoot()", "rootGoal is null!");
		}
	}

	/**
	 * 拿到agent的引用
	 * 
	 * @return agent
	 */
	private AideAgentInterface getAideAgentInterface() {
		AideAgentInterface aideAgentInterface = null; // agent interface
		try {
			aideAgentInterface = MicroRuntime.getAgent(agentNickname)
					.getO2AInterface(AideAgentInterface.class);
		} catch (StaleProxyException e) {
			e.printStackTrace();
		} catch (ControllerException e) {
			e.printStackTrace();
		}
		return aideAgentInterface;
	}

	/**
	 * 根据指定的element name，从goal model中取出对应的element machine
	 * 
	 * @param goalModel
	 *            goal model
	 * @param elementName
	 *            指定的element name
	 * @return 对应的element machine，如果没有返回null
	 */
	private ElementMachine getEMFromGoalModelByName(GoalModel goalModel,
			String elementName) {
		ElementMachine ret = null;
		for (ElementMachine em : goalModel.getElementMachines()) {
			if (em.getName().equals(elementName)) {
				ret = em;
			}
		}
		return ret;
	}

	public void addGoalModel(GoalModel gm) {
		this.goalModelList.put(gm.getName(), gm);
		gm.setGoalModelManager(this);
	}

	public Hashtable<String, GoalModel> getGoalModelList() {
		return goalModelList;
	}

	public BlockingQueue<SGMMessage> getMsgPool() {
		return msgPool;
	}

	public void setMsgPool(BlockingQueue<SGMMessage> msgPool) {
		this.msgPool = msgPool;
	}

	public String getAgentNickname() {
		return agentNickname;
	}

	public void setAgentNickname(String agentNickname) {
		this.agentNickname = agentNickname;
	}

}
