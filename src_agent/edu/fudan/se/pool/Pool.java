/**
 * 
 */
package edu.fudan.se.pool;

import java.util.ArrayList;

/**
 * @author zjh
 * 
 */
public class Pool {

	private static ArrayList<Message> gOutMsgs = new ArrayList<Message>();// 本机目标要发出去的
	private static ArrayList<Message> gInMsgs = new ArrayList<Message>();// 本机目标接受的被委托方给我的回馈信息
	private static ArrayList<Message> dOutMsgs = new ArrayList<Message>();// 返回
																			// 本机对于委托消息的态度
																			// 接受还是拒绝
	private static ArrayList<Message> dInMsgs = new ArrayList<Message>();// 接收（不是接受）给本机的委托消息

//	static {
//		// test 用
//		Message msg = new Message();
//		msg.setToRole("teacher");
//		msg.setContent("hehehe");
//		gOutMsgs.add(msg);
//
//		Message msg2 = new Message();
//		msg2.setToRole("teacher");
//		msg2.setContent("xixixi");
//		gOutMsgs.add(msg2);
//
//	}

	public static void setDInMessage(Message msg) {
		synchronized (dInMsgs) {
			dInMsgs.add(msg);
		}
	}

	public static Message getDInMessage() {
		synchronized (dInMsgs) {
			if (dInMsgs.size() > 0)
				return dInMsgs.remove(0);
			return null;
		}
	}

	public static void setDOutMessage(Message msg) {
		synchronized (dOutMsgs) {
			dOutMsgs.add(msg);
		}
	}

	public static Message getDOutMessage() {
		synchronized (dOutMsgs) {
			if (dOutMsgs.size() > 0) {
				return dOutMsgs.remove(0);
			}
			return null;
		}
	}

	public static void setGInMessage(Message msg) {// 外部发来给我内部 goal model
													// 的消息，消息内容成功或者失败
		synchronized (gInMsgs) {
			gInMsgs.add(msg);
		}
	}

	public static Message getGInMessage() {// 外部发来给我内部 goal model 的消息，消息内容成功或者失败
		synchronized (gInMsgs) {
			if (gInMsgs.size() > 0)
				return gInMsgs.remove(0);
			return null;
		}
	}

	public static void setGOutMessage(Message msg) {
		synchronized (gOutMsgs) {
			gOutMsgs.add(msg);
		}
	}

	public static synchronized Message getGOutMessage() {
		synchronized (gOutMsgs) {
			if (gOutMsgs.size() > 0)
				return gOutMsgs.remove(0);
			return null;
		}
	}
}
