/**
 * 
 */
package edu.fudan.se.messageFragment;


import java.util.List;

import edu.fudan.se.R;
import edu.fudan.se.initial.SGMApplication;
import edu.fudan.se.userMes.UserTask;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * @author whh
 * 
 */
public class ReadFragment extends ListFragment {

	private SGMApplication application; // 获取应用程序，以得到里面的全局变量
	private ReadUserTaskAdapter adapter;

	private Handler handler;
	private Runnable runnable;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		application = (SGMApplication) getActivity().getApplication();

		adapter = new ReadUserTaskAdapter(getActivity(),
				R.layout.listview_readmes, application.getUserDoneTaskList());

		setListAdapter(adapter);

		// 用于定时刷新
		handler = new Handler();
		runnable = new Runnable() {
			@Override
			public void run() {
				adapter.notifyDataSetChanged();
				handler.postDelayed(this, 500);
			}
		};
		handler.postDelayed(runnable, 500); // 0.5s刷新一次

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		setUserVisibleHint(true);
		super.onActivityCreated(savedInstanceState);

		getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
	}

	@Override
	public void onDestroy() {
		handler.removeCallbacks(runnable);
		super.onDestroy();
	}
}

class ReadUserTaskAdapter extends ArrayAdapter<UserTask> {

	private List<UserTask> mObjects;
	private int mResource;
	private Context mContext;
	private LayoutInflater mInflater;

	public ReadUserTaskAdapter(Context context, int resource,
			List<UserTask> objects) {
		super(context, resource, objects);
		init(context, resource, objects);
	}

	private void init(Context context, int resource, List<UserTask> objects) {
		this.mContext = context;
		this.mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.mResource = resource;
		this.mObjects = objects;
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

			holder.time = (TextView) convertView
					.findViewById(R.id.tv_taskTime_done);
			holder.fromAgent = (TextView) convertView
					.findViewById(R.id.tv_taskFromName_done);
			holder.description = (TextView) convertView
					.findViewById(R.id.tv_taskDescription_done);
			holder.del_mes = (ImageView) convertView.findViewById(R.id.iv_task_delete);

			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		// 下面部分不可缺少，是设置每个item具体显示的地方！
		// final User usertask = getItem(position);
		final UserTask userTask = getItem(position);
		holder.time.setText(userTask.getTime());
		holder.fromAgent.setText("From: "+userTask.getFromAgentName());
		holder.description.setText(userTask.getDescription());
		holder.del_mes.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
				builder.setTitle("Delete");
				builder.setIcon(android.R.drawable.ic_dialog_alert);
				builder.setMessage("Are you sure to delete this message?");
				builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						mObjects.remove(userTask);
						dialog.dismiss();
					}
				});
				builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				
				AlertDialog dialog = builder.create();
				dialog.setCanceledOnTouchOutside(false);// 使除了dialog以外的地方不能被点击
				dialog.show();
			}
		});
		

		return convertView;
	}

	class ViewHolder {
		TextView time;
		TextView fromAgent;
		TextView description;
		ImageView del_mes;
	}
}
