/**
 * 
 */
package edu.fudan.se.userMes;

/**
 * 发送给用户的通知
 * @author whh
 *
 */
public class UserMessage {

	private String time;
	private String content;
	
	public UserMessage(String time,String content){
		this.time = time;
		this.content  = content;
	}


	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}
