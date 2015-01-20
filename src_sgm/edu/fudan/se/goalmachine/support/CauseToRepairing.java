/**
 * 
 */
package edu.fudan.se.goalmachine.support;


/**
 * 导致跳转到repairing状态的原因类型
 * 
 * @author whh
 *
 */
public enum CauseToRepairing{
	PostCondition,SubExecutingFail,SubActivatedFail;
}
