/**
 * 
 */
package edu.fudan.se.contextmanager;

import service.sync.webservice.ClientAuthorization;

/**
 * 授权检查
 * @author whh
 *
 */
public class CAuthorization implements IContext {

	@Override
	public Object getValue() {
		return ClientAuthorization.isAuthorized();
	}

}
