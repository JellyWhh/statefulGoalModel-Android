package edu.fudan.se.agent;

import edu.fudan.agent.support.ACLMC_DelegateTask;
import edu.fudan.se.goalmachine.message.SGMMessage;

public interface AideAgentInterface {

	public void sendMesToManager(SGMMessage msg);

	public void handleMesFromManager(SGMMessage msg);

	public void sendMesToExternalAgent(ACLMC_DelegateTask aclmc_DelegateTask);
	
//	public void obtainFriends(UserTask userTask);
	
	public void handleMesFromService(SGMMessage msg);
	
	public void sendLocationToServerAgent(String userLocation);
	
//	public void registerGoalModelService(GoalModel goalModel);

}
