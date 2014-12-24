/**
 * 
 */
package edu.fudan.se.app.mainfragments;

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
import edu.fudan.se.goalmodeldetails.GoalModelDetailsActivity;
import edu.fudan.se.R;

/**
 * myGoal标签页
 * 
 * @author whh
 * 
 */
public class MyGoalFragment extends ListFragment {

	private MyGoalListAdapter<String> adapter;
	private ArrayList<String> goalmodels = new ArrayList<>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		initData();
		adapter = new MyGoalListAdapter(getActivity(),
				R.layout.goalmodel_list_item, goalmodels);
//		setListAdapter(adapter);
//		registerForContextMenu(getListView());
	}

	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		setListAdapter(adapter);

		getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		// showDetails(mCurCheckPosition);
	}
	/**
	 * list里面显示的数据
	 * 
	 */
	private void initData() {
		String s1 = "goalmodel1";
		String s2 = "goalmodel2";

		this.goalmodels.add(s1);
		this.goalmodels.add(s2);
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		if (this.goalmodels.get(position).equals("goalmodel1")) {
//			FragmentTransaction transaction = getFragmentManager().beginTransaction();
//			transaction.replace(R.id.fragment_main_container, new GoalTreeFragment()).commit();
			Intent intent = new Intent();
			intent.setClass(getActivity(), GoalModelDetailsActivity.class);
			startActivity(intent);
		}
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
	
	@SuppressWarnings("unchecked")
	private View createViewFromResource(int position, View convertView,
			ViewGroup parent, int resource) {
		ViewHolder holder;

		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(resource, parent, false);

			holder.text = (TextView) convertView
					.findViewById(R.id.goal_text);
			holder.icon = (ImageView) convertView
					.findViewById(R.id.goal_image);

			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		//下面部分不可缺少，是设置每个item具体显示的地方！
		T item = getItem(position);
		if (item instanceof String) {
			holder.text.setText((String)item);
		}
		holder.icon.setImageResource(R.drawable.goal_set_image);
		
		
		return convertView;
	}

	class ViewHolder {
		TextView text;
		ImageView icon;
	}

}