/**
 * 
 */
package edu.fudan.se.userMes;


/**
 * 用户收到这个任务后，点击上面的show按钮，会显示一个对话框，这个对话框里显示适当的内容；点击quit按钮表示失败
 * 
 * @author whh
 * 
 */
public class UserShowContentTask extends UserTask {

	public UserShowContentTask(String time, String goalModelName,
			String elementName, boolean isDone) {
		super(time, goalModelName, elementName, isDone);
	}

}