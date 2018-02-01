package com.alexpatriche.demoapp;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.CellLocation;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    TelephonyManager tm;

    private static final int CELL_LOCATION_INDEX = 0;
    private static final int SIGNAL_STRENGTH_INDEX = 1;
    private static final int SERVICE_STATE_INDEX = 2;
    private final int MY_GEO_PERMISSION = 13;

    private static final int[] info_ids = {
            R.id.cell_location_value,
            R.id.signal_strength_value,
            R.id.service_state_value
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_GEO_PERMISSION);
            }
        }
        startListener();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //Stop listening to the telephony events
        stopListener();

    }

    @Override
    protected void onPause() {
        super.onPause();
        //Stop listening to the telephony events
        stopListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //subscribes to the telephony related events
        startListener();
    }

    /*
    * Sets the textview contents
    * */
    private void setTextViewText(int id, String text) {
        ((TextView) findViewById(id)).setText(text);
    }

    private void startListener() {
        tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        int events = PhoneStateListener.LISTEN_CELL_LOCATION | PhoneStateListener.LISTEN_SERVICE_STATE | PhoneStateListener.LISTEN_SIGNAL_STRENGTHS;

        tm.listen(myPhoneStateListener, events);
    }

    private void stopListener() {
        TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        tm.listen(myPhoneStateListener, PhoneStateListener.LISTEN_NONE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_GEO_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    private final PhoneStateListener myPhoneStateListener = new PhoneStateListener() {
        /*
         * Cell location changed event handler
         * */
        public void onCellLocationChanged(CellLocation location)  {
            TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
         //   GsmCellLocation gsmLoc = (GsmCellLocation) tm.getCellLocation();

            String strLocation = location.toString();

            setTextViewText(info_ids[CELL_LOCATION_INDEX], strLocation);

            super.onCellLocationChanged(location);
        }

        /*
         * Cellphone Service status
         * */
        public void onServiceStateChanged(ServiceState serviceState) {

            String strServiceState = "NONE";

            switch (serviceState.getState()) {

                case ServiceState.STATE_EMERGENCY_ONLY:
                    strServiceState = "Emergency";
                    break;

                case ServiceState.STATE_IN_SERVICE:
                    strServiceState = "In Service";
                    break;
                case ServiceState.STATE_OUT_OF_SERVICE:
                    strServiceState = "Out of Service";
                    break;
                case ServiceState.STATE_POWER_OFF:
                    strServiceState = "Power off";
                    break;
            }

            setTextViewText(info_ids[SERVICE_STATE_INDEX], strServiceState);

            super.onServiceStateChanged(serviceState);

        }

        public void onSignalStrengthsChanged(SignalStrength signalStrength) {

            //retrieve the signal strength measured in asu and convert it to dbm with this formula
             int rssi = -113 + 2 * signalStrength.getGsmSignalStrength();

             String strSignalStrength = String.valueOf(rssi);

             setTextViewText(info_ids[SIGNAL_STRENGTH_INDEX],strSignalStrength + " dbm");
        }
    };
}
