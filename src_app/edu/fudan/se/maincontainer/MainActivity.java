package edu.fudan.se.maincontainer;

import edu.fudan.se.R;
import edu.fudan.se.initial.SGMApplication;
import edu.fudan.se.mainfragments.MainFragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;

/**
 * 主体activity，加载了一个fragment
 * 
 * @author whh
 * 
 */
public class MainActivity extends FragmentActivity {
	
//	private int initialItem = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		if (savedInstanceState == null) {
			//休眠5s是为了让agent能够启动起来，不然在MessageFragment里得不到agent的引用
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String agentNickname = ((SGMApplication) getApplication())
					.getAgentNickname();
			FragmentTransaction transaction = getSupportFragmentManager()
					.beginTransaction();
			transaction.add(R.id.container, new MainFragment(0)).commit();
		}
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
	
//	@Override
//	protected void onNewIntent(Intent intent){
//		super.onNewIntent(intent);
//		String flagFragment= intent.getStringExtra("FLAG");
//		if (flagFragment.equals("MESSAGE")) {
//			this.setInitialItem(0);
//		}
//	}

	@Override
	public void onBackPressed() {
		// 点击返回键后不会退出程序，也就是再次进来的时候还是原来的运行状态
		this.moveTaskToBack(true);
		return;
	}

//	public int getInitialItem() {
//		return initialItem;
//	}
//
//	public void setInitialItem(int initialItem) {
//		this.initialItem = initialItem;
//	}

}
