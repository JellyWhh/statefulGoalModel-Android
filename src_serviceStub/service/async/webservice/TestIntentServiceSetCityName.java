/**
 * 
 */
package service.async.webservice;

import edu.fudan.se.goalmachine.message.MesBody_Mes2Manager;
import edu.fudan.se.goalmachine.message.MesHeader_Mes2Manger;
import edu.fudan.se.goalmachine.message.SGMMessage;
import edu.fudan.se.goalmodel.RequestData;
import edu.fudan.se.initial.SGMApplication;
import edu.fudan.se.support.GetAgent;
import android.app.IntentService;
import android.content.Intent;

/**
 * 用来测试数据传递，这个服务给intentServiceWeather里用到的cityname赋值
 * 
 * @author whh
 * 
 */
public class TestIntentServiceSetCityName extends IntentService {

	String retCityName;

	private String goalModelName, elementName;

	public TestIntentServiceSetCityName() {
		super("TestIntentServiceSetCityName");
	}

	public TestIntentServiceSetCityName(String name) {
		super(name);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		goalModelName = intent.getExtras().getString("GOAL_MODEL_NAME");
		elementName = intent.getExtras().getString("ELEMENT_NAME");

		retCityName = "Beijing";

	}

	@Override
	public void onDestroy() {
		System.out.println("TestIntentServiceSetCityName onDestroy");

		// 在destory前把天气信息通过agent发送给manager
		SGMMessage msg = new SGMMessage(
				MesHeader_Mes2Manger.LOCAL_AGENT_MESSAGE, null, null, null,
				null, goalModelName, elementName,
				MesBody_Mes2Manager.ServiceExecutingDone);
		RequestData requestData = new RequestData("Text");
		requestData.setContent(retCityName.getBytes());
		msg.setContent(requestData);

		GetAgent.getAideAgentInterface((SGMApplication) getApplication())
				.sendMesToManager(msg);
		
		super.onDestroy();
	}

}
