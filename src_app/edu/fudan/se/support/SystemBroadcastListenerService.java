/**
 * 
 */
package edu.fudan.se.support;

import java.text.SimpleDateFormat;
import java.util.Date;

import edu.fudan.se.goalmachine.message.MesBody_Mes2Manager;
import edu.fudan.se.goalmachine.message.MesHeader_Mes2Manger;
import edu.fudan.se.goalmachine.message.SGMMessage;
import edu.fudan.se.initial.SGMApplication;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

/**
 * 监听各种系统广播的服务，在应用启动时启动服务
 * 
 * @author whh
 * 
 */
public class SystemBroadcastListenerService extends Service {

	private BroadcastReceiver mReceiver;

	public SystemBroadcastListenerService() {
		mReceiver = new My_BroadcastReceiver();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		android.util.Log.i("MY_LOG",
				"-------SystemBroadcastListenerService onCreate()-------");
		IntentFilter mFilter = new IntentFilter();
		// mFilter.addAction("android.provider.Telephony.SMS_RECEIVED");// 新短信
		mFilter.addAction(Intent.ACTION_SCREEN_OFF);// 屏幕关闭
		mFilter.addAction(Intent.ACTION_HEADSET_PLUG);// 耳机的插入和拔出
		mFilter.addAction(Intent.ACTION_TIME_TICK); // 时间流逝
		registerReceiver(mReceiver, mFilter);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mReceiver);
	}

	private class My_BroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			SGMMessage msgToMessage = null;
			switch (action) {
			// case "android.provider.Telephony.SMS_RECEIVED":
			// android.util.Log
			// .i("MY_LOG",
			// "-------SystemBroadcastListenerService new SMS!!!-------");
			//
			// msgToMessage = new SGMMessage(
			// MesHeader_Mes2Manger.LOCAL_AGENT_MESSAGE, null, null,
			// null, MesBody_Mes2Manager.NewSMS);
			// break;
			case Intent.ACTION_SCREEN_OFF: // 屏幕被关闭
				android.util.Log
						.i("MY_LOG",
								"-------SystemBroadcastListenerService screen off!!!-------");
				msgToMessage = new SGMMessage(
						MesHeader_Mes2Manger.LOCAL_AGENT_MESSAGE, null, null,
						null, MesBody_Mes2Manager.NewSMS);
				break;

			case Intent.ACTION_HEADSET_PLUG: // 耳机的插入和拔出
				if (intent.hasExtra("state")) {
					if (intent.getIntExtra("state", 0) == 0) {// 0代表拔出，1代表插入
						android.util.Log
								.i("MY_LOG",
										"-------SystemBroadcastListenerService headset not connected!!!-------");
						// Toast.makeText(context, "headset not connected",
						// Toast.LENGTH_LONG).show();
					} else if (intent.getIntExtra("state", 0) == 1) {
						// Toast.makeText(context, "headset  connected",
						// Toast.LENGTH_LONG).show();

						android.util.Log
								.i("MY_LOG",
										"-------SystemBroadcastListenerService headset connected!!!-------");
						msgToMessage = new SGMMessage(
								MesHeader_Mes2Manger.LOCAL_AGENT_MESSAGE, null,
								null, null, MesBody_Mes2Manager.NewSMS);

					}
				}
				break;

			case Intent.ACTION_TIME_TICK: // 时间流逝，1s触发一次
				SimpleDateFormat formatter = new SimpleDateFormat("hh:mm:ss");
				String nowTime = formatter.format(new Date(System
						.currentTimeMillis()));
				switch (nowTime) {
				case "01:00:00":
					msgToMessage = new SGMMessage(
							MesHeader_Mes2Manger.LOCAL_AGENT_MESSAGE, null,
							null, null, MesBody_Mes2Manager.Time1);
					break;
				case "02:00:00":
					msgToMessage = new SGMMessage(
							MesHeader_Mes2Manger.LOCAL_AGENT_MESSAGE, null,
							null, null, MesBody_Mes2Manager.Time2);
					break;
				case "03:00:00":
					msgToMessage = new SGMMessage(
							MesHeader_Mes2Manger.LOCAL_AGENT_MESSAGE, null,
							null, null, MesBody_Mes2Manager.Time3);
					break;
				case "04:00:00":
					msgToMessage = new SGMMessage(
							MesHeader_Mes2Manger.LOCAL_AGENT_MESSAGE, null,
							null, null, MesBody_Mes2Manager.Time4);
					break;
				case "05:00:00":
					msgToMessage = new SGMMessage(
							MesHeader_Mes2Manger.LOCAL_AGENT_MESSAGE, null,
							null, null, MesBody_Mes2Manager.Time5);
					break;
				case "06:00:00":
					msgToMessage = new SGMMessage(
							MesHeader_Mes2Manger.LOCAL_AGENT_MESSAGE, null,
							null, null, MesBody_Mes2Manager.Time6);
					break;
				case "07:00:00":
					msgToMessage = new SGMMessage(
							MesHeader_Mes2Manger.LOCAL_AGENT_MESSAGE, null,
							null, null, MesBody_Mes2Manager.Time7);
					break;
				case "08:00:00":
					msgToMessage = new SGMMessage(
							MesHeader_Mes2Manger.LOCAL_AGENT_MESSAGE, null,
							null, null, MesBody_Mes2Manager.Time8);
					break;
				case "09:00:00":
					msgToMessage = new SGMMessage(
							MesHeader_Mes2Manger.LOCAL_AGENT_MESSAGE, null,
							null, null, MesBody_Mes2Manager.Time9);
					break;
				case "10:00:00":
					msgToMessage = new SGMMessage(
							MesHeader_Mes2Manger.LOCAL_AGENT_MESSAGE, null,
							null, null, MesBody_Mes2Manager.Time10);
					break;
				case "11:00:00":
					msgToMessage = new SGMMessage(
							MesHeader_Mes2Manger.LOCAL_AGENT_MESSAGE, null,
							null, null, MesBody_Mes2Manager.Time11);
					break;
				case "12:00:00":
					msgToMessage = new SGMMessage(
							MesHeader_Mes2Manger.LOCAL_AGENT_MESSAGE, null,
							null, null, MesBody_Mes2Manager.Time12);
					break;
				case "13:00:00":
					msgToMessage = new SGMMessage(
							MesHeader_Mes2Manger.LOCAL_AGENT_MESSAGE, null,
							null, null, MesBody_Mes2Manager.Time13);
					break;
				case "14:00:00":
					msgToMessage = new SGMMessage(
							MesHeader_Mes2Manger.LOCAL_AGENT_MESSAGE, null,
							null, null, MesBody_Mes2Manager.Time14);
					break;
				case "15:00:00":
					msgToMessage = new SGMMessage(
							MesHeader_Mes2Manger.LOCAL_AGENT_MESSAGE, null,
							null, null, MesBody_Mes2Manager.Time15);
					break;
				case "16:00:00":
					msgToMessage = new SGMMessage(
							MesHeader_Mes2Manger.LOCAL_AGENT_MESSAGE, null,
							null, null, MesBody_Mes2Manager.Time16);
					break;
				case "17:00:00":
					msgToMessage = new SGMMessage(
							MesHeader_Mes2Manger.LOCAL_AGENT_MESSAGE, null,
							null, null, MesBody_Mes2Manager.Time17);
					break;
				case "18:00:00":
					msgToMessage = new SGMMessage(
							MesHeader_Mes2Manger.LOCAL_AGENT_MESSAGE, null,
							null, null, MesBody_Mes2Manager.Time18);
					break;
				case "19:00:00":
					msgToMessage = new SGMMessage(
							MesHeader_Mes2Manger.LOCAL_AGENT_MESSAGE, null,
							null, null, MesBody_Mes2Manager.Time19);
					break;
				case "20:00:00":
					msgToMessage = new SGMMessage(
							MesHeader_Mes2Manger.LOCAL_AGENT_MESSAGE, null,
							null, null, MesBody_Mes2Manager.Time20);
					break;
				case "21:00:00":
					msgToMessage = new SGMMessage(
							MesHeader_Mes2Manger.LOCAL_AGENT_MESSAGE, null,
							null, null, MesBody_Mes2Manager.Time21);
					break;
				case "22:00:00":
					msgToMessage = new SGMMessage(
							MesHeader_Mes2Manger.LOCAL_AGENT_MESSAGE, null,
							null, null, MesBody_Mes2Manager.Time22);
					break;

				case "23:00:00":
					msgToMessage = new SGMMessage(
							MesHeader_Mes2Manger.LOCAL_AGENT_MESSAGE, null,
							null, null, MesBody_Mes2Manager.Time23);
					break;

				default:
					android.util.Log.i("MY_LOG",
							"-------ACTION_TIME_TICK time!!!! nowTime: "
									+ nowTime + " -------");
					break;
				}

				// android.util.Log
				// .i("MY_LOG",
				// "-------SystemBroadcastListenerService ACTION_TIME_TICK time!!!!-------");

				break;

			default:
				break;
			}

			if (msgToMessage != null) {
				GetAgent.getAideAgentInterface(
						(SGMApplication) getApplication()).sendMesToManager(
						msgToMessage);
			}

		}
	}

}
