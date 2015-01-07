/**
 * 
 */
package edu.fudan.se.messageFragment;

import jade.core.MicroRuntime;
import jade.wrapper.ControllerException;
import jade.wrapper.StaleProxyException;

import java.util.List;

import android.content.Context;
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
import edu.fudan.se.initial.SGMApplication;
import edu.fudan.se.userMes.UserTask;

/**
 * @author whh
 * 
 */
public class TaskFragment extends ListFragment {

	private SGMApplication application; // 获取应用程序，以得到里面的全局变量
	private UserTaskAdapter adapter;

	private AideAgentInterface aideAgentInterface; // agent interface

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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		adapter = new UserTaskAdapter(getActivity(),
				R.layout.listview_usertask, application.getUserTaskList(),
				aideAgentInterface);

		adapter.notifyDataSetChanged();
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

}

class UserTaskAdapter extends ArrayAdapter<UserTask> {
	private List<UserTask> mObjects;
	private int mResource;
	private Context mContext;
	private LayoutInflater mInflater;
	private AideAgentInterface aideAgentInterface; // agent interface

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
		if (usertask.isDone()) { // 用户已经完成的
			holder.taskLayout.setBackgroundColor(mContext.getResources()
					.getColor(R.color.done_grey));
			holder.done.setClickable(false);
			holder.quit.setClickable(false);
		} else {
			holder.taskLayout.setBackgroundColor(mContext.getResources()
					.getColor(R.color.nodone_white));
			holder.done.setClickable(true);
			holder.done.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// aideAgentInterface.endTaskMachine(
					// usertask.getTaskMachine(), "END");
					aideAgentInterface.sendMesToManager(new SGMMessage(
							MesHeader_Mes2Manger.LOCAL_AGENT_MESSAGE, null,
							null, null, null, usertask.getGoalModelName(),
							usertask.getElementName(),
							MesBody_Mes2Manager.EndTE));
					usertask.setDone(true);
					notifyDataSetChanged();
				}
			});
			holder.quit.setClickable(true);
			holder.quit.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// aideAgentInterface.endTaskMachine(
					// usertask.getTaskMachine(), "QUIT");
					aideAgentInterface.sendMesToManager(new SGMMessage(
							MesHeader_Mes2Manger.LOCAL_AGENT_MESSAGE, null,
							null, null, null, usertask.getGoalModelName(),
							usertask.getElementName(),
							MesBody_Mes2Manager.QuitTE));
					usertask.setDone(true);
					notifyDataSetChanged();
				}
			});
		}
		String description = "You need to do:\n";
		if (usertask.getDescription() == null
				|| usertask.getDescription().equals("")) {
			description += usertask.getElementName();
		} else {
			description += usertask.getDescription();
		}
		holder.description.setText(description);

		return convertView;
	}

	class ViewHolder {
		LinearLayout taskLayout;
		TextView description;
		Button done;
		Button quit;
	}
}
