/**
 * 
 */
package service.async.webservice;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import service.support.GetAgent;
import android.app.IntentService;
import android.content.Intent;
import android.os.Environment;
import edu.fudan.se.goalmachine.message.MesBody_Mes2Manager;
import edu.fudan.se.goalmachine.message.MesHeader_Mes2Manger;
import edu.fudan.se.goalmachine.message.SGMMessage;
import edu.fudan.se.goalmodel.EncodeDecodeRequestData;
import edu.fudan.se.goalmodel.RequestData;
import edu.fudan.se.initial.SGMApplication;

/**
 * 用来测试数据传递，这个服务给读取一张图片到程序内存中
 * 
 * @author whh
 * 
 */
public class TestIntentServiceSetImage extends IntentService {

	private String goalModelName, elementName;

	public TestIntentServiceSetImage() {
		super("TestIntentServiceSetImage");
	}

	public TestIntentServiceSetImage(String name) {
		super(name);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		goalModelName = intent.getExtras().getString("GOAL_MODEL_NAME");
		elementName = intent.getExtras().getString("ELEMENT_NAME");
	}

	@Override
	public void onDestroy() {
		System.out.println("TestIntentServiceSetImage onDestroy");

		InputStream fis = null;
		try {
			File sdCardDir = Environment.getExternalStorageDirectory();
			// 得到一个路径，内容是sdcard的文件夹路径和APP自身名字
			String image = sdCardDir.getPath() + "/sgm/test.png";
			fis = new FileInputStream(image);
		} catch (FileNotFoundException e) {
			System.out.println("TestIntentServiceSetImage read image error!!");
			e.printStackTrace();
		}

		// 在destory前把天气信息通过agent发送给manager
		SGMMessage msg = new SGMMessage(
				MesHeader_Mes2Manger.LOCAL_AGENT_MESSAGE, null, null, null,
				null, goalModelName, elementName,
				MesBody_Mes2Manager.ServiceExecutingDone);
		RequestData requestData = new RequestData("Image");
		requestData.setContent(EncodeDecodeRequestData.encodeInputStream(fis));
		msg.setContent(requestData);

		GetAgent.getAideAgentInterface((SGMApplication) getApplication())
				.sendMesToManager(msg);

		super.onDestroy();
	}

}
