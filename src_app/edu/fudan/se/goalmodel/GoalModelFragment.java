/**
 * 
 */
package edu.fudan.se.goalmodel;

import java.util.ArrayList;

import edu.fudan.se.R;
import edu.fudan.se.agent.AideAgentInterface;
import edu.fudan.se.goalmachine.message.MesBody_Mes2Manager;
import edu.fudan.se.goalmachine.message.MesHeader_Mes2Manger;
import edu.fudan.se.goalmachine.message.SGMMessage;
import edu.fudan.se.goalmodel.GoalModel;
import edu.fudan.se.initial.SGMApplication;
import edu.fudan.se.maincontainer.MainActivity;
import edu.fudan.se.support.GetAgent;
import android.content.Intent;
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

/**
 * @author whh
 * 
 */
public class GoalModelFragment extends Fragment {

	private GoalModel goalModel; // 要显示详情的Goal Model
	private SGMApplication application; // 获取应用程序，以得到里面的全局变量

	private SelectOrderPopupWindow popupWindow;
	private ViewPager mPager;
	private ImageView iv_gmdetails_back, iv_gmdetails_refresh,
			iv_gmdetails_orders;
	private TextView tv_gmdetails_name;

	private AideAgentInterface aideAgentInterface; // agent interface

	public GoalModelFragment(GoalModel goalModel) {
		this.goalModel = goalModel;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		application = (SGMApplication) getActivity().getApplication();

		aideAgentInterface = GetAgent.getAideAgentInterface(application);

	}

	@Override
	public View onCreateView(final LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.fragment_goalmodel, null);
		iv_gmdetails_back = (ImageView) view
				.findViewById(R.id.iv_gmdetails_back); // 返回按钮
		iv_gmdetails_refresh = (ImageView) view
				.findViewById(R.id.iv_gmdetails_refresh); // 刷新按钮
		iv_gmdetails_orders = (ImageView) view
				.findViewById(R.id.iv_gmdetails_orders); // 底部的显示命令按钮

		tv_gmdetails_name = (TextView) view
				.findViewById(R.id.tv_gmdetails_name); // goal model名字
		tv_gmdetails_name.setText(this.goalModel.getName()); // 显示goal model的名字

		initViewPager(view);

		// 为按钮添加监听器
		iv_gmdetails_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 结束这个activity，和后退键的效果一样
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

