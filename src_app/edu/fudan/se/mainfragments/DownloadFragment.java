/**
 * 
 */
package edu.fudan.se.mainfragments;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import edu.fudan.se.R;
import edu.fudan.se.goalmodel.GmXMLParser;
import edu.fudan.se.goalmodel.GoalModel;
import edu.fudan.se.goalmodel.GoalModelManager;
import edu.fudan.se.initial.SGMApplication;
import edu.fudan.se.support.DownloadTask;

/**
 * 从服务器上下载goal model xml文件的fragment
 * 
 * @author whh
 * 
 */
public class DownloadFragment extends ListFragment {

	private SGMApplication application; // 获取应用程序，以得到里面的全局变量

	private DownloadListAdapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		application = (SGMApplication) getActivity().getApplication();

		adapter = new DownloadListAdapter(getActivity(),
				R.layout.listview_download, application.getDownloadTaskList(),
				application.getGoalModelManager());

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		setListAdapter(adapter);

		getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

	}

}

/**
 * 用于DownloadFragment的list适配器<br/>
 * 
 * @author whh
 * 
 * @param <T>
 */
class DownloadListAdapter extends ArrayAdapter<DownloadTask> {

	private List<DownloadTask> mObjects;
	private int mResource;
	private Context mContext;
	private LayoutInflater mInflater;
	private GoalModelManager goalModelManager;

	public DownloadListAdapter(Context context, int resource,
			List<DownloadTask> objects, GoalModelManager goalModelManager) {
		super(context, resource, objects);
		init(context, resource, objects, goalModelManager);
	}

	private void init(Context context, int resource,
			List<DownloadTask> objects, GoalModelManager goalModelManager) {
		mContext = context;
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mResource = resource;
		mObjects = objects;
		this.goalModelManager = goalModelManager;
	}

	@Override
	public int getCount() {
		return this.mObjects.size();
	}

	@Override
	public DownloadTask getItem(int position) {
		return this.mObjects.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return createViewFromResource(position, convertView, parent, mResource);
	}

	private View createViewFromResource(int position, View convertView,
			ViewGroup parent, int resource) {
		ViewHolder holder;

		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(resource, parent, false);

			holder.text = (TextView) convertView
					.findViewById(R.id.tv_download_filename);
			holder.icon = (ImageView) convertView
					.findViewById(R.id.iv_download);

			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		// 下面部分不可缺少，是设置每个item具体显示的地方！
		final DownloadTask downloadTask = getItem(position);
		holder.text.setText(downloadTask.getName());
		if (downloadTask.isDownload()) {
			holder.icon.setImageResource(R.drawable.goal_download_image);
			holder.icon.setClickable(false);
		} else {
			holder.icon.setImageResource(R.drawable.goal_nodownload_image);
			// 还没有下载的要添加下载按钮的监听器，点击后下载
			holder.icon.setClickable(true);
			holder.icon.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					ExecutorService executorService = Executors.newCachedThreadPool();
					DownLoadGoalModelTask task = new DownLoadGoalModelTask(downloadTask);
					Future<Integer> result=executorService.submit(task);
					try {
						System.out.println("下载成功： " + result.get());
					} catch (InterruptedException | ExecutionException e) {
						e.printStackTrace();
					}
				}
			});
		}

		return convertView;
	}
	
	class DownLoadGoalModelTask implements Callable<Integer>{
		
		private DownloadTask downloadTask;
		private DownLoadGoalModelTask(DownloadTask downloadTask){
			this.downloadTask = downloadTask;
		}

		@Override
		public Integer call() throws Exception {
			downloadGoalModel(downloadTask);
			return 1;
		}
		
	}

	/**
	 * 从服务器上下载一个goal model xml文件保存到本地，并解析它，将解析出来的goal model添加到goalModelManager中
	 * 
	 * @param downloadTask
	 *            要下载的goal model的downloadTask
	 */
	private void downloadGoalModel(DownloadTask downloadTask) {
		String sdCardDir = Environment.getExternalStorageDirectory().getPath()
				+ "/sgm/fxml/";
		try {
			URL url = new URL(downloadTask.getUrl());
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setRequestMethod("GET");
			connection.setConnectTimeout(5000);// 设置超时时间为5s
			if (connection.getResponseCode() == 200) {
				InputStream inputStream = connection.getInputStream(); // 获得输入流
				File file = new File(sdCardDir + downloadTask.getName());
				FileOutputStream fileOutputStream = new FileOutputStream(file); // 对应文件建立输出流
				byte[] buffer = new byte[1024];// 新建缓存, 用来存储从网络读取数据 再写入文件
				int len = 0;
				while ((len = inputStream.read(buffer)) != -1) {// 当没有读到最后的时候
					fileOutputStream.write(buffer, 0, len);
				}
				fileOutputStream.flush();
				fileOutputStream.close();
				inputStream.close();

				// 文件已经保存到sbCard里了，解析它
				GmXMLParser gmXMLParser = new GmXMLParser();
				GoalModel goalModel = gmXMLParser.newGoalModel(sdCardDir
						+ downloadTask.getName());
				goalModelManager.addGoalModel(goalModel);

				// 把downloadTask标记为已下载
				downloadTask.setDownload(true);
				notifyDataSetChanged();
			} else {
				System.err
						.println("DownloadFragment--downloadGoalModel()--网络连接失败, Error Code: "
								+ connection.getResponseCode());
			}
		} catch (IOException e) {
			System.err
					.println("DownloadFragment--downloadGoalModel()--IOException");
			e.printStackTrace();
		}
	}

	class ViewHolder {
		TextView text;
		ImageView icon;
	}

}