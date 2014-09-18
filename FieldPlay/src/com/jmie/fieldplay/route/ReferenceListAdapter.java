package com.jmie.fieldplay.route;

import java.util.List;

import com.jmie.fieldplay.R;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ReferenceListAdapter extends BaseAdapter{
	private List<Reference> refList;
	private LayoutInflater inflater;
	
	public ReferenceListAdapter(ReferenceFragment refFragment ){
		refList = refFragment.getRefList();
		inflater = LayoutInflater.from(refFragment.getActivity());
	}
	@Override
	public int getCount() {
		return refList.size();
	}

	@Override
	public Reference getItem(int i) {
		return refList.get(i);
	}

	@Override
	public long getItemId(int i) {
		return i;
	}

	@Override
	public View getView(int position, View contentView, ViewGroup parent) {
		View view;
		ViewHolder holder;
		if(contentView == null){
			view = inflater.inflate(R.layout.reference_row, parent, false);
			holder = new ViewHolder();
			holder.referenceText = (TextView)view.findViewById(R.id.reference_row);

			view.setTag(holder);
		}
		else{
			view = contentView;
			holder = (ViewHolder)view.getTag();
		}
		Reference ref = refList.get(position);
		holder.referenceText.setText("["+ ref.getId() + "] " + ref.getText());
		return view;
	}
	private class ViewHolder {
		private TextView referenceText;
	}
}
