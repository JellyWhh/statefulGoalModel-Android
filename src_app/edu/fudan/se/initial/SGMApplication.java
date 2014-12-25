/**
 * 
 */
package edu.fudan.se.initial;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import edu.fudan.se.goalmachine.Condition;
import edu.fudan.se.goalmachine.GoalMachine;
import edu.fudan.se.goalmachine.SGMMessage;
import edu.fudan.se.goalmachine.TaskMachine;
import edu.fudan.se.goalmodel.GoalModel;

import android.app.Application;

/**
 * 重写Application，主要重写里面的onCreate方法，设置并初始化一些全局变量
 * 
 * @author whh
 * 
 */
public class SGMApplication extends Application implements Serializable {

	private static final long serialVersionUID = 1L;

	private ArrayList<GoalModel> goalModelList;

	@Override
	public void onCreate() {
		super.onCreate();
		initialData();
	}

	private void initialData() {
		this.goalModelList = new ArrayList<>();

		this.goalModelList.add(newGoalModel());

	}

	public ArrayList<GoalModel> getGoalModelList() {
		return this.goalModelList;
	}

	private GoalModel newGoalModel() {

		GoalModel goalModel = new GoalModel("my goal model");

		GoalMachine root = new GoalMachine("root", 0, 1, null, 0) {

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

		GoalMachine alice = new GoalMachine("alice", 0, 0, root, 1) {

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
		GoalMachine bob = new GoalMachine("bob", 1, -1, root, 1) {

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

		TaskMachine aliceChild_1 = new TaskMachine("aliceChild_1", alice, 2) {

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
			public void executingDo(SGMMessage msg) {
				System.out
						.println("aliceChild_1 is doing his executingDoAction...");
				this.executingDo_waitingEnd(msg);
			}
		};

		TaskMachine aliceChild_2 = new TaskMachine("aliceChild_2", alice, 2) {

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
			public void executingDo(SGMMessage msg) {
				System.out
						.println("aliceChild_2 is doing his executingDoAction...");
				this.executingDo_waitingEnd(msg);

			}
		};

		TaskMachine bobChild_1 = new TaskMachine("bobChild_1", bob, 2) {

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
			public void executingDo(SGMMessage msg) {
				System.out
						.println("bobChild_1 is doing his executingDoAction...");
				this.executingDo_waitingEnd(msg);

			}
		};

		TaskMachine bobChild_2 = new TaskMachine("bobChild_2", bob, 2) {

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
			public void executingDo(SGMMessage msg) {
				System.out
						.println("bobChild_2 is doing his executingDoAction...");
				this.executingDo_waitingEnd(msg);

			}
		};
		TaskMachine bobChild_3 = new TaskMachine("bobChild_3", bob, 2) {

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
			public void executingDo(SGMMessage msg) {
				System.out
						.println("bobChild_3 is doing his executingDoAction...");
				this.executingDo_waitingEnd(msg);
			}
		};
		// root.setCommitmentCondition(new Condition("COMMITMENT"));
		// root.setTimeLimit(10); // 10S

		// aliceChild_1.setContextCondition(new Condition("CONTEXT"));

		bobChild_1.setContextCondition(new Condition("CONTEXT"));

		bobChild_2.setPreCondition(new Condition("PRE", false));
		bobChild_2.setWaitingTimeLimit(5);// 5s

		bobChild_3.setPostCondition(new Condition("POST"));

		root.addSubElement(alice, 1);
		root.addSubElement(bob, 1);

		alice.addSubElement(aliceChild_1, 1);
		alice.addSubElement(aliceChild_2, 1);

		bob.addSubElement(bobChild_1, 1);
		bob.addSubElement(bobChild_2, 2);
		bob.addSubElement(bobChild_3, 3);

		goalModel.setDescription("This is the description of the goal model!");
		goalModel.setRootGoal(root);
		goalModel.addElementMachine(root);
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
