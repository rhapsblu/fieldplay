package com.jmie.fieldplay.route;

import java.util.ArrayList;

import java.util.List;

import com.jmie.fieldplay.R;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

public class ReferenceFragment extends Fragment {
	private Route route;
	private List<Reference> refList;
	private ListView lv;
	private ReferenceListAdapter refListAdapter;
	
	private Context c;

	
	public static ReferenceFragment newInstance(Bundle b, Context c){
		ReferenceFragment fragment = new ReferenceFragment(c);
		fragment.setArguments(b);

		return fragment;
	}
	public ReferenceFragment(Context c){
		this.c = c;
	}
	@Override
	public void onCreate(Bundle savedinstance){
		super.onCreate(savedinstance);
		setRetainInstance(true);

	}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	if(refList.size()==0) return getDefaultView();
        View rootView = inflater.inflate(R.layout.reference_fragment, container, false);


        lv = (ListView) rootView.findViewById(R.id.referencelist);
		refListAdapter = new ReferenceListAdapter(this);
	
		lv.setAdapter(refListAdapter);
		lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parentAdapter, View view, int position,
					long id) {
	            for (int j = 0; j < parentAdapter.getChildCount(); j++)
	                parentAdapter.getChildAt(j).setBackgroundColor(Color.TRANSPARENT);

	            // change the background color of the selected element
	            view.setBackgroundColor(Color.LTGRAY);
	            //				TextView clickedView = (TextView) view;
	
			}
		});
        return rootView;
    }
    @Override
    public void setArguments(Bundle b){
    	super.setArguments(b);
		refList = new ArrayList<Reference>();
		
		route = b.getParcelable("com.jmie.fieldplay.route");
		refList = route.getReferences();	
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
    	super.onActivityCreated(savedInstanceState);

		
    }
    private View getDefaultView(){
    	TextView tv = new TextView(getActivity());
    	tv.setGravity(Gravity.CENTER);
    	tv.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics()));
    	tv.setText("No references found");
    	return tv;
    }
    public List<Reference> getRefList(){
    	return refList;
    }

}
