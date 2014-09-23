package com.jmie.fieldplay.route;

import java.util.List;

import com.jmie.fieldplay.R;



import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import android.widget.TextView;

public class LocationsListAdapter extends BaseAdapter{
	private RouteLocationsFragment locationsFragment;
	private List<FPLocation> locationList;
	private LayoutInflater inflater;
	
	public LocationsListAdapter(RouteLocationsFragment locationsFragment ){
		this.locationsFragment = locationsFragment;
		locationList = locationsFragment.getLocationList();
		inflater = LayoutInflater.from(locationsFragment.getActivity());
	}
	@Override
	public int getCount() {
		return locationList.size();
	}

	@Override
	public FPLocation getItem(int i) {
		return locationList.get(i);
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
			view = inflater.inflate(R.layout.location_row, parent, false);
			holder = new ViewHolder();
			holder.locationName = (TextView)view.findViewById(R.id.location_row_name);

			view.setTag(holder);
		}
		else{
			view = contentView;
			holder = (ViewHolder)view.getTag();
		}
		holder.locationName.setText(locationList.get(position).getName());
		return view;
	}
	private class ViewHolder {
		private TextView locationName;
	}
}
