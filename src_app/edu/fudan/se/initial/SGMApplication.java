/**
 * 
 */
package edu.fudan.se.initial;

import jade.util.Logger;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.logging.Level;

import edu.fudan.se.goalmodel.GmXMLParser;
import edu.fudan.se.goalmodel.GoalModel;
import edu.fudan.se.goalmodel.GoalModelManager;
import edu.fudan.se.userMes.UserMessage;
import edu.fudan.se.userMes.UserTask;

import android.app.Application;
import android.content.SharedPreferences;
import android.os.Environment;

/**
 * 重写Application，主要重写里面的onCreate方法，设置并初始化一些全局变量
 * 
 * @author whh
 * 
 */
public class SGMApplication extends Application implements Serializable {

	private static final long serialVersionUID = 1L;

	private Logger logger = Logger.getJADELogger(this.getClass().getName());

	private ArrayList<UserTask> userTaskList;
	private ArrayList<UserMessage> userMessageList;

	private String agentNickname;

	// private GoalModelController goalModelController;

	private GoalModelManager goalModelManager;

	private String location = ""; // 位置信息

	@Override
	public void onCreate() {
		super.onCreate();
		initialData();
		initialJadePreferences();
	}

	/**
	 * 把用户的goal model list数据加载进来，如果以后要从xml文件里读取，就是在这里设置
	 */
	private void initialData() {
		this.userTaskList = new ArrayList<>();
		this.userMessageList = new ArrayList<>();

		GmXMLParser gmXMLParser = new GmXMLParser();
		File sdCardDir = Environment.getExternalStorageDirectory();
		// 得到一个路径，内容是sdcard的文件夹路径和APP自身名字
		String mygoalDir1 = sdCardDir.getPath() + "/sgm/mygoal.xml";
		String needdelegatebobDir2 = sdCardDir.getPath()
				+ "/sgm/needdelegatebob.xml";
		String bobDir3 = sdCardDir.getPath() + "/sgm/bob.xml";
		String testDir4 = sdCardDir.getPath() + "/sgm/test.xml";
		GoalModel mygoal = gmXMLParser.newGoalModel(mygoalDir1);
		GoalModel needdelegatebob = gmXMLParser
				.newGoalModel(needdelegatebobDir2);
		GoalModel bob = gmXMLParser.newGoalModel(bobDir3);
		GoalModel test = gmXMLParser.newGoalModel(testDir4);

		// GoalModel testGM = newTestGoalModel(); // 一个完全本地没有委托的

		goalModelManager = new GoalModelManager();
		goalModelManager.addGoalModel(mygoal);
		goalModelManager.addGoalModel(needdelegatebob);
		goalModelManager.addGoalModel(bob);
		goalModelManager.addGoalModel(test);
		Thread gmm = new Thread(goalModelManager);
		gmm.start();

		// this.goalModelController = new
		// GoalModelController(this.goalModelList);

	}

	/**
	 * zjh所写代码，把jade需要的相关属性初始化
	 */
	private void initialJadePreferences() {
		SharedPreferences settings = getSharedPreferences("jadeChatPrefsFile",
				0);

		String defaultHost = settings.getString("defaultHost", "");
		String defaultPort = settings.getString("defaultPort", "");
		if (defaultHost.isEmpty() || defaultPort.isEmpty()) {
			logger.log(Level.INFO, "Create default properties");
			SharedPreferences.Editor editor = settings.edit();
			editor.putString("defaultHost", "10.131.253.133"); // 改成jade平台的ip
			editor.putString("defaultPort", "1099");
			editor.commit();
		}
	}

	public String getAgentNickname() {
		return this.agentNickname;
	}

	public GoalModelManager getGoalModelManager() {
		return this.goalModelManager;
	}

	public void setAgentNickname(String agentNickname) {
		this.agentNickname = agentNickname;
	}

	public ArrayList<UserTask> getUserTaskList() {
		return userTaskList;
	}

	public void clearTasksOfGoalModel(GoalModel goalModel) {
		ArrayList<UserTask> toRemoveArrayList = new ArrayList<>();
		for (UserTask userTask : this.userTaskList) {
			if (userTask.getGoalModelName().equals(goalModel.getName())) {
				toRemoveArrayList.add(userTask);
			}
		}
		this.userTaskList.removeAll(toRemoveArrayList);
	}

	public ArrayList<UserMessage> getUserMessageList() {
		return userMessageList;
	}

	public void clearUserMessages() {
		this.userMessageList.clear();
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	// private GoalModel newTestGoalModel() {
	//
	// Hashtable<String, IContext> contextHashtable = new Hashtable<>();
	// contextHashtable.put("Temperature", new CTemperature());
	// contextHashtable.put("Weather", new CWeather());
	// contextHashtable.put("Time", new CTime());
	//
	// GoalModel goalModel = new GoalModel("my goal model test");
	//
	// GoalMachine myGoal = new GoalMachine("my goal model test", 0, 1, null,
	// 0, false);
	//
	// GoalMachine alice = new GoalMachine("alice", 0, 0, myGoal, 1, false);
	// GoalMachine bob = new GoalMachine("bob", 1, -1, myGoal, 1, false);
	//
	// Condition postCondition1 = new Condition("POST", "Int", "Temperature",
	// ">", "0");
	// postCondition1.setContextHashtable(contextHashtable);
	//
	// TaskMachine aliceChild_1 = new TaskMachine("aliceChild_1", alice, 2,
	// false);
	// aliceChild_1.setPostCondition(postCondition1);
	//
	// aliceChild_1.setExecutingRequestedServiceName("service.intentservice.weather");
	//
	// TaskMachine aliceChild_2 = new TaskMachine("aliceChild_2", alice, 2,
	// true);
	//
	// TaskMachine bobChild_1 = new TaskMachine("bobChild_1", bob, 2, true);
	//
	// TaskMachine bobChild_2 = new TaskMachine("bobChild_2", bob, 2, true);
	// TaskMachine bobChild_3 = new TaskMachine("bobChild_3", bob, 2, true);
	//
	// Condition contextCondition = new Condition("CONTEXT", "Int",
	// "Temperature", "<", "0");
	// contextCondition.setContextHashtable(contextHashtable);
	//
	// bobChild_1.setContextCondition(contextCondition);
	//
	// bobChild_2.setWaitingTimeLimit(5);// 5s
	//
	// Condition postCondition = new Condition("POST", "Int", "Temperature",
	// "<", "0");
	// postCondition.setContextHashtable(contextHashtable);
	// bobChild_3.setPostCondition(postCondition);
	//
	// myGoal.addSubElement(alice, 1);
	// myGoal.addSubElement(bob, 1);
	//
	// alice.addSubElement(aliceChild_1, 1);
	// alice.addSubElement(aliceChild_2, 1);
	//
	// bob.addSubElement(bobChild_1, 1);
	// bob.addSubElement(bobChild_2, 2);
	// bob.addSubElement(bobChild_3, 3);
	//
	// goalModel.setDescription("This is the description of the goal model!");
	// goalModel.setRootGoal(myGoal);
	// goalModel.addElementMachine(myGoal);
	// goalModel.addElementMachine(alice);
	// goalModel.addElementMachine(aliceChild_1);
	// goalModel.addElementMachine(aliceChild_2);
	// goalModel.addElementMachine(bob);
	// goalModel.addElementMachine(bobChild_1);
	// goalModel.addElementMachine(bobChild_2);
	// goalModel.addElementMachine(bobChild_3);
	//
	// return goalModel;
	//
	// }

}
