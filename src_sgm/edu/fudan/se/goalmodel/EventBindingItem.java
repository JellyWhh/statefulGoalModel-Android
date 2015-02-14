/**
 * 
 */
package edu.fudan.se.goalmodel;

/**
 * @author whh
 * 
 */
public class EventBindingItem {

	private ExternalEvent externalEvent;
	private String elementName;

	public EventBindingItem(ExternalEvent externalEvent, String elementName) {
		this.externalEvent = externalEvent;
		this.elementName = elementName;
	}

	public ExternalEvent getExternalEvent() {
		return externalEvent;
	}

	public String getElementName() {
		return elementName;
	}

}
