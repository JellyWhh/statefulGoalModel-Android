/**
 * 
 */
package edu.fudan.se.agent;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import edu.fudan.se.goalmachine.message.SGMMessage;
import edu.fudan.se.goalmodel.GoalModelManager;
import edu.fudan.se.pool.Message;
import edu.fudan.se.pool.Pool;
import edu.fudan.se.userMes.UserTask;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

/**
 * @author zjh
 * 
 */
public class AideAgent extends Agent implements AideAgentInterface {

	private static final long serialVersionUID = 1L;

	// private GoalModelController goalModelController;

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

		// add different behaviour
		addBehaviour(new TickerBehaviour(this, 1000) {

			@Override
			protected void onTick() {
				Message msg = Pool.getGOutMessage();// 取出本地Agent希望我发出去的消息。
				try {
					if (msg != null) {
						msg.setFromAID(getAID());// Note that: this statement
													// must be put ahead of
													// statement
													// "setContentObject"
						ACLMessage aclMsg = new ACLMessage(ACLMessage.INFORM);
						aclMsg.setConversationId("delegate");

						System.out.println("本机aid:" + getAID());
						if (msg.getToAid() != null) {
							aclMsg.addReceiver(msg.getToAid());
						} else if (msg.getToRole() != null) {
							DFAgentDescription tmp = new DFAgentDescription();
							ServiceDescription sd = new ServiceDescription();
							sd.setType(msg.getToRole());
							tmp.addServices(sd);

							DFAgentDescription[] result = DFService.search(
									myAgent, tmp);
							if (result.length > 0) {
								aclMsg.addReceiver(result[0].getName());// 依据钱博的描述，我现在是找到了很多的Role，但是默认发送第一个。
								msg.setToAid(result[0].getName());

								aclMsg.setContentObject(msg);// 这句话要和send语句紧紧贴在一起，必须在发送前一刻序列化。否则对msg信息的操作将得不到及时序列化。
								send(aclMsg);
								System.out.println("I am fasong ing mubiao"
										+ result[0].getName());
							}
						}
					}

				} catch (Exception exp) {
				}

			}
		});

		addBehaviour(new CyclicBehaviour() {// 这个行为主要是對外部發來的消息进行处理。
			@Override
			public void action() {
				System.out.println("I am keep 4");
				MessageTemplate mt = MessageTemplate.and(
						MessageTemplate.MatchPerformative(ACLMessage.INFORM),
						MessageTemplate.MatchConversationId("delegate"));
				ACLMessage aclMsg = receive(mt);
				if (aclMsg != null) {
					try {
						Message msg = (Message) aclMsg.getContentObject();
						System.out.println("just" + msg);
						Pool.setDInMessage(msg);// 外部Agent委托给我的消息
					} catch (UnreadableException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else
					block();

			}
		});

		addBehaviour(new TickerBehaviour(this, 1000) {
			@Override
			protected void onTick() {
				Message msg = Pool.getDOutMessage();
				if (msg != null)
					System.out.println("msg nuo null  + " + msg);
				if (msg != null) {
					ACLMessage aclMsg = new ACLMessage(ACLMessage.CONFIRM);
					aclMsg.setConversationId("confirm");
					aclMsg.addReceiver(msg.getFromAID());
					try {
						aclMsg.setContentObject(msg);
						send(aclMsg);
					} catch (Exception exp) {
					}

				}
			}
		});

		addBehaviour(new CyclicBehaviour() {
			@Override
			public void action() {
				// TODO Auto-generated method stub
				System.out.println("I am keep 3");
				MessageTemplate mt = MessageTemplate.and(
						MessageTemplate.MatchConversationId("confirm"),
						MessageTemplate.MatchPerformative(ACLMessage.CONFIRM));
				ACLMessage aclMsg = receive(mt);
				if (aclMsg != null) {
					try {
						Message msg = (Message) aclMsg.getContentObject();
						System.out.println("我收到委托出去的消息的反馈了:" + msg);
						Pool.setGInMessage(msg);
					} catch (UnreadableException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else
					block();

			}
		});

		// 接受委托请求启动相应的goal model
		addBehaviour(new CyclicBehaviour() {

			/**
			 * 
			 */
			private static final long serialVersionUID = -2613731756345395235L;

			@Override
			public void action() {
				// TODO Auto-generated method stub
				ACLMessage msg = receive();
				try {
					if (msg != null) {
						Log.i("MY_LOG_receive acl", "Message received!");
						String content = msg.getContent();
						ByteArrayInputStream bais = new ByteArrayInputStream(
								content.getBytes());
						ObjectInputStream ois = new ObjectInputStream(bais);
						SGMMessage inner_msg = (SGMMessage) ois.readObject(); // 反序列化获得msg

						inner_msg.setHeader("EXTERNAL_EVENT"); // 将委托请求在本地改成外部事件

						goalModelManager.getMsgPool().offer(inner_msg);
					}
				} catch (Exception e) {
					Log.i("MY_LOG_receive acl", e.toString());
				}
			}

		});

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
			// 添加一个user task到全局变量的user task列表中，然后MessageFragment会自动刷新user
			// task的显示
			UserTask userTask = new UserTask(
					msg.getSender().getGoalModelName(), msg.getSender()
							.getElementName(), false);
			userTask.setDescription(msg.getDescription());
			userTaskList.add(userTask);

			// 发送 弹窗广播，在MainActivity会监听这个广播然后弹出通知窗口
			Intent broadcast = new Intent();
			broadcast.setAction("jade.task.NOTIFICATION");
			context.sendBroadcast(broadcast);
		}

	}

}
