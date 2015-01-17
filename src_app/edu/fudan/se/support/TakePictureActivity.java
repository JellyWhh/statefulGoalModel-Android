/**
 * 
 */
package edu.fudan.se.support;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import edu.fudan.se.R;
import edu.fudan.se.agent.AideAgentInterface;
import edu.fudan.se.goalmachine.message.MesBody_Mes2Manager;
import edu.fudan.se.goalmachine.message.MesHeader_Mes2Manger;
import edu.fudan.se.goalmachine.message.SGMMessage;
import edu.fudan.se.goalmodel.EncodeDecodeRequestData;
import edu.fudan.se.goalmodel.RequestData;
import edu.fudan.se.initial.SGMApplication;
import edu.fudan.se.maincontainer.MainActivity;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

/**
 * 拍照，在用户收到一个<code>UserTakePictureTask</code>任务后，点击上面的camera按钮会跳转到这个activity
 * 
 * @author whh
 * 
 */
public class TakePictureActivity extends Activity {

	private ImageView iv_show_pic;
	private Button bt_take_pic, bt_ok;

	private Bitmap bitmap;

	private AideAgentInterface aideAgentInterface; // agent interface
	private String goalModelName, elementName;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_takepicture);

		aideAgentInterface = GetAgent
				.getAideAgentInterface((SGMApplication) this.getApplication());

		Intent intent = getIntent();
		goalModelName = intent.getStringExtra("goalmodelname");
		elementName = intent.getStringExtra("elementname");

		iv_show_pic = (ImageView) findViewById(R.id.iv_show_pic);
		bt_take_pic = (Button) findViewById(R.id.bt_take_pic);
		bt_ok = (Button) findViewById(R.id.bt_save_pic);

		bt_take_pic.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				startActivityForResult(intent, 1);
			}
		});

		bt_ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				savePicToLocal(bitmap);
				sendPicToAgent(bitmap);
				
				Intent intent = new Intent();
				intent.setClass(TakePictureActivity.this, MainActivity.class);
				startActivity(intent);
			}
		});

		bt_ok.setClickable(false);
		bt_ok.setTextColor(getResources().getColor(R.color.unclickable_grey));
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == Activity.RESULT_OK) {
			Bundle bundle = data.getExtras();
			bitmap = (Bitmap) bundle.get("data");// 获取相机返回的数据，并转换为Bitmap图片格式
			iv_show_pic.setImageBitmap(bitmap);
			bt_ok.setClickable(true);
			bt_ok.setTextColor(getResources().getColor(R.color.clickable_black));
		}
	}

	/**
	 * 把图片保存到本地
	 * 
	 * @param bitmap
	 */
	private void savePicToLocal(Bitmap bitmap) {
		System.out.println("MY_LOG-TakePictureActivity--savePicToLocal()");

		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddhhmmss");
		String picName = df.format(new Date()) + ".jpg";

		File picture = new File(Environment.getExternalStorageDirectory()
				+ "/sgm/pic/" + picName);

		try {
			// 保存图片到本地
			FileOutputStream fos = new FileOutputStream(picture.getPath());
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);// 把数据写入文件
			fos.flush();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 告诉agent拍照任务做完了，让其通知相关的element machine，同时把Image数据传回去
	 * 
	 * @param data
	 *            图片数据
	 */
	private void sendPicToAgent(Bitmap bitmap) {
		System.out.println("MY_LOG-TakePictureActivity--sendPicToAgent()");

		SGMMessage msg = new SGMMessage(
				MesHeader_Mes2Manger.LOCAL_AGENT_MESSAGE, null, null, null,
				null, goalModelName, elementName,
				MesBody_Mes2Manager.ServiceExecutingDone);
		RequestData requestData = new RequestData("Image");
		requestData.setContent(EncodeDecodeRequestData.encodeBitmap(bitmap));
		msg.setContent(requestData);

		aideAgentInterface.sendMesToManager(msg);
	}

}
