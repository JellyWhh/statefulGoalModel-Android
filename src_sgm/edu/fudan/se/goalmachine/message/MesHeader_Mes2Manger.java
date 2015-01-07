/**
 * 
 */
package edu.fudan.se.goalmachine.message;

/**
 * @author whh
 *
 */
public enum MesHeader_Mes2Manger implements MesHeader {
	EXTERNAL_AGENT_MESSAGE,LOCAL_AGENT_MESSAGE,ELEMENT_MESSAGE;
	
	public static MesHeader getMesHeader(String header){
		switch (header) {
		case "EXTERNAL_AGENT_MESSAGE":
			return EXTERNAL_AGENT_MESSAGE;
		}
		return null;
	}
}
