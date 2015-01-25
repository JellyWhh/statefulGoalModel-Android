/**
 * 
 */
package edu.fudan.se.messageFragment;

import jade.core.MicroRuntime;
import jade.wrapper.ControllerException;
import jade.wrapper.StaleProxyException;

import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import edu.fudan.agent.support.ACLMC_DelegateTask;
import edu.fudan.se.R;
import edu.fudan.se.agent.AideAgentInterface;
import edu.fudan.se.goalmachine.message.MesBody_Mes2Manager;
import edu.fudan.se.goalmachine.message.MesHeader_Mes2Manger;
import edu.fudan.se.goalmachine.message.SGMMessage;
import edu.fudan.se.goalmodel.EncodeDecodeRequestData;
import edu.fudan.se.goalmodel.RequestData;
import edu.fudan.se.initial.SGMApplication;
import edu.fudan.se.support.TakePictureActivity;
import edu.fudan.se.userMes.UserInputTextTask;
import edu.fudan.se.userMes.UserShowContentTask;
import edu.fudan.se.userMes.UserTakePictureTask;
import edu.fudan.se.userMes.UserTask;

/**
 * @author whh
 * 
 */
public class TaskFragment extends ListFragment {

	private SGMApplication application; // 获取应用程序，以得到里面的全局变量
	private UserTaskAdapter adapter;

	private AideAgentInterface aideAgentInterface; // agent interface

	// private ProgressDialog progressDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		application = (SGMApplication) getActivity().getApplication();

		try {
			Thread.sleep(2 * 1000);
			aideAgentInterface = MicroRuntime.getAgent(
					application.getAgentNickname()).getO2AInterface(
					AideAgentInterface.class);
		} catch (StaleProxyException e) {
			Log.e("MessageFragment", "StaleProxyException");
			e.printStackTrace();
		} catch (ControllerException e) {
			Log.e("MessageFragment", "ControllerException");
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		adapter = new UserTaskAdapter(getActivity(),
				R.layout.listview_usertask, application.getUserTaskList(),
				aideAgentInterface);

		setListAdapter(adapter);

		getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
	}

}

class UserTaskAdapter extends ArrayAdapter<UserTask> {
	
	private int mResource;
	private Context mContext;
	private LayoutInflater mInflater;
	private List<UserTask> mObjects;
	private AideAgentInterface aideAgentInterface; // agent interface

	// private ProgressDialog progressDialog;

	public UserTaskAdapter(Context context, int resource,
			List<UserTask> objects, AideAgentInterface aideAgentInterface) {
		super(context, resource, objects);
		init(context, resource, objects, aideAgentInterface);
	}

	private void init(Context context, int resource, List<UserTask> objects,
			AideAgentInterface aideAgentInterface) {
		this.mContext = context;
		this.mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.mResource = resource;
		this.mObjects = objects;
		this.aideAgentInterface = aideAgentInterface;
		// this.progressDialog = progressDialog;
	}

	@Override
	public int getCount() {
		return this.mObjects.size();
	}

	@Override
	public UserTask getItem(int position) {
		return this.mObjects.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return createViewFromResource(position, convertView, parent, mResource);
	}

