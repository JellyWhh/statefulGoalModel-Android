/**
 * 
 */
package edu.fudan.se.mainfragments;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import edu.fudan.se.goalmodel.GoalModel;
import edu.fudan.se.goalmodel.GoalModelActivity;
import edu.fudan.se.initial.SGMApplication;
import edu.fudan.se.R;

/**
 * myGoal标签页
 * 
 * @author whh
 * 
 */
public class MyGoalFragment extends ListFragment {

	private SGMApplication application; // 获取应用程序，以得到里面的全局变量

	private MyGoalListAdapter<GoalModel> adapter;

	private ArrayList<GoalModel> goalmodels;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		application = (SGMApplication) getActivity().getApplication();

		goalmodels = new ArrayList<>(application.getGoalModelManager()
				.getGoalModelList().values());

		adapter = new MyGoalListAdapter<GoalModel>(getActivity(),
				R.layout.listview_mygoal, goalmodels);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		setListAdapter(adapter);

		getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		Intent intent = new Intent();
		intent.setClass(getActivity(), GoalModelActivity.class);
		intent.putExtra("goalmodelname", goalmodels.get(position).getName());
		startActivity(intent);
	}

}

/**
 * 用于MyGoalFragment的list适配器<br/>
 * 使用Adapter轻松完成注册Listener的过程。我们继承BaseAdapter，然后在getView中实现整个初始化的过程。
 * 
 * @author whh
 * 
 * @param <T>
 */
class MyGoalListAdapter<T> extends ArrayAdapter<T> {

	private List<T> mObjects;
	private int mResource;
	private Context mContext;
	private LayoutInflater mInflater;

	public MyGoalListAdapter(Context context, int resource, List<T> objects) {
		super(context, resource, objects);
		init(context, resource, objects);
	}

	private void init(Context context, int resource, List<T> objects) {
		mContext = context;
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mResource = resource;
		mObjects = objects;
	}

	@Override
	public int getCount() {
		return this.mObjects.size();
	}

	@Override
	public T getItem(int position) {
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

			holder.text = (TextView) convertView.findViewById(R.id.goal_text);
			holder.icon = (ImageView) convertView.findViewById(R.id.goal_image);

			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		// 下面部分不可缺少，是设置每个item具体显示的地方！
		T item = getItem(position);
		if (item instanceof GoalModel) {
			holder.text.setText(((GoalModel) item).getName());
		}
		holder.icon.setImageResource(R.drawable.goal_set_image);

		return convertView;
	}

	class ViewHolder {
		TextView text;
		ImageView icon;
	}

}