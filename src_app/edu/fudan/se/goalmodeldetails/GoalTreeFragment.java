/**
 * 
 */
package edu.fudan.se.goalmodeldetails;

import java.util.ArrayList;
import java.util.List;

import edu.fudan.se.R;
import edu.fudan.se.goalmachine.ElementMachine;
import edu.fudan.se.goalmachine.GoalMachine;
import edu.fudan.se.goalmachine.State;
import edu.fudan.se.goalmachine.TaskMachine;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author whh
 * 
 */
public class GoalTreeFragment extends ListFragment {

	private GoalModel goalModel; // 要显示目标树的goal model

	private ArrayList<ElementMachine> allTreeElements = new ArrayList<ElementMachine>(); // 所有的tree节点
	private TreeViewAdapter treeViewAdapter = null;

	public GoalTreeFragment(GoalModel goalModel) {
		this.goalModel = goalModel;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		initialData();
		treeViewAdapter = new TreeViewAdapter(getActivity(),
				R.layout.tree_view_item_layout, allTreeElements);
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
		Toast.makeText(getActivity().getApplicationContext(),
				"clicked " + allTreeElements.get(position).getName(), 2000)
				.show();
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
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.tree_view_item_layout,
					null);
			holder = new ViewHolder();
			holder.icon = (ImageView) convertView
					.findViewById(R.id.tree_iv_goal_icon);
			holder.name = (TextView) convertView
					.findViewById(R.id.tree_tv_goal_name);
			holder.state = (TextView) convertView
					.findViewById(R.id.tree_tv_goal_state);
			holder.end = (Button) convertView
					.findViewById(R.id.tree_bt_goal_end);
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

		// 如果这个ElementMachine是一个GoalMachine
		if (treeElements.get(position) instanceof GoalMachine) {
			if (((GoalMachine) treeElements.get(position)).getDecomposition() == 0) { // AND分解
				holder.icon.setImageBitmap(iconAND);
			} else if (((GoalMachine) treeElements.get(position))
					.getDecomposition() == 1) { // OR分解
				holder.icon.setImageBitmap(iconOR);
			}
			holder.icon.setVisibility(View.VISIBLE);
		}
		// 如果这个ElementMachine是一个TaskMachine
		else if (treeElements.get(position) instanceof TaskMachine) {
			holder.icon.setImageBitmap(iconOR);
			holder.icon.setVisibility(View.INVISIBLE); // 图标隐藏
			// 再设置是否显示end按钮
			if (treeElements.get(position).getCurrentState() == State.Executing) {
				holder.end.setVisibility(View.VISIBLE);
			} else {
				holder.end.setVisibility(View.INVISIBLE);
			}
		}

		return convertView;
	}

	class ViewHolder {
		ImageView icon;
		TextView name;
		TextView state;
		Button end;
	}

}
