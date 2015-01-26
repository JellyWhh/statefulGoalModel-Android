/**
 * 
 */
package edu.fudan.se.contextmanager;

import service.sync.webservice.ClientAccountBalance;

/**
 * @author whh
 * 
 */
public class CAccountBalance implements IContext {

	@Override
	public Object getValue() {
		return ClientAccountBalance.getAccountBalance();
	}

}
