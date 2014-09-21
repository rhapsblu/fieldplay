package com.jmie.fieldplay.route;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jmie.fieldplay.map.FPMapActivity;

import android.content.Context;
import android.content.Intent;
import android.text.SpannableString;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;

public class ReferenceLinker {
	private SpannableString ss;
	private Context context;
	private Route route;
	
	public ReferenceLinker(Context c, Route route, String ref){
		context = c;
		this.route = route;
		ss = new SpannableString(ref);
		Pattern pattern = Pattern.compile("\\[\\d+\\]");
		Matcher matcher = pattern.matcher(ref);
		while(matcher.find()){
			int i = Integer.valueOf(matcher.group().substring(1, matcher.group().length()-1));
			ss.setSpan(new ClickableReferenceSpan(i), matcher.start(), matcher.end(), 0);
		}
	}
	public SpannableString getSpannableString(){
		return ss;
	}
	public class ClickableReferenceSpan extends ClickableSpan {
		int ref;
		public ClickableReferenceSpan(int ref){
			super();
			this.ref = ref;
		}
		@Override
		public void onClick(View arg0) {
			Log.d("CLick", "Click");
			Intent i = new Intent(context, RouteFullDetailsActivity.class);
			i.putExtra("com.jmie.fieldplay.route", route);
			i.putExtra("com.jmie.fieldplay.reference", ref);
			context.startActivity(i);
			
		}
		
	}
}
