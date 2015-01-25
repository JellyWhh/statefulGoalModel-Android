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
 * 这个服务返回一条文本信息，可用来作为天气服务的输入，也就是城市名字
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
		// 服务执行成功
		SGMMessage msg = new SGMMessage(
				MesHeader_Mes2Manger.LOCAL_AGENT_MESSAGE, goalModelName, null,
				elementName, MesBody_Mes2Manager.ServiceExecutingDone);
		RequestData retRequestData = new RequestData("cityName", "Text");
		retRequestData.setContent(retCityName.getBytes());
		msg.setRetContent(retRequestData);

		GetAgent.getAideAgentInterface((SGMApplication) getApplication())
				.sendMesToManager(msg);

		super.onDestroy();
	}

}
