/**
 * 
 */
package edu.fudan.se.goalmachine.message;

import java.io.Serializable;

import edu.fudan.se.goalmodel.RequestData;

/**
 * 实现线程间的通信的消息格式，是可序列化对象
 * 
 * @author whh
 * 
 */
public class SGMMessage implements Serializable {

	private static final long serialVersionUID = -1901699789736351108L;

	private MesHeader header; // 消息头部
	private Messager sender; // 消息发送者
	private Messager receiver; // 消息接收者，只有一个，如果一个Goal要发消息给多个接收者，就发多条信息，每条信息只有一个接收者
	private MesBody body; // 消息主体
	private String description; // 消息附加的描述，在需要人为参与的task时，发送消息给manager，要把任务描述附加上去
	private RequestData content;

	public SGMMessage(MesHeader header, String senderAgentName,
			String senderGoalModelName, String senderElementName,
			String receiverAgentName, String receiverGoalModelName,
			String receiverElementName, MesBody body) {
		this.header = header;
		this.sender = new Messager(senderAgentName, senderGoalModelName,
				senderElementName);
		this.receiver = new Messager(receiverAgentName, receiverGoalModelName,
				receiverElementName);
		this.body = body;
	}

	public String toString() {
		String message = header.toString() + "-" + sender.getAgentName() + "-"
				+ sender.getGoalModelName() + "-" + sender.getElementName()
				+ "-" + receiver.getAgentName() + "-"
				+ receiver.getGoalModelName() + "-" + receiver.getElementName()
				+ "-" + body.toString() + "-" + description;
		return message;
	}

	public MesHeader getHeader() {
		return header;
	}

	public void setHeader(MesHeader header) {
		this.header = header;
	}

	public Messager getSender() {
		return sender;
	}

	public void setSender(String senderAgentName, String senderGoalModelName,
			String senderElementName) {
		this.sender = new Messager(senderAgentName, senderGoalModelName,
				senderElementName);
	}

	public Messager getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiverAgentName,
			String receiverGoalModelName, String receiverElementName) {
		this.receiver = new Messager(receiverAgentName, receiverGoalModelName,
				receiverElementName);
	}

	public MesBody getBody() {
		return body;
	}

	public void setBody(MesBody body) {
		this.body = body;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public RequestData getContent() {
		return content;
	}

	public void setContent(RequestData content) {
		this.content = content;
	}

	public class Messager implements Serializable {

		private static final long serialVersionUID = 8513804283789760689L;

		private String agentName;
		private String goalModelName;
		private String elementName;

		private Messager(String agentName, String goalModelName,
				String elementName) {
			this.agentName = agentName;
			this.goalModelName = goalModelName;
			this.elementName = elementName;
		}

		public String toString() {
			return agentName + "-" + goalModelName + "-" + elementName;
		}

		public String getAgentName() {
			return this.agentName;
		}

		public String getGoalModelName() {
			return this.goalModelName;
		}

		public String getElementName() {
			return this.elementName;
		}

		public void setAgentName(String agentName) {
			this.agentName = agentName;
		}

		public void setGoalModelName(String goalModelName) {
			this.goalModelName = goalModelName;
		}

		public void setElementName(String elementName) {
			this.elementName = elementName;
		}
	}
	
	/**
	 * 消息头部
	 * @author whh
	 *
	 */
	public interface MesHeader {

	}

	
	/**
	 * @author whh
	 *
	 */
	public interface MesBody {
	}

}
