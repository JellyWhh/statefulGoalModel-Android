/**
 * 
 */
package edu.fudan.se.messageFragment;

import jade.core.MicroRuntime;
import jade.wrapper.ControllerException;
import jade.wrapper.StaleProxyException;

import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import edu.fudan.se.R;
import edu.fudan.se.agent.AideAgentInterface;
import edu.fudan.se.goalmachine.message.MesBody_Mes2Manager;
import edu.fudan.se.goalmachine.message.MesHeader_Mes2Manger;
import edu.fudan.se.goalmachine.message.SGMMessage;
import edu.fudan.se.goalmodel.RequestData;
import edu.fudan.se.initial.SGMApplication;
import edu.fudan.se.userMes.UserDelegateOutTask;
import edu.fudan.se.userMes.UserTask;

/**
 * @author whh
 * 
 */
public class TaskFragment extends ListFragment {

	private SGMApplication application; // 获取应用程序，以得到里面的全局变量
	private UserTaskAdapter adapter;

	private AideAgentInterface aideAgentInterface; // agent interface
	private ProgressDialog progressDialog;

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

		progressDialog = onCreateProgressDialog();

		adapter = new UserTaskAdapter(getActivity(),
				R.layout.listview_usertask, application.getUserTaskList(),
				aideAgentInterface, progressDialog);

		adapter.notifyDataSetChanged();

		// 设置接受agent发来的friends列表的receiver
		MyFriendsReceiver receiver = new MyFriendsReceiver();
		IntentFilter refreshChatFilter = new IntentFilter();
		refreshChatFilter.addAction("jade.delegate.FRIENDS");
		getActivity().registerReceiver(receiver, refreshChatFilter);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		setListAdapter(adapter);
		adapter.notifyDataSetChanged();

		getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
	}

	/**
	 * 创建一个进度条对话框
	 * 
	 * @return ProgressDialog
	 */
	private ProgressDialog onCreateProgressDialog() {
		ProgressDialog dialog = new ProgressDialog(getActivity());
		dialog.setMessage("Loading friends. Please wait...");
		return dialog;
	}

	/**
	 * 创建一个显示可委托对象的单选对话框
	 * 
	 * @return Dialog
	 */
	private Dialog onCreateFriendsDialog(final String[] friends,
			final String goalModelName, final String elementName) {

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		final ChoiceOnClickListener choiceListener = new ChoiceOnClickListener();
		builder.setTitle("Select a friend:");
		builder.setSingleChoiceItems(friends, 0, choiceListener);

		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				// 要把这个task设置成已做过状态
				UserDelegateOutTask userDelegateOutTask = null;
				for (UserTask ut : application.getUserTaskList()) {
					if (ut.getGoalModelName().equals(goalModelName)
							&& ut.getElementName().equals(elementName)) {
						userDelegateOutTask = (UserDelegateOutTask) ut;
						ut.setDone(true);
						adapter.notifyDataSetChanged();
						break;
					}
				}

				// 获得选取的friend名字
				String friendSelected = friends[choiceListener.getWhich()]
						.split(":")[0];

				// 发送消息给agent
				SGMMessage msgToExternalAgent = new SGMMessage(
						MesHeader_Mes2Manger.EXTERNAL_AGENT_MESSAGE, null,
						goalModelName, elementName, friendSelected,
						elementName, elementName,
						MesBody_Mes2Manager.DelegateOut);
				
				//看是否有需要在委托出去的时候顺便传递出去的数据
				if (userDelegateOutTask!=null && userDelegateOutTask.getRequestData()!=null) {
					msgToExternalAgent.setContent(userDelegateOutTask.getRequestData());
				}

				aideAgentInterface.sendMesToExternalAgent(msgToExternalAgent);

			}
		});
		builder.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});

		Dialog dialog = builder.create();
		return dialog;
	}

	private class ChoiceOnClickListener implements
			DialogInterface.OnClickListener {

		private int which = 0;

		@Override
		public void onClick(DialogInterface dialogInterface, int which) {
			this.which = which;
		}

		public int getWhich() {
			return which;
		}
	}

	/**
	 * 如果没有可委托对象，弹出这个警告对话框
	 * 
	 * @return
	 */
	private AlertDialog onCreateNullFriendsDialog() {

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setMessage("No Friends!").setNeutralButton("OK",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
		
		AlertDialog dialog = builder.create();

		return dialog;
	}

	private class MyFriendsReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equalsIgnoreCase("jade.delegate.FRIENDS")) {
				progressDialog.dismiss();
				String[] friends = intent.getExtras().getStringArray("Friends");
				String goalModelName = intent.getExtras().getString(
						"GoalModelName");
				String elementName = intent.getExtras()
						.getString("ElementName");
				

				if (friends == null || friends.length == 0) {
					android.util.Log
							.e("MY_LOG",
									"MyFriendsReceiver,friends is null or its length is 0.");
					onCreateNullFriendsDialog().show();
				} else {
					android.util.Log.i("MY_LOG",
							"MyFriendsReceiver,friends num is: "
									+ friends.length);
					onCreateFriendsDialog(friends, goalModelName, elementName)
							.show();
				}
			}
		}

	}

}

