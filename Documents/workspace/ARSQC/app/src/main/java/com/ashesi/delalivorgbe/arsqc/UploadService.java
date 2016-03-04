package com.ashesi.delalivorgbe.arsqc;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class UploadService extends Service {

    private Calendar c;
    private SimpleDateFormat sdf;
    private String sdfString;
    private String timeStamp;
    private String fileExtension;


    public UploadService() {
        c = Calendar.getInstance();
        sdfString = "dd-MM-yy-ss";
        sdf = new SimpleDateFormat(sdfString);
        timeStamp = sdf.format(c.getTime());
        fileExtension = ".txt";

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        //Toast.makeText(this, "Service was Created. Not started", Toast.LENGTH_LONG).show();
        System.out.println("Service was Created. Not started");
    }

    @Override
    public void onStart(Intent intent, int startId) {
        // Perform your long running operations here.
        //Toast.makeText(this, "Service Started \n About to start uploads", Toast.LENGTH_LONG).show();
        System.out.println("Service Started \n About to start uploads");
        queueUploads();
    }

    @Override
    public void onDestroy() {
        //Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
        System.out.println("Service Destroyed");
    }


    public String getFileTimestamp(String fileName){
        return fileName.substring(fileName.length()-(sdfString.length()+fileExtension.length()),
                fileName.length()-fileExtension.length());
    }

    public boolean fileDirectoryIsEmpty(){
        return(fileList().length==0);
    }

    public void postFile(File fileToUpload){

        //File fileToUpload = getFileAtIndex(fileList().length-1);

        System.out.println("About to upload " + fileToUpload.getName());

        //Toast.makeText(getBaseContext(), fileToUpload.toString(), Toast.LENGTH_SHORT).show();

        OkHTTPAsync fileUploadHandler = new OkHTTPAsync(fileToUpload);
        System.out.println(("File Size: "+fileToUpload.length()/1024));
        System.out.println("File name: "+fileToUpload.getName());

        try {
            fileUploadHandler.execute(fileToUpload);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private File getFileAtIndex(int index){
        return new File(getFilesDir() + "/" + fileList()[index]);
    }

    public int getNumberOfFilesInDirectory(){
        return fileList().length;
    }

    public String getTodayTimestamp(){
        return timeStamp;
    }

    private boolean queueUploads(){

        System.out.println("Queue Started");

        for(int i=0; i<getNumberOfFilesInDirectory(); i++){
            System.out.println("Trying "+getFileAtIndex(i).getName());
            if(!(getFileTimestamp(getFileAtIndex(i).getName()).equals(getTodayTimestamp()))){
                System.out.println("Not equal to today. Uploading");
                postFile(getFileAtIndex(i));
            }else{
                System.out.println("Equal to today. Not uploading");
                postFile(getFileAtIndex(i));
            }
        }

        if(getNumberOfFilesInDirectory()==0){
            System.out.println("Directory is empty");
            stopSelf();
        }

        return false;
    }
}