	private View createViewFromResource(int position, View convertView,
			ViewGroup parent, int resource) {
		ViewHolder holder;

		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(resource, parent, false);

			holder.taskLayout = (LinearLayout) convertView
					.findViewById(R.id.ll_tasklayout);
			holder.time = (TextView) convertView.findViewById(R.id.tv_taskTime);
			holder.description = (TextView) convertView
					.findViewById(R.id.tv_taskDescription);
			holder.done = (Button) convertView.findViewById(R.id.bt_taskDone);
			holder.quit = (Button) convertView.findViewById(R.id.bt_taskQuit);

			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		// 下面部分不可缺少，是设置每个item具体显示的地方！
		final UserTask usertask = getItem(position);
		holder.time.setText(usertask.getTime());

		holder.done.setOnClickListener(new UserTaskDoneListener(usertask));
		holder.quit.setOnClickListener(new UserTaskQuitListener(usertask));

		// if (usertask instanceof UserDelegateInTask) { // 如果是需要用户选择委托去向的任务
		// holder.done.setText("Accept");
		// } else
		if (usertask instanceof UserShowContentTask) {// 展示内容的user task
			holder.done.setText("Show");
		} else if (usertask instanceof UserTakePictureTask) {// 让用户拍照的task
			holder.done.setText("Camera");
		} else if (usertask instanceof UserInputTextTask) {// 让用户输入一段文本的task
			holder.done.setText("Input");
		} else {// 普通的user task
			holder.done.setText("Done");
		}

		// 必须把setClickable放在setOnClickListener后面，否则不起作用
		if (usertask.isDone()) { // 用户已经完成的
			holder.taskLayout.setBackgroundColor(mContext.getResources()
					.getColor(R.color.done_grey));
			holder.done.setClickable(false);
			holder.quit.setClickable(false);
			holder.done.setTextColor(mContext.getResources().getColor(
					R.color.unclickable_grey));
			holder.quit.setTextColor(mContext.getResources().getColor(
					R.color.unclickable_grey));
		} else {
			// if (usertask instanceof UserDelegateInTask) {
			// holder.taskLayout.setBackgroundColor(mContext.getResources()
			// .getColor(R.color.nodone_green));
			// } else
			if (usertask instanceof UserShowContentTask) {
				holder.taskLayout.setBackgroundColor(mContext.getResources()
						.getColor(R.color.nodone_pink));
			} else if (usertask instanceof UserTakePictureTask) {
				holder.taskLayout.setBackgroundColor(mContext.getResources()
						.getColor(R.color.nodone_purple));
			} else if (usertask instanceof UserInputTextTask) {
				holder.taskLayout.setBackgroundColor(mContext.getResources()
						.getColor(R.color.nodone_yellow));
			} else {
				holder.taskLayout.setBackgroundColor(mContext.getResources()
						.getColor(R.color.nodone_white));
			}
			holder.done.setClickable(true);
			holder.quit.setClickable(true);
			holder.done.setTextColor(mContext.getResources().getColor(
					R.color.clickable_black));
			holder.quit.setTextColor(mContext.getResources().getColor(
					R.color.clickable_black));

		}

		holder.description.setText(usertask.getDescription());

		return convertView;
	}

	/**
	 * 用户点击done按钮时的监听器，根据不同的UserTask类型有不同的响应
	 * 
	 * @author whh
	 * 
	 */
	private class UserTaskDoneListener implements OnClickListener {
		private UserTask userTask;

		public UserTaskDoneListener(UserTask userTask) {
			this.userTask = userTask;
		}

		@Override
		public void onClick(View v) {
			// 让用户输入文本的task
			if (userTask instanceof UserInputTextTask) {
				showInputTextDialog(userTask);
			}
			// 展示内容的user task
			else if (userTask instanceof UserShowContentTask) {
				showContentDialog(userTask);
			}
			// 让用户拍照的task
			else if (userTask instanceof UserTakePictureTask) {
				userTask.setDone(true);
				notifyDataSetChanged();

				Intent intent = new Intent();
				intent.setClass(mContext, TakePictureActivity.class);
				intent.putExtra("fromAgentName", userTask.getFromAgentName());
				intent.putExtra("goalmodelname", userTask.getGoalModelName());
				intent.putExtra("elementname", userTask.getElementName());
				intent.putExtra("requestDataName", userTask.getRequestDataName());
				mContext.startActivity(intent);
			}
			// 普通的user task
			else {
				ACLMC_DelegateTask aclmc_DelegateTask = new ACLMC_DelegateTask(
						ACLMC_DelegateTask.DTHeader.DTBACK, null,
						userTask.getFromAgentName(),
						userTask.getGoalModelName(), userTask.getElementName());

				aclmc_DelegateTask.setDone(true);// 完成了
				aideAgentInterface.sendMesToExternalAgent(aclmc_DelegateTask);

				// aideAgentInterface.sendMesToManager(new SGMMessage(
				// MesHeader_Mes2Manger.LOCAL_AGENT_MESSAGE, null, null,
				// null, null, userTask.getGoalModelName(), userTask
				// .getElementName(), MesBody_Mes2Manager.EndTE));
				userTask.setDone(true);
				notifyDataSetChanged();
			}
		}

	}

	/**
	 * 用户点击quit按钮时的监听器，只有委托出去的任务是结束一个goal machine，其余都是结束一个task machine
	 * 
	 * @author whh
	 * 
	 */
	private class UserTaskQuitListener implements OnClickListener {
		private UserTask userTask;

		public UserTaskQuitListener(UserTask userTask) {
			this.userTask = userTask;
		}

