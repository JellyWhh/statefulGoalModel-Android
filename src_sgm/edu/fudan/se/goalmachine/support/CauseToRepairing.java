/**
 * 
 */
package edu.fudan.se.goalmachine.support;

import java.io.Serializable;

/**
 * 导致跳转到repairing状态的原因类型
 * 
 * @author whh
 *
 */
public enum CauseToRepairing implements Serializable{
	InvViolated, CcViolated, SubFail, PreCondition, PostCondition, DefaultPreCondition
}
