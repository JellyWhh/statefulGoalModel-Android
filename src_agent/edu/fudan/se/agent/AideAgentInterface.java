package edu.fudan.se.agent;

import edu.fudan.se.goalmachine.message.SGMMessage;

public interface AideAgentInterface {

	public void sendExternalEvent(SGMMessage msg);
	
	public void handleUserServiceRequest(SGMMessage msg);
	
	public void sendDelegateServiceRequest(SGMMessage msg);
	
	public void sendDelegatedSericeResult(String targetAgent, String receiver, String sender, String body);
	
	public void sendLocalServiceRequest(String serviceDescription, String sender);
}
