/**
 * 
 */
package edu.fudan.se.agent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import edu.fudan.se.goalmodel.GoalModel;
import edu.fudan.se.goalmodel.GoalModelController;
import edu.fudan.se.pool.Message;
import edu.fudan.se.pool.Pool;
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
	
	private GoalModelController goalModelController;
	
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
			if(args[1] instanceof GoalModelController){
				goalModelController = (GoalModelController) args[1];
			}
		}
		
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

	}

	@Override
	protected void takeDown() {
		try {
			DFService.deregister(this);
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}
	}

	private class GSMStarter extends OneShotBehaviour{
		/**
		 * 
		 */
		private static final long serialVersionUID = 2126730704005002010L;
		private String goalName;
		
		private GSMStarter(Agent a, String goalModelName){
			super(a);
			goalName = goalModelName;
		}
		
		@Override
		public void action() {
			// TODO Auto-generated method stub
			Log.i("MY_LOG", "Start Goal Model...");
			goalModelController.startGoalModel(goalName);
		}
		
	}
	
	@Override
	public void startGoalModel(String goalModelName) {
		// TODO Auto-generated method stub
		this.addBehaviour(new GSMStarter(this, goalModelName));
	}

}
