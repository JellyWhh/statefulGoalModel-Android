/**
 * 
 */
package edu.fudan.se.mainfragments;

import edu.fudan.se.R;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat.Builder;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
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

	private Button bt_showNotification;

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
		bt_showNotification = (Button) rootView
				.findViewById(R.id.bt_showNotification);
		bt_showNotification.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showNotification();
			}
		});

		return rootView;
	}

	/**
	 * 弹出一个通知
	 */
	private void showNotification() {
		
		NotificationManager mNotificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE); 
		
		Builder mBuilder = new Builder(getActivity());
		mBuilder.setContentTitle("Test Title").setContentText("Test Content")
				.setTicker("New Notification from SGM!")
				.setWhen(System.currentTimeMillis())
				.setPriority(Notification.PRIORITY_HIGH).setOngoing(false)
				.setDefaults(Notification.DEFAULT_VIBRATE)
				.setSmallIcon(R.drawable.ic_launcher);
		
		Notification notification = mBuilder.build();
		mNotificationManager.notify(200, notification);
	}
}
