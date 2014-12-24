/**
 * 
 */
package edu.fudan.se.goalmodeldetails;

import edu.fudan.se.R;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.PopupWindow;

/**
 *  实现目标模型详细情况页面，点击下面的order按钮时的弹出框样式
 * @author whh
 *
 */
public class SelectOrderPopupWindow extends PopupWindow {

	private Button bt_dialog_start, bt_dialog_suspend, bt_dialog_resume,
			bt_dialog_stop, bt_dialog_refresh, bt_dialog_cancel;
	private View mMenuView;

	/**
	 * 构造方法
	 * 
	 * @param context
	 *            activity上下文
	 * @param itemsOnClick
	 *            为弹出框里面的按钮添加的点击事件监听器
	 */
	public SelectOrderPopupWindow(LayoutInflater mInflater,
			OnClickListener itemsOnClick) {
		super();
//		LayoutInflater mInflater = (LayoutInflater) context
//				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mMenuView = mInflater.inflate(R.layout.popupwindow_dialog, null);

		bt_dialog_start = (Button) mMenuView.findViewById(R.id.bt_dialog_start);
		bt_dialog_suspend = (Button) mMenuView
				.findViewById(R.id.bt_dialog_suspend);
		bt_dialog_resume = (Button) mMenuView
				.findViewById(R.id.bt_dialog_resume);
		bt_dialog_stop = (Button) mMenuView.findViewById(R.id.bt_dialog_stop);
		bt_dialog_refresh = (Button) mMenuView
				.findViewById(R.id.bt_dialog_refresh);
		bt_dialog_cancel = (Button) mMenuView
				.findViewById(R.id.bt_dialog_cancel);

		// 设置按钮监听
		bt_dialog_start.setOnClickListener(itemsOnClick);
		bt_dialog_suspend.setOnClickListener(itemsOnClick);
		bt_dialog_resume.setOnClickListener(itemsOnClick);
		bt_dialog_stop.setOnClickListener(itemsOnClick);
		bt_dialog_refresh.setOnClickListener(itemsOnClick);
		bt_dialog_cancel.setOnClickListener(itemsOnClick);

		// 设置SelectPicPopupWindow的View
		this.setContentView(mMenuView);
		// 设置SelectPicPopupWindow弹出窗体的宽
		this.setWidth(LayoutParams.MATCH_PARENT);
		// 设置SelectPicPopupWindow弹出窗体的高
		this.setHeight(LayoutParams.WRAP_CONTENT);
		// 设置SelectPicPopupWindow弹出窗体可点击
		this.setFocusable(true);
		// 设置SelectPicPopupWindow弹出窗体动画效果
		this.setAnimationStyle(R.style.AnimBottom);
		// 实例化一个ColorDrawable颜色为半透明
		ColorDrawable dw = new ColorDrawable(0xb0000000);
		// 设置SelectPicPopupWindow弹出窗体的背景
		this.setBackgroundDrawable(dw);


		// mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
		mMenuView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int height = mMenuView.findViewById(R.id.popdialog_layout)
						.getTop();
				int y = (int) event.getY();
				if (event.getAction() == MotionEvent.ACTION_UP) {
					if (y < height) {
						dismiss();
					}
				}
				return true;
			}
		});

	}


}
