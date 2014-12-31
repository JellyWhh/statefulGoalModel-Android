/**
 * 
 */
package edu.fudan.se.userMes;

import edu.fudan.se.goalmachine.TaskMachine;
import edu.fudan.se.goalmodel.GoalModel;

/**
 * 用户要做的任务
 * 
 * @author whh
 * 
 */
public class UserTask {

	private GoalModel goalModel;
	private TaskMachine taskMachine;
	private boolean isDone; // 用户是否做过了
	
	public UserTask(GoalModel goalModel,TaskMachine taskMachine, boolean isDone){
		this.goalModel = goalModel;
		this.taskMachine = taskMachine;
		this.isDone = isDone;
	}

	public TaskMachine getTaskMachine() {
		return taskMachine;
	}

	public void setTaskMachine(TaskMachine taskMachine) {
		this.taskMachine = taskMachine;
	}

	public boolean isDone() {
		return isDone;
	}

	public void setDone(boolean isDone) {
		this.isDone = isDone;
	}

	public GoalModel getGoalModel() {
		return goalModel;
	}

	public void setGoalModel(GoalModel goalModel) {
		this.goalModel = goalModel;
	}

}
