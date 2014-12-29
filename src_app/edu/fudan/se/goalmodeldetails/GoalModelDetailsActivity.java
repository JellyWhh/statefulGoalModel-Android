/**
 * 
 */
package edu.fudan.se.goalmodeldetails;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import edu.fudan.se.R;
import edu.fudan.se.goalmodel.GoalModel;
import edu.fudan.se.initial.SGMApplication;

/**
 * @author whh
 *
 */
public class GoalModelDetailsActivity extends FragmentActivity {
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_goalmodeldetails);
		
		//获取传递过来的intent中的goal model
		Intent intent = getIntent();
		GoalModel goalModel = (GoalModel) intent.getSerializableExtra("goalmodel");
		Log.i("GoalModelDetailsActivity", ((SGMApplication)getApplication()).getAgentNickname());
		String agentNickname = ((SGMApplication)getApplication()).getAgentNickname();
		
		
		if (savedInstanceState == null) {
			FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
			transaction.add(R.id.container, new GoalModelDetailsFragment(goalModel, agentNickname)).commit();
//			getFragmentManager().beginTransaction()
//					.add(R.id.container, new MainFragment()).commit();
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
}
