/**
 * 
 */
package edu.fudan.se.mainfragments;

import java.util.ArrayList;

import edu.fudan.se.R;
import edu.fudan.se.messageFragment.LogFragment;
import edu.fudan.se.messageFragment.TaskFragment;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
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
	private TextView tv_tab_task, tv_tab_message;

	public final static int num = 2;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_message, null);
		resources = getResources();
		initTextView(view);
		initViewPager(view);
		
		tv_tab_task.setTextColor(resources.getColor(R.color.focus_black));
		return view;
	}

	

	private void initTextView(View parentView) {
		tv_tab_task = (TextView) parentView.findViewById(R.id.tv_tab_task);
		tv_tab_message = (TextView) parentView
				.findViewById(R.id.tv_tab_message);

		tv_tab_task.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				tv_tab_task.setTextColor(resources.getColor(R.color.focus_black));
				tv_tab_message.setTextColor(resources.getColor(R.color.unfocus_grey));
				mPager.setCurrentItem(0);
			}
		});
		tv_tab_message.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				tv_tab_message.setTextColor(resources.getColor(R.color.focus_black));
				tv_tab_task.setTextColor(resources.getColor(R.color.unfocus_grey));
				mPager.setCurrentItem(1);
			}
		});
	}

	private void initViewPager(View parentView) {
		mPager = (ViewPager) parentView.findViewById(R.id.vp_message);
		fragmentsList = new ArrayList<Fragment>();

		Fragment taskFragment = new TaskFragment();
		Fragment mesFragment = new LogFragment();

		fragmentsList.add(taskFragment);
		fragmentsList.add(mesFragment);

		mPager.setAdapter(new MyMessageFragmentPagerAdapter(
				getChildFragmentManager(), fragmentsList));
		mPager.setOnPageChangeListener(new MyMessageOnPageChangeListener());
		mPager.setCurrentItem(1);
		mPager.setOffscreenPageLimit(1);
	}
	
	
	class MyMessageOnPageChangeListener implements OnPageChangeListener {

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
				tv_tab_task.setTextColor(resources.getColor(R.color.focus_black));
				tv_tab_message.setTextColor(resources.getColor(R.color.unfocus_grey));
				break;

			case 1:
				tv_tab_message.setTextColor(resources.getColor(R.color.focus_black));
				tv_tab_task.setTextColor(resources.getColor(R.color.unfocus_grey));
				break;
			}


		}
	}
	class MyMessageFragmentPagerAdapter extends FragmentPagerAdapter {
		private ArrayList<Fragment> fragmentsList;

		public MyMessageFragmentPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		public MyMessageFragmentPagerAdapter(FragmentManager fm,
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
	
}




