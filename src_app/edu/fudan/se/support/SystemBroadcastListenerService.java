/**
 * 
 */
package edu.fudan.se.support;

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
		mFilter.addAction("android.provider.Telephony.SMS_RECEIVED");// 新短信
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
			case "android.provider.Telephony.SMS_RECEIVED":
				android.util.Log
						.i("MY_LOG",
								"-------SystemBroadcastListenerService new SMS!!!-------");

				msgToMessage = new SGMMessage(
						MesHeader_Mes2Manger.LOCAL_AGENT_MESSAGE, null, null,
						null, MesBody_Mes2Manager.NewSMS);
				break;

			case Intent.ACTION_HEADSET_PLUG: // 耳机的插入和拔出
				if (intent.hasExtra("state")) {
					if (intent.getIntExtra("state", 0) == 0) {// 0代表拔出，1代表插入
						// Toast.makeText(context, "headset not connected",
						// Toast.LENGTH_LONG).show();
					} else if (intent.getIntExtra("state", 0) == 1) {
						// Toast.makeText(context, "headset  connected",
						// Toast.LENGTH_LONG).show();

						android.util.Log
								.i("MY_LOG",
										"-------SystemBroadcastListenerService headset  connected!!!-------");
						msgToMessage = new SGMMessage(
								MesHeader_Mes2Manger.LOCAL_AGENT_MESSAGE, null,
								null, null, MesBody_Mes2Manager.NewSMS);

					}
				}
				break;

			case Intent.ACTION_TIME_TICK: // 耳机的插入和拔出

				android.util.Log
						.i("MY_LOG",
								"-------SystemBroadcastListenerService ACTION_TIME_TICK time!!!!-------");

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
