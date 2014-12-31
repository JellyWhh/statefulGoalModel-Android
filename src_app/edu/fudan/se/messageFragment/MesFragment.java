/**
 * 
 */
package edu.fudan.se.messageFragment;

import edu.fudan.se.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author whh
 *
 */
public class MesFragment extends Fragment{
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_message_mes, null);
		return view;
	}
}
