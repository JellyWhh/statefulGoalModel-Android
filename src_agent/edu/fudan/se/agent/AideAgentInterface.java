package edu.fudan.se.agent;

import edu.fudan.se.goalmachine.message.SGMMessage;

public interface AideAgentInterface {

	public void sendMesToManager(SGMMessage msg);

	public void handleMesFromManager(SGMMessage msg);

	public void sendMesToExternalAgent(SGMMessage msg);

}
