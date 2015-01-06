/**
 * 
 */
package edu.fudan.se.goalmodel;

import java.util.ArrayList;
import java.util.List;

import edu.fudan.se.R;
import edu.fudan.se.goalmachine.ElementMachine;
import edu.fudan.se.goalmachine.GoalMachine;
import edu.fudan.se.goalmodel.GoalModel;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * @author whh
 * 
 */
public class GoalModelDetailsFragment extends ListFragment {

	private GoalModel goalModel; // 要显示目标树的goal model

	private ArrayList<ElementMachine> allTreeElements = new ArrayList<ElementMachine>(); // 所有的tree节点
	private TreeViewAdapter treeViewAdapter = null;

	public GoalModelDetailsFragment(GoalModel goalModel) {
		this.goalModel = goalModel;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		initialData();
		treeViewAdapter = new TreeViewAdapter(getActivity(),
				R.layout.listview_goalmodeldetails, allTreeElements);
		// setListAdapter(treeViewAdapter);
		// registerForContextMenu(getListView());
	}

	private void initialData() {
		allTreeElements = this.goalModel.getElementMachines();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		setListAdapter(treeViewAdapter);

		getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		// showDetails(mCurCheckPosition);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
	}
}

/**
 * 内部类，adapter
 * 
 * @author whh
 * 
 */
class TreeViewAdapter extends ArrayAdapter<ElementMachine> {

	private LayoutInflater mInflater;
	private List<ElementMachine> treeElements;
	private Bitmap iconAND; // and分解
	private Bitmap iconOR; // or分解

	public TreeViewAdapter(Context context, int textViewResourceId,
			List<ElementMachine> treeElements) {
		super(context, textViewResourceId, treeElements);
		this.mInflater = LayoutInflater.from(context);
		this.treeElements = treeElements;
		this.iconAND = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.tree_view_icon_and);
		this.iconOR = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.tree_view_icon_or);
	}

	@Override
	public int getCount() {
		return this.treeElements.size();
	}

	@Override
	public ElementMachine getItem(int position) {
		return this.treeElements.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.listview_goalmodeldetails,
					null);
			holder = new ViewHolder();
			holder.icon = (ImageView) convertView
					.findViewById(R.id.tree_iv_goal_icon);
			holder.name = (TextView) convertView
					.findViewById(R.id.tree_tv_goal_name);
			holder.state = (TextView) convertView
					.findViewById(R.id.tree_tv_goal_state);
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		int level = treeElements.get(position).getLevel();
		holder.icon.setPadding(25 * (level + 1), holder.icon.getPaddingTop(),
				0, holder.icon.getPaddingBottom());
		holder.name.setText(treeElements.get(position).getName());
		holder.state.setText(treeElements.get(position).getCurrentState()
				.toString());

		// 如果是root goal，不用显示图标
		if (treeElements.get(position).getParentGoal() == null) {
			holder.icon.setVisibility(View.INVISIBLE);
		} else {
			// 如果父目标是AND分解，当前目标显示AND图标
			if (((GoalMachine) (treeElements.get(position).getParentGoal()))
					.getDecomposition() == 0) { // AND分解
				holder.icon.setImageBitmap(iconAND);
			}
			// 如果父目标是OR分解，当前目标显示OR图标
			else if (((GoalMachine) (treeElements.get(position).getParentGoal()))
					.getDecomposition() == 1) { // OR分解
				holder.icon.setImageBitmap(iconOR);
			}
			holder.icon.setVisibility(View.VISIBLE);
		}


		return convertView;
	}

	class ViewHolder {
		ImageView icon;
		TextView name;
		TextView state;
	}

}