/**
 * 
 */
package edu.fudan.se.initial;

import jade.util.Logger;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;

import edu.fudan.se.goalmachine.Condition;
import edu.fudan.se.goalmachine.GoalMachine;
import edu.fudan.se.goalmachine.TaskMachine;
import edu.fudan.se.goalmodel.GoalModel;
import edu.fudan.se.goalmodel.GoalModelManager;
import edu.fudan.se.userMes.UserMessage;
//import edu.fudan.se.goalmodel.GoalModelController;
import edu.fudan.se.userMes.UserTask;

import android.app.Application;
import android.content.SharedPreferences;

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

		GoalModel testGM = newTestGoalModel(); // 一个完全本地没有委托的

		GoalModel gm = newGoalModel(); // 需要把bob委托出去的
		GoalModel gm_bob = newGoalModel_delegateBob(); // 接受委托的bob

		goalModelManager = new GoalModelManager();
		goalModelManager.addGoalModel(testGM);
		goalModelManager.addGoalModel(gm);
		goalModelManager.addGoalModel(gm_bob);
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

	/**
	 * 目前测试时只用这个方法添加了一个测试用的GoalModel
	 * 
	 * @return 测试用的GoalModel
	 */
	private GoalModel newGoalModel() {

		GoalModel goalModel = new GoalModel("myGoal");

		GoalMachine myGoal = new GoalMachine("myGoal", 0, 1, null, 0,false) {

			@Override
			public void checkPreCondition() {
				// TODO Auto-generated method stub

			}

			@Override
			public void checkPostCondition() {
				// TODO Auto-generated method stub

			}

			@Override
			public void checkInvariantCondition() {
				// TODO Auto-generated method stub

			}

			@Override
			public void checkContextCondition() {
				// TODO Auto-generated method stub

			}

			@Override
			public void checkCommitmentCondition() {
				Date nowTime = new Date();
				long runningTime = nowTime.getTime()
						- this.getStartTime().getTime(); // 得到的差值单位是毫秒
				// TODO 这里记得可能要在*1000前面加上*60，因为现在设的等待时间限制单位为秒，实际运行时可能需要设置为分钟
				if (runningTime > (this.getTimeLimit() * 1000)) { // 超时
					this.getCommitmentCondition().setSatisfied(false);
				} else {
					this.getCommitmentCondition().setSatisfied(true);
				}
			}
		};

		GoalMachine alice = new GoalMachine("alice", 0, 0, myGoal, 1,false) {

			@Override
			public void checkPreCondition() {
				// TODO Auto-generated method stub

			}

			@Override
			public void checkPostCondition() {
				// TODO Auto-generated method stub

			}

			@Override
			public void checkInvariantCondition() {
				// TODO Auto-generated method stub

			}

			@Override
			public void checkContextCondition() {
				// TODO Auto-generated method stub

			}

			@Override
			public void checkCommitmentCondition() {
				// TODO Auto-generated method stub

			}
		};
		GoalMachine bob = new GoalMachine("bob", 1, -1, myGoal, 1,true) {

			@Override
			public void checkPreCondition() {
				// TODO Auto-generated method stub

			}

			@Override
			public void checkPostCondition() {
				// TODO Auto-generated method stub

			}

			@Override
			public void checkInvariantCondition() {
				// TODO Auto-generated method stub

			}

			@Override
			public void checkContextCondition() {
				// TODO Auto-generated method stub

			}

			@Override
			public void checkCommitmentCondition() {
				// TODO Auto-generated method stub

			}
		};

		TaskMachine aliceChild_1 = new TaskMachine("aliceChild_1", alice, 2,
				true) {

			@Override
			public void checkPreCondition() {
				// TODO Auto-generated method stub

			}

			@Override
			public void checkPostCondition() {
				// TODO Auto-generated method stub

			}

			@Override
			public void checkInvariantCondition() {
				// TODO Auto-generated method stub

			}

			@Override
			public void checkContextCondition() {
				if (true) {
					this.getContextCondition().setSatisfied(false);
				}

			}

			@Override
			public void checkCommitmentCondition() {
				// TODO Auto-generated method stub

			}

			@Override
			public void executingDo_once() {
				System.out
						.println("aliceChild_1 is doing his executingDoAction...");
			}
		};

		TaskMachine aliceChild_2 = new TaskMachine("aliceChild_2", alice, 2,
				true) {

			@Override
			public void checkPreCondition() {
				// TODO Auto-generated method stub

			}

			@Override
			public void checkPostCondition() {
				// TODO Auto-generated method stub

			}

			@Override
			public void checkInvariantCondition() {
				// TODO Auto-generated method stub

			}

			@Override
			public void checkContextCondition() {
				if (true) {
					this.getContextCondition().setSatisfied(false);
				}
			}

			@Override
			public void checkCommitmentCondition() {
				// TODO Auto-generated method stub

			}

			@Override
			public void executingDo_once() {
				System.out
						.println("aliceChild_2 is doing his executingDoAction...");
			}
		};

		myGoal.addSubElement(alice, 1);
		myGoal.addSubElement(bob, 1);

		alice.addSubElement(aliceChild_1, 1);
		alice.addSubElement(aliceChild_2, 1);

		goalModel.setDescription("This is the description of the goal model!");
		goalModel.setRootGoal(myGoal);
		goalModel.addElementMachine(myGoal);
		goalModel.addElementMachine(alice);
		goalModel.addElementMachine(aliceChild_1);
		goalModel.addElementMachine(aliceChild_2);
		goalModel.addElementMachine(bob);

		return goalModel;

	}

	private GoalModel newGoalModel_delegateBob() {

		GoalModel goalModel = new GoalModel("bob");

		GoalMachine bob = new GoalMachine("bob", 1, -1, null, 1,false) {

			@Override
			public void checkPreCondition() {
				// TODO Auto-generated method stub

			}

			@Override
			public void checkPostCondition() {
				// TODO Auto-generated method stub

			}

			@Override
			public void checkInvariantCondition() {
				// TODO Auto-generated method stub

			}

			@Override
			public void checkContextCondition() {
				// TODO Auto-generated method stub

			}

			@Override
			public void checkCommitmentCondition() {
				// TODO Auto-generated method stub

			}
		};

		TaskMachine bobChild_1 = new TaskMachine("bobChild_1", bob, 2, true) {

			@Override
			public void checkPreCondition() {
				// TODO Auto-generated method stub

			}

			@Override
			public void checkPostCondition() {
				// TODO Auto-generated method stub

			}

			@Override
			public void checkInvariantCondition() {
				// TODO Auto-generated method stub

			}

			@Override
			public void checkContextCondition() {
				if (true) {
					this.getContextCondition().setSatisfied(false);
				}

			}

			@Override
			public void checkCommitmentCondition() {
				// TODO Auto-generated method stub

			}

			@Override
			public void executingDo_once() {
				System.out
						.println("bobChild_1 is doing his executingDoAction...");
			}
		};

		TaskMachine bobChild_2 = new TaskMachine("bobChild_2", bob, 2, true) {

			@Override
			public void checkPreCondition() {
				if (true) {
					this.getPreCondition().setSatisfied(false);
				}

			}

			@Override
			public void checkPostCondition() {
				// TODO Auto-generated method stub

			}

			@Override
			public void checkInvariantCondition() {
				// TODO Auto-generated method stub

			}

			@Override
			public void checkContextCondition() {
				if (true) {
					this.getContextCondition().setSatisfied(false);
				}

			}

			@Override
			public void checkCommitmentCondition() {
				// TODO Auto-generated method stub

			}

			@Override
			public void executingDo_once() {
				System.out
						.println("bobChild_2 is doing his executingDoAction...");
			}
		};
		TaskMachine bobChild_3 = new TaskMachine("bobChild_3", bob, 2, true) {

			@Override
			public void checkPreCondition() {
				// TODO Auto-generated method stub

			}

			@Override
			public void checkPostCondition() {
				if (true) {
					this.getPostCondition().setSatisfied(false);
				}

			}

			@Override
			public void checkInvariantCondition() {
				// TODO Auto-generated method stub

			}

			@Override
			public void checkContextCondition() {
				if (true) {
					this.getContextCondition().setSatisfied(false);
				}

			}

			@Override
			public void checkCommitmentCondition() {
				// TODO Auto-generated method stub

			}

			@Override
			public void executingDo_once() {
				System.out
						.println("bobChild_3 is doing his executingDoAction...");
			}
		};
		// root.setCommitmentCondition(new Condition("COMMITMENT"));
		// root.setTimeLimit(10); // 10S

		// aliceChild_1.setContextCondition(new Condition("CONTEXT"));

		bobChild_1.setContextCondition(new Condition("CONTEXT"));

		bobChild_2.setPreCondition(new Condition("PRE", false));
		bobChild_2.setWaitingTimeLimit(5);// 5s

		bobChild_3.setPostCondition(new Condition("POST"));

		bob.addSubElement(bobChild_1, 1);
		bob.addSubElement(bobChild_2, 2);
		bob.addSubElement(bobChild_3, 3);

		goalModel.setDescription("This is the description of the goal model!");
		goalModel.setRootGoal(bob);

		goalModel.addElementMachine(bob);
		goalModel.addElementMachine(bobChild_1);
		goalModel.addElementMachine(bobChild_2);
		goalModel.addElementMachine(bobChild_3);

		return goalModel;

	}

	private GoalModel newTestGoalModel() {

		GoalModel goalModel = new GoalModel("my goal model test");

		GoalMachine myGoal = new GoalMachine("my goal model test", 0, 1, null,
				0,false) {

			@Override
			public void checkPreCondition() {
				// TODO Auto-generated method stub

			}

			@Override
			public void checkPostCondition() {
				// TODO Auto-generated method stub

			}

			@Override
			public void checkInvariantCondition() {
				// TODO Auto-generated method stub

			}

			@Override
			public void checkContextCondition() {
				// TODO Auto-generated method stub

			}

			@Override
			public void checkCommitmentCondition() {
				Date nowTime = new Date();
				long runningTime = nowTime.getTime()
						- this.getStartTime().getTime(); // 得到的差值单位是毫秒
				// TODO 这里记得可能要在*1000前面加上*60，因为现在设的等待时间限制单位为秒，实际运行时可能需要设置为分钟
				if (runningTime > (this.getTimeLimit() * 1000)) { // 超时
					this.getCommitmentCondition().setSatisfied(false);
				} else {
					this.getCommitmentCondition().setSatisfied(true);
				}
			}
		};

		GoalMachine alice = new GoalMachine("alice", 0, 0, myGoal, 1,false) {

			@Override
			public void checkPreCondition() {
				// TODO Auto-generated method stub

			}

			@Override
			public void checkPostCondition() {
				// TODO Auto-generated method stub

			}

			@Override
			public void checkInvariantCondition() {
				// TODO Auto-generated method stub

			}

			@Override
			public void checkContextCondition() {
				// TODO Auto-generated method stub

			}

			@Override
			public void checkCommitmentCondition() {
				// TODO Auto-generated method stub

			}
		};
		GoalMachine bob = new GoalMachine("bob", 1, -1, myGoal, 1,false) {

			@Override
			public void checkPreCondition() {
				// TODO Auto-generated method stub

			}

			@Override
			public void checkPostCondition() {
				// TODO Auto-generated method stub

			}

			@Override
			public void checkInvariantCondition() {
				// TODO Auto-generated method stub

			}

			@Override
			public void checkContextCondition() {
				// TODO Auto-generated method stub

			}

			@Override
			public void checkCommitmentCondition() {
				// TODO Auto-generated method stub

			}
		};

		TaskMachine aliceChild_1 = new TaskMachine("aliceChild_1", alice, 2,
				true) {

			@Override
			public void checkPreCondition() {
				// TODO Auto-generated method stub

			}

			@Override
			public void checkPostCondition() {
				// TODO Auto-generated method stub

			}

			@Override
			public void checkInvariantCondition() {
				// TODO Auto-generated method stub

			}

			@Override
			public void checkContextCondition() {
				if (true) {
					this.getContextCondition().setSatisfied(false);
				}

			}

			@Override
			public void checkCommitmentCondition() {
				// TODO Auto-generated method stub

			}

			@Override
			public void executingDo_once() {
				System.out
						.println("aliceChild_1 is doing his executingDoAction...");
			}
		};

		TaskMachine aliceChild_2 = new TaskMachine("aliceChild_2", alice, 2,
				true) {

			@Override
			public void checkPreCondition() {
				// TODO Auto-generated method stub

			}

			@Override
			public void checkPostCondition() {
				// TODO Auto-generated method stub

			}

			@Override
			public void checkInvariantCondition() {
				// TODO Auto-generated method stub

			}

			@Override
			public void checkContextCondition() {
				if (true) {
					this.getContextCondition().setSatisfied(false);
				}
			}

			@Override
			public void checkCommitmentCondition() {
				// TODO Auto-generated method stub

			}

			@Override
			public void executingDo_once() {
				System.out
						.println("aliceChild_2 is doing his executingDoAction...");
			}
		};

		TaskMachine bobChild_1 = new TaskMachine("bobChild_1", bob, 2, true) {

			@Override
			public void checkPreCondition() {
				// TODO Auto-generated method stub

			}

			@Override
			public void checkPostCondition() {
				// TODO Auto-generated method stub

			}

			@Override
			public void checkInvariantCondition() {
				// TODO Auto-generated method stub

			}

			@Override
			public void checkContextCondition() {
				if (true) {
					this.getContextCondition().setSatisfied(false);
				}

			}

			@Override
			public void checkCommitmentCondition() {
				// TODO Auto-generated method stub

			}

			@Override
			public void executingDo_once() {
				System.out
						.println("bobChild_1 is doing his executingDoAction...");
			}
		};

		TaskMachine bobChild_2 = new TaskMachine("bobChild_2", bob, 2, true) {

			@Override
			public void checkPreCondition() {
				if (true) {
					this.getPreCondition().setSatisfied(false);
				}

			}

			@Override
			public void checkPostCondition() {
				// TODO Auto-generated method stub

			}

			@Override
			public void checkInvariantCondition() {
				// TODO Auto-generated method stub

			}

			@Override
			public void checkContextCondition() {
				if (true) {
					this.getContextCondition().setSatisfied(false);
				}

			}

			@Override
			public void checkCommitmentCondition() {
				// TODO Auto-generated method stub

			}

			@Override
			public void executingDo_once() {
				System.out
						.println("bobChild_2 is doing his executingDoAction...");
			}
		};
		TaskMachine bobChild_3 = new TaskMachine("bobChild_3", bob, 2, true) {

			@Override
			public void checkPreCondition() {
				// TODO Auto-generated method stub

			}

			@Override
			public void checkPostCondition() {
				if (true) {
					this.getPostCondition().setSatisfied(false);
				}

			}

			@Override
			public void checkInvariantCondition() {
				// TODO Auto-generated method stub

			}

			@Override
			public void checkContextCondition() {
				if (true) {
					this.getContextCondition().setSatisfied(false);
				}

			}

			@Override
			public void checkCommitmentCondition() {
				// TODO Auto-generated method stub

			}

			@Override
			public void executingDo_once() {
				System.out
						.println("bobChild_3 is doing his executingDoAction...");
			}
		};

		bobChild_1.setContextCondition(new Condition("CONTEXT"));

		bobChild_2.setPreCondition(new Condition("PRE", false));
		bobChild_2.setWaitingTimeLimit(5);// 5s

		bobChild_3.setPostCondition(new Condition("POST"));

		myGoal.addSubElement(alice, 1);
		myGoal.addSubElement(bob, 1);

		alice.addSubElement(aliceChild_1, 1);
		alice.addSubElement(aliceChild_2, 1);

		bob.addSubElement(bobChild_1, 1);
		bob.addSubElement(bobChild_2, 2);
		bob.addSubElement(bobChild_3, 3);

		goalModel.setDescription("This is the description of the goal model!");
		goalModel.setRootGoal(myGoal);
		goalModel.addElementMachine(myGoal);
		goalModel.addElementMachine(alice);
		goalModel.addElementMachine(aliceChild_1);
		goalModel.addElementMachine(aliceChild_2);
		goalModel.addElementMachine(bob);
		goalModel.addElementMachine(bobChild_1);
		goalModel.addElementMachine(bobChild_2);
		goalModel.addElementMachine(bobChild_3);

		return goalModel;

	}

}
