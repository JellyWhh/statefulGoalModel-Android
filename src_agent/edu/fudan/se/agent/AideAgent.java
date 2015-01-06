/**
 * 
 */
package edu.fudan.se.agent;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import edu.fudan.se.goalmachine.message.MesBody_Mes2Manager;
import edu.fudan.se.goalmachine.message.SGMMessage;
import edu.fudan.se.goalmodel.GoalModelManager;
import edu.fudan.se.userMes.UserTask;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

/**
 * @author qwy
 * 
 */
public class AideAgent extends Agent implements AideAgentInterface {

	private static final long serialVersionUID = 1L;

	private GoalModelManager goalModelManager;

	private ArrayList<UserTask> userTaskList;

	private Context context;

	@Override
	protected void setup() {
		// TODO Auto-generated method stub
		super.setup();
		Object[] args = getArguments();
		if (args != null && args.length > 0) {
			if (args[0] instanceof Context) {
				context = (Context) args[0];
			}
			if (args[1] instanceof GoalModelManager) {
				goalModelManager = (GoalModelManager) args[1];
			}
			if (args[2] instanceof ArrayList<?>) {
				userTaskList = (ArrayList<UserTask>) args[2];
			}
		}

		// goalModelController = new GoalModelController();

		registerO2AInterface(AideAgentInterface.class, this);

		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setName(getLocalName() + "--teacher");
		sd.setType("teacher");
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		} catch (FIPAException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	protected void takeDown() {
		try {
			DFService.deregister(this);
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}
	}

	@Override
	public void sendExternalEvent(SGMMessage msg) {
		this.addBehaviour(new ExternalEventSender(this, msg));
	}

	@Override
	public void handleUserServiceRequest(SGMMessage msg) {
		this.addBehaviour(new UserServiceRequestHandler(this, msg));

	}

	@Override
	public void sendDelegateServiceRequest(SGMMessage msg) {
		this.addBehaviour(new DelegateServiceSender(this, msg));
	}

	@Override
	public void sendLocalServiceRequest(String serviceDescription, String sender) {
		// TODO Auto-generated method stub

	}

	@Override
	public void sendDelegatedSericeResult(String targetAgent, String receiver,
			String sender, String body) {
		// TODO Auto-generated method stub

	}

	private class ExternalEventSender extends OneShotBehaviour {
		/**
		 * 
		 */
		private static final long serialVersionUID = -2331623840990344209L;
		private SGMMessage msg;
		private Agent a;

		private ExternalEventSender(Agent a, SGMMessage msg) {
			super(a);
			this.a = a;
			this.msg = msg;
		}

		@Override
		public void action() {
			Log.i("MY_LOG", "Send externa event...");
			msg.getSender().setAgentName(a.getName());
			goalModelManager.getMsgPool().offer(msg);
		}

	}

	private class DelegateServiceSender extends OneShotBehaviour {
		/**
		 * 
		 */
		private static final long serialVersionUID = -6490156755551520310L;
		private SGMMessage msg;
		private Agent a;

		private DelegateServiceSender(Agent a, SGMMessage msg) {
			super(a);
			this.msg = msg;
		}

		@Override
		public void action() {
			try {
				String targetAgent = msg.getReceiver().getAgentName(); // 获得委托对象的agent名字

				msg.getSender().setAgentName(a.getName()); // 设置发送方agent名字

				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ObjectOutputStream oos = new ObjectOutputStream(baos);
				oos.writeObject(msg);
				oos.flush();
				byte[] sendBuf = baos.toByteArray();
				String content = new String(sendBuf); // 序列化msg之后转化成string准别用ACL发送

				Log.i("MY_LOG_DelegateServiceSender",
						"Send delegate request...");
				ACLMessage aclmsg = new ACLMessage(ACLMessage.INFORM);
				aclmsg.addReceiver(new AID(targetAgent, AID.ISLOCALNAME));
				aclmsg.setContent(content);
				send(aclmsg);
			} catch (Exception e) {
				Log.i("MY_LOG_DelegateServiceSender", e.toString());
			}

		}
	}

	/**
	 * 收到manager发来的需要用户参与的消息后，添加一个userTask以展示在UI上，同时弹出一个通知栏来通知用户
	 * 
	 * @author whh
	 * 
	 */
	private class UserServiceRequestHandler extends OneShotBehaviour {

		private static final long serialVersionUID = 3080351293978938974L;
		private SGMMessage msg;

		// private Agent a;

		private UserServiceRequestHandler(Agent a, SGMMessage msg) {
			super(a);
			this.msg = msg;
		}

		@Override
		public void action() {
			Log.i("MY_LOG", "Handle user service request...");

			switch ((MesBody_Mes2Manager) msg.getBody()) {
			case RequestPersonIA:
				// 添加一个user task到全局变量的user task列表中，然后MessageFragment会自动刷新user
				// task的显示
				UserTask userTask = new UserTask(msg.getSender()
						.getGoalModelName(), msg.getSender().getElementName(),
						false);
				userTask.setDescription(msg.getDescription());
				userTaskList.add(userTask);

				// 发送 弹窗广播，在MainActivity会监听这个广播然后弹出通知窗口
				Intent broadcast_nt = new Intent();
				broadcast_nt.setAction("jade.task.NOTIFICATION");
				broadcast_nt.putExtra("Content", "You have received a new task.");
				context.sendBroadcast(broadcast_nt);
				break;
				
			case DelegatedAchieved:
				//TODO 告诉委托方agent任务完成
				break;
				
			case DelegatedFailed:
				//TODO 告诉委托方agent任务失败
				break;
				
			case NoDelegatedAchieved:
			case NoDelegatedFailed:
				Intent broadcast_nda = new Intent();
				broadcast_nda.setAction("jade.task.NOTIFICATION");
				broadcast_nda.putExtra("Content", msg.getDescription());
				context.sendBroadcast(broadcast_nda);
				break;

			default:
				break;
			}

		}

	}

}
