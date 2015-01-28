/**
 * 
 */
package service.async.webservice;

import java.text.SimpleDateFormat;
import java.util.Date;

import service.sync.webservice.ClientAuthorization;

import android.app.IntentService;
import android.content.Intent;
import edu.fudan.se.goalmodel.EncodeDecodeRequestData;
import edu.fudan.se.goalmodel.RequestData;
import edu.fudan.se.initial.SGMApplication;
import edu.fudan.se.userMes.UserInputTextTask;
import edu.fudan.se.userMes.UserTask;

/**
 * @author whh
 * 
 */
public class IntentServiceInputText extends IntentService {

	private String goalModelName, elementName;

	public IntentServiceInputText() {
		super("IntentServiceInputText");
	}

	public IntentServiceInputText(String name) {
		super(name);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		RequestData retRequestData = (RequestData) intent
				.getSerializableExtra("RET_REQUEST_DATA_CONTENT");

		RequestData needRequestData = (RequestData) intent
				.getSerializableExtra("NEED_REQUEST_DATA_CONTENT");

		goalModelName = intent.getExtras().getString("GOAL_MODEL_NAME");
		elementName = intent.getExtras().getString("ELEMENT_NAME");
		
		//创建用用户输入的task

		String userTaskTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
				.format(new Date());
		
		UserTask userTask = new UserInputTextTask(userTaskTime, ClientAuthorization.agentNickName,
				goalModelName, elementName);
		String userTaskDescription = "You need to input a span of text about:\n"
				+ retRequestData.getName();
		if (needRequestData != null
				&& needRequestData.getContentType().equals("Text")) {
			userTaskDescription += " of "
					+ EncodeDecodeRequestData
							.decodeToText(needRequestData.getContent());
		}
		userTask.setRequestDataName(retRequestData.getName());
		userTask.setDescription(userTaskDescription);
		((SGMApplication) getApplication()).getUserCurrentTaskList().add(0, userTask);

		// 新任务广播
		Intent broadcast_nda = new Intent();
		broadcast_nda.setAction("jade.task.NOTIFICATION");
		broadcast_nda.putExtra("Content", userTask.getDescription());
		getApplicationContext().sendBroadcast(broadcast_nda);

	}

	@Override
	public void onDestroy() {
		System.out.println("IntentServiceInputText onDestroy");
		super.onDestroy();
	}


}
