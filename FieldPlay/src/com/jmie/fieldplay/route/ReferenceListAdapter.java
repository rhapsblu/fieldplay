package com.jmie.fieldplay.route;

import java.util.List;

import com.jmie.fieldplay.R;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class ReferenceListAdapter extends BaseAdapter{
	private List<Reference> refList;
	private LayoutInflater inflater;

	public ReferenceListAdapter(ReferenceFragment refFragment){
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
			holder.imageView = (ImageView)view.findViewById(R.id.weblink_button);
			view.setTag(holder);
		}
		else{
			view = contentView;
			holder = (ViewHolder)view.getTag();
		}
		Reference ref = refList.get(position);
		holder.referenceText.setText("["+ ref.getId() + "] " + ref.getText());
		if(ref.getLink().length()>0){
			holder.imageView.setVisibility(ImageView.VISIBLE);
		}
		else{
			holder.imageView.setVisibility(ImageView.INVISIBLE);
		}
		
		return view;
	}
	private class ViewHolder {
		private TextView referenceText;
		private ImageView imageView;
	}
}
