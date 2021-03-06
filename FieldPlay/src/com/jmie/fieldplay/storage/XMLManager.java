package com.jmie.fieldplay.storage;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;


import com.jmie.fieldplay.map.MapLayer;
import com.jmie.fieldplay.route.BinocularLocation;
import com.jmie.fieldplay.route.FPAudio;
import com.jmie.fieldplay.route.FPLocation;
import com.jmie.fieldplay.route.FPPicture;
import com.jmie.fieldplay.route.FPVideo;
import com.jmie.fieldplay.route.InterestLocation;
import com.jmie.fieldplay.route.Reference;
import com.jmie.fieldplay.route.Route;
import com.jmie.fieldplay.route.StopLocation;

import android.util.Log;
import android.util.Xml;

public class XMLManager {
	public static String TAG = "XML Manager";
    private static final String ns = null;
	public XMLManager() {
		
	}
    public Route parse(InputStream in) throws XmlPullParserException, IOException {

        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readRoute(parser);
        } finally {
            in.close();
        }
    }
    public String parseNameOnly(InputStream in) throws XmlPullParserException, IOException{

        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readNameOnly(parser);
        } finally {
           in.close();
        }
    }
    public String parseDescriptionOnly(InputStream in) throws XmlPullParserException, IOException{

        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readDescriptionOnly(parser);
        } finally {
            in.close();
        }
    }
    public String[] parseNameAndDescription(InputStream in) throws XmlPullParserException, IOException{

        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readNameAndDescription(parser);
        } finally {
            //in.close();
        }
    }
    private String readNameOnly(XmlPullParser parser) throws XmlPullParserException, IOException {
    	//Log.d(TAG, "Reading name and description only");
    	String routeName=null;
    	 parser.require(XmlPullParser.START_TAG, ns, "route");
    	
    	 while (parser.next() != XmlPullParser.END_TAG) {
             if (parser.getEventType() != XmlPullParser.START_TAG) {
                 continue;
             }
             String name = parser.getName();
             
             if (name.equals("name")){
             	routeName = readName(parser);
             	//Log.d(TAG, "Found Name "+ routeName);
             }
             else {
                 skip(parser);
             }
    	 }
    	 return routeName;
    }
    private String readDescriptionOnly(XmlPullParser parser) throws XmlPullParserException, IOException {
    	//Log.d(TAG, "Reading name and description only");
    	String routeDescription=null;
    	 parser.require(XmlPullParser.START_TAG, ns, "route");
    	
    	 while (parser.next() != XmlPullParser.END_TAG) {
             if (parser.getEventType() != XmlPullParser.START_TAG) {
                 continue;
             }
             String name = parser.getName();
             
             if (name.equals("description")){
             	routeDescription = readDescription(parser);
             	//Log.d(TAG, "Found description "+ routeDescription);
             }
             else {
                 skip(parser);
             }
    	 }
    	 return routeDescription;
    }
    private String[] readNameAndDescription(XmlPullParser parser) throws XmlPullParserException, IOException{
    	String[] nameAndDescription = new String[2];
    	parser.require(XmlPullParser.START_TAG, ns, "route");
    	
   	 while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            
            if (name.equals("description")){
            	nameAndDescription[1] = readDescription(parser);
            	//Log.d(TAG, "Found description "+ routeDescription);
            }
            else if (name.equals("name")){
             	nameAndDescription[0] = readName(parser);
             	//Log.d(TAG, "Found Name "+ routeName);
             }
            else {
                skip(parser);
            }
   	 	}
   	 return nameAndDescription;
    }
    private Route readRoute(XmlPullParser parser) throws XmlPullParserException, IOException {
    	//Log.d(TAG, "Top Level");
    	Route route = new Route();
    	//route.setStorageName(routeStorageName);
        parser.require(XmlPullParser.START_TAG, ns, "route");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
 
            // Starts by looking for the entry tag
            if (name.equals("location")) {
            	//Log.d(TAG, "Start location parse");
            	String type = parser.getAttributeValue(null, "type");
            	//Log.d(TAG, "Found attribute " + type);
                route.addLocation(readLocation(parser, type));
                //Log.d(TAG, "Found Location");
            } 
            else if (name.equals("map_layer")){
            	//Log.d(TAG, "Start Map layer parse");
            	String type = parser.getAttributeValue(null, "type");
            	route.addMapLayer(readMapLayer(parser, type));
            	//Log.d(TAG, "Found map layer ");
            }

            else if (name.equals("length")){
            	route.setLength(readLength(parser));
            	//Log.d(TAG, "Found length "+ route.getLength());
            }
            else if (name.equals("reference")){
            	int id = Integer.valueOf(parser.getAttributeValue(null, "id"));
            	route.addReference(readReference(parser, id));
            }
            else {
                skip(parser);
            }
        }  
        return route;
    }
    private MapLayer readMapLayer(XmlPullParser parser, String type) throws XmlPullParserException, IOException {
    	parser.require(XmlPullParser.START_TAG, ns, "map_layer");
    	String layerName = null; 
    	String description = null;
    	String layerPath = null;
    	//Log.d(TAG, "Setup done for mapLayer, parsing members");
    	 while (parser.next() != XmlPullParser.END_TAG) {
             if (parser.getEventType() != XmlPullParser.START_TAG) {
                 continue;
             }
             String name = parser.getName();
	    	if (name.equals("name")) {
	            layerName = readName(parser);
	            //Log.d(TAG, "Layer name: "+ layerName);
	        } else if (name.equals("description")) {
	            description = readDescription(parser);
	            //Log.d(TAG, "Description: "+ description);
	        } else if(name.equals("file")){
	        	layerPath = readFile(parser);
	        }
    	 }
    	 return new MapLayer(layerName, description, layerPath);
    }
    private Reference readReference(XmlPullParser parser, int id) throws XmlPullParserException, IOException {
    	parser.require(XmlPullParser.START_TAG, ns, "reference");
    	String text = ""; 
    	String link = "";
    	//Log.d(TAG, "Setup done for mapLayer, parsing members");
    	 while (parser.next() != XmlPullParser.END_TAG) {
             if (parser.getEventType() != XmlPullParser.START_TAG) {
                 continue;
             }
            String name = parser.getName();
	    	if (name.equals("text")) {
	            text = readRefText(parser);
	            //Log.d(TAG, "Layer name: "+ layerName);
	        } else if (name.equals("link")) {
	            link = readLink(parser);
	            //Log.d(TAG, "Description: "+ description);
	        } 
    	 }
    	 return new Reference(id, text, link);
    }
    private FPLocation readLocation(XmlPullParser parser, String type) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "location");
        FPLocation location = null;
        String locationName = null;
        String description = null;
        double latitude = Double.MAX_VALUE;
        double longitude = Double.MAX_VALUE;
        double elevation = Double.MAX_VALUE;
        double alertRadius = Double.MAX_VALUE;
        double contentRadius = Double.MAX_VALUE;
        List<FPPicture> imageList = new ArrayList<FPPicture>();
        List<FPAudio> audioList = new ArrayList<FPAudio>();
        List<FPVideo> videoList = new ArrayList<FPVideo>();
        List<String> binocularPoints = new ArrayList<String>();
        //Log.d(TAG, "Setup done for location, parsing members");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("name")) {
                locationName = readName(parser);
                //Log.d(TAG, "Location name: "+ locationName);
            } else if (name.equals("description")) {
                description = readDescription(parser);
               // Log.d(TAG, "Description: "+ description);
            } else if (name.equals("lat")) {
                latitude = readLatitude(parser);
                //Log.d(TAG, "Latitude: "+ latitude);
            } else if (name.equals("long")) {
                longitude = readLongitude(parser);
                //Log.d(TAG, "Longitude: "+ longitude);
            }else if (name.equals("elevation")) {
                elevation = readElevation(parser);
                Log.d(TAG, "Elevation: "+ elevation);
            }else if (name.equals("alert_radius")) {
                alertRadius = readAlertRadius(parser);
                Log.d(TAG, "Alert Radius: "+ alertRadius);
            }else if (name.equals("content_radius")) {
                contentRadius = readContentRadius(parser);
                //Log.d(TAG, "Content Radius: "+ contentRadius);
            }else if (name.equals("audio")) {
                audioList.add(readAudio(parser));
                //Log.d(TAG, "Audio: "+ audioList.get(audioList.size()-1).getName());
            }else if (name.equals("image")) {
                imageList.add(readImage(parser));
               // Log.d(TAG, "Image: "+ imageList.get(imageList.size()-1).getName());
            }else if (name.equals("video")) {
                videoList.add(readVideo(parser));
                //Log.d(TAG, "Video: "+ videoList.get(videoList.size()-1).getName());
            }else if (name.equals("binocularView")) {
                binocularPoints.add(readBinocularPoint(parser));
                //Log.d(TAG, "BinocularView: "+ binocularPoints.get(binocularPoints.size()-1));
            }else {
                skip(parser);
            }
        }
        if(type.contains("stop")){
            StopLocation stop = new StopLocation(latitude, longitude, elevation, locationName, description);
            stop.setAlertRadius(alertRadius);
            stop.setContentRadius(contentRadius);
            for(FPAudio audio: audioList) stop.addAudio(audio);
            for(FPVideo video: videoList) stop.addVideo(video);
            for(FPPicture picture: imageList) stop.addImage(picture);
            location = stop;
            for(String point: binocularPoints) stop.addBinocularLocation(point);
            
        }
        else if(type.contains("interest")){
        	//Log.d(TAG, "Processing interest location");
        	InterestLocation interest = new InterestLocation(latitude, longitude, elevation, locationName, description);
        	interest.setContentRadius(contentRadius);
        	for(FPAudio audio: audioList) interest.addAudio(audio);
        	for(FPPicture picture: imageList) interest.addImage(picture);
        	location = interest;
        }
        else if(type.contains("binocular")){
        	location = new BinocularLocation(latitude, longitude, elevation, locationName, description);
        	for(FPPicture picture: imageList) location.addImage(picture);
        }
        Log.d(TAG, location.getName());

        return location;
    }
    private FPAudio readAudio(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "audio");
        String audioName =  null;
        String audioPath = null;
        Integer audioPriority = Integer.MAX_VALUE;
        FPAudio audio = null;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("name")) {
                audioName = readName(parser);
            }
            else if(name.equals("file")){
            	audioPath = readFile(parser);
            }
            else if(name.equals("priority")){
            	audioPriority = readPriority(parser);
            }
            else {
                skip(parser);
            }   
        }
        audio= new FPAudio(audioName, audioPath, audioPriority);
        return audio;
    }
    private FPVideo readVideo(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "audio");
        String videoName =  null;
        String videoPath = null;
        String videoDescription = null;
        FPVideo video = null;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("name")) {
                videoName = readName(parser);
            }
            else if(name.equals("file")){
            	videoPath = readFile(parser);
            }
            else if(name.equals("description")){
            	videoDescription = readDescription(parser);
            }
            else {
                skip(parser);
            }   
        }
        video= new FPVideo(videoName, videoDescription, videoPath);
        return video;
    }
    private FPPicture readImage(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "image");
        String imageName =  null;
        String imagePath = null;
        String imageDescription = null;
        FPPicture image = null;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("name")) {
                imageName = readName(parser);
            }
            else if(name.equals("file")){
            	imagePath = readFile(parser);
            }
            else if(name.equals("description")){
            	imageDescription = readDescription(parser);
            }
            else {
                skip(parser);
            }   
        }
        image= new FPPicture(imageName, imageDescription, imagePath);
        return image;
    }
    private String readName(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "name");
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "name");
        return title;
    }
    private String readRefText(XmlPullParser parser) throws IOException, XmlPullParserException{
    	parser.require(XmlPullParser.START_TAG, ns, "text");
    	String text = readText(parser);
    	parser.require(XmlPullParser.END_TAG,  ns, "text");
    	return text;
    }
    private String readLink(XmlPullParser parser) throws IOException, XmlPullParserException{
    	parser.require(XmlPullParser.START_TAG, ns, "link");
    	String link = readText(parser);
    	parser.require(XmlPullParser.END_TAG,  ns, "link");
    	return link;
    }
    private String readBinocularPoint(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "binocularView");
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "binocularView");
        return title;
    }
    private Double readAlertRadius(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "alert_radius");
        Double radius = readDouble(parser);
        parser.require(XmlPullParser.END_TAG, ns, "alert_radius");
        return radius;
    }
    private Integer readPriority(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "priority");
        Integer priority = readInteger(parser);
        Log.d(TAG, "priority= " + priority);
        parser.require(XmlPullParser.END_TAG, ns, "priority");
        return priority;
    }
    private Double readContentRadius(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "content_radius");
        Double radius = readDouble(parser);
        parser.require(XmlPullParser.END_TAG, ns, "content_radius");
        return radius;
    }
    private String readFile(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "file");
        String file = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "file");
        return file;
    }
    private String readDescription(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "description");
        String description = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "description");
        return description;
    }
    private Double readLatitude(XmlPullParser parser) throws IOException, XmlPullParserException {
    	//Log.d(TAG, "Parsing latitude");
        parser.require(XmlPullParser.START_TAG, ns, "lat");
        Double latitude = readDouble(parser);
        parser.require(XmlPullParser.END_TAG, ns, "lat");
        return latitude;
    }
    private Double readElevation(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "elevation");
        Double elevation = readDouble(parser);
        parser.require(XmlPullParser.END_TAG, ns, "elevation");
        return elevation;
    }
    private Double readLongitude(XmlPullParser parser) throws IOException, XmlPullParserException {
    	//Log.d(TAG, "Parsing longitude");
        parser.require(XmlPullParser.START_TAG, ns, "long");
        Double longitude = readDouble(parser);
        parser.require(XmlPullParser.END_TAG, ns, "long");
        return longitude;
    }
    private Double readLength(XmlPullParser parser) throws IOException, XmlPullParserException {
    	//Log.d(TAG, "Parsing legnth");
        parser.require(XmlPullParser.START_TAG, ns, "length");
        Double length = readDouble(parser);
        parser.require(XmlPullParser.END_TAG, ns, "length");
        return length;
    }
    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }
    private Double readDouble(XmlPullParser parser) throws IOException, XmlPullParserException {
        Double result = null;
        if (parser.next() == XmlPullParser.TEXT) {
        	//Log.d(TAG, "Processing long value of " + parser.getText());
            result = Double.valueOf(parser.getText());
            parser.nextTag();
        }
        return result;
    }
    private Integer readInteger(XmlPullParser parser) throws IOException, XmlPullParserException {
        Integer result = null;
        if (parser.next() == XmlPullParser.TEXT) {
            result = Integer.valueOf(parser.getText());
            parser.nextTag();
        }
        return result;
    }
    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
            case XmlPullParser.END_TAG:
                depth--;
                break;
            case XmlPullParser.START_TAG:
                depth++;
                break;
            }
        }
     }
}