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
public class IntentServiceQuerySeller extends IntentService {

	private String bookName = "";

	private String sellerName = "";

	private String goalModelName, elementName;

	/**
	 * 必须有一个空的构造函数，不然会报错
	 */
	public IntentServiceQuerySeller() {
		super("IntentServiceQuerySeller");
	}

	/**
	 * @param name
	 */
	public IntentServiceQuerySeller(String name) {
		super(name);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		RequestData requestData = (RequestData) intent
				.getSerializableExtra("REQUEST_DATA_CONTENT");

		bookName = EncodeDecodeRequestData.decodeToText(requestData
				.getContent());

		goalModelName = intent.getExtras().getString("GOAL_MODEL_NAME");
		elementName = intent.getExtras().getString("ELEMENT_NAME");

		if (querySellerName(bookName)) {

			// 服务执行成功，也就是图书馆有这本书
			SGMMessage msg = new SGMMessage(
					MesHeader_Mes2Manger.LOCAL_AGENT_MESSAGE, goalModelName,
					null, elementName, MesBody_Mes2Manager.ServiceExecutingDone);

			RequestData retRequestData = new RequestData("seller name", "Text");
			retRequestData.setContent(sellerName.getBytes());
			msg.setRetContent(retRequestData);

			GetAgent.getAideAgentInterface((SGMApplication) getApplication())
					.handleMesFromService(msg);

		} else {
			// 服务执行失败
			SGMMessage msg = new SGMMessage(
					MesHeader_Mes2Manger.LOCAL_AGENT_MESSAGE, goalModelName,
					null, elementName,
					MesBody_Mes2Manager.ServiceExecutingFailed);

			GetAgent.getAideAgentInterface((SGMApplication) getApplication())
					.handleMesFromService(msg);

		}

	}

	@Override
	public void onDestroy() {
		System.out.println("IntentServiceQuerySeller onDestroy");
		super.onDestroy();
	}

	private boolean querySellerName(String bookname) {

		HashMap<String, ArrayList<String>> seller = new HashMap<>();

		ArrayList<String> bookList1 = new ArrayList<>();
		bookList1.add("Math");
		bookList1.add("Science");
		bookList1.add("Language");
		bookList1.add("Biology");
		bookList1.add("English");

		ArrayList<String> bookList2 = new ArrayList<>();
		bookList2.add("Math");
		bookList2.add("Geography");
		bookList2.add("Biology");
		bookList2.add("Piano");
		bookList2.add("English");

		ArrayList<String> bookList3 = new ArrayList<>();
		bookList3.add("Science");
		bookList3.add("Language");
		bookList3.add("Geography");
		bookList3.add("Piano");

		seller.put("alice", bookList1);
		seller.put("bob", bookList2);
		seller.put("tom", bookList3);

		boolean ret = false;

		for (String selleName : seller.keySet()) {
			if (seller.get(selleName).contains(bookname)) {
				sellerName = selleName;
				ret = true;
				break;
			}
		}

		return ret;
	}

}
