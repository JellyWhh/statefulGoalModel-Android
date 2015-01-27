package service.async.webservice;

import java.util.ArrayList;

import android.app.IntentService;
import android.content.Intent;
import edu.fudan.se.goalmachine.message.MesBody_Mes2Manager;
import edu.fudan.se.goalmachine.message.MesHeader_Mes2Manger;
import edu.fudan.se.goalmachine.message.SGMMessage;
import edu.fudan.se.goalmodel.EncodeDecodeRequestData;
import edu.fudan.se.goalmodel.RequestData;
import edu.fudan.se.initial.SGMApplication;
import edu.fudan.se.support.GetAgent;

public class IntentServiceQueryBookFromShop extends IntentService {
	
	private String bookName = "";
	
	private String bookPrice="";

	private String goalModelName, elementName;
	
	/**
	 * 必须有一个空的构造函数，不然会报错
	 */
	public IntentServiceQueryBookFromShop() {
		super("IntentServiceQueryBookFromShop");
	}

	/**
	 * @param name
	 */
	public IntentServiceQueryBookFromShop(String name) {
		super(name);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		RequestData requestData = (RequestData) intent
				.getSerializableExtra("NEED_REQUEST_DATA_CONTENT");

		bookName = EncodeDecodeRequestData.decodeToText(requestData
				.getContent());

		goalModelName = intent.getExtras().getString("GOAL_MODEL_NAME");
		elementName = intent.getExtras().getString("ELEMENT_NAME");
		
		if (queryBookFromShop(bookName)) {
			
			// 服务执行成功，也就是书店有这本书
			SGMMessage msg = new SGMMessage(
					MesHeader_Mes2Manger.LOCAL_AGENT_MESSAGE,
					goalModelName, null, elementName,
					MesBody_Mes2Manager.ServiceExecutingDone);
			
			RequestData retRequestData = new RequestData("book price", "Text");
			retRequestData.setContent(bookPrice.getBytes());
			msg.setRetContent(retRequestData);

			GetAgent.getAideAgentInterface((SGMApplication) getApplication())
					.handleMesFromService(msg);

		}else {
			// 服务执行失败
			SGMMessage msg = new SGMMessage(
					MesHeader_Mes2Manger.LOCAL_AGENT_MESSAGE,
					goalModelName, null, elementName,
					MesBody_Mes2Manager.ServiceExecutingFailed);

			GetAgent.getAideAgentInterface((SGMApplication) getApplication())
					.handleMesFromService(msg);

		}
		
		

	}

	@Override
	public void onDestroy() {
		System.out.println("IntentServiceQueryBookFromShop onDestroy");
		super.onDestroy();
	}
	
	private boolean queryBookFromShop(String bookname){

		//这里应该是调用图书馆的web service来返回结果
		ArrayList<String> bookList = new ArrayList<>();
		bookList.add("Math");
		bookList.add("Science");
		bookList.add("Chinese");
		bookList.add("Language");
		bookList.add("Geography");
		bookList.add("Biology");
		bookList.add("Art");
		bookList.add("Piano");
		bookList.add("English");
		bookList.add("Chemistry");
		bookList.add("Computer");
		
		if (bookList.contains(bookname)) {
			int random = (int) (Math.random() * 100); // [0,100)
			bookPrice = String.valueOf(random);
			return true;
		}else {
			return false;
		}
	}
	
}