class UserTaskAdapter extends ArrayAdapter<UserTask> {
	private List<UserTask> mObjects;
	private int mResource;
	private Context mContext;
	private LayoutInflater mInflater;
	private AideAgentInterface aideAgentInterface; // agent interface
	private ProgressDialog progressDialog;

	public UserTaskAdapter(Context context, int resource,
			List<UserTask> objects, AideAgentInterface aideAgentInterface,
			ProgressDialog progressDialog) {
		super(context, resource, objects);
		init(context, resource, objects, aideAgentInterface, progressDialog);
	}

	private void init(Context context, int resource, List<UserTask> objects,
			AideAgentInterface aideAgentInterface, ProgressDialog progressDialog) {
		this.mContext = context;
		this.mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.mResource = resource;
		this.mObjects = objects;
		this.aideAgentInterface = aideAgentInterface;
		this.progressDialog = progressDialog;
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
		String description = "";

		if (usertask instanceof UserDelegateOutTask) { // 如果是需要用户选择委托去向的任务
			holder.done.setText("Friends");
			holder.done.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// 调用agent从platform上搜索可委托对象
					aideAgentInterface.obtainFriends(usertask);

					// 把进度条对话框显示出来
					progressDialog.show();

				}
			});

			holder.quit.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					aideAgentInterface.sendMesToManager(new SGMMessage(
							MesHeader_Mes2Manger.LOCAL_AGENT_MESSAGE, null,
							null, null, null, usertask.getGoalModelName(),
							usertask.getElementName(),
							MesBody_Mes2Manager.QuitGM));
					usertask.setDone(true);
					notifyDataSetChanged();
				}
			});

			description += "You need to choose a friend to help you complete the goal:\n";

		} else { // 普通的user task
			holder.done.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					aideAgentInterface.sendMesToManager(new SGMMessage(
							MesHeader_Mes2Manger.LOCAL_AGENT_MESSAGE, null,
							null, null, null, usertask.getGoalModelName(),
							usertask.getElementName(),
							MesBody_Mes2Manager.EndTE));
					usertask.setDone(true);
					notifyDataSetChanged();
				}
			});
			holder.quit.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					aideAgentInterface.sendMesToManager(new SGMMessage(
							MesHeader_Mes2Manger.LOCAL_AGENT_MESSAGE, null,
							null, null, null, usertask.getGoalModelName(),
							usertask.getElementName(),
							MesBody_Mes2Manager.QuitTE));
					usertask.setDone(true);
					notifyDataSetChanged();
				}
			});

			description += "You need to do:\n";

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
			if (usertask instanceof UserDelegateOutTask) {
				holder.taskLayout.setBackgroundColor(mContext.getResources()
						.getColor(R.color.nodone_green));
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

		if (usertask.getDescription() == null
				|| usertask.getDescription().equals("")) {
			description += usertask.getElementName();
		} else {
			description += usertask.getDescription();
		}
		holder.description.setText(description);

		return convertView;
	}

	private class ViewHolder {
		LinearLayout taskLayout;
		TextView time;
		TextView description;
		Button done;
		Button quit;
	}

}
