package edu.fudan.se.agent;

import edu.fudan.se.goalmachine.SGMMessage;

public interface AideAgentInterface {

//	public void startGoalModel(GoalModel goalModel);
//
//	public void stopGoalModel(GoalModel goalModel);
//
//	public void suspendGoalModel(GoalModel goalModel);
//
//	public void resumeGoalModel(GoalModel goalModel);
//
//	public void resetGoalModel(GoalModel goalModel);
//
//	public void endTaskMachine(TaskMachine taskMachine,String mes);
	
	public void sendExternalEvent(SGMMessage msg);
	
	public void sendUserServiceRequest(String request, String sender);
	
	public void sendDelegateServiceRequest(SGMMessage msg);
	
	public void sendDelegatedSericeResult(String targetAgent, String receiver, String sender, String body);
	
	public void sendLocalServiceRequest(String serviceDescription, String sender);
}
