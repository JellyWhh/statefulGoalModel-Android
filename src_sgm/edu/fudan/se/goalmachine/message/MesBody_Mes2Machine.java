/**
 * 
 */
package edu.fudan.se.goalmachine.message;

import java.io.Serializable;

/**
 * manager与element machine之间的消息的body部分
 * 
 * @author whh
 * 
 */
public enum MesBody_Mes2Machine implements MesBody, Serializable {
	ACTIVATE, SUSPEND, STOP, RESUME, TASK_DONE, TASK_FAILED, ACTIVATEDDONE, FAILED, ACHIEVEDDONE, START, ACTIVATEDFAILED, STARTEXECUTING
}
