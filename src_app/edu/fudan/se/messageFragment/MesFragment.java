/**
 * 
 */
package edu.fudan.se.messageFragment;

import java.util.List;

import edu.fudan.se.R;
import edu.fudan.se.initial.SGMApplication;
import edu.fudan.se.userMes.UserMessage;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

/**
 * @author whh
 * 
 */
public class MesFragment extends ListFragment {

	private SGMApplication application; // 获取应用程序，以得到里面的全局变量
	private UserMessageAdapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		application = (SGMApplication) getActivity().getApplication();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		adapter = new UserMessageAdapter(getActivity(),
				R.layout.listview_usermessage, application.getUserMessageList());

		setListAdapter(adapter);

		getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
	}
}

class UserMessageAdapter extends ArrayAdapter<UserMessage> {

	private List<UserMessage> mObjects;
	private int mResource;
	private Context mContext;
	private LayoutInflater mInflater;

	public UserMessageAdapter(Context context, int resource,
			List<UserMessage> objects) {
		super(context, resource, objects);
		init(context, resource, objects);
	}

	private void init(Context context, int resource, List<UserMessage> objects) {
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
	public UserMessage getItem(int position) {
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

			holder.time = (TextView) convertView.findViewById(R.id.tv_mes_time);
			holder.content = (TextView) convertView
					.findViewById(R.id.tv_mes_content);
			holder.del = (Button) convertView.findViewById(R.id.bt_mes_del);

			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		// 下面部分不可缺少，是设置每个item具体显示的地方！
		final UserMessage usertask = getItem(position);
		holder.time.setText(usertask.getTime());
		holder.content.setText(usertask.getContent());
		holder.del.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mObjects.remove(usertask);
				notifyDataSetChanged();
			}
		});

		return convertView;
	}

	class ViewHolder {
		TextView time;
		TextView content;
		Button del;
	}
}
