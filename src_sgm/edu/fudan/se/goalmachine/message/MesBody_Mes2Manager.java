package edu.fudan.se.goalmachine.message;

/**
 * agent与manager之间消息的Body部分
 * 
 * @author whh
 * 
 */
public enum MesBody_Mes2Manager implements MesBody {
	StartGM, StopGM, SuspendGM, ResumeGM, ResetGM, EndTE, QuitTE
}
