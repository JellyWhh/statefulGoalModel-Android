/**
 * 
 */
package edu.fudan.se.mainfragments;

import edu.fudan.se.R;
import edu.fudan.se.clientgui.SettingAgentActivity;
import edu.fudan.se.clientgui.StartAgentActivity;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * setting标签页
 * 
 * @author whh
 * 
 */
public class SettingFragment extends Fragment {

	private Button bt_setAgent;
	private Button bt_startAgent;

	static final int SETTINGS_REQUEST = 1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_setting, container,
				false);

//		bt_setAgent = (Button) rootView.findViewById(R.id.bt_setAgent);
//		bt_startAgent = (Button) rootView.findViewById(R.id.bt_startAgent);
//
//		bt_setAgent.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// 跳转到zjh写的设置界面
//				Intent showSettings = new Intent(getActivity(),
//						SettingAgentActivity.class);
//				startActivityForResult(showSettings, SETTINGS_REQUEST);
//			}
//		});
//
//		bt_startAgent.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// 跳转到zjh写的开始agent界面，也就是他代码中的MainActivity，在这里更名为了StartAgentActivity
//				Intent startAgent = new Intent(getActivity(),
//						StartAgentActivity.class);
//				startActivity(startAgent);
//
//			}
//		});

		return rootView;
	}
}
