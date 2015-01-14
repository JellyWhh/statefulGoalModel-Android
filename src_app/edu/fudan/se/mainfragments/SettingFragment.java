/**
 * 
 */
package edu.fudan.se.mainfragments;

import edu.fudan.se.R;
import edu.fudan.se.initial.SGMApplication;
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
import android.widget.RemoteViews;
import android.widget.TextView;

/**
 * setting标签页
 * 
 * @author whh
 * 
 */
public class SettingFragment extends Fragment {

	private Button button;
	private TextView textView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_setting, container,
				false);

		button = (Button) rootView.findViewById(R.id.bt_showNotification);
		textView = (TextView) rootView.findViewById(R.id.tv_showResult);
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// showNotification("Test title",
				// "test content!\n金州勇士官方宣布球队已经解雇了主帅马克-杰克逊，随后宣布了最后的结果。",
				// "New Msg From SGM!");
//				invokeWeatherService("testGoalModelName", "testElementName",
//						"Shanghai");
				textView.setText(((SGMApplication)getActivity().getApplication()).getLocation());
			}
		});
		return rootView;
	}

	private void invokeWeatherService(String goalModelName, String elementName,
			String city) {
		Intent serviceIntent = new Intent("service.intentservice.weather");
		Bundle bundle = new Bundle();
		bundle.putString("CITY", city);
		bundle.putString("GOAL_MODEL_NAME", goalModelName);
		bundle.putString("ELEMENT_NAME", elementName);
		serviceIntent.putExtras(bundle);
		getActivity().startService(serviceIntent);
	}

	

	private void showNotification(String title, String content, String ticker) {

		RemoteViews notification_view = new RemoteViews(getActivity()
				.getPackageName(), R.layout.view_notification);
		notification_view.setImageViewResource(R.id.notification_icon,
				R.drawable.app__launcher);
		notification_view.setTextViewText(R.id.tv_notification_title, title);
		notification_view
				.setTextViewText(R.id.tv_notification_content, content);

		NotificationManager mNotificationManager = (NotificationManager) getActivity()
				.getSystemService(Context.NOTIFICATION_SERVICE);

		Builder mBuilder = new Builder(getActivity());
		mBuilder.setContent(notification_view).setTicker(ticker)
				.setWhen(System.currentTimeMillis())
				.setPriority(Notification.PRIORITY_DEFAULT).setOngoing(false)
				.setDefaults(Notification.DEFAULT_ALL)
				.setSmallIcon(R.drawable.ic_launcher).setAutoCancel(true);

		// 点击的意图ACTION是跳转到Intent
		Intent resultIntent = new Intent(getActivity(), MainActivity.class);
		// resultIntent.putExtra("FLAG", "MESSAGE");
		resultIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent pendingIntent = PendingIntent.getActivity(getActivity(),
				0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(pendingIntent);

		Notification notification = mBuilder.build();
		notification.contentView = notification_view;
		mNotificationManager.notify(200, notification);
	}

}
