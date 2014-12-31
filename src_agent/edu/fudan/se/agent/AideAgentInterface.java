package edu.fudan.se.agent;

import edu.fudan.se.goalmachine.TaskMachine;
import edu.fudan.se.goalmodel.GoalModel;

public interface AideAgentInterface {

	public void startGoalModel(GoalModel goalModel);

	public void stopGoalModel(GoalModel goalModel);

	public void suspendGoalModel(GoalModel goalModel);

	public void resumeGoalModel(GoalModel goalModel);

	public void resetGoalModel(GoalModel goalModel);

	public void endTaskMachine(TaskMachine taskMachine,String mes);
}
