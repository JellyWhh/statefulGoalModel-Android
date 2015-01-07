/**
 * 
 */
package edu.fudan.se.agent;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import edu.fudan.se.goalmachine.message.MesBody_Mes2Manager;
import edu.fudan.se.goalmachine.message.MesHeader_Mes2Manger;
import edu.fudan.se.goalmachine.message.SGMMessage;
import edu.fudan.se.goalmodel.GoalModelManager;
import edu.fudan.se.userMes.UserMessage;
import edu.fudan.se.userMes.UserTask;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
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
	private static final long serialVersionUID = -540261740171554489L;

	private GoalModelManager goalModelManager;

	private ArrayList<UserTask> userTaskList;
	private ArrayList<UserMessage> userMessageList;

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
			if (args[3] instanceof ArrayList<?>) {
				userMessageList = (ArrayList<UserMessage>) args[3];
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

		// 循环接收并处理来自外部agent的消息
		addBehaviour(new HandleMesFromExternalAgent());

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
	public void sendMesToManager(SGMMessage msg) {
		this.addBehaviour(new SendMesToManager(this, msg));
	}

	@Override
	public void handleMesFromManager(SGMMessage msg) {
		this.addBehaviour(new HandleMesFromManager(this, msg));
	}

	@Override
	public void sendMesToExternalAgent(SGMMessage msg) {
		this.addBehaviour(new SendMesToExternalAgent(this, msg));
	}

	/**
	 * 发送消息给本地manager
	 * 
	 * @author whh
	 * 
	 */
	private class SendMesToManager extends OneShotBehaviour {

		private static final long serialVersionUID = 4474626789362069528L;
		private SGMMessage msg;
		private Agent a;

		public SendMesToManager(Agent a, SGMMessage msg) {
			super(a);
			this.msg = msg;
		}

		@Override
		public void action() {
			Log.i("MY_LOG", "Send mes to manager...");
			msg.getSender().setAgentName(a.getName());
			goalModelManager.getMsgPool().offer(msg);
		}

	}

	/**
	 * 处理来自本地manager的消息
	 * 
	 * @author whh
	 * 
	 */
	private class HandleMesFromManager extends OneShotBehaviour {

		private static final long serialVersionUID = -628259960649979121L;
		private SGMMessage msg;

		public HandleMesFromManager(Agent a, SGMMessage msg) {
			super(a);
			this.msg = msg;
		}

		@Override
		public void action() {
			Log.i("MY_LOG", "Handle mes from manager...");

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
				broadcast_nt.putExtra("Content",
						"You have received a new task.");
				context.sendBroadcast(broadcast_nt);
				break;

			case NoDelegatedAchieved:
			case NoDelegatedFailed:

				SimpleDateFormat df = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");
				String mesTime = df.format(new Date());
				UserMessage userMessage = new UserMessage(mesTime,
						msg.getDescription());
				userMessageList.add(userMessage);

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

	/**
	 * 发送消息给外部agent
	 * 
	 * @author whh
	 * 
	 */
	private class SendMesToExternalAgent extends OneShotBehaviour {

		private static final long serialVersionUID = 226868601699462162L;
		private SGMMessage msg;
		private Agent a;

		public SendMesToExternalAgent(Agent a, SGMMessage msg) {
			super(a);
			this.msg = msg;
		}

		@Override
		public void action() {
			try {
				String targetAgent = msg.getReceiver().getAgentName(); // 获得委托对象的agent名字

				msg.getSender().setAgentName(a.getName()); // 设置发送方agent名字
				msg.setHeader(MesHeader_Mes2Manger.EXTERNAL_AGENT_MESSAGE);

				// 根据manager发来的消息body部分重新设置发出去的消息的body部分，只有DelegateOut需要重新设置
				// 委托出去后，对方agent把消息转发给对方manager，其实也就是start对方的goal model
				if (msg.getBody().equals(MesBody_Mes2Manager.DelegateOut)) {
					msg.setBody(MesBody_Mes2Manager.StartGM);
				}
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         
				// 序列化，然后在agent之间转发
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ObjectOutputStream oos = new ObjectOutputStream(baos);
				oos.writeObject(msg);
				oos.flush();
				byte[] sendBuf = baos.toByteArray();
				String content = new String(sendBuf); // 序列化msg之后转化成string准别用ACL发送

				Log.i("MY_LOG", "Send mes to external agent...");
				ACLMessage aclmsg = new ACLMessage(ACLMessage.INFORM);
				aclmsg.addReceiver(new AID(targetAgent, AID.ISLOCALNAME));
				aclmsg.setContent(content);
				send(aclmsg);
			} catch (Exception e) {
				Log.i("MY_LOG",
						"Send mes to external agent error! " + e.toString());
			}

		}

	}

	/**
	 * 处理来自外部agent的消息
	 * 
	 * @author whh
	 * 
	 */
	private class HandleMesFromExternalAgent extends CyclicBehaviour {

		private static final long serialVersionUID = -4159835298981404030L;

		@Override
		public void action() {
			ACLMessage msg = receive();
			try {
				if (msg != null) {
					Log.i("MY_LOG", "Handle mes from external agent...");
					String content = msg.getContent();
					ByteArrayInputStream bais = new ByteArrayInputStream(
							content.getBytes());
					ObjectInputStream ois = new ObjectInputStream(bais);
					SGMMessage inner_msg = (SGMMessage) ois.readObject(); // 反序列化获得msg

					if (inner_msg.getHeader().equals(
							MesHeader_Mes2Manger.EXTERNAL_AGENT_MESSAGE)) {

						Intent broadcast_nda = new Intent();
						broadcast_nda.setAction("jade.task.NOTIFICATION");
						broadcast_nda
								.putExtra("Content",
										"You have received a new mes from external agent!");
						context.sendBroadcast(broadcast_nda);

						goalModelManager.getMsgPool().offer(inner_msg);
					}

				}
			} catch (Exception e) {
				Log.i("MY_LOG_receive acl", e.toString());
			}
		}

	}

}
