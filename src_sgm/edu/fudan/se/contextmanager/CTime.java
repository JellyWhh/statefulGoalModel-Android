/**
 * 
 */
package edu.fudan.se.contextmanager;

import service.sync.webservice.ClientTime;

/**
 * @author whh
 *
 */
public class CTime implements IContext {

	/* (non-Javadoc)
	 * @see edu.fudan.se.contextmanager.IContext#getValue()
	 */
	@Override
	public Object getValue() {
		return ClientTime.getDate();
	}

}
