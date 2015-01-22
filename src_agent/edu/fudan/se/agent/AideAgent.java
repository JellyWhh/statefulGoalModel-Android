/**
 * 
 */
package edu.fudan.se.agent;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import edu.fudan.agent.support.AideAgentSupport;
import edu.fudan.agent.support.ServiceInvocationUtil;
import edu.fudan.se.agent.data.RequestUserInformation;
import edu.fudan.se.agent.data.UserInformation;
import edu.fudan.se.goalmachine.message.MesBody_Mes2Manager;
import edu.fudan.se.goalmachine.message.MesHeader_Mes2Manger;
import edu.fudan.se.goalmachine.message.SGMMessage;
import edu.fudan.se.goalmodel.GoalModel;
import edu.fudan.se.goalmodel.GoalModelManager;
import edu.fudan.se.goalmodel.RequestData;
import edu.fudan.se.initial.SGMApplication;
import edu.fudan.se.log.Log;
import edu.fudan.se.userMes.UserDelegateInTask;
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
import jade.lang.acl.UnreadableException;

/**
 * @author qwy, whh
 * 
 */
public class AideAgent extends Agent implements AideAgentInterface {
	private static final long serialVersionUID = -540261740171554489L;

	private SGMApplication sgmApplication;
	private GoalModelManager goalModelManager;

	private ArrayList<UserTask> userTaskList;
	private ArrayList<UserMessage> userMessageList;

	private Context context;

	int canInvokeTimes = 2;// agent收到调用服务失败消息后重新查找替换服务的次数
	ArrayList<String> allIntentServiceNameArrayList = new ArrayList<>();
	ArrayList<ServiceInvocationUtil> serviceInvocationUtilList = new ArrayList<>();

	// 用来临时储存委托出去的时候goalModelName-ElementName对应的requestData
	Hashtable<String, RequestData> delegateOutRequestData = new Hashtable<>();

