/**
 * 
 */
package service.async.webservice;

import android.app.IntentService;
import android.content.Intent;
import edu.fudan.se.goalmachine.message.MesBody_Mes2Manager;
import edu.fudan.se.goalmachine.message.MesHeader_Mes2Manger;
import edu.fudan.se.goalmachine.message.SGMMessage;
import edu.fudan.se.goalmodel.EncodeDecodeRequestData;
import edu.fudan.se.goalmodel.RequestData;
import edu.fudan.se.initial.SGMApplication;
import edu.fudan.se.support.GetAgent;

/**
 * 天气服务的候选服务，一定会返回执行失败的信息
 * @author whh
 * 
 */
public class IntentServiceWeatherCandidate extends IntentService {

	private String cityName = "";

	private String weatherInfo = "";
	private String goalModelName, elementName;

	/**
	 * 必须有一个空的构造函数，不然会报错
	 */
	public IntentServiceWeatherCandidate() {
		super("IntentServiceWeatherCandidate");
	}

	/**
	 * @param name
	 */
	public IntentServiceWeatherCandidate(String name) {
		super(name);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		RequestData requestData = (RequestData) intent
				.getSerializableExtra("REQUEST_DATA_CONTENT");
		System.out
				.println("IntentServiceWeatherCandidate, requestData content is null?:"
						+ (requestData.getContent() == null)
						+ ", type:"
						+ requestData.getContentType());

		cityName = EncodeDecodeRequestData.decodeToText(requestData
				.getContent());

		goalModelName = intent.getExtras().getString("GOAL_MODEL_NAME");
		elementName = intent.getExtras().getString("ELEMENT_NAME");
		
		weatherInfo = "cityName: " + cityName + ", no more info!";
		
		// 服务执行失败
		SGMMessage msg = new SGMMessage(
				MesHeader_Mes2Manger.LOCAL_AGENT_MESSAGE,
				goalModelName, null, elementName,
				MesBody_Mes2Manager.ServiceExecutingFailed);


		GetAgent.getAideAgentInterface((SGMApplication) getApplication())
				.handleMesFromService(msg);

//		// 测试时用，弹出一个通知，显示这个web service调用完毕要返回了
//		NotificationUtil notificationUtil = new NotificationUtil(this);
//		notificationUtil.showNotification("Web service Done",
//				"intent service weather candidate done!\nweatherInfo: " + weatherInfo,
//				"Web Service Done", 100);

	}

	@Override
	public void onDestroy() {
		System.out.println("IntentServiceWeatherCandidate onDestroy");
		super.onDestroy();
	}
}
