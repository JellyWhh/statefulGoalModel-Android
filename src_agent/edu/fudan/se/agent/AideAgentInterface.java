package edu.fudan.se.agent;

import edu.fudan.se.goalmachine.message.SGMMessage;
import edu.fudan.se.userMes.UserTask;

public interface AideAgentInterface {

	public void sendMesToManager(SGMMessage msg);

	public void handleMesFromManager(SGMMessage msg);

	public void sendMesToExternalAgent(SGMMessage msg);
	
	public void obtainFriends(UserTask userTask);
	
	public void handleMesFromService(SGMMessage msg);
	
	public void sendLocationToServerAgent(String userLocation);

}