	@Override
	protected void setup() {
		super.setup();
		Object[] args = getArguments();
		if (args != null && args.length > 0) {
			if (args[0] instanceof Context) {
				context = (Context) args[0];
			}
			if (args[1] instanceof SGMApplication) {
				sgmApplication = (SGMApplication) args[1];
			}
			// if (args[1] instanceof GoalModelManager) {
			// goalModelManager = (GoalModelManager) args[1];
			// }
			// if (args[2] instanceof ArrayList<?>) {
			// userTaskList = (ArrayList<UserTask>) args[2];
			// }
			// if (args[3] instanceof ArrayList<?>) {
			// userMessageList = (ArrayList<UserMessage>) args[3];
			// }
			goalModelManager = sgmApplication.getGoalModelManager();
			userTaskList = sgmApplication.getUserTaskList();
			userMessageList = sgmApplication.getUserMessageList();
		}

		registerO2AInterface(AideAgentInterface.class, this);

		/* 初始化所有可能用到的intent service */
		AideAgentSupport
				.initAllIntentService(this.allIntentServiceNameArrayList);

		/* 在黄页服务中注册所有的goal model服务 */
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		for (GoalModel gm : goalModelManager.getGoalModelList().values()) {
			ServiceDescription sd = new ServiceDescription();
			sd.setType("GOALMODEL");
			sd.setName(gm.getName());
			dfd.addServices(sd);
		}

		try {
			DFService.register(this, dfd);
		} catch (FIPAException e) {
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
	public void handleMesFromService(SGMMessage msg) {
		this.addBehaviour(new HandleMesFromService(this, msg));
	}

	@Override
	public void sendLocationToServerAgent(String userLocation) {
		this.addBehaviour(new SendLocationToServerAgent(this, userLocation));
	}

	@Override
	public void registerGoalModelService(GoalModel goalModel) {
		this.addBehaviour(new RegisterGoalModelService(this, goalModel));
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
		private Agent agent;

		public HandleMesFromManager(Agent a, SGMMessage msg) {
			super(a);
			this.agent = a;
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

			case RequestService:
				String abstractServiceName = msg.getDescription();
				ArrayList<String> serviceInvocationChoice = AideAgentSupport
						.getServiceNameListBasedAbstractServiceName(
								abstractServiceName,
								allIntentServiceNameArrayList);
				// 第一次调用，直接调用可选服务中的第一个，并且记录服务调用信息
				ServiceInvocationUtil serviceInvocationUtil = new ServiceInvocationUtil(
						msg.getSender().getGoalModelName(), msg.getSender()
								.getElementName(), msg);
				serviceInvocationUtil.setInvocationTimes(1);// 第一次调用
				serviceInvocationUtil.getAlreadyTriedServiceNameList().add(
						serviceInvocationChoice.get(0));
				serviceInvocationUtilList.add(serviceInvocationUtil);

				// 开始服务调用
				Intent serviceIntent = new Intent(
						serviceInvocationChoice.get(0));
				Bundle bundle = new Bundle();
				bundle.putString("GOAL_MODEL_NAME", msg.getSender()
						.getGoalModelName());
				bundle.putString("ELEMENT_NAME", msg.getSender()
						.getElementName());

				if (msg.getContent() != null) {
					bundle.putSerializable("REQUEST_DATA_CONTENT",
							msg.getContent());
				}

				serviceIntent.putExtras(bundle);
				context.startService(serviceIntent);
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

				String goalModelName = msg.getSender().getGoalModelName();
				String elementName = msg.getSender().getElementName();

				if (msg.getContent() != null) {
					delegateOutRequestData.put(goalModelName + "#"
							+ elementName, msg.getContent());
				}

				ArrayList<String> friendsArrayList = new ArrayList<>();
				DFAgentDescription template = new DFAgentDescription();
				ServiceDescription serviceDescription = new ServiceDescription();
				serviceDescription.setType("GOALMODEL");
				serviceDescription.setName(elementName);
				template.addServices(serviceDescription);
				try {
					DFAgentDescription[] results = DFService.search(agent,
							template);
					for (int i = 0; i < results.length; i++) {
						String agentNickName = results[i].getName()
								.getLocalName();
						// 跳过自己
						if ((!agentNickName.equals(this.getAgent()
								.getLocalName()))
								&& agentNickName != null
								&& agentNickName != "") {
							friendsArrayList.add(agentNickName);
						}
					}

				} catch (FIPAException e) {
					e.printStackTrace();
				}

				// 向server agent发送获取位置信息的ACLMessage
				String content = goalModelName + "#" + elementName + "###";
				for (String frientName : friendsArrayList) {
					content += frientName + "---";
				}

				android.util.Log.i("MY_LOG",
						"Send OBTAINLOCATION aclMessage to server agent...content is: "
								+ content);

				ACLMessage aclmsg = new ACLMessage(ACLMessage.CFP);
				aclmsg.addReceiver(new AID("ServerAgent", AID.ISLOCALNAME));
				aclmsg.setContent(content);
				send(aclmsg);
				// 然后会一直等待server agent回复信息，在handleMesFromExternalAgent会对信息进行处理

				// String delegateOutTaskTime = df.format(new Date());
				// UserDelegateOutTask userDelegateOutTask = new
				// UserDelegateOutTask(
				// delegateOutTaskTime,
				// msg.getSender().getGoalModelName(), msg.getSender()
				// .getElementName(), false);
				// // 有需要在委托出去时顺便传输的数据
				// if (msg.getContent() != null) {
				// userDelegateOutTask.setRequestData(msg.getContent());
				// }
				// userTaskList.add(userDelegateOutTask);
				//
				// String description2 =
				// "You need to choose a friend to help you complete the goal:\n";
				// if (userDelegateOutTask.getDescription() == null
				// || userDelegateOutTask.getDescription().equals("")) {
				// description2 += userDelegateOutTask.getElementName();
				// } else {
				// description2 += userDelegateOutTask.getDescription();
				// }
				//
				// // 发送 弹窗广播，在MainActivity会监听这个广播然后弹出通知窗口
				// Intent broadcast_ndt = new Intent();
				// broadcast_ndt.setAction("jade.task.NOTIFICATION");
				// broadcast_ndt.putExtra("Content", description2);
				// context.sendBroadcast(broadcast_ndt);
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

			// String content = msg.toString();
			Log.logMessage(msg, true);

			android.util.Log.i(
					"MY_LOG",
					"Send mes to external agent...content is: "
							+ msg.toString());
			android.util.Log.i("MY_LOG",
					"Send mes to external agent...if send request data? "
							+ (msg.getContent() != null));

			ACLMessage aclmsg = new ACLMessage(ACLMessage.INFORM);
			aclmsg.addReceiver(new AID(targetAgent, AID.ISLOCALNAME));
			// aclmsg.setContent(content);
			try {
				aclmsg.setContentObject(msg);
			} catch (IOException e) {
				android.util.Log
						.i("MY_LOG",
								"Send mes to external agent...aclmsg.setContentObject(msg) error!!!");
				e.printStackTrace();
			}
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

				// 来自server agent的消息
				if (msg.getPerformative() == ACLMessage.PROPOSE) {

					try {
						RequestUserInformation requestUserInformation = (RequestUserInformation) msg
								.getContentObject();

						String goalModelName = requestUserInformation
								.getGoalModelName();
						String elementName = requestUserInformation
								.getElementName();

						ArrayList<UserInformation> userInformationList = requestUserInformation
								.getUserInformations();
						if (userInformationList == null
								|| userInformationList.isEmpty()) {// 没有可委托对象

							android.util.Log.i("MY_LOG", "no delegateDo user!");

							SGMMessage sgmMessage = new SGMMessage(
									MesHeader_Mes2Manger.LOCAL_AGENT_MESSAGE,
									null, null, null, null, goalModelName,
									elementName, MesBody_Mes2Manager.QuitGM);

							android.util.Log.i("MY_LOG",
									"Send mes to manager...");
							if (goalModelManager.getMsgPool().offer(sgmMessage)) {
								Log.logMessage(sgmMessage, true);
							} else {
								Log.logMessage(sgmMessage, false);
							}
						} else {
							String delegateTo = AideAgentSupport
									.getDelegateToBasedRanking(
											userInformationList, sgmApplication
													.getLocation(), this
													.getAgent().getLocalName());

							android.util.Log.i("MY_LOG", " delegateDo user is "
									+ delegateTo);

							// 发送消息给agent
							SGMMessage msgToExternalAgent = new SGMMessage(
									MesHeader_Mes2Manger.EXTERNAL_AGENT_MESSAGE,
									null, goalModelName, elementName,
									delegateTo, elementName, elementName,
									MesBody_Mes2Manager.DelegateOut);

							RequestData requestData = delegateOutRequestData
									.get(goalModelName + "#" + elementName);
							if (requestData != null) {
								msgToExternalAgent.setContent(requestData);
							}
							sendMesToExternalAgent(msgToExternalAgent);
						}

					} catch (UnreadableException e) {
						e.printStackTrace();
					}

					// String content = msg.getContent();
					//
					// android.util.Log.i("MY_LOG", "server agent reply: "
					// + content);
					//
					// String[] item = content.split("###");
					// String goalModelElementName = item[0];
					// ArrayList<String> friendsLocationArrayList = new
					// ArrayList<>();
					//
					// if (item.length > 1) { // 有friend location信息传过来
					// String[] userLocations = item[1].split("---");
					// for (int i = 0; i < userLocations.length; i++) {
					// // #号前面是昵称，后面是地址
					// String[] friendLocation = userLocations[i]
					// .split("#");
					// String item2 = friendLocation[0] + ":";
					// if (friendLocation[1].split("\n").length == 5) { //
					// 有网络定位结果，也就是具体物理地址
					// item2 += friendLocation[1].split("\n")[4]
					// .split(":")[1];
					// } else {
					// item2 += "UnKnown";
					// }
					//
					// friendsLocationArrayList.add(item2);
					// }
					// }
					// // 发送 UI更新广播，在TaskFragment会监听这个广播然后弹出通知窗口
					// String[] friends = new String[friendsLocationArrayList
					// .size()];
					// friends = friendsLocationArrayList.toArray(friends);
					// Intent broadcast_fs = new Intent();
					// broadcast_fs.setAction("jade.delegate.FRIENDS");
					// broadcast_fs.putExtra("Friends", friends);
					// broadcast_fs.putExtra("GoalModelName",
					// goalModelElementName.split("#")[0]);
					// broadcast_fs.putExtra("ElementName",
					// goalModelElementName.split("#")[1]);
					// context.sendBroadcast(broadcast_fs);
				}

				// 来自其他aide agent的ACLMessage.INFORM消息
				if (msg.getPerformative() == ACLMessage.INFORM) {

					SGMMessage inner_msg = null;

					try {
						inner_msg = (SGMMessage) msg.getContentObject();
					} catch (UnreadableException e) {
						android.util.Log
								.i("MY_LOG",
										"Handle mes from external agent...acl msg.getContentObject() error!");
						e.printStackTrace();
					}
					System.out
							.println("Handle mes from external agent:content is null?:"
									+ (inner_msg.getContent() == null));

					// String message[] = content.split("-");
					// SGMMessage inner_msg = new SGMMessage(
					// MesHeader_Mes2Manger.getMesHeader(message[0]),
					// message[1], message[2], message[3], message[4],
					// message[5], message[6],
					// MesBody_Mes2Manager.getMesBody(message[7]));
					// inner_msg.setDescription(message[8]);

					if (inner_msg != null
							&& inner_msg
									.getHeader()
									.equals(MesHeader_Mes2Manger.EXTERNAL_AGENT_MESSAGE)) {

						SimpleDateFormat df = new SimpleDateFormat(
								"yyyy-MM-dd HH:mm:ss");
						String mesTime = df.format(new Date());

						String userMesContent = "";
						switch ((MesBody_Mes2Manager) inner_msg.getBody()) {
						case DelegatedAchieved:
							userMesContent = inner_msg.getSender()
									.getAgentName()
									+ " has completed the goal: "
									+ inner_msg.getReceiver().getElementName()
									+ "!";
							goalModelManager.getMsgPool().offer(inner_msg);
							break;

						case DelegatedFailed:
							userMesContent = inner_msg.getSender()
									.getAgentName()
									+ " didn't complete the goal: "
									+ inner_msg.getReceiver().getElementName()
									+ "!";
							goalModelManager.getMsgPool().offer(inner_msg);
							break;

						case StartGM:
							userMesContent = inner_msg.getSender()
									.getAgentName()
									+ " want you to help him/her complete the goal: "
									+ inner_msg.getReceiver().getElementName()
									+ "! Please make a choice.";
							// TODO 添加一个userDelegateInTask
							UserDelegateInTask userDelegateInTask = new UserDelegateInTask(
									mesTime, inner_msg.getReceiver()
											.getElementName(), inner_msg
											.getReceiver().getElementName(),
									false);
							userDelegateInTask
									.setDescription(inner_msg.getSender()
											.getAgentName()
											+ "want you to help him/her complete the goal: "
											+ inner_msg.getReceiver()
													.getElementName());

							userDelegateInTask.setRelateSgmMessage(inner_msg);
							userTaskList.add(userDelegateInTask);
							break;

						default:
							break;
						}

						UserMessage userMessage = new UserMessage(mesTime,
								userMesContent);
						userMessageList.add(userMessage);

						Intent broadcast_nda = new Intent();
						broadcast_nda.setAction("jade.mes.NOTIFICATION");
						broadcast_nda.putExtra("Content",
								userMessage.getContent());
						context.sendBroadcast(broadcast_nda);

					}
				}

			}
		}

	}

	// /**
	// * 从agent platform上获取可委托对象
	// *
	// * @author whh
	// *
	// */
	// private class ObtainFriends extends OneShotBehaviour {
	//
	// private static final long serialVersionUID = 5110063359758030270L;
	// private Agent agent;
	// private UserTask userTask;
	//
	// public ObtainFriends(Agent a, UserTask userTask) {
	// super(a);
	// this.agent = a;
	// this.userTask = userTask;
	// }
	//
	// @Override
	// public void action() {
	// android.util.Log.i(
	// "MY_LOG",
	// "Obtain friends...goal model name is: "
	// + userTask.getElementName());
	//
	// ArrayList<String> friendsArrayList = new ArrayList<>();
	// DFAgentDescription template = new DFAgentDescription();
	// ServiceDescription serviceDescription = new ServiceDescription();
	// serviceDescription.setType("GOALMODEL");
	// serviceDescription.setName(userTask.getElementName());
	// template.addServices(serviceDescription);
	// try {
	// DFAgentDescription[] results = DFService
	// .search(agent, template);
	// for (int i = 0; i < results.length; i++) {
	// String agentNickName = results[i].getName().getLocalName();
	// // 跳过自己
	// if ((!agentNickName.equals(this.getAgent().getLocalName()))
	// && agentNickName != null && agentNickName != "") {
	// friendsArrayList.add(agentNickName);
	// }
	// }
	//
	// } catch (FIPAException e) {
	// e.printStackTrace();
	// }
	//
	// // 向server agent发送获取位置信息的ACLMessage
	// String content = userTask.getGoalModelName() + "#"
	// + userTask.getElementName() + "###";
	// for (String frientName : friendsArrayList) {
	// content += frientName + "---";
	// }
	//
	// android.util.Log.i("MY_LOG",
	// "Send OBTAINLOCATION aclMessage to server agent...content is: "
	// + content);
	//
	// ACLMessage aclmsg = new ACLMessage(ACLMessage.CFP);
	// aclmsg.addReceiver(new AID("ServerAgent", AID.ISLOCALNAME));
	// aclmsg.setContent(content);
	// send(aclmsg);
	// // 然后会一直等待server agent回复信息，在handleMesFromExternalAgent会对信息进行处理
	// }
	//
	// }

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
			// 服务调用成功，直接把msg发送给manager
			if (msg.getBody().equals(MesBody_Mes2Manager.ServiceExecutingDone)) {
				android.util.Log.i("MY_LOG", "Send mes to manager...");
				if (goalModelManager.getMsgPool().offer(msg)) {
					Log.logMessage(msg, true);
				} else {
					Log.logMessage(msg, false);
				}
			}
			// 服务调用失败
			else if (msg.getBody().equals(
					MesBody_Mes2Manager.ServiceExecutingFailed)) {
				String goalModelName = msg.getReceiver().getGoalModelName();
				String elementName = msg.getReceiver().getElementName();
				ServiceInvocationUtil serviceInvocationUtil = AideAgentSupport
						.getServiceInvocationUtil(goalModelName, elementName,
								serviceInvocationUtilList);
				int invocationTimes = serviceInvocationUtil
						.getInvocationTimes();

				if (invocationTimes < canInvokeTimes) { // 还可以再重新尝试调用
					SGMMessage originalSgmMessage = serviceInvocationUtil
							.getSgmMessage();
					ArrayList<String> serviceInvocationChoice = AideAgentSupport
							.getServiceNameListBasedAbstractServiceName(
									originalSgmMessage.getDescription(),
									allIntentServiceNameArrayList);
					// 把已经尝试过的服务名称移除
					serviceInvocationChoice.removeAll(serviceInvocationUtil
							.getAlreadyTriedServiceNameList());
					if (serviceInvocationChoice.isEmpty()) {// 移除之后没有可选的了
						android.util.Log.i("MY_LOG", "Send mes to manager...");
						if (goalModelManager.getMsgPool().offer(msg)) {
							Log.logMessage(msg, true);
						} else {
							Log.logMessage(msg, false);
						}
					} else {// 选择仍然可选的服务中的第一个

						serviceInvocationUtil
								.setInvocationTimes(invocationTimes++);
						serviceInvocationUtil.getAlreadyTriedServiceNameList()
								.add(serviceInvocationChoice.get(0));

						// 开始服务调用
						Intent serviceIntent = new Intent(
								serviceInvocationChoice.get(0));
						Bundle bundle = new Bundle();
						bundle.putString("GOAL_MODEL_NAME", originalSgmMessage
								.getSender().getGoalModelName());
						bundle.putString("ELEMENT_NAME", originalSgmMessage
								.getSender().getElementName());

						if (originalSgmMessage.getContent() != null) {
							bundle.putSerializable("REQUEST_DATA_CONTENT",
									originalSgmMessage.getContent());
						}

						serviceIntent.putExtras(bundle);
						context.startService(serviceIntent);
						android.util.Log.i("MY_LOG",
								"ServiceInvocation, goalModelName: "
										+ goalModelName + ", elementName: "
										+ elementName + ", serviceName: "
										+ serviceInvocationChoice.get(0)
										+ ", invocationTimes: "
										+ invocationTimes);
					}
				} else {// 已经超过重试次数，直接把msg发送给manager
					android.util.Log.i("MY_LOG", "Send mes to manager...");
					if (goalModelManager.getMsgPool().offer(msg)) {
						Log.logMessage(msg, true);
					} else {
						Log.logMessage(msg, false);
					}
				}
			}

		}

	}

	/**
	 * 发送位置信息给ServerAgent
	 * 
	 * @author whh
	 * 
	 */
	private class SendLocationToServerAgent extends OneShotBehaviour {

		private static final long serialVersionUID = -8674488872785177053L;
		private Agent a;
		private String userLocation;

		public SendLocationToServerAgent(Agent a, String userLocation) {
			super(a);
			this.a = a;
			this.userLocation = userLocation;
		}

		@Override
		public void action() {
			String content = a.getLocalName() + "---" + userLocation;

			android.util.Log.i("MY_LOG",
					"Send user location to server agent...content is: "
							+ content);

			ACLMessage aclmsg = new ACLMessage(ACLMessage.INFORM);
			aclmsg.addReceiver(new AID("ServerAgent", AID.ISLOCALNAME));
			aclmsg.setContent(content);
			send(aclmsg);
		}

	}

	/**
	 * 发送位置信息给ServerAgent
	 * 
	 * @author whh
	 * 
	 */
	private class RegisterGoalModelService extends OneShotBehaviour {

		private static final long serialVersionUID = -8674488872785177053L;
		private Agent a;
		private GoalModel goalModel;

		public RegisterGoalModelService(Agent a, GoalModel goalModel) {
			super(a);
			this.a = a;
			this.goalModel = goalModel;
		}

		@Override
		public void action() {

			// 在黄页服务中注册所有的goal model服务
			DFAgentDescription dfd = new DFAgentDescription();
			dfd.setName(getAID());
			ServiceDescription sd = new ServiceDescription();
			sd.setType("GOALMODEL");
			sd.setName(goalModel.getName());
			dfd.addServices(sd);

			try {
				// 这里是更新服务，而不是重新注册服务
				DFService.modify(a, dfd);
			} catch (FIPAException e) {
				e.printStackTrace();
			}
		}

	}

}
