/**
 * 
 */
package edu.fudan.se.goalmachine;

import edu.fudan.se.goalmachine.message.MesBody_Mes2Machine;
import edu.fudan.se.goalmachine.message.MesBody_Mes2Manager;
import edu.fudan.se.goalmachine.message.MesHeader_Mes2Manger;
import edu.fudan.se.goalmachine.message.SGMMessage;
import edu.fudan.se.log.Log;

/**
 * 抽象类<br>
 * Task Machine，继承自<code>ElementMachine</code>
 * 
 * @author whh
 * 
 */
public abstract class TaskMachine extends ElementMachine {

	private boolean needPeopleInteraction; // 是否需要人的交互
	private String executingRequestedServiceName; // 执行这个task时具体需要调用的服务名称，如果需要人的交互，这个就为空了

	/**
	 * 构造方法
	 * 
	 * @param name
	 *            task machine名字
	 * @param parentGoal
	 *            父目标
	 */
	public TaskMachine(String name, ElementMachine parentGoal, int level,
			boolean needPeopleInteraction) {
		super(name, parentGoal, level);
		this.needPeopleInteraction = needPeopleInteraction;
	}

	/**
	 * activated状态中entry所做的action：在initialDo中已经尝试把自己状态转换为Activated了，进入这个状态后，
	 * 说明已经被激活了，于是向父目标发送ACTIVATEDDONE消息，然后进入activatedDo()方法中，等待父进程的START消息
	 */
	@Override
	public void activatedEntry() {
		Log.logDebug(this.getName(), "activatedEntry()", "init.");

		if (this.sendMessageToParent(MesBody_Mes2Machine.ACTIVATEDDONE)) {
			Log.logDebug(this.getName(), "activatedEntry()",
					"send ACTIVATEDDONE msg to "
							+ this.getParentGoal().getName() + " succeed!");
		} else {
			Log.logError(this.getName(), "activatedEntry()",
					"send ACTIVATEDDONE msg to "
							+ this.getParentGoal().getName() + " error!");
		}

	}

	/**
	 * activated状态中do所做的action：自身不是root goal，所以要一直等待父目标的START指令，收到后才可以发生状态转换<br>
	 * <code>GoalMachine中需要重写</code>
	 */
	@Override
	public void activateDo(SGMMessage msg) {
		Log.logDebug(this.getName(), "activateDo()", "init.");

		// SGMMessage msg = this.getMsgPool().poll(); // 每次拿出一条消息
		if (msg != null) {
			Log.logDebug(this.getName(), "activateDo()",
					"get a message from " + msg.getSender().toString()
							+ "; body is: " + msg.getBody());

			// 消息内容是START，表示父目标让当前目标开始状态转换
			if (msg.getBody().equals(MesBody_Mes2Machine.START)) {
				this.getMsgPool().poll();
				this.setCurrentState(this.transition(State.Activated,
						this.getPreCondition()));
			}

		}
	}

	/**
	 * executing状态中entry所做的action：给manager发送消息，消息内容是需要人的参与或者是调用服务
	 */
	public void executingEntry() {
		Log.logDebug(this.getName(), "executingEntry()", "init.");

		SGMMessage msgToManager = null;
		if (this.isNeedPeopleInteraction()) { // 需要人的参与
			// 发送消息给agent,让agent提醒用户需要他的参与
			msgToManager = new SGMMessage(MesHeader_Mes2Manger.ELEMENT_MESSAGE,
					null, this.getGoalModel().getName(), this.getName(), null,
					null, null, MesBody_Mes2Manager.RequestPersonIA);
			msgToManager.setDescription(this.getDescription());
		} else {// 不需要人的参与，而是需要调服务
			msgToManager = new SGMMessage(MesHeader_Mes2Manger.ELEMENT_MESSAGE,
					null, this.getGoalModel().getName(), this.getName(), null,
					null, null, MesBody_Mes2Manager.RequestService);
			// 将需要调用的服务名称附加在description里
			msgToManager
					.setDescription(this.getExecutingRequestedServiceName());
		}

		sendMesToManager(msgToManager);

	}

	/**
	 * executing状态中do所做的action：等待TASK_END的消息到达
	 */
	@Override
	public void executingDo(SGMMessage msg) {
		Log.logDebug(this.getName(), "executingDo()", "init.");

		if (msg != null) {
			Log.logDebug(this.getName(), "executingDo_waitingEnd()",
					"get a message from " + msg.getSender().toString()
							+ "; body is: " + msg.getBody());

			if (msg.getBody().equals(MesBody_Mes2Machine.TASK_DONE)) { // 收到外部UI的END消息
				this.getMsgPool().poll();
				this.setCurrentState(this.transition(State.Executing,
						this.getPostCondition()));
			} else if (msg.getBody().equals(MesBody_Mes2Machine.SUSPEND)) { // 收到父目标的SUSPEND消息
				this.getMsgPool().poll();
				this.setCurrentState(State.Suspended);
			} else if (msg.getBody().equals(MesBody_Mes2Machine.TASK_FAILED)) { // 用户没有完成这个任务，放弃了
				this.getMsgPool().poll();
				this.setCurrentState(State.Failed);
			}
		}

	}

	/**
	 * suspended状态中do所做的action：目标处于挂起状态时，只需要不断检查是否有RESUME到来即可，如果收到了，
	 * 把目标状态转换为executing状态
	 */
	@Override
	public void suspendedDo(SGMMessage msg) {
		Log.logDebug(this.getName(), "suspendedDo()", "init.");
		// SGMMessage msg = this.getMsgPool().poll(); // 每次拿出一条消息
		if (msg != null) {
			Log.logDebug(this.getName(), "suspendedDo()",
					"get a message from " + msg.getSender().toString()
							+ "; body is: " + msg.getBody());
			if (msg.getBody().equals(MesBody_Mes2Machine.RESUME)) {
				this.getMsgPool().poll();
				// 把自己状态设置为executing,同时resetSuspendEntry
				this.setCurrentState(State.Executing);
				resetSuspendEntry();
			}
		}
	}

	/**
	 * 让TaskMachine重写，用来初始化里面的一个变量
	 */
	public void resetTaskMachine() {
	}

	public boolean isNeedPeopleInteraction() {
		return needPeopleInteraction;
	}

	public void setNeedPeopleInteraction(boolean needPeopleInteraction) {
		this.needPeopleInteraction = needPeopleInteraction;
	}

	public String getExecutingRequestedServiceName() {
		return executingRequestedServiceName;
	}

	public void setExecutingRequestedServiceName(
			String executingRequestedServiceName) {
		this.executingRequestedServiceName = executingRequestedServiceName;
	}

}
