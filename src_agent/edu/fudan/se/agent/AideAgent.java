/**
 * 
 */
package edu.fudan.se.agent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.content.Context;
import android.content.Intent;
import edu.fudan.se.goalmachine.message.MesBody_Mes2Manager;
import edu.fudan.se.goalmachine.message.MesHeader_Mes2Manger;
import edu.fudan.se.goalmachine.message.SGMMessage;
import edu.fudan.se.goalmodel.GoalModel;
import edu.fudan.se.goalmodel.GoalModelManager;
import edu.fudan.se.log.Log;
import edu.fudan.se.userMes.UserDelegateOutTask;
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

		// 在黄页服务中注册所有的goal model服务
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		for (GoalModel gm : goalModelManager.getGoalModelList()) {
			ServiceDescription sd = new ServiceDescription();
			sd.setType("GOALMODEL");
			sd.setName(gm.getName());
			dfd.addServices(sd);
		}
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

	@Override
	public void obtainFriends(UserTask userTask) {
		this.addBehaviour(new ObtainFriends(this, userTask));
	}
	

	@Override
	public void handleMesFromService(SGMMessage msg) {
		this.addBehaviour(new HandleMesFromService(this, msg));
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

		public SendMesToManager(Agent a, SGMMessage msg) {
			super(a);
			this.msg = msg;
		}

		@Override
		public void action() {
			Log.logDebug("AideAgent", "SendMesToManager()", "init.");
			android.util.Log.i("MY_LOG", "Send mes to manager...");
			// msg.getSender().setAgentName(a.getName());
			if (goalModelManager.getMsgPool().offer(msg)) {
				Log.logMessage(msg, true);
			} else {
				Log.logMessage(msg, false);
			}
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
			Log.logDebug("AideAgent", "HandleMesFromManager()", "init.");
			android.util.Log.i("MY_LOG", "Handle mes from manager...");

			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			switch ((MesBody_Mes2Manager) msg.getBody()) {
			case RequestPersonIA:
				// 添加一个user task到全局变量的user task列表中，然后MessageFragment会自动刷新user
				// task的显示

				String taskTime = df.format(new Date());
				UserTask userTask = new UserTask(taskTime, msg.getSender()
						.getGoalModelName(), msg.getSender().getElementName(),
						false);
				userTask.setDescription(msg.getDescription());
				userTaskList.add(userTask);

				String description = "You need to do:\n";
				if (userTask.getDescription() == null
						|| userTask.getDescription().equals("")) {
					description += userTask.getElementName();
				} else {
					description += userTask.getDescription();
				}

				// 发送 弹窗广播，在MainActivity会监听这个广播然后弹出通知窗口
				Intent broadcast_nt = new Intent();
				broadcast_nt.setAction("jade.task.NOTIFICATION");
				broadcast_nt.putExtra("Content", description);
				context.sendBroadcast(broadcast_nt);
				break;

			case NoDelegatedAchieved:
			case NoDelegatedFailed:

				String mesTime = df.format(new Date());
				UserMessage userMessage = new UserMessage(mesTime,
						msg.getDescription());
				userMessageList.add(userMessage);

				Intent broadcast_nda = new Intent();
				broadcast_nda.setAction("jade.mes.NOTIFICATION");
				broadcast_nda.putExtra("Content", userMessage.getContent());
				context.sendBroadcast(broadcast_nda);
				break;

			case DelegateOut:
				String delegateOutTaskTime = df.format(new Date());
				UserDelegateOutTask userDelegateOutTask = new UserDelegateOutTask(
						delegateOutTaskTime,
						msg.getSender().getGoalModelName(), msg.getSender()
								.getElementName(), false);
				userTaskList.add(userDelegateOutTask);

				String description2 = "You need to choose a friend to help you complete the goal:\n";
				if (userDelegateOutTask.getDescription() == null
						|| userDelegateOutTask.getDescription().equals("")) {
					description2 += userDelegateOutTask.getElementName();
				} else {
					description2 += userDelegateOutTask.getDescription();
				}

				// 发送 弹窗广播，在MainActivity会监听这个广播然后弹出通知窗口
				Intent broadcast_ndt = new Intent();
				broadcast_ndt.setAction("jade.task.NOTIFICATION");
				broadcast_ndt.putExtra("Content", description2);
				context.sendBroadcast(broadcast_ndt);
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
			this.a = a;
			this.msg = msg;
		}

		@Override
		public void action() {
			Log.logDebug("AideAgent", "SendMesToExternalAgent()", "init.");

			String targetAgent = msg.getReceiver().getAgentName(); // 获得委托对象的agent名字

			msg.getSender().setAgentName(a.getLocalName()); // 设置发送方agent名字，只设置昵称就好
			msg.setHeader(MesHeader_Mes2Manger.EXTERNAL_AGENT_MESSAGE);

			// 根据manager发来的消息body部分重新设置发出去的消息的body部分，只有DelegateOut需要重新设置
			// 委托出去后，对方agent把消息转发给对方manager，其实也就是start对方的goal model
			if (msg.getBody().equals(MesBody_Mes2Manager.DelegateOut)) {
				msg.setBody(MesBody_Mes2Manager.StartGM);
			}

			String content = msg.toString();
			Log.logMessage(msg, true);

			android.util.Log.i("MY_LOG",
					"Send mes to external agent...content is: " + content);
			ACLMessage aclmsg = new ACLMessage(ACLMessage.INFORM);
			aclmsg.addReceiver(new AID(targetAgent, AID.ISLOCALNAME));
			aclmsg.setContent(content);
			send(aclmsg);

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
			if (msg != null) {
				Log.logDebug("AideAgent", "HandleMesFromExternalAgent()",
						"init.");
				android.util.Log.i("MY_LOG",
						"Handle mes from external agent...");
				String content = msg.getContent();

				String message[] = content.split("-");
				SGMMessage inner_msg = new SGMMessage(
						MesHeader_Mes2Manger.getMesHeader(message[0]),
						message[1], message[2], message[3], message[4],
						message[5], message[6],
						MesBody_Mes2Manager.getMesBody(message[7]));
				inner_msg.setDescription(message[8]);

				if (inner_msg.getHeader().equals(
						MesHeader_Mes2Manger.EXTERNAL_AGENT_MESSAGE)) {

					SimpleDateFormat df = new SimpleDateFormat(
							"yyyy-MM-dd HH:mm:ss");
					String mesTime = df.format(new Date());

					String userMesContent = "";
					switch ((MesBody_Mes2Manager) inner_msg.getBody()) {
					case DelegatedAchieved:
						userMesContent = inner_msg.getSender().getAgentName()
								+ " has completed the goal: "
								+ inner_msg.getReceiver().getElementName()
								+ "!";
						break;
					case DelegatedFailed:
						userMesContent = inner_msg.getSender().getAgentName()
								+ " didn't complete the goal: "
								+ inner_msg.getReceiver().getElementName()
								+ "!";
						break;

					case StartGM:
						userMesContent = inner_msg.getSender().getAgentName()
								+ " want you to help him/her complete the goal: "
								+ inner_msg.getReceiver().getElementName()
								+ "! GoalModel has started!";
						break;

					default:
						break;
					}

					UserMessage userMessage = new UserMessage(mesTime,
							userMesContent);
					userMessageList.add(userMessage);

					Intent broadcast_nda = new Intent();
					broadcast_nda.setAction("jade.mes.NOTIFICATION");
					broadcast_nda.putExtra("Content", userMessage.getContent());
					context.sendBroadcast(broadcast_nda);

					goalModelManager.getMsgPool().offer(inner_msg);
				}

			}
		}

	}

	/**
	 * 从agent platform上获取可委托对象
	 * 
	 * @author whh
	 * 
	 */
	private class ObtainFriends extends OneShotBehaviour {

		private static final long serialVersionUID = 5110063359758030270L;
		private Agent agent;
		private UserTask userTask;

		public ObtainFriends(Agent a, UserTask userTask) {
			super(a);
			this.agent = a;
			this.userTask = userTask;
		}

		@Override
		public void action() {
			android.util.Log.i(
					"MY_LOG",
					"Obtain friends...goal model name is: "
							+ userTask.getGoalModelName());

			ArrayList<String> friendsArrayList = new ArrayList<>();
			DFAgentDescription template = new DFAgentDescription();
			ServiceDescription serviceDescription = new ServiceDescription();
			serviceDescription.setType("GOALMODEL");
			serviceDescription.setName(userTask.getGoalModelName());
			template.addServices(serviceDescription);
			try {
				DFAgentDescription[] results = DFService
						.search(agent, template);
				for (int i = 0; i < results.length; i++) {
					String agentNickName = results[i].getName().getLocalName();
					// 跳过自己
					if ((!agentNickName.equals(this.getAgent().getLocalName()))
							&& agentNickName != null && agentNickName != "") {
						friendsArrayList.add(agentNickName);
					}
				}

			} catch (FIPAException e) {
				e.printStackTrace();
			}

			// 发送 UI更新广播，在TaskFragment会监听这个广播然后弹出通知窗口
			String[] friends = new String[friendsArrayList.size()];
			friends = friendsArrayList.toArray(friends);
			Intent broadcast_fs = new Intent();
			broadcast_fs.setAction("jade.delegate.FRIENDS");
			broadcast_fs.putExtra("Friends", friends);
			broadcast_fs.putExtra("GoalModelName", userTask.getGoalModelName());
			broadcast_fs.putExtra("ElementName", userTask.getElementName());
			context.sendBroadcast(broadcast_fs);

		}

	}
	
	private class HandleMesFromService extends OneShotBehaviour {

		private static final long serialVersionUID = 4286751790137940309L;
		private SGMMessage msg;
		private Agent a;

		public HandleMesFromService(Agent a, SGMMessage msg) {
			super(a);
			this.a = a;
			this.msg = msg;
		}

		@Override
		public void action() {
			Log.logDebug("AideAgent", "HandleMesFromService()", "init.");
			if (msg.getBody().equals(MesBody_Mes2Manager.ServiceResult)) {
				
				SimpleDateFormat df = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");
				String mesTime = df.format(new Date());
				UserMessage userMessage = new UserMessage(mesTime,
						msg.getDescription());
				userMessageList.add(userMessage);

				Intent broadcast_nda = new Intent();
				broadcast_nda.setAction("jade.mes.NOTIFICATION");
				broadcast_nda.putExtra("Content", userMessage.getContent());
				context.sendBroadcast(broadcast_nda);
			}
		}
		
	}


}
