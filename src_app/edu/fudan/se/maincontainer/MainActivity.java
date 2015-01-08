package edu.fudan.se.maincontainer;

import edu.fudan.se.R;
import edu.fudan.se.mainfragments.MainFragment;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat.Builder;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RemoteViews;

/**
 * 主体activity，加载了一个fragment
 * 
 * @author whh
 * 
 */
public class MainActivity extends FragmentActivity {

	private MyReceiver myReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		if (savedInstanceState == null) {
			// 休眠5s是为了让agent能够启动起来，不然在MessageFragment里得不到agent的引用
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			FragmentTransaction transaction = getSupportFragmentManager()
					.beginTransaction();
			transaction.add(R.id.container, new MainFragment()).commit();
		}

		// 处理agent弹窗相关
		myReceiver = new MyReceiver();
		IntentFilter refreshChatFilter = new IntentFilter();
		refreshChatFilter.addAction("jade.task.NOTIFICATION");
		refreshChatFilter.addAction("jade.mes.NOTIFICATION");
		registerReceiver(myReceiver, refreshChatFilter);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		// 点击返回键后不会退出程序，也就是再次进来的时候还是原来的运行状态
		this.moveTaskToBack(true);
		return;
	}

	/**
	 * 用来监听agent发来的弹窗UI的消息
	 * 
	 * @author whh
	 * 
	 */
	private class MyReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equalsIgnoreCase("jade.task.NOTIFICATION")) {
				showNotification("New Task",
						intent.getExtras().getString("Content"),
						"New Task From SGM!");
			}
			if (action.equalsIgnoreCase("jade.mes.NOTIFICATION")) {
				showNotification("New Mes",
						intent.getExtras().getString("Content"),
						"New Mes from SGM!");
			}
		}

	}

	/**
	 * 弹出一个通知
	 * 
	 * @param title
	 *            通知的title
	 * @param content
	 *            通知的内容
	 * @param ticker
	 *            通知的提示
	 */
	private void showNotification(String title, String content, String ticker) {
		
		RemoteViews notification_view=new RemoteViews(getPackageName(), R.layout.view_notification);
		notification_view.setImageViewResource(R.id.notification_icon, R.drawable.app__launcher);
		notification_view.setTextViewText(R.id.tv_notification_title, title);
		notification_view.setTextViewText(R.id.tv_notification_content, content);

		NotificationManager mNotificationManager = (NotificationManager) this
				.getSystemService(Context.NOTIFICATION_SERVICE);

		Builder mBuilder = new Builder(this);
		mBuilder.setContent(notification_view)
				.setTicker(ticker).setWhen(System.currentTimeMillis())
				.setPriority(Notification.PRIORITY_DEFAULT).setOngoing(false)
				.setDefaults(Notification.DEFAULT_ALL)
				.setSmallIcon(R.drawable.ic_launcher).setAutoCancel(true);

		// 点击的意图ACTION是跳转到Intent
		Intent resultIntent = new Intent(this, MainActivity.class);
		// resultIntent.putExtra("FLAG", "MESSAGE");
		resultIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
				resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(pendingIntent);

		Notification notification = mBuilder.build();
		notification.contentView = notification_view;
		mNotificationManager.notify(200, notification);
	}

}
