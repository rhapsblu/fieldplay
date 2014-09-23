package com.jmie.fieldplay.route;


import java.io.File;


import com.jmie.fieldplay.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import android.widget.TextView;

public class RouteDownloadsAdapter extends BaseAdapter{
	private LayoutInflater inflater;

	private File[] routeFiles;
	
	public RouteDownloadsAdapter (Context c, File[] routeFiles){
		inflater = LayoutInflater.from(c);
		this.routeFiles = routeFiles;
	}
	@Override
	public int getCount() {
		return routeFiles.length;
	}

	@Override
	public Object getItem(int position) {
		return routeFiles[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View contentView, ViewGroup parent) {
		View view;
		ViewHolder holder;
		if(contentView == null){
			view = inflater.inflate(R.layout.route_row, parent, false);
			holder = new ViewHolder();
			holder.routePath = (TextView)view.findViewById(R.id.routename);

			view.setTag(holder);
		}
		else{
			view = contentView;
			holder = (ViewHolder)view.getTag();
		}
		File routePath = routeFiles[position];
		holder.routePath.setText(routePath.getName());

		return view;
	}
	private class ViewHolder {
		private TextView routePath;
	}
}
