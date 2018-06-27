package com.dldud.riceapp;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;

import java.util.Iterator;

/**
 * Created by dldud on 2018-04-23.
 */

public class GPSInfo extends Service implements LocationListener {

    private final Context mContext;

    // Confirm Current GPS Enable
    boolean isGPSEnabled = false;

    // Confirm Current Network Enable
    boolean isNetworkEnabled = false;

    // GPS Status
    boolean isGetLocation = false;

    Location location;
    double latitude;
    double longitude;

    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    private static final long MIN_TIME_BW_UPDATE = 1000;

    protected LocationManager locationManager;

    // Number of Satellite
    int j;

    public GPSInfo(Context context){
        this.mContext = context;
        getLocation();
    }

    @TargetApi(23)
    public Location getLocation(){
        if(Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            return null;
        }

        try{
            locationManager = (LocationManager)mContext.getSystemService(LOCATION_SERVICE);
            getGpsSatelliteCount();

            //Get GPS information
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            //Get Network information
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if(!isGPSEnabled && !isNetworkEnabled){
                //GPS,NETWORK 사용불가일때 구현
            } else {
                this.isGetLocation = true;
                //From network
                if(isNetworkEnabled){
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,MIN_TIME_BW_UPDATE,MIN_DISTANCE_CHANGE_FOR_UPDATES,this);
                    if(locationManager != null){
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if(location != null){
                            //to variable
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }

                if(isGPSEnabled && j > 3){
                    if(location != null){
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,MIN_TIME_BW_UPDATE,MIN_DISTANCE_CHANGE_FOR_UPDATES,this);
                        if(locationManager != null){
                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if(location != null){
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return location;
    }

    /**
     * When GPS OFF
     **/
    public void stopUsingGPS(){
        if(locationManager != null){
            locationManager.removeUpdates(GPSInfo.this);
        }
    }
    /**
     * GET Longitude
     **/
    public double getLongitude(){
        if(location != null){
            longitude = location.getLongitude();
        }
        return longitude;
    }
    /**
     * GET Latitude
     **/
    public double getLatitude(){
        if(location != null){
            latitude = location.getLatitude();
        }
        return latitude;
    }
    /**
     * Confirm GPS or WIFI Information
     * */
    public boolean isGetLocation() {
        return this.isGetLocation;
    }

    /**
     * GPS 정보를 가져오지 못했을때
     * 설정값으로 갈지 물어보는 alert 창
     * */
    public void showSettingsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

        alertDialog.setTitle("GPS 사용유무세팅");
        alertDialog.setMessage("GPS 세팅이 되어있지 않습니다. \n 설정창으로 가시겠습니까?");

        //When Press OK
        alertDialog.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        mContext.startActivity(intent);
                    }
                });
        //When Press Cancel
        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        alertDialog.show();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    //Get Satellite count
    public int getGpsSatelliteCount() {
        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            final GpsStatus gs = locationManager.getGpsStatus(null);

            int i = 0;
            final Iterator<GpsSatellite> it = gs.getSatellites().iterator();

            while (it.hasNext()) {
                GpsSatellite satellite = it.next();

                // [수정 : 2013/10/25]
                // 단순 위성 갯수가 아니라 사용할 수 있게 잡히는 위성의 갯수가 중요하다.
                if (satellite.usedInFix()) {
                    j++; // i 값 보다는 이 값이 GPS 위성 사용 여부를 확인하는데 더 중요하다.
                }
                i++;
            }
        }
        return j;
    }
}