				// 弹出窗口的按钮是否可见由goal model的root goal的状态决定
				popupWindow = new SelectOrderPopupWindow(inflater,
						itemsOnClick, goalModel.getRootGoal().getCurrentState()
								.toString(), getResources());

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
				showGoalModelMenu();
			}
		});

		return view;
	}

	private void initViewPager(View view) {
		mPager = (ViewPager) view
				.findViewById(R.id.view_pager_goalmodeldetails);

		ArrayList<Fragment> fragmentsList = new ArrayList<Fragment>();

		Fragment goalModelDetailsFragment = new GoalModelDetailsFragment(
				goalModel);
		Fragment goalModelAbstractFragment = new GoalModelAbstractFragment(
				goalModel);

		fragmentsList.add(goalModelAbstractFragment);
		fragmentsList.add(goalModelDetailsFragment);

		mPager.setAdapter(new MyFragmentPagerAdapter(getChildFragmentManager(),
				fragmentsList));
		mPager.setCurrentItem(0);

	}

	/**
	 * 点击上方的goal model name后弹出一个menu，有abstract和details两个选项
	 * 
	 */
	private void showGoalModelMenu() {
		final View popupMenuView = LayoutInflater.from(getActivity()).inflate(
				R.layout.popupwindow_menu, null);
		final PopupWindow popupWindow = new PopupWindow(popupMenuView,
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT, true);

		popupWindow.setOutsideTouchable(true);
		popupWindow.setFocusable(true);

		TextView tv_menu_abstract = (TextView) popupMenuView
				.findViewById(R.id.tv_menu_abstract);
		TextView tv_menu_details = (TextView) popupMenuView
				.findViewById(R.id.tv_menu_details);

		tv_menu_abstract.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mPager.setCurrentItem(0);
				popupWindow.dismiss();
			}
		});
		tv_menu_details.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mPager.setCurrentItem(1);
				popupWindow.dismiss();
			}
		});

		ColorDrawable dw = new ColorDrawable(-00000);
		popupWindow.setBackgroundDrawable(dw);

		// mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
		popupMenuView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int height = popupMenuView.findViewById(R.id.ll_popupmenu)
						.getTop();
				int y = (int) event.getY();
				if (event.getAction() == MotionEvent.ACTION_UP) {
					if (y < height) {
						popupWindow.dismiss();
					}
				}
				return true;
			}
		});

		// 在goal model名字下方显示
		popupWindow.showAsDropDown(tv_gmdetails_name);

	}

	// 为命令的弹出窗口实现监听类
	private OnClickListener itemsOnClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			popupWindow.dismiss();

			switch (v.getId()) {

			case R.id.bt_dialog_start:
				aideAgentInterface.sendMesToManager(new SGMMessage(
						MesHeader_Mes2Manger.LOCAL_AGENT_MESSAGE, goalModel
								.getName(), null, null,
						MesBody_Mes2Manager.StartGM));
				mPager.getAdapter().notifyDataSetChanged(); // 更新数据显示
				
				//跳到消息界面
				Intent startMain = new Intent(getActivity(),
						MainActivity.class);
				startMain.putExtra("INITIALINDEX", 2);
				startActivity(startMain);
				
				break;

			case R.id.bt_dialog_suspend:
				aideAgentInterface.sendMesToManager(new SGMMessage(
						MesHeader_Mes2Manger.LOCAL_AGENT_MESSAGE, goalModel
								.getName(), null, null,
						MesBody_Mes2Manager.SuspendGM));
				mPager.getAdapter().notifyDataSetChanged(); // 更新数据显示
				break;

			case R.id.bt_dialog_resume:
				aideAgentInterface.sendMesToManager(new SGMMessage(
						MesHeader_Mes2Manger.LOCAL_AGENT_MESSAGE, goalModel
								.getName(), null, null,
						MesBody_Mes2Manager.ResumeGM));
				mPager.getAdapter().notifyDataSetChanged(); // 更新数据显示
				break;

			case R.id.bt_dialog_stop:
				aideAgentInterface.sendMesToManager(new SGMMessage(
						MesHeader_Mes2Manger.LOCAL_AGENT_MESSAGE, goalModel
								.getName(), null, null,
						MesBody_Mes2Manager.StopGM));
				mPager.getAdapter().notifyDataSetChanged(); // 更新数据显示
				break;

			case R.id.bt_dialog_reset:
				// reset的时候要把让用户做的任务列表中的相关任务清除
				application.clearTasksOfGoalModel(goalModel);
				aideAgentInterface.sendMesToManager(new SGMMessage(
						MesHeader_Mes2Manger.LOCAL_AGENT_MESSAGE, goalModel
								.getName(), null, null,
						MesBody_Mes2Manager.ResetGM));
				mPager.getAdapter().notifyDataSetChanged(); // 更新数据显示
				break;

			case R.id.bt_dialog_cancel:
				// 销毁弹出框
				popupWindow.dismiss();
				break;

			default:
				popupWindow.dismiss();
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

		private ArrayList<Fragment> fragmentsList;

		// Fragment goalTreeFragment;

		public MyFragmentPagerAdapter(FragmentManager fm,
				ArrayList<Fragment> fragments) {
			super(fm);
			this.fragmentsList = fragments;
			// goalTreeFragment = new GoalModelDetailsFragment(goalModel,
			// aideAgentInterface);
		}

		@Override
		public Fragment getItem(int arg0) {
			// return goalTreeFragment;
			return fragmentsList.get(arg0);
		}

		@Override
		public int getCount() {
			return fragmentsList.size();
		}

		@Override
		public int getItemPosition(Object object) {
			return POSITION_NONE; // To make notifyDataSetChanged() do something
		}

	}
}
