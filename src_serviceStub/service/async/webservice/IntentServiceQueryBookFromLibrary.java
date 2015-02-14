/**
 * 
 */
package service.async.webservice;

import java.util.ArrayList;
import java.util.HashMap;

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
 * @author whh
 *
 */
public class IntentServiceQueryBookFromLibrary extends IntentService {
	
	private String bookName = "";
	private String bookandLib="";

	private String goalModelName, elementName;
	
	/**
	 * 必须有一个空的构造函数，不然会报错
	 */
	public IntentServiceQueryBookFromLibrary() {
		super("IntentServiceQueryBookFromLibrary");
	}

	/**
	 * @param name
	 */
	public IntentServiceQueryBookFromLibrary(String name) {
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
		
		if (queryBookFromLibrary(bookName)) {
			
			// 服务执行成功，也就是图书馆有这本书
			SGMMessage msg = new SGMMessage(
					MesHeader_Mes2Manger.LOCAL_AGENT_MESSAGE,
					goalModelName, null, elementName,
					new MesBody_Mes2Manager("ServiceExecutingDone"));
			
			RequestData retRequestData = new RequestData("book name", "BooleanText");
			retRequestData.setContent(bookandLib.getBytes());
			msg.setRetContent(retRequestData);


			GetAgent.getAideAgentInterface((SGMApplication) getApplication())
					.handleMesFromService(msg);

		}else {
			// 服务执行失败
			SGMMessage msg = new SGMMessage(
					MesHeader_Mes2Manger.LOCAL_AGENT_MESSAGE,
					goalModelName, null, elementName,
					new MesBody_Mes2Manager("ServiceExecutingFailed"));

			GetAgent.getAideAgentInterface((SGMApplication) getApplication())
					.handleMesFromService(msg);

		}
		
		

	}

	@Override
	public void onDestroy() {
		System.out.println("IntentServiceQueryBookFromLibrary onDestroy");
		super.onDestroy();
	}
	
	private boolean queryBookFromLibrary(String bookname){
		boolean ret=false;
		
		HashMap<String, ArrayList<String>> libraryBookList = new HashMap<>();

		//这里应该是调用图书馆的web service来返回结果
		ArrayList<String> bookList1 = new ArrayList<>();
		bookList1.add("Thinking in Java");
		bookList1.add("Computer Systems Architecture");
		
		ArrayList<String> bookList2 = new ArrayList<>();
		bookList2.add("Thinking in Java");
		bookList2.add("Design Patterns");
		
		libraryBookList.put("Library1", bookList1);
		libraryBookList.put("Library2", bookList2);
		
		for (String lib:libraryBookList.keySet()) {
			ArrayList<String> bookList= libraryBookList.get(lib);
			if (bookList.contains(bookname)) {
				bookandLib = bookname + " at " + lib;
				ret = true;
				break;
			}
		}
		
		return ret;
	}
	
}
