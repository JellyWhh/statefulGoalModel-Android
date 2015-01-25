/**
 * 
 */
package service.async.webservice;

import java.text.SimpleDateFormat;
import java.util.Date;

import edu.fudan.se.goalmodel.EncodeDecodeRequestData;
import edu.fudan.se.goalmodel.RequestData;
import edu.fudan.se.initial.SGMApplication;
import edu.fudan.se.userMes.UserInputTextTask;
import android.app.IntentService;
import android.content.Intent;

/**
 * 让用户输入一段本文的服务
 * 
 * @author whh
 * 
 */
public class IntentServiceUserInput extends IntentService {

	private String goalModelName, elementName;

	public IntentServiceUserInput() {
		super("IntentServiceUserInput");
	}

	public IntentServiceUserInput(String name) {
		super(name);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// 调用拍照服务的时候携带的request data应该是一段文本，描述要输入的内容
		RequestData requestData = (RequestData) intent
				.getSerializableExtra("REQUEST_DATA_CONTENT");

		goalModelName = intent.getExtras().getString("GOAL_MODEL_NAME");
		elementName = intent.getExtras().getString("ELEMENT_NAME");

		createUserInputTextTask(requestData);
	}

	@Override
	public void onDestroy() {
		System.out.println("IntentServiceUserInput onDestroy");
		super.onDestroy();
	}

	/**
	 * 创建一个<code>UserShowContentTask</code>，展示某些内容
	 * 
	 * @param requestData
	 *            要展示的内容
	 */
	private void createUserInputTextTask(RequestData requestData) {

		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String delegateOutTaskTime = df.format(new Date());
		UserInputTextTask userInputTextTask = new UserInputTextTask(
				delegateOutTaskTime, null,goalModelName, elementName);

		String description = "You need to input a span of text:\n";

		// 必须有要拍摄内容的描述
		if (requestData != null && requestData.getContentType().equals("Text")) {
			description += EncodeDecodeRequestData.decodeToText(requestData
					.getContent());
		}

		userInputTextTask.setDescription(description);
		((SGMApplication) getApplication()).getUserTaskList().add(
				userInputTextTask);

		// 发送 弹窗广播，在MainActivity会监听这个广播然后弹出通知窗口
		Intent broadcast_ndt = new Intent();
		broadcast_ndt.setAction("jade.task.NOTIFICATION");
		broadcast_ndt.putExtra("Content", description);
		getApplicationContext().sendBroadcast(broadcast_ndt);
	}

}
