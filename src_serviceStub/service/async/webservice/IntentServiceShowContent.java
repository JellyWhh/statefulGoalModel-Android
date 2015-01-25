/**
 * 
 */
package service.async.webservice;

import java.text.SimpleDateFormat;
import java.util.Date;

import edu.fudan.se.goalmodel.RequestData;
import edu.fudan.se.initial.SGMApplication;
import edu.fudan.se.userMes.UserShowContentTask;
import edu.fudan.se.userMes.UserTask;
import android.app.IntentService;
import android.content.Intent;

/**
 * 创建一个<code>UserShowContentTask</code>，展示某些内容
 * 
 * @author whh
 * 
 */
public class IntentServiceShowContent extends IntentService {

	private String goalModelName, elementName;

	public IntentServiceShowContent() {
		super("IntentServiceShowContent");
	}

	public IntentServiceShowContent(String name) {
		super(name);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		RequestData requestData = (RequestData) intent
				.getSerializableExtra("REQUEST_DATA_CONTENT");

		goalModelName = intent.getExtras().getString("GOAL_MODEL_NAME");
		elementName = intent.getExtras().getString("ELEMENT_NAME");

		createUserShowContentTask(requestData);
	}

	@Override
	public void onDestroy() {
		System.out.println("IntentServiceShowContent onDestroy");
		super.onDestroy();
	}

	/**
	 * 创建一个<code>UserShowContentTask</code>，展示某些内容
	 * 
	 * @param requestData
	 *            要展示的内容
	 */
	private void createUserShowContentTask(RequestData requestData) {

		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String delegateOutTaskTime = df.format(new Date());
		UserShowContentTask userShowContentTask = new UserShowContentTask(delegateOutTaskTime, goalModelName, elementName);
		// UserShowContentTask userShowContentTask = new UserShowContentTask(
		// delegateOutTaskTime, goalModelName, elementName, false);
		// 有需要展示的数据
		if (requestData != null) {
			userShowContentTask.setRequestData(requestData);
		}
		String description = "You have received ";

		if (requestData.getContentType().equals("Text")) {
			description += "a span of text.";
		} else if (requestData.getContentType().equals("Image")) {
			description += "an image.";
		}
		userShowContentTask.setDescription(description);
		((SGMApplication) getApplication()).getUserTaskList().add(
				userShowContentTask);

		// 发送 弹窗广播，在MainActivity会监听这个广播然后弹出通知窗口
		Intent broadcast_ndt = new Intent();
		broadcast_ndt.setAction("jade.task.NOTIFICATION");
		broadcast_ndt.putExtra("Content", description);
		getApplicationContext().sendBroadcast(broadcast_ndt);
	}

}
