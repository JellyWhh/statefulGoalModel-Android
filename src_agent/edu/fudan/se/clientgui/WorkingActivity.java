/**
 * 
 */
package edu.fudan.se.clientgui;

import java.util.Timer;
import java.util.TimerTask;

import edu.fudan.se.R;
import edu.fudan.se.pool.Message;
import edu.fudan.se.pool.Pool;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author zjh
 *
 */
public class WorkingActivity extends Activity {


	final static Message NoMessage = new Message();
	final static String NULLSTRING = new String("NULLSTRING");
	Message currentMsg = NoMessage;
	TextView hint = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_working);
		
		hint = (TextView)findViewById(R.id.hint);
		hint.setText(NULLSTRING);
		
		final Button accept = (Button)findViewById(R.id.accept);
		final Button reject = (Button)findViewById(R.id.reject);
		accept.setEnabled(false);
		reject.setEnabled(false);
		
		
		accept.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				currentMsg.setConfirm("accept");
				Pool.setDOutMessage(currentMsg);
				System.out.println("SetDoutmsg"+currentMsg);
				currentMsg = NoMessage;
				hint.setText(NULLSTRING);
				accept.setEnabled(false);
				reject.setEnabled(false);
				Toast.makeText(WorkingActivity.this, "操作成功",Toast.LENGTH_LONG);
				
			}
		} );
		
		
		reject.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				currentMsg.setConfirm("reject");
				Pool.setDOutMessage(currentMsg);
				currentMsg = NoMessage;
				hint.setText(NULLSTRING);
				accept.setEnabled(false);
				reject.setEnabled(false);
				Toast.makeText(WorkingActivity.this, "操作成功",Toast.LENGTH_LONG);
			}
		});
 
		final Handler handler = new Handler(){
			@Override
			public void handleMessage(android.os.Message message) {
				// TODO Auto-generated method stub
				if(message.what == 0x123){
					
					if(currentMsg == NoMessage){//代表用户当前没有可以处理的消息，那么此时可以考虑下一个消息是否到来，让用户可以处理
						currentMsg = Pool.getDInMessage();
						if(currentMsg != null){//代表消息池里面有到来的消息需要用户处理
							System.out.println("admin  "+currentMsg.toString());
							hint.setText("新消息来了:"+currentMsg.toString());
							accept.setEnabled(true);
							reject.setEnabled(true);
						}else currentMsg = NoMessage;//此处debug将近8小时。。。。。。也是醉了
					}
				}			
			}
		};
	
		new Timer().schedule(new TimerTask() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				android.os.Message msg = new android.os.Message();
				msg.what = 0x123;
				handler.sendMessage(msg);
			}
		}, 0, 2000);
		
	
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