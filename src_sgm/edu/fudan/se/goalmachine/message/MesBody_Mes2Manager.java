package edu.fudan.se.goalmachine.message;

import java.io.Serializable;

import edu.fudan.se.goalmachine.message.SGMMessage.MesBody;

/**
 * agent与manager之间消息的Body部分
 * 
 * @author whh
 * 
 */
public class MesBody_Mes2Manager implements MesBody, Serializable {

	private static final long serialVersionUID = -2315912253031070781L;
	
	private String body;
	public MesBody_Mes2Manager(String body){
		this.body = body;
	}
	public String getBody() {
		return body;
	}
	
	public String toString(){
		return body;
	}
	
//	StartGM, StopGM, SuspendGM, ResumeGM, ResetGM,EndTE, QuitTE, 
//	RequestService, DelegateToPeople,NoDelegatedAchieved,
//	NoDelegatedFailed, ServiceExecutingDone,ServiceExecutingFailed,
//	NewSMS,Time1,Time2,Time3,Time4,Time5,Time6,Time7,Time8,Time9,
//	Time10,Time11,Time12,Time13,Time14,Time15,Time16,Time17,Time18,Time19,
//	Time20,Time21,Time22,Time23;

}
