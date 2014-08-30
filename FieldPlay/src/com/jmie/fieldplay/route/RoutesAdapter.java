package com.jmie.fieldplay.route;


import com.jmie.fieldplay.R;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

public class RoutesAdapter extends BaseAdapter{
	private LayoutInflater inflater;
	private String TAG = "Routes Adapter";
	private RouteLoaderActivity loader;
	
	public RoutesAdapter (RouteLoaderActivity loader){
		inflater = LayoutInflater.from(loader);
		this.loader = loader;
	}
	@Override
	public int getCount() {
		return loader.getRouteList().size();
	}

	@Override
	public Object getItem(int position) {
		return loader.getRouteList().get(position);
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
			holder.routeName = (TextView)view.findViewById(R.id.routename);
			holder.progressBar = (ProgressBar)view.findViewById(R.id.download_progress);

			view.setTag(holder);
		}
		else{
			view = contentView;
			holder = (ViewHolder)view.getTag();
		}
			RouteData routeData = loader.getRouteList().get(position);
			if(routeData.get_downloadProgress()<100){
				holder.routeName.setText("Downloading " + routeData.get_routeName());
				int progress = routeData.get_downloadProgress();
				if(progress <=0)holder.progressBar.setIndeterminate(true);
				else{
					holder.progressBar.setIndeterminate(false);
					holder.progressBar.setProgress(progress);
					
				}
			}
			else if(routeData.get_unzipProgress()<100){
				
				holder.routeName.setText("Loading " + routeData.get_routeName());
				int progress = routeData.get_unzipProgress();
				if(progress<=0){
					holder.progressBar.setIndeterminate(true);
				}
				else{
					holder.progressBar.setIndeterminate(false);
					holder.progressBar.setProgress(progress);
				}
			}
			else{
				holder.routeName.setText(routeData.get_routeName());
				holder.progressBar.setVisibility(View.GONE);
			}

		return view;
	}
	private class ViewHolder {
		private TextView routeName;
		private ProgressBar progressBar;
	}
}
