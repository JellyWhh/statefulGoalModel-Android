/**
 * 
 */
package edu.fudan.se.goalmodeldetails;

import edu.fudan.se.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * @author whh
 *
 */
public class GoalModelDetailsFragment extends Fragment {
	
	private SelectOrderPopupWindow popupWindow;
	private ViewPager mPager;
	private ImageView iv_gmdetails_back, iv_gmdetails_refresh,
			iv_gmdetails_orders;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(final LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.fragment_goalmodeldetails, null);
		iv_gmdetails_back = (ImageView) view
				.findViewById(R.id.iv_gmdetails_back); // 返回按钮
		iv_gmdetails_refresh = (ImageView) view
				.findViewById(R.id.iv_gmdetails_refresh); // 刷新按钮
		iv_gmdetails_orders = (ImageView) view
				.findViewById(R.id.iv_gmdetails_orders); // 底部的显示命令按钮

		mPager = (ViewPager) view
				.findViewById(R.id.view_pager_goalmodeldetails);
		mPager.setAdapter(new FragmentPagerAdapter(getChildFragmentManager()) {
			Fragment goalTreeFragment = new GoalTreeFragment();

			@Override
			public int getCount() {
				// TODO Auto-generated method stub
				return 1;
			}

			@Override
			public Fragment getItem(int arg0) {
				// TODO Auto-generated method stub
				return goalTreeFragment;
			}
		});
		mPager.setCurrentItem(0);

		// 为显示命令按钮添加监听器
		iv_gmdetails_orders.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Toast.makeText(getActivity(), "order pressed", 2000).show();

				popupWindow = new SelectOrderPopupWindow(inflater, itemsOnClick);
				// 显示窗口
				popupWindow.showAtLocation(
						view.findViewById(R.id.linearLayout_main),
						Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 240);
			}
		});

		return view;
	}

	// @Override
	// public void onCreate(Bundle savedInstanceState) {
	// super.onCreate(savedInstanceState);
	// setContentView(R.layout.goalmodel_details);
	//
	//
	//
	// }

	// 为弹出窗口实现监听类
	private OnClickListener itemsOnClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			popupWindow.dismiss();
			switch (v.getId()) {
			case R.id.bt_dialog_start:
				Toast.makeText(getActivity(), "start pressed", 2000).show();
				break;
			case R.id.bt_dialog_suspend:
				Toast.makeText(getActivity(), "suspend pressed", 2000).show();
				break;
			case R.id.bt_dialog_resume:
				Toast.makeText(getActivity(), "resume pressed", 2000).show();
				break;
			case R.id.bt_dialog_stop:
				Toast.makeText(getActivity(), "stop pressed", 2000).show();
				break;
			case R.id.bt_dialog_refresh:
				Toast.makeText(getActivity(), "refresh pressed", 2000).show();
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
}
