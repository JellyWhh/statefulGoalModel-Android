/**
 * 
 */
package edu.fudan.se.goalmodeldetails;

import java.util.ArrayList;
import java.util.List;

import edu.fudan.se.R;


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
import android.widget.Toast;

/**
 * @author whh
 *
 */
public class GoalTreeFragment extends ListFragment {
	
	private ArrayList<TreeElement> allTreeElements = new ArrayList<TreeElement>();	//所有的tree节点
	private TreeViewAdapter treeViewAdapter = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		initialData();
		treeViewAdapter = new TreeViewAdapter(getActivity(), R.layout.tree_view_item_layout,
				allTreeElements);
//		setListAdapter(treeViewAdapter);
//		registerForContextMenu(getListView());
	}
	
	private void initialData(){
		TreeElement TreeElement1=new TreeElement("01", "关键类", false	, false, "00", 0,false);
		TreeElement TreeElement2=new TreeElement("02", "应用程序组件", false	, true, "00", 0,false);
		TreeElement TreeElement3=new TreeElement("03", "Activity和任务", false	, true, "00", 0,false);
		TreeElement TreeElement4=new TreeElement("04", "激活组件：intent", true	, false, "02", 1,false);
		TreeElement TreeElement5=new TreeElement("05", "关闭组件", true	, false, "02", 1,false);
		TreeElement TreeElement6=new TreeElement("06", "manifest文件", true	, false, "02", 1,false);
		TreeElement TreeElement7=new TreeElement("07", "Intent过滤器", true	, false, "02", 1,false);
		TreeElement TreeElement8=new TreeElement("08", "Affinity（吸引力）和新任务", true	, false, "03", 1,false);
		TreeElement TreeElement9=new TreeElement("09", "加载模式", true	, true, "03", 1,false);
		TreeElement TreeElement10=new TreeElement("10", "加载模式孩子1", true	, true, "09", 2,false);
		TreeElement TreeElement11=new TreeElement("11", "加载模式孩子2", true	, true, "09", 2,false);
		TreeElement TreeElement12=new TreeElement("12", "加载模式孩子2的孩子1", true	, false, "11", 3,false);
		TreeElement TreeElement13=new TreeElement("13", "加载模式孩子2的孩子2", true	, false, "11", 3,false);
		TreeElement TreeElement14=new TreeElement("14", "加载模式孩子1的孩子1", true	, false, "10", 3,false);
		TreeElement TreeElement15=new TreeElement("15", "加载模式孩子1的孩子2", true	, false, "10", 3,false);
		TreeElement TreeElement16=new TreeElement("16", "加载模式孩子1的孩子3", true	, false, "10", 3,false);
		TreeElement TreeElement17=new TreeElement("17", "加载模式孩子1的孩子4", true	, false, "10", 3,false);
		TreeElement TreeElement18=new TreeElement("18", "加载模式孩子1的孩子5", true	, false, "10", 3,false);
		TreeElement TreeElement19=new TreeElement("19", "加载模式孩子1的孩子6", true	, false, "10", 3,false);
//		visibleTreeElements.add(TreeElement1);
//		visibleTreeElements.add(TreeElement2);
//		visibleTreeElements.add(TreeElement3);
	
		
		allTreeElements.add(TreeElement1);
		allTreeElements.add(TreeElement2);
		allTreeElements.add(TreeElement4);
		allTreeElements.add(TreeElement5);
		allTreeElements.add(TreeElement6);
		allTreeElements.add(TreeElement7);
		allTreeElements.add(TreeElement3);
		allTreeElements.add(TreeElement8);
		allTreeElements.add(TreeElement9);
		allTreeElements.add(TreeElement10);
		allTreeElements.add(TreeElement11);
		allTreeElements.add(TreeElement12);
		allTreeElements.add(TreeElement13);
		allTreeElements.add(TreeElement14);
		allTreeElements.add(TreeElement15);
		allTreeElements.add(TreeElement16);
		allTreeElements.add(TreeElement17);
		allTreeElements.add(TreeElement18);
		allTreeElements.add(TreeElement19);
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
		if (!allTreeElements.get(position).isHasChild()) {	//没有子节点
			Toast.makeText(getActivity().getApplicationContext() , allTreeElements.get(position).getTitle(), 2000).show();
			return;
		}
	}
}

/**
 * 内部类，adapter
 * @author whh
 *
 */
class TreeViewAdapter extends ArrayAdapter<TreeElement> {

	private LayoutInflater mInflater;
	private List<TreeElement> treeElements;
	private Bitmap mIconUnFold; // 展开
	private Bitmap mIconFold; // 折叠

	public TreeViewAdapter(Context context, int textViewResourceId,
			List<TreeElement> treeElements) {
		super(context, textViewResourceId, treeElements);
		this.mInflater = LayoutInflater.from(context);
		this.treeElements = treeElements;
		this.mIconUnFold = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.tree_view_icon_unfold);
		this.mIconFold = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.tree_view_icon_fold);
	}

	@Override
	public int getCount() {
		return this.treeElements.size();
	}

	@Override
	public TreeElement getItem(int position) {
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
			holder.text = (TextView) convertView
					.findViewById(R.id.tree_view_item_title);
			holder.icon = (ImageView) convertView
					.findViewById(R.id.tree_view_item_icon);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		int level = treeElements.get(position).getLevel();
		holder.icon.setPadding(25 * (level + 1), holder.icon.getPaddingTop(),
				0, holder.icon.getPaddingBottom());
		holder.text.setText(treeElements.get(position).getTitle());
		
		if (treeElements.get(position).isHasChild()) { // 有子节点
			if (treeElements.get(position).isFold()) {	//折叠
				holder.icon.setImageBitmap(mIconFold);
			}else {	//展开
				holder.icon.setImageBitmap(mIconUnFold);
			}
			holder.icon.setVisibility(View.VISIBLE);
		}else {// 没有子节点
			holder.icon.setImageBitmap(mIconFold);
			holder.icon.setVisibility(View.INVISIBLE);
		}


		return convertView;
	}

	class ViewHolder {
		TextView text;
		ImageView icon;
	}

}


