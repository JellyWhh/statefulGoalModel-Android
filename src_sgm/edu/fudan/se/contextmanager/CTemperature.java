/**
 * 
 */
package edu.fudan.se.contextmanager;

import service.sync.webservice.ClientTemperature;

/**
 * 温度上下文
 * 
 * @author whh
 * 
 */
public class CTemperature implements IContext {

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.fudan.se.contextmanager.IContext#getValue()
	 */
	@Override
	public Object getValue() {
		return ClientTemperature.getTemperature();
	}

}
