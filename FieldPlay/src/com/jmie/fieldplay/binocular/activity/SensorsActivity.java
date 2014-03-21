package com.jmie.fieldplay.binocular.activity;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.jmie.fieldplay.binocular.common.LowPassFilter;
import com.jmie.fieldplay.binocular.common.Matrix;
import com.jmie.fieldplay.binocular.data.ARData;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

/**
 * This class extends Activity and processes sensor data and location data.
 * 
 * @author Justin Wetherell <phishman3579@gmail.com>
 */
public class SensorsActivity extends Activity implements SensorEventListener, LocationListener, GooglePlayServicesClient.ConnectionCallbacks,
GooglePlayServicesClient.OnConnectionFailedListener{

    private static final String TAG = "SensorsActivity";
    private static final AtomicBoolean computing = new AtomicBoolean(false);
    private final static int
    CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    // Milliseconds per second
    private static final int MILLISECONDS_PER_SECOND = 1000;
    // Update frequency in seconds
    public static final int UPDATE_INTERVAL_IN_SECONDS = 5;
    // Update frequency in milliseconds
    private static final long UPDATE_INTERVAL =
            MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS;
    // The fastest update frequency, in seconds
    private static final int FASTEST_INTERVAL_IN_SECONDS = 1;
    // A fast frequency ceiling in milliseconds
    private static final long FASTEST_INTERVAL =
            MILLISECONDS_PER_SECOND * FASTEST_INTERVAL_IN_SECONDS;

    // Define an object that holds accuracy and frequency parameters
    LocationRequest mLocationRequest;



    private static final float temp[] = new float[9]; // Temporary rotation
                                                      // matrix in Android
                                                      // format
    private static final float rotation[] = new float[9]; // Final rotation
                                                          // matrix in Android
                                                          // format
    private static final float grav[] = new float[3]; // Gravity (a.k.a
                                                      // accelerometer data)
    private static final float mag[] = new float[3]; // Magnetic
    /*
     * Using Matrix operations instead. This was way too inaccurate, private
     * static final float apr[] = new float[3]; //Azimuth, pitch, roll
     */

    private static final Matrix worldCoord = new Matrix();
    private static final Matrix magneticCompensatedCoord = new Matrix();
    private static final Matrix xAxisRotation = new Matrix();
    private static final Matrix yAxisRotation = new Matrix();
    private static final Matrix mageticNorthCompensation = new Matrix();

    private static GeomagneticField gmf = null;
    private static float smooth[] = new float[3];
    private static SensorManager sensorMgr = null;
    private static List<Sensor> sensors = null;
    private static Sensor sensorGrav = null;
    private static Sensor sensorMag = null;
    private static LocationManager locationMgr = null;
    private static LocationClient mLocationClient;

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocationRequest = LocationRequest.create();
        // Use high accuracy
        mLocationRequest.setPriority(
                LocationRequest.PRIORITY_HIGH_ACCURACY);
        // Set the update interval to 5 seconds
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        // Set the fastest update interval to 1 second
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationClient = new LocationClient(this, this, this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onStart() {
        super.onStart();

        float neg90rads = (float)Math.toRadians(-90);

        // Counter-clockwise rotation at -90 degrees around the x-axis
        // [ 1, 0, 0 ]
        // [ 0, cos, -sin ]
        // [ 0, sin, cos ]

        xAxisRotation.set(1f, 0f,                    0f, 
                0f, (float)Math.cos(neg90rads), (float)-Math.sin(neg90rads), 
                0f, (float)Math.sin(neg90rads), (float)Math.cos(neg90rads));

        // Counter-clockwise rotation at -90 degrees around the y-axis
        // [ cos,  0,   sin ]
        // [ 0,    1,   0   ]
        // [ -sin, 0,   cos ]

        yAxisRotation.set((float)Math.cos(neg90rads),  0f, (float)Math.sin(neg90rads),
                0f,                     1f, 0f,
                (float)-Math.sin(neg90rads), 0f, (float)Math.cos(neg90rads));

        try {
            sensorMgr = (SensorManager) getSystemService(SENSOR_SERVICE);

            sensors = sensorMgr.getSensorList(Sensor.TYPE_ACCELEROMETER);
            if (sensors.size() > 0)
                sensorGrav = sensors.get(0);

            sensors = sensorMgr.getSensorList(Sensor.TYPE_MAGNETIC_FIELD);
            if (sensors.size() > 0)
                sensorMag = sensors.get(0);

            sensorMgr.registerListener(this, sensorGrav, SensorManager.SENSOR_DELAY_FASTEST);
            sensorMgr.registerListener(this, sensorMag, SensorManager.SENSOR_DELAY_FASTEST);;
            locationMgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    
            mLocationClient.connect();

 
            try {

                try {
              
                    Location gps = locationMgr.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    Location network = locationMgr.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (gps != null) onLocationChanged(gps); 
                    else if (network != null) onLocationChanged(network);
                    else onLocationChanged(ARData.hardFix);
                } catch (Exception ex2) {
                    onLocationChanged(ARData.hardFix);
                }

                gmf = new GeomagneticField((float) ARData.getCurrentLocation().getLatitude(), 
                                           (float) ARData.getCurrentLocation().getLongitude(),
                                           (float) ARData.getCurrentLocation().getAltitude(), 
                                           System.currentTimeMillis());

                float dec = (float)Math.toRadians(-gmf.getDeclination());

                synchronized (mageticNorthCompensation) {
                    // Identity matrix
                    // [ 1, 0, 0 ]
                    // [ 0, 1, 0 ]
                    // [ 0, 0, 1 ]
                    mageticNorthCompensation.toIdentity();

                    // Counter-clockwise rotation at negative declination around
                    // the y-axis
                    // note: declination of the horizontal component of the
                    // magnetic field
                    // from true north, in degrees (i.e. positive means the
                    // magnetic
                    // field is rotated east that much from true north).
                    // note2: declination is the difference between true north
                    // and magnetic north
                    // [ cos, 0, sin ]
                    // [ 0, 1, 0 ]
                    // [ -sin, 0, cos ]

                    mageticNorthCompensation.set((float)Math.cos(dec),     0f, (float)Math.sin(dec), 
                            0f,                     1f, 0f, 
                            (float)-Math.sin(dec), 0f, (float)Math.cos(dec));
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } catch (Exception ex1) {
            try {
                if (sensorMgr != null) {
                    sensorMgr.unregisterListener(this, sensorGrav);
                    sensorMgr.unregisterListener(this, sensorMag);
                    sensorMgr = null;
                }
                if (locationMgr != null) {
                    //locationMgr.removeUpdates(this);
                    locationMgr = null;
                }
            } catch (Exception ex2) {
                ex2.printStackTrace();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onStop() {
        super.onStop();
        if (mLocationClient.isConnected()) {
            /*
             * Remove location updates for a listener.
             * The current Activity is the listener, so
             * the argument is "this".
             */
            mLocationClient.removeLocationUpdates(this);
        }
        /*
         * After disconnect() is called, the client is
         * considered "dead".
         */
        mLocationClient.disconnect();
        try {
            try {
                sensorMgr.unregisterListener(this, sensorGrav);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            try {
                sensorMgr.unregisterListener(this, sensorMag);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            sensorMgr = null;

            try {
              //  locationMgr.removeUpdates(this);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            locationMgr = null;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onSensorChanged(SensorEvent evt) {
        if (!computing.compareAndSet(false, true)) return;

        if (evt.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            smooth = LowPassFilter.filter(0.5f, 1.0f, evt.values, grav);
            grav[0] = smooth[0];
            grav[1] = smooth[1];
            grav[2] = smooth[2];
        } else if (evt.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            smooth = LowPassFilter.filter(2.0f, 4.0f, evt.values, mag);
            mag[0] = smooth[0];
            mag[1] = smooth[1];
            mag[2] = smooth[2];
        }

        //// Find real world position relative to phone location ////
        // Get rotation matrix given the gravity and geomagnetic matrices
        SensorManager.getRotationMatrix(temp, null, grav, mag);

        // Translate the rotation matrices from Y and -Z (landscape)
        //SensorManager.remapCoordinateSystem(temp, SensorManager.AXIS_Y, SensorManager.AXIS_MINUS_X, rotation);
        //SensorManager.remapCoordinateSystem(temp, SensorManager.AXIS_X, SensorManager.AXIS_MINUS_Z, rotation);
        SensorManager.remapCoordinateSystem(temp, SensorManager.AXIS_Y, SensorManager.AXIS_MINUS_Z, rotation);

        /*
         * Using Matrix operations instead. This was way too inaccurate, 
         * //Get the azimuth, pitch, roll 
         * SensorManager.getOrientation(rotation,apr);
         * float floatAzimuth = (float)Math.toDegrees(apr[0]); 
         * if (floatAzimuth<0) floatAzimuth+=360; 
         * ARData.setAzimuth(floatAzimuth);
         * ARData.setPitch((float)Math.toDegrees(apr[1]));
         * ARData.setRoll((float)Math.toDegrees(apr[2]));
         */

        // Convert from float[9] to Matrix
        worldCoord.set(rotation[0], rotation[1], rotation[2], rotation[3], rotation[4], rotation[5], rotation[6], rotation[7], rotation[8]);

        //// Find position relative to magnetic north ////
        // Identity matrix
        // [ 1, 0, 0 ]
        // [ 0, 1, 0 ]
        // [ 0, 0, 1 ]
        magneticCompensatedCoord.toIdentity();

        synchronized (mageticNorthCompensation) {
            // Cross product the matrix with the magnetic north compensation
            magneticCompensatedCoord.prod(mageticNorthCompensation);
        }

        // The compass assumes the screen is parallel to the ground with the screen pointing
        // to the sky, rotate to compensate.
        magneticCompensatedCoord.prod(xAxisRotation);

        // Cross product with the world coordinates to get a mag north compensated coords
        magneticCompensatedCoord.prod(worldCoord);

        // Y axis
        magneticCompensatedCoord.prod(yAxisRotation);

        // Invert the matrix since up-down and left-right are reversed in landscape mode
        magneticCompensatedCoord.invert();

        // Set the rotation matrix (used to translate all object from lat/lon to x/y/z)
        ARData.setRotationMatrix(magneticCompensatedCoord);

        computing.set(false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onLocationChanged(Location location) {
        ARData.setCurrentLocation(location);
        gmf = new GeomagneticField((float) ARData.getCurrentLocation().getLatitude(), 
                                   (float) ARData.getCurrentLocation().getLongitude(), 
                                   (float) ARData.getCurrentLocation().getAltitude(), System.currentTimeMillis());

        float dec = (float)Math.toRadians(-gmf.getDeclination());

        synchronized (mageticNorthCompensation) {
            mageticNorthCompensation.toIdentity();

            mageticNorthCompensation.set((float)Math.cos(dec), 0f, (float)Math.sin(dec), 
                    0f,                 1f, 0f, 
                    (float)-Math.sin(dec), 0f, (float)Math.cos(dec));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        if (sensor == null) throw new NullPointerException();

        if (sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD && accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE) {
            Log.e(TAG, "Compass data unreliable");
        }
    }
    public static class ErrorDialogFragment extends DialogFragment {
        // Global field to contain the error dialog
        private Dialog mDialog;
        // Default constructor. Sets the dialog field to null
        public ErrorDialogFragment() {
            super();
            mDialog = null;
        }
        // Set the dialog to display
        public void setDialog(Dialog dialog) {
            mDialog = dialog;
        }
        // Return a Dialog to the DialogFragment.
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return mDialog;
        }
    }
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        /*
         * Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error.
         */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(
                        this,
                        CONNECTION_FAILURE_RESOLUTION_REQUEST);
                /*
                * Thrown if Google Play services canceled the original
                * PendingIntent
                */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
            /*
             * If no resolution is available, display a dialog to the
             * user with the error.
             */
        	AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("connection error: " + connectionResult.getErrorCode())
                   .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                       @Override
					public void onClick(DialogInterface dialog, int id) {
                    	   
                       }
                   });
        	ErrorDialogFragment errorFragment =
                    new ErrorDialogFragment();
        	errorFragment.setDialog(builder.create());
            errorFragment.show(
                    getFragmentManager(),
                    "Location Updates");
        }
    }
    private boolean servicesConnected() {
        // Check that Google Play services is available
        int resultCode =
                GooglePlayServicesUtil.
                        isGooglePlayServicesAvailable(this);
        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            // In debug mode, log the status
            Log.d("Location Updates",
                    "Google Play services is available.");
            // Continue
            return true;
        // Google Play services was not available for some reason
        } else {
            // Get the error code

            // Get the error dialog from Google Play services
            Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(
                    resultCode,
                    this,
                    CONNECTION_FAILURE_RESOLUTION_REQUEST);
            // If Google Play services can provide an error dialog
            if (errorDialog != null) {
                // Create a new DialogFragment for the error dialog
                ErrorDialogFragment errorFragment =
                        new ErrorDialogFragment();
                // Set the dialog in the DialogFragment
                errorFragment.setDialog(errorDialog);
                // Show the error dialog in the DialogFragment
                errorFragment.show(
                        getFragmentManager(),
                        "Location Updates");
            }
            return false;
        }
    }
    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        // Decide what to do based on the original request code
        switch (requestCode) {
            //...
            case CONNECTION_FAILURE_RESOLUTION_REQUEST :
            /*
             * If the result code is Activity.RESULT_OK, try
             * to connect again
             */
                switch (resultCode) {
                    case Activity.RESULT_OK :
  
                    break;
                }

        }

    }
    @Override
    public void onConnected(Bundle dataBundle) {
        // Display the connection status
        //Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
        startPeriodicUpdates();
    }
    private void startPeriodicUpdates() {

        mLocationClient.requestLocationUpdates(mLocationRequest, this);
    
    }
    @Override
    public void onDisconnected() {
        // Display the connection status
        Toast.makeText(this, "Disconnected. Please re-connect.",
                Toast.LENGTH_SHORT).show();
    }
}