		@Override
		public void onClick(View v) {

			if (userTask instanceof UserShowContentTask) {
				aideAgentInterface.sendMesToManager(new SGMMessage(
						MesHeader_Mes2Manger.LOCAL_AGENT_MESSAGE, userTask
								.getGoalModelName(), null, userTask
								.getElementName(), MesBody_Mes2Manager.QuitTE));
			} else {// 其余的都是委托任务
				ACLMC_DelegateTask aclmc_DelegateTask = new ACLMC_DelegateTask(
						ACLMC_DelegateTask.DTHeader.DTBACK, null,
						userTask.getFromAgentName(),
						userTask.getGoalModelName(), userTask.getElementName());

				aclmc_DelegateTask.setDone(false);// 没有完成
				aideAgentInterface.sendMesToExternalAgent(aclmc_DelegateTask);
			}

			userTask.setDone(true);
			notifyDataSetChanged();
		}

	}

	private class ViewHolder {
		LinearLayout taskLayout;
		TextView time;
		TextView description;
		Button done;
		Button quit;
	}

	/**
	 * 让用户输入文本的task
	 * 
	 * @param userTask
	 *            UserInputTextTask
	 */
	private void showInputTextDialog(final UserTask userTask) {
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setTitle("Input:");
		builder.setIcon(android.R.drawable.ic_dialog_info);

		View view = LayoutInflater.from(mContext).inflate(
				R.layout.dialog_userinput, null);
		final EditText editText = (EditText) view
				.findViewById(R.id.et_userinput);

		builder.setView(view);
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				String userInput = editText.getText().toString();

				ACLMC_DelegateTask aclmc_DelegateTask = new ACLMC_DelegateTask(
						ACLMC_DelegateTask.DTHeader.DTBACK, null, userTask
								.getFromAgentName(), userTask
								.getGoalModelName(), userTask.getElementName());
				aclmc_DelegateTask.setDone(true);

				RequestData requestData = new RequestData(userTask
						.getRequestDataName(), "Text");
				requestData.setContent(userInput.getBytes());
				
				aclmc_DelegateTask.setRequestData(requestData);
				aideAgentInterface.sendMesToExternalAgent(aclmc_DelegateTask);

				// SGMMessage msg = new SGMMessage(
				// MesHeader_Mes2Manger.LOCAL_AGENT_MESSAGE, null, null,
				// null, null, userTask.getGoalModelName(), userTask
				// .getElementName(),
				// MesBody_Mes2Manager.ServiceExecutingDone);
				// RequestData requestData = new RequestData("Text");
				// requestData.setContent(userInput.getBytes());
				// msg.setContent(requestData);
				//
				// aideAgentInterface.sendMesToManager(msg);

				userTask.setDone(true);
				notifyDataSetChanged();

				dialog.cancel();
			}

		});

		AlertDialog dialog = builder.create();
		dialog.show();
	}

	/**
	 * 创建一个显示RequestData的对话框，显示的可以是Text或者Image
	 * 
	 * @param requestData
	 *            要显示的requestData
	 */
	private void showContentDialog(final UserTask userTask) {

		RequestData requestData = ((UserShowContentTask) userTask).getRequestData();

		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setTitle("Content:");
		builder.setIcon(android.R.drawable.ic_dialog_info);

		if (requestData.getContentType().equals("Text")) {
			builder.setMessage(EncodeDecodeRequestData.decodeToText(requestData
					.getContent()));
		} else if (requestData.getContentType().equals("Image")) {
			View view = LayoutInflater.from(mContext).inflate(
					R.layout.dialog_showcontent, null);
			ImageView imageView = (ImageView) view
					.findViewById(R.id.iv_dialog_content);
			imageView.setImageBitmap(EncodeDecodeRequestData
					.decodeToBitmap(requestData.getContent()));
			builder.setView(view);
		}

		builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

				// 只要点击了show按钮就表示这个“展示任务”完成了
				aideAgentInterface.sendMesToManager(new SGMMessage(
						MesHeader_Mes2Manger.LOCAL_AGENT_MESSAGE, userTask
								.getGoalModelName(), null, userTask
								.getElementName(), MesBody_Mes2Manager.EndTE));
				userTask.setDone(true);
				notifyDataSetChanged();

				dialog.cancel();
			}
		});

		AlertDialog dialog = builder.create();
		dialog.show();
	}

}
