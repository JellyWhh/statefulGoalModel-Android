/**
 * 
 */
package edu.fudan.se.goalmodel;

import java.util.ArrayList;

import edu.fudan.se.goalmachine.ElementMachine;
import edu.fudan.se.goalmachine.SGMMessage;
import edu.fudan.se.log.Log;

/**
 * 对一个<code>GoalModel</code>执行各种控制命令
 * @author whh
 *
 */
public class GoalModelController {
	
//	private ArrayList<GoalModel> goalModelList;
//	
//	public GoalModelController(ArrayList<GoalModel> goalModelList){
//		this.goalModelList = goalModelList;
//	}
//	
//	public void startGoalModel(String goalModelName){
//		boolean suc = false;
//		for(GoalModel gm : goalModelList){
//			if(gm.getName().equals(goalModelName)){
//				suc = true;
//				start(gm);
//				break;
//			}
//		}
//		if(suc)
//			Log.i("MY_LOG", "Start Goal Model Successfully");
//		else
//			Log.i("MY_LOG", "Failed to Start Goal Model");
//	}
	
	/**
	 * start这个goal model里面的所有element machines
	 * @param goalModel 要start的goal model
	 */
	public void start(GoalModel goalModel){
		Log.logDebug("GoalModelController:" + goalModel.getName(), "start()", "init.");
		if (goalModel.getElementMachines() != null && goalModel.getElementMachines().size() != 0) {
			for (ElementMachine elementMachine : goalModel.getElementMachines()) {
				Thread thread = new Thread(elementMachine);
				thread.start();
			}
			// 然后给root goal发送激活消息
			SGMMessage msg = new SGMMessage("TOROOT", "UI",
					goalModel.getRootGoal().getName(), "ACTIVATE");
			if (goalModel.getRootGoal().getMsgPool().offer(msg)) {
				Log.logMessage(msg, true);
				Log.logDebug(
						"GoalModelController:" + goalModel.getName(),
						"start()",
						"UI thread send a ACTIVATE msg to "
								+ goalModel.getRootGoal().getName() + " succeed!");
			} else {
				Log.logMessage(msg, false);
				Log.logError(
						"GoalModelController:" + goalModel.getName(),
						"start()",
						"UI thread send a ACTIVATE msg to "
								+ goalModel.getRootGoal().getName() + " error!");
			}
		} else {
			Log.logError("GoalModelController:" + goalModel.getName(), "start()",
					"elementMachines is null or its size is 0!");
		}
		goalModel.setState("STARTED");
	}
	
	/**
	 * stop这个goal model，只需要给这个goal model里面的root goal发送STOP消息即可
	 * @param goalModel 要stop的goal model
	 */
	public void stop(GoalModel goalModel){
		Log.logDebug("goal model:" + goalModel.getName(), "stop()", "init.");
		if (goalModel.getRootGoal() != null) {
			SGMMessage msg = new SGMMessage("TOROOT", "UI",
					goalModel.getRootGoal().getName(), "STOP");
			if (goalModel.getRootGoal().getMsgPool().offer(msg)) {
				Log.logMessage(msg, true);
				Log.logDebug(
						"goal model:" + goalModel.getName(),
						"stop()",
						"UI thread send a STOP msg to "
								+ goalModel.getRootGoal().getName() + " succeed!");
			} else {
				Log.logMessage(msg, false);
				Log.logError(
						"goal model:" + goalModel.getName(),
						"stop()",
						"UI thread send a STOP msg to "
								+ goalModel.getRootGoal().getName() + " error!");
			}

		} else {
			Log.logError("goal model:" + goalModel.getName(), "stop()",
					"rootGoal is null!");
		}
		goalModel.setState("STOPED");
	}

}
