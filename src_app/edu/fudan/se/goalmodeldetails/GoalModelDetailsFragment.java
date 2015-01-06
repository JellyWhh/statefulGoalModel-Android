/**
 * 
 */
package edu.fudan.se.goalmodeldetails;

import jade.core.MicroRuntime;
import jade.wrapper.ControllerException;
import jade.wrapper.StaleProxyException;
import edu.fudan.se.R;
import edu.fudan.se.agent.AideAgentInterface;
import edu.fudan.se.goalmachine.message.MesBody_Mes2Manager;
import edu.fudan.se.goalmachine.message.SGMMessage;
import edu.fudan.se.goalmodel.GoalModel;
import edu.fudan.se.initial.SGMApplication;
import android.annotation.SuppressLint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
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
@SuppressLint("ValidFragment")
public class GoalModelDetailsFragment extends Fragment {

	private GoalModel goalModel; // 要显示详情的Goal Model
	private SGMApplication application; // 获取应用程序，以得到里面的全局变量

	private SelectOrderPopupWindow popupWindow;
	private ViewPager mPager;
	private ImageView iv_gmdetails_back, iv_gmdetails_refresh,
			iv_gmdetails_orders;
	private TextView tv_gmdetails_name;

	private AideAgentInterface aideAgentInterface; // agent interface
	// private String agentNickname;

	public GoalModelDetailsFragment(GoalModel goalModel) {
		this.goalModel = goalModel;
		// this.agentNickname = agentNickname;

		// try {
		// Log.i("GoalModelDetailsFragment", "getting agent interface..."
		// + this.agentNickname);
		// aideAgentInterface = MicroRuntime.getAgent(this.agentNickname)
		// .getO2AInterface(AideAgentInterface.class);
		// Log.i("GoalModelDetailsFragment", "check interface, null?: "
		// + (aideAgentInterface == null));
		// } catch (StaleProxyException e) {
		// Log.e("GoalModelDetailsFragment", "StaleProxyException");
		// e.printStackTrace();
		// } catch (ControllerException e) {
		// Log.e("GoalModelDetailsFragment", "ControllerException");
		// e.printStackTrace();
		// }
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		application = (SGMApplication) getActivity().getApplication();
		try {
			aideAgentInterface = MicroRuntime.getAgent(
					application.getAgentNickname()).getO2AInterface(
					AideAgentInterface.class);
		} catch (StaleProxyException e) {
			Log.e("GoalModelDetailsFragment", "StaleProxyException");
			e.printStackTrace();
		} catch (ControllerException e) {
			Log.e("GoalModelDetailsFragment", "ControllerException");
			e.printStackTrace();
		}
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
				getChildFragmentManager(), aideAgentInterface));
		mPager.setCurrentItem(0);

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

		// 这个简介在屏幕中央显示
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
				// aideAgentInterface.startGoalModel(goalModel);
				aideAgentInterface.sendExternalEvent(new SGMMessage(
						"EXTERNAL_EVENT", null, null, null, null, goalModel
								.getName(), null, MesBody_Mes2Manager.StartGM));
				mPager.getAdapter().notifyDataSetChanged(); // 更新数据显示
				break;
			case R.id.bt_dialog_suspend:
				// aideAgentInterface.suspendGoalModel(goalModel);
				aideAgentInterface.sendExternalEvent(new SGMMessage(
						"EXTERNAL_EVENT", null, null, null, null, goalModel
								.getName(), null, MesBody_Mes2Manager.SuspendGM));
				mPager.getAdapter().notifyDataSetChanged(); // 更新数据显示
				break;
			case R.id.bt_dialog_resume:
				// aideAgentInterface.resumeGoalModel(goalModel);
				aideAgentInterface.sendExternalEvent(new SGMMessage(
						"EXTERNAL_EVENT", null, null, null, null, goalModel
								.getName(), null, MesBody_Mes2Manager.ResumeGM));
				mPager.getAdapter().notifyDataSetChanged(); // 更新数据显示
				break;
			case R.id.bt_dialog_stop:
				// aideAgentInterface.stopGoalModel(goalModel);
				aideAgentInterface.sendExternalEvent(new SGMMessage(
						"EXTERNAL_EVENT", null, null, null, null, goalModel
								.getName(), null, MesBody_Mes2Manager.StopGM));
				mPager.getAdapter().notifyDataSetChanged(); // 更新数据显示
				break;
			case R.id.bt_dialog_reset:
				// reset的时候要把让用户做的任务列表中的相关任务清除
				application.clearTasksOfGoalModel(goalModel);
				// aideAgentInterface.resetGoalModel(goalModel);
				aideAgentInterface.sendExternalEvent(new SGMMessage(
						"EXTERNAL_EVENT", null, null, null, null, goalModel
								.getName(), null, MesBody_Mes2Manager.ResetGM));
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

		Fragment goalTreeFragment;

		public MyFragmentPagerAdapter(GoalModel goalModel, FragmentManager fm,
				AideAgentInterface aideAgentInterface) {
			super(fm);
			goalTreeFragment = new GoalTreeFragment(goalModel,
					aideAgentInterface);
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
