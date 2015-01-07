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
		Fragment mesFragment = new MesFragment();

		fragmentsList.add(taskFragment);
		fragmentsList.add(mesFragment);

		mPager.setAdapter(new MyMessageFragmentPagerAdapter(
				getChildFragmentManager(), fragmentsList));
		mPager.setCurrentItem(0);
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
