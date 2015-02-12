/**
 * 
 */
package edu.fudan.se.mainfragments;

import java.util.ArrayList;

import edu.fudan.se.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 在主体container中的fragment，它有四个view pager，实现了左右标签页的滑动
 * 
 * @author whh
 * 
 */
public class MainFragment extends Fragment {

	private ViewPager mPager;
	private ArrayList<Fragment> fragmentsList;
	private LinearLayout ll_tab_message, ll_tab_mygoal, ll_tab_download,
			ll_tab_procedure;
	private ImageView iv_tab_message, iv_tab_mygoal, iv_tab_download,
			iv_tab_procedure;

	private TextView tv_tab_mygoal, tv_tab_message, tv_tab_procedure,
			tv_tab_download;

	// private ImageView iv_bottom_line;

	// public final static int num = 4;
	// private int currIndex;// 当前页卡编号
	// private int bmpW;// 横线图片宽度
	// private int offset;// 图片移动的偏移量

	private Fragment messageFragment, mygoalFragment, downloadFragment,
			procedureFragment;

	private int initialIndex;

	public MainFragment(int initialIndex) {
		this.initialIndex = initialIndex;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_main2, container,
				false);

		// initTabLayout
		ll_tab_mygoal = (LinearLayout) rootView
				.findViewById(R.id.ll_tab_mygoal);
		ll_tab_message = (LinearLayout) rootView
				.findViewById(R.id.ll_tab_message);
		ll_tab_procedure = (LinearLayout) rootView
				.findViewById(R.id.ll_tab_procedure);
		ll_tab_download = (LinearLayout) rootView
				.findViewById(R.id.ll_tab_download);

		ll_tab_mygoal.setOnClickListener(new MyOnClickListener(0));
		ll_tab_message.setOnClickListener(new MyOnClickListener(1));
		ll_tab_procedure.setOnClickListener(new MyOnClickListener(2));
		ll_tab_download.setOnClickListener(new MyOnClickListener(3));

		iv_tab_message = (ImageView) rootView.findViewById(R.id.iv_tab_message);
		iv_tab_mygoal = (ImageView) rootView.findViewById(R.id.iv_tab_mygoal);
		iv_tab_download = (ImageView) rootView
				.findViewById(R.id.iv_tab_download);
		iv_tab_procedure = (ImageView) rootView
				.findViewById(R.id.iv_tab_procedure);

		tv_tab_mygoal = (TextView) rootView.findViewById(R.id.tv_tab_mygoal);
		tv_tab_message = (TextView) rootView.findViewById(R.id.tv_tab_message);
		tv_tab_procedure = (TextView) rootView
				.findViewById(R.id.tv_tab_procedure);
		tv_tab_download = (TextView) rootView
				.findViewById(R.id.tv_tab_download);

		// initWidth(rootView);

		// initViewpager
		initViewPager(rootView);

