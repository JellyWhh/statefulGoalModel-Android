/**
 * 
 */
package service.webservice.weather;

import jade.core.MicroRuntime;
import jade.wrapper.ControllerException;
import jade.wrapper.StaleProxyException;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import edu.fudan.se.agent.AideAgentInterface;
import edu.fudan.se.goalmachine.message.MesBody_Mes2Manager;
import edu.fudan.se.goalmachine.message.MesHeader_Mes2Manger;
import edu.fudan.se.goalmachine.message.SGMMessage;
import edu.fudan.se.initial.SGMApplication;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

/**
 * 调用天气服务的桩，用安卓的<code>IntentService</code>实现
 * 
 * @author whh
 * 
 */
public class IntentServiceWeather extends IntentService {

	private String weatherInfo = "";
	private String goalModelName, elementName;

	/**
	 * 必须有一个空的构造函数，不然会报错
	 */
	public IntentServiceWeather() {
		super("IntentServiceWeather");
	}

	/**
	 * @param name
	 */
	public IntentServiceWeather(String name) {
		super(name);
	}

	@Override
	protected void onHandleIntent(Intent intent) {

		String city = intent.getExtras().getString("CITY");
		goalModelName = intent.getExtras().getString("GOAL_MODEL_NAME");
		elementName = intent.getExtras().getString("ELEMENT_NAME");
		weatherInfo = getWeather(city);
	}

	@Override
	public void onDestroy() {
		System.out.println("onDestroy");
		// 在destory前把天气信息通过agent发送给manager
		SGMMessage msg = new SGMMessage(
				MesHeader_Mes2Manger.LOCAL_AGENT_MESSAGE, null, null, null,
				null, goalModelName, elementName,
				MesBody_Mes2Manager.ServiceResult);
		msg.setDescription("weatherInfo: " + weatherInfo);

		getAideAgentInterface().handleMesFromService(msg);

		super.onDestroy();
	}

	/**
	 * 调用中国气象局提供的查询天气的web service，查询某个城市的天气
	 * 
	 * @param city
	 *            要查询天气的城市
	 * @return 这个城市的天气
	 */
	private String getWeather(String cityName) {
		System.out.println("cityName: " + cityName);
		String ret = "";

		/* 1.指定 WebService 的命名空间和调用方法 */
		final String NAMESPACE = "http://webservice.se.fudan.edu/";
		// final String NAMESPACE = "http://weather.service.se.fudan.edu/";

		// web service地址，可以访问，然后可以看到有很多个查询天气的方法
		String SERVICE_URL = "http://10.131.252.246:8080/WeatherService/WeatherPort";
		// String SERVICE_URL = "http://10.131.253.133:8888/WeatherService";

		// 调用的方法，通过城市名称获取天气情况
		final String METHOD_NAME = "getWeatherByName";

		try {
			HttpTransportSE ht = new HttpTransportSE(SERVICE_URL, 10 * 1000);
			SoapObject soapObject = new SoapObject(NAMESPACE, METHOD_NAME);

			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);

			soapObject.addProperty("arg0", cityName);
			// 设置与.Net提供的Web Serviceb保持较好的兼容性（true）
			envelope.dotNet = false;
			envelope.bodyOut = soapObject;

			ht.call(null, envelope); // 必须是null！！！！！否则web
										// service服务端收不到传过去的参数！！！为了这个bug奋斗了快一下午了。。。
			if (envelope.getResponse() != null) {
				SoapObject result = (SoapObject) envelope.bodyIn;
				System.out.println(result.getPropertyAsString(0)); // 打印出来的是:
				ret = result.getPropertyAsString(0);
			} else {
				ret = "error";
			}
			// 必须断开连接，不然再次调用这个intent service会报错
			ht.getServiceConnection().disconnect();

		} catch (Exception e) {
			Log.e("MY_LOG", "catch soap exection");
			e.printStackTrace();
		}

		return ret;
	}

	/**
	 * 拿到agent的引用
	 * 
	 * @return agent
	 */
	private AideAgentInterface getAideAgentInterface() {
		AideAgentInterface aideAgentInterface = null; // agent interface
		try {
			aideAgentInterface = MicroRuntime.getAgent(
					((SGMApplication) getApplication()).getAgentNickname())
					.getO2AInterface(AideAgentInterface.class);
		} catch (StaleProxyException e) {
			e.printStackTrace();
		} catch (ControllerException e) {
			e.printStackTrace();
		}
		return aideAgentInterface;
	}

}
