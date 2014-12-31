/**
 * 
 */
package edu.fudan.se.mainfragments;


import java.util.ArrayList;

import edu.fudan.se.R;
import edu.fudan.se.messageFragment.MesFragment;
import edu.fudan.se.messageFragment.TaskFragment;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics; 
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * message标签页
 * 
 * @author whh
 * 
 */
public class MessageFragment extends Fragment {

	Resources resources;
	private ViewPager mPager;
	private ArrayList<Fragment> fragmentsList;
	private ImageView iv_bottom_line;
	private TextView tv_tab_task, tv_tab_message;

	private int currIndex = 0;
	private int bottomLineWidth;
	private int offset = 0;
	private int position_one;
	public final static int num = 2;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_message, null);
		resources = getResources();

		initWidth(view);
		initTextView(view);
		initViewPager(view);

		TranslateAnimation animation = new TranslateAnimation(position_one,
				offset, 0, 0);
		tv_tab_task.setTextColor(resources.getColor(R.color.focus_black));
		animation.setFillAfter(true);
		animation.setDuration(300);
		iv_bottom_line.startAnimation(animation);
		return view;
	}

	private void initWidth(View parentView) {
		iv_bottom_line = (ImageView) parentView
				.findViewById(R.id.iv_bottom_line);
		bottomLineWidth = iv_bottom_line.getLayoutParams().width;
		DisplayMetrics dm = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenW = dm.widthPixels;
		offset = (int) ((screenW / num - bottomLineWidth) / 2);
		int avg = (int) (screenW / num);
		position_one = avg + offset;
	}

	private void initTextView(View parentView) {
		tv_tab_task = (TextView) parentView.findViewById(R.id.tv_tab_task);
		tv_tab_message = (TextView) parentView
				.findViewById(R.id.tv_tab_message);

		tv_tab_task.setOnClickListener(new MyOnClickListener(0));
		tv_tab_message.setOnClickListener(new MyOnClickListener(1));
	}

	private void initViewPager(View parentView) {
		mPager = (ViewPager) parentView.findViewById(R.id.vp_message);
		fragmentsList = new ArrayList<Fragment>();

		Fragment taskFragment = new TaskFragment();
		Fragment mesFragment = new MesFragment();

		fragmentsList.add(taskFragment);
		fragmentsList.add(mesFragment);

		mPager.setAdapter(new MyFragmentPagerAdapter(getChildFragmentManager(),
				fragmentsList));
		mPager.setOnPageChangeListener(new MyOnPageChangeListener());
		mPager.setCurrentItem(0);
	}

	public class MyOnClickListener implements View.OnClickListener {
		private int index = 0;

		public MyOnClickListener(int i) {
			index = i;
		}

		@Override
		public void onClick(View v) {
			mPager.setCurrentItem(index);
		}
	};

	public class MyOnPageChangeListener implements OnPageChangeListener {

		@Override
		public void onPageSelected(int arg0) {
			Animation animation = null;
			switch (arg0) {
			case 0:
				if (currIndex == 1) {
					animation = new TranslateAnimation(position_one, offset, 0,
							0);
					tv_tab_message.setTextColor(resources
							.getColor(R.color.unfocus_grey));
				}
				tv_tab_task.setTextColor(resources
						.getColor(R.color.focus_black));
				break;
			case 1:
				if (currIndex == 0) {
					animation = new TranslateAnimation(offset, position_one, 0,
							0);
					tv_tab_task.setTextColor(resources
							.getColor(R.color.unfocus_grey));
				}
				tv_tab_message.setTextColor(resources
						.getColor(R.color.focus_black));
				break;
			}
			currIndex = arg0;
			animation.setFillAfter(true);
			animation.setDuration(300);
			iv_bottom_line.startAnimation(animation);
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
	}
}

class MyFragmentPagerAdapter extends FragmentPagerAdapter {
	private ArrayList<Fragment> fragmentsList;

	public MyFragmentPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	public MyFragmentPagerAdapter(FragmentManager fm,
			ArrayList<Fragment> fragments) {
		super(fm);
		this.fragmentsList = fragments;
	}

	@Override
	public int getCount() {
		return fragmentsList.size();
	}

	@Override
	public Fragment getItem(int arg0) {
		return fragmentsList.get(arg0);
	}

	@Override
	public int getItemPosition(Object object) {
		return super.getItemPosition(object);
	}

}