		return rootView;
	}

	// /**
	// * 自定义底部横线的宽度
	// *
	// * @param parentView
	// * parentView
	// */
	// private void initWidth(View parentView) {
	// iv_bottom_line = (ImageView) parentView
	// .findViewById(R.id.iv_bottom_line);
	// bmpW = iv_bottom_line.getLayoutParams().width;
	// DisplayMetrics dm = new DisplayMetrics();
	// getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
	// int screenW = dm.widthPixels;
	// offset = (int) ((screenW / num - bmpW) / 2);
	//
	// // imgageview设置平移，使下划线平移到初始位置（平移一个offset）
	// Matrix matrix = new Matrix();
	// matrix.postTranslate(offset, 0);
	// iv_bottom_line.setImageMatrix(matrix);
	//
	// }

	/**
	 * 加载viewPager
	 * 
	 * @param parentView
	 */
	private void initViewPager(View parentView) {
		mPager = (ViewPager) parentView.findViewById(R.id.vp_main);
		fragmentsList = new ArrayList<Fragment>();

		mygoalFragment = new MyGoalFragment();
		messageFragment = new MessageFragment();
		procedureFragment = new ProcedureFragment();
		downloadFragment = new DownloadFragment();

		fragmentsList.add(mygoalFragment);
		fragmentsList.add(messageFragment);
		fragmentsList.add(procedureFragment);
		fragmentsList.add(downloadFragment);

		mPager.setAdapter(new MyFragmentPagerAdapter(getChildFragmentManager(),
				fragmentsList));
		mPager.setOnPageChangeListener(new MyOnPageChangeListener());
		mPager.setCurrentItem(initialIndex);

	}

	/**
	 * 内部类，用来响应tab的点击
	 * 
	 * @author whh
	 * 
	 */
	class MyOnClickListener implements View.OnClickListener {
		private int index = 0;

		public MyOnClickListener(int i) {
			index = i;
		}

		@Override
		public void onClick(View v) {
			mPager.setCurrentItem(index);
		}
	}

	/**
	 * 内部类
	 * 
	 * @author whh
	 * 
	 */
	class MyOnPageChangeListener implements OnPageChangeListener {
		// private int one = offset * 2 + bmpW;// 两个相邻页面的偏移量

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		@Override
		public void onPageScrollStateChanged(int arg0) {

		}

		@Override
		public void onPageSelected(int arg0) {

			// 设置按钮的显示
			switch (arg0) {
			case 0:
				iv_tab_mygoal.setBackgroundResource(R.drawable.tab_mygoal_sel);
				iv_tab_message
						.setBackgroundResource(R.drawable.tab_message_nor);
				iv_tab_procedure
						.setBackgroundResource(R.drawable.tab_procedure_nor);
				iv_tab_download
						.setBackgroundResource(R.drawable.tab_download_nor);

				tv_tab_mygoal.setTextColor(getResources().getColor(
						R.color.tab_blue));
				tv_tab_message.setTextColor(getResources().getColor(
						R.color.tab_grey));
				tv_tab_procedure.setTextColor(getResources().getColor(
						R.color.tab_grey));
				tv_tab_download.setTextColor(getResources().getColor(
						R.color.tab_grey));

				break;

			case 1:
				iv_tab_mygoal.setBackgroundResource(R.drawable.tab_mygoal_nor);
				iv_tab_message
						.setBackgroundResource(R.drawable.tab_message_sel);
				iv_tab_procedure
						.setBackgroundResource(R.drawable.tab_procedure_nor);
				iv_tab_download
						.setBackgroundResource(R.drawable.tab_download_nor);

				tv_tab_mygoal.setTextColor(getResources().getColor(
						R.color.tab_grey));
				tv_tab_message.setTextColor(getResources().getColor(
						R.color.tab_blue));
				tv_tab_procedure.setTextColor(getResources().getColor(
						R.color.tab_grey));
				tv_tab_download.setTextColor(getResources().getColor(
						R.color.tab_grey));
				break;
			case 2:

				iv_tab_mygoal.setBackgroundResource(R.drawable.tab_mygoal_nor);
				iv_tab_message
						.setBackgroundResource(R.drawable.tab_message_nor);
				iv_tab_procedure
						.setBackgroundResource(R.drawable.tab_procedure_sel);
				iv_tab_download
						.setBackgroundResource(R.drawable.tab_download_nor);

				tv_tab_mygoal.setTextColor(getResources().getColor(
						R.color.tab_grey));
				tv_tab_message.setTextColor(getResources().getColor(
						R.color.tab_grey));
				tv_tab_procedure.setTextColor(getResources().getColor(
						R.color.tab_blue));
				tv_tab_download.setTextColor(getResources().getColor(
						R.color.tab_grey));
				break;
			case 3:
				iv_tab_mygoal.setBackgroundResource(R.drawable.tab_mygoal_nor);
				iv_tab_message
						.setBackgroundResource(R.drawable.tab_message_nor);
				iv_tab_procedure
						.setBackgroundResource(R.drawable.tab_procedure_nor);
				iv_tab_download
						.setBackgroundResource(R.drawable.tab_download_sel);

				tv_tab_mygoal.setTextColor(getResources().getColor(
						R.color.tab_grey));
				tv_tab_message.setTextColor(getResources().getColor(
						R.color.tab_grey));
				tv_tab_procedure.setTextColor(getResources().getColor(
						R.color.tab_grey));
				tv_tab_download.setTextColor(getResources().getColor(
						R.color.tab_blue));
				break;

			default:
				break;
			}

			// Animation animation = new TranslateAnimation(currIndex * one,
			// arg0
			// * one, 0, 0);// 平移动画
			// currIndex = arg0;
			// animation.setFillAfter(true);// 动画终止时停留在最后一帧，不然会回到没有执行前的状态
			// animation.setDuration(100);// 动画持续时间0.1秒
			// iv_bottom_line.startAnimation(animation);// 是用ImageView来显示动画的

		}
	}

	/**
	 * 内部类，MyFragmentPagerAdapter
	 * 
	 * @author whh
	 * 
	 */
	class MyFragmentPagerAdapter extends FragmentPagerAdapter {

		private ArrayList<Fragment> fragmentsList;

		/**
		 * @param fm
		 *            FragmentManager
		 */
		public MyFragmentPagerAdapter(FragmentManager fm,
				ArrayList<Fragment> fragments) {
			super(fm);
			this.fragmentsList = fragments;
		}

		@Override
		public Fragment getItem(int arg0) {
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
