package com.ashesi.delalivorgbe.arsqc;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.widget.Toast;

public class WiFiChangeReceiver extends BroadcastReceiver {

    private Context appContext;

    public WiFiChangeReceiver() {
        appContext = ApplicationContextProvider.getContext();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.

        WifiManager wifi = (WifiManager)appContext.getSystemService(Context.WIFI_SERVICE);

        if (wifi.isWifiEnabled()){
            //Toast.makeText(appContext, "Wifi Enabled", Toast.LENGTH_SHORT).show();

            ConnectivityManager connManager = (ConnectivityManager) appContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

            if (mWifi.isConnected()) {
                //Toast.makeText(appContext, "Wifi Connected", Toast.LENGTH_SHORT).show();

                if(!isUploadServiceRunning(UploadService.class) && getNumberOfFilesInDirectory()>0){
                    System.out.println("Service is not running");
                    appContext.startService(new Intent(appContext, UploadService.class));
                }else{
                    System.out.println("Service ALREADY running");
                }
            }else{
                //Toast.makeText(appContext, "Wifi Not Connected", Toast.LENGTH_SHORT).show();
                //appContext.stopService(new Intent(appContext, UploadService.class));
            }

        }else{
            //Toast.makeText(appContext, "Wifi Disabled", Toast.LENGTH_SHORT).show();
            appContext.stopService(new Intent(appContext, UploadService.class));
        }




    }

    private boolean isUploadServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) appContext.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public int getNumberOfFilesInDirectory(){
        return appContext.fileList().length;
    }
}
