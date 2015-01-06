/**
 * 
 */
package edu.fudan.se.goalmodel;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import edu.fudan.se.R;

/**
 * @author whh
 * 
 */
public class GoalModelAbstractFragment extends Fragment {

	private GoalModel goalModel; // 要显示目标树的goal model
	private TextView tv_gm_description;

	public GoalModelAbstractFragment(GoalModel goalModel) {
		this.goalModel = goalModel;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_goalmodelabstract, null);

		tv_gm_description = (TextView) view
				.findViewById(R.id.tv_gm_description);
		tv_gm_description.setText(goalModel.getDescription());
		
		return view;
	}
}