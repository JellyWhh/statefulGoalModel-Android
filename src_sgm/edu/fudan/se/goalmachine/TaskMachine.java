/**
 * 
 */
package edu.fudan.se.goalmachine;

import edu.fudan.se.log.Log;

/**
 * 抽象类<br>
 * Task Machine，继承自<code>ElementMachine</code>
 * 
 * @author whh
 * 
 */
public abstract class TaskMachine extends ElementMachine {

	private static final long serialVersionUID = 4384425169172317259L;
	
	private boolean needPeopleInteraction;	//是否需要人的交互
	
	/**
	 * 构造方法
	 * 
	 * @param name
	 *            task machine名字
	 * @param parentGoal
	 *            父目标
	 */
	public TaskMachine(String name, ElementMachine parentGoal, int level,boolean needPeopleInteraction) {
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

		if (this.sendMessageToParent("ACTIVATEDDONE")) {
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
			Log.logDebug(this.getName(), "activateDo()", "get a message from "
					+ msg.getSender() + "; body is: " + msg.getBody());

			// 消息内容是START，表示父目标让当前目标开始状态转换
			if (msg.getBody().equals("START")) {
				this.getMsgPool().poll();
				this.setCurrentState(this.transition(State.Activated,
						this.getPreCondition()));
			}

		}
	}
	
	boolean isSendUIMesDone = false;	//标记是否给用户发送提醒完毕

	/**
	 * executing状态中do所做的action：这个需要根据具体的task有不同的具体执行行为，所以这个是抽象方法，在实例化时具体实现
	 */
	@Override
	public void executingDo(SGMMessage msg){
		
		if(this.isNeedPeopleInteraction()){	//需要人的参与
			//弹出窗口提示用户需要他的参与，然后进入等待end消息中
			if (isSendUIMesDone) {	//发送提示完毕
				executingDo_waitingEnd(msg);
			}else {
				//发送消息给agent,让agent提醒用户需要他的参与
				sendMesToAgent();
				isSendUIMesDone = true;
			}
		}else{	//不需要人的参与
			//执行完要做的事情后直接跳转
			executingDo_once();
			this.setCurrentState(this.transition(State.Executing,
					this.getPostCondition()));
		}
		
	}
	
	/**
	 * 弹出弹窗，提醒用户需要它的参与
	 */
	private void sendMesToAgent(){
		
	}
	
	/**
	 * executing状态中do所做的action：不需要人的参与，直接根据具体的task有不同的具体执行行为，抽象方法，在实例化时具体实现
	 */
	public abstract void executingDo_once();

	/**
	 * executing状态中do所做的action：等待外部进程（比如UI）发来的END消息，这个方法在executingDo()方法的具体实现中调用<br>
	 * 接到END消息后，尝试跳转到Achieved
	 */
	private void executingDo_waitingEnd(SGMMessage msg) {
		Log.logDebug(this.getName(), "executingDo_waitingEnd()", "init.");

		// SGMMessage msg = this.getMsgPool().poll(); // 拿出一条消息
		if (msg != null) {
			Log.logDebug(this.getName(), "executingDo_waitingEnd()",
					"get a message from " + msg.getSender() + "; body is: "
							+ msg.getBody());

			if (msg.getBody().equals("END")) { // 收到外部UI的END消息
				this.getMsgPool().poll();
				this.setCurrentState(this.transition(State.Executing,
						this.getPostCondition()));
			} else if (msg.getBody().equals("SUSPEND")) { // 收到父目标的SUSPEND消息
				this.getMsgPool().poll();
				this.setCurrentState(State.Suspended);
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
			Log.logDebug(this.getName(), "suspendedDo()", "get a message from "
					+ msg.getSender() + "; body is: " + msg.getBody());
			if (msg.getBody().equals("RESUME")) {
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
	public void resetTaskMachine(){
		isSendUIMesDone = false;	//标记是否给用户发送提醒完毕
	}

	public boolean isNeedPeopleInteraction() {
		return needPeopleInteraction;
	}

	public void setNeedPeopleInteraction(boolean needPeopleInteraction) {
		this.needPeopleInteraction = needPeopleInteraction;
	}

}
