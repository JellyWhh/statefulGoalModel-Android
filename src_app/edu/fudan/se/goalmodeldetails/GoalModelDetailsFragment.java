/**
 * 
 */
package edu.fudan.se.goalmodeldetails;

import edu.fudan.se.R;
import edu.fudan.se.goalmodel.GoalModel;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author whh
 * 
 */
public class GoalModelDetailsFragment extends Fragment {

	private GoalModel goalModel; // 要显示详情的Goal Model

	private SelectOrderPopupWindow popupWindow;
	private ViewPager mPager;
	private ImageView iv_gmdetails_back, iv_gmdetails_refresh,
			iv_gmdetails_orders;
	private TextView tv_gmdetails_name;

	public GoalModelDetailsFragment(GoalModel goalModel) {
		this.goalModel = goalModel;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(final LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.fragment_goalmodeldetails,
				null);
		iv_gmdetails_back = (ImageView) view
				.findViewById(R.id.iv_gmdetails_back); // 返回按钮
		iv_gmdetails_refresh = (ImageView) view
				.findViewById(R.id.iv_gmdetails_refresh); // 刷新按钮
		iv_gmdetails_orders = (ImageView) view
				.findViewById(R.id.iv_gmdetails_orders); // 底部的显示命令按钮

		tv_gmdetails_name = (TextView) view
				.findViewById(R.id.tv_gmdetails_name); // goal model名字
		tv_gmdetails_name.setText(this.goalModel.getName()); // 显示goal model的名字

		mPager = (ViewPager) view
				.findViewById(R.id.view_pager_goalmodeldetails);
		mPager.setAdapter(new MyFragmentPagerAdapter(this.goalModel,
				getChildFragmentManager()));
		mPager.setCurrentItem(0);

		// 为按钮添加监听器
		iv_gmdetails_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Toast.makeText(getActivity(), "back pressed", 2000).show();
				// TODO 安卓的回收机制！！！！！目前返回后activity会销毁，于是goal
				// model里面开启的进程都会关闭，下次点击进来后又重新初始化了goal model
				getActivity().finish();
				

			}
		});

		iv_gmdetails_refresh.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mPager.getAdapter().notifyDataSetChanged(); // 更新数据显示
			}
		});

		iv_gmdetails_orders.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				popupWindow = new SelectOrderPopupWindow(inflater,
						itemsOnClick, goalModel.getState(), getResources());

				// 显示窗口

				popupWindow.showAtLocation(
						view.findViewById(R.id.linearLayout_main),
						Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 240);

			}
		});

		// 为goal model name添加监听器，点击后弹出goal model的介绍
		tv_gmdetails_name.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showGoalModelDescription(
						view.findViewById(R.id.linearLayout_main),
						goalModel.getDescription());
			}
		});

		return view;
	}

	/**
	 * 点击上方的goal model name后弹出goal model的介绍，是一个普通的popupwindow
	 * 
	 * @param description
	 *            goal model的介绍
	 */
	private void showGoalModelDescription(View parentView, String description) {
		final View popupDescriptionView = LayoutInflater.from(getActivity())
				.inflate(R.layout.popupwindow_description, null);
		final PopupWindow popupWindow = new PopupWindow(popupDescriptionView,
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT, true);

		popupWindow.setOutsideTouchable(true);
		popupWindow.setFocusable(true);
		TextView tv_gmdetails_description = (TextView) popupDescriptionView
				.findViewById(R.id.tv_gmdetails_description);
		tv_gmdetails_description.setText(description);

		ColorDrawable dw = new ColorDrawable(-00000);
		popupWindow.setBackgroundDrawable(dw);

		// mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
		popupDescriptionView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int height = popupDescriptionView.findViewById(
						R.id.tv_gmdetails_description).getTop();
				int y = (int) event.getY();
				if (event.getAction() == MotionEvent.ACTION_UP) {
					if (y < height) {
						popupWindow.dismiss();
					}
				}
				return true;
			}
		});

		popupWindow.showAtLocation(parentView, Gravity.CENTER | Gravity.CENTER,
				0, 0);
	}

	// 为命令的弹出窗口实现监听类
	private OnClickListener itemsOnClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			popupWindow.dismiss();
			switch (v.getId()) {
			case R.id.bt_dialog_start:
				goalModel.start();
				break;
			case R.id.bt_dialog_suspend:
				goalModel.suspend();
				break;
			case R.id.bt_dialog_resume:
				goalModel.resume();
				break;
			case R.id.bt_dialog_stop:
				goalModel.stop();
				break;
			case R.id.bt_dialog_reset:
				goalModel.reset();
				break;
			case R.id.bt_dialog_cancel:
				// 销毁弹出框
				popupWindow.dismiss();
				break;

			default:
				break;
			}

		}
	};

	/**
	 * 内部类，为了把GoalModel参数传进来
	 * 
	 * @author whh
	 * 
	 */
	class MyFragmentPagerAdapter extends FragmentPagerAdapter {

		Fragment goalTreeFragment;

		public MyFragmentPagerAdapter(GoalModel goalModel, FragmentManager fm) {
			super(fm);
			goalTreeFragment = new GoalTreeFragment(goalModel);
		}

		@Override
		public Fragment getItem(int arg0) {
			return goalTreeFragment;
		}

		@Override
		public int getCount() {
			return 1;
		}

		@Override
		public int getItemPosition(Object object) {
			return POSITION_NONE; // To make notifyDataSetChanged() do something
		}

	}
}
