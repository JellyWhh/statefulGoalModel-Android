package edu.fudan.se.goalmachine.message;

import java.io.Serializable;

/**
 * agent与manager之间消息的Body部分
 * 
 * @author whh
 * 
 */
public enum MesBody_Mes2Manager implements MesBody, Serializable {
	StartGM, StopGM, SuspendGM, ResumeGM, ResetGM, EndTE, QuitTE,
	RequestPersonIA, DelegatedAchieved, DelegatedFailed, NoDelegatedAchieved, NoDelegatedFailed, DelegateOut;

	public static MesBody getMesBody(String body) {
		switch (body) {
		case "DelegatedAchieved":
			return DelegatedAchieved;
		case "StartGM":
			return StartGM;
		case "DelegatedFailed":
			return DelegatedFailed;

		default:
			break;
		}
		return null;
	}
}
