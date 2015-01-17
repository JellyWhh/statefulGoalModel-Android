/**
 * 
 */
package service.async.webservice;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.IntentService;
import android.content.Intent;
import edu.fudan.se.goalmodel.EncodeDecodeRequestData;
import edu.fudan.se.goalmodel.RequestData;
import edu.fudan.se.initial.SGMApplication;
import edu.fudan.se.userMes.UserTakePictureTask;

/**
 * 拍照服务，创建一个UserTakePictureTask添加到task列表，具体拍照UI是在<code>TaskFragment</code>中实现的
 * 
 * @author whh
 * 
 */
public class IntentServiceTakePicture extends IntentService {

	private String goalModelName, elementName;

	public IntentServiceTakePicture() {
		super("IntentServiceTakePicture");
	}

	public IntentServiceTakePicture(String name) {
		super(name);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// 调用拍照服务的时候携带的request data应该是一段文本，描述要拍的内容
		RequestData requestData = (RequestData) intent
				.getSerializableExtra("REQUEST_DATA_CONTENT");

		goalModelName = intent.getExtras().getString("GOAL_MODEL_NAME");
		elementName = intent.getExtras().getString("ELEMENT_NAME");

		createUserTakePictureTask(requestData);
	}

	@Override
	public void onDestroy() {
		System.out.println("IntentServiceTakePicture onDestroy");
		super.onDestroy();
	}

	/**
	 * 创建一个<code>UserShowContentTask</code>，展示某些内容
	 * 
	 * @param requestData
	 *            要展示的内容
	 */
	private void createUserTakePictureTask(RequestData requestData) {

		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String delegateOutTaskTime = df.format(new Date());
		UserTakePictureTask userTakePictureTask = new UserTakePictureTask(
				delegateOutTaskTime, goalModelName, elementName, false);

		String description = "You need to take a picture of ";

		// 必须有要拍摄内容的描述
		if (requestData.getContentType().equals("Text")) {
			description += EncodeDecodeRequestData.decodeToText(requestData
					.getContent());
		}

		userTakePictureTask.setDescription(description);
		((SGMApplication) getApplication()).getUserTaskList().add(
				userTakePictureTask);

		// 发送 弹窗广播，在MainActivity会监听这个广播然后弹出通知窗口
		Intent broadcast_ndt = new Intent();
		broadcast_ndt.setAction("jade.task.NOTIFICATION");
		broadcast_ndt.putExtra("Content", description);
		getApplicationContext().sendBroadcast(broadcast_ndt);
	}

}
