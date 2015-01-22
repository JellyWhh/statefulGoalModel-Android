/**
 * 
 */
package edu.fudan.agent.support;

import edu.fudan.se.goalmachine.message.SGMMessage;

/**
 * 服务调用自适应时用到的util
 * @author whh
 * 
 */
public class ServiceInvocationAUtil extends AdaptationUtil {

	/**
	 * 调用服务时element machine发来的msg信息，里面有隐含的content部分，所以要记录下来
	 */
	private SGMMessage sgmMessage;

	public ServiceInvocationAUtil(String goalModelName, String elementName) {
		super(goalModelName, elementName);
	}

	public ServiceInvocationAUtil(String goalModelName, String elementName,
			SGMMessage sgmMessage) {
		super(goalModelName, elementName);
		this.sgmMessage = sgmMessage;
	}

	public SGMMessage getSgmMessage() {
		return sgmMessage;
	}

}
