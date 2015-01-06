/**
 * 
 */
package edu.fudan.se.goalmodel;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import edu.fudan.se.R;

/**
 * @author whh
 * 
 */
public class GoalModelAbstractFragment extends Fragment {
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_goalmodelabstract, null);
		return view;
	}
}