/**
 * 
 */
package edu.fudan.se.mainfragments;

import edu.fudan.se.R;
import edu.fudan.se.maincontainer.MainActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat.Builder;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
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

		NotificationManager mNotificationManager = (NotificationManager) getActivity()
				.getSystemService(Context.NOTIFICATION_SERVICE);

		Builder mBuilder = new Builder(getActivity());
		mBuilder.setContentTitle("Test Title").setContentText("Test Content")
				.setTicker("New Notification from SGM!")
				.setWhen(System.currentTimeMillis())
				.setPriority(Notification.PRIORITY_HIGH).setOngoing(false)
				.setDefaults(Notification.DEFAULT_ALL)
				.setSmallIcon(R.drawable.ic_launcher).setAutoCancel(true);

		// 点击的意图ACTION是跳转到Intent
		Intent resultIntent = new Intent(getActivity(), MainActivity.class);
		//resultIntent.putExtra("FLAG", "MESSAGE");
		resultIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent pendingIntent = PendingIntent.getActivity(getActivity(),
				0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(pendingIntent);

		Notification notification = mBuilder.build();
		mNotificationManager.notify(200, notification);
	}
}
