/**
 * 
 */
package edu.fudan.se.contextmanager;

import service.sync.webservice.ClientWeather;

/**
 * 天气上下文，也就是是晴天（SUNNY）还是下雨（RAINY）
 * 
 * @author whh
 * 
 */
public class CWeather implements IContext {

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.fudan.se.contextmanager.IContext#getValue()
	 */
	@Override
	public Object getValue() {
		return ClientWeather.getWeather();
	}

}
