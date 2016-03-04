package com.ashesi.delalivorgbe.arsqc;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.internal.widget.AdapterViewCompat;
import android.view.Menu;
import android.view.MenuItem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.provider.Settings.Secure;



public class MainActivity extends AppCompatActivity implements OkHTTPAsync.OkHttpCallback, SensorEventListener, LocationListener, AdapterView.OnItemSelectedListener {

    private static final String TAG = "MainActivity.java";

    private static final String GOOD_ROAD = "GOOD_ROAD";
    private static final String FAIR_ROAD = "FAIR_ROAD";
    private static final String BAD_ROAD = "BAD_ROAD";
    static final int REQUEST_TAKE_PHOTO = 1;
    private static final int MEDIA_TYPE_IMAGE = 1;
    private static final int MEDIA_TYPE_VIDEO = 2;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;

    private Uri fileUri;

    private ImageView iv;
    private Spinner vehicleAgeSpinner;
    private Spinner vehicleClassSpinner;
    private Spinner vehicleConditionSpinner;

    ArrayAdapter<String> vehicleClassAdapter;
    ArrayAdapter<String> vehicleConditionAdapter;
    ArrayAdapter<String> vehicleAgeAdapter;

    private SensorManager senSensorManager;
    private Sensor senAccelerometer;
    private Sensor senRotationVestor;

    private Float accelerationX;
    private Float accelerationY;
    private Float accelerationZ;
    private Float gravityX;
    private Float gravityY;
    private Float gravityZ;

    private LocationManager locationManager;
    private LocationListener locationListener;
    private FileOutputStream outputStream;
    private OutputStreamWriter outputWriter;
    private Double longitude;
    private Double latitude;
    private Float accuracy;
    private Float speed;
    private TextView txtView;
    private ToggleButton toggle;
    private RadioButton sampleFastRadioButton;
    private RadioButton sampleMidRadioButton;
    private RadioButton sampleSlowRadioButton;
    private RadioButton sampleSnailRadioButton;
    private Calendar c;
    private SimpleDateFormat sdf;
    private String sdfString;
    private String timeStamp;
    private String filename;
    private String fileExtension;
    private boolean locationLocked;
    private String androidId;
    private Button uploadFileButton;
    private String phoneModel;
    private String phoneName;
    private int sensorDelay;

    private String vehicleAge;
    private String vehicleClass;
    private String vehicleCondition;

    static final int READ_BLOCK_SIZE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);

        locationLocked = false;

        longitude = 0.0;
        latitude = 0.0;
        speed = 0.0F;
        accuracy = 0.0F;

        accelerationX = 0.0F;
        accelerationY = 0.0F;
        accelerationZ = 0.0F;
        gravityX = 0.0F;
        gravityY = 0.0F;
        gravityZ = 0.0F;

        txtView = (TextView) findViewById(R.id.testX);
        toggle = (ToggleButton) findViewById(R.id.start_stop_toggle);
        sampleSlowRadioButton = (RadioButton) findViewById(R.id.sampleSlowRadioButton);
        sampleMidRadioButton = (RadioButton) findViewById(R.id.sampleMidRadioButton);
        sampleFastRadioButton = (RadioButton) findViewById(R.id.sampleFastRadioButton);
        sampleSnailRadioButton = (RadioButton) findViewById(R.id.sampleSnailRadioButton);

        vehicleAge = vehicleClass = vehicleCondition = "";

        vehicleAgeSpinner = (Spinner) findViewById(R.id.vehicle_age);
        vehicleClassSpinner = (Spinner) findViewById(R.id.vehicle_type);
        vehicleConditionSpinner = (Spinner) findViewById(R.id.vehicle_condition);

        vehicleAgeSpinner.setOnItemSelectedListener(this);
        vehicleClassSpinner.setOnItemSelectedListener(this);
        vehicleConditionSpinner.setOnItemSelectedListener(this);


        List<String> carAgeCategories = new ArrayList<String>();
        carAgeCategories.add("< 1 year");
        carAgeCategories.add("1 - 2 years");
        carAgeCategories.add("2 - 3 years");
        carAgeCategories.add("3 - 4 years");
        carAgeCategories.add("> 5 years");


        List<String> carClassCategories = new ArrayList<String>();
        carClassCategories.add("Micro car");
        carClassCategories.add("Saloon car");
        carClassCategories.add("SUV");
        carClassCategories.add("4 wheel drive");
        carClassCategories.add("Bus");


        List<String> carConditionCategories = new ArrayList<String>();
        carConditionCategories.add("Well maintained");
        carConditionCategories.add("Fairly maintained");
        carConditionCategories.add("Poorly maintained");

        vehicleAgeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, carAgeCategories);
        vehicleAgeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        vehicleAgeSpinner.setAdapter(vehicleAgeAdapter);

        vehicleClassAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, carClassCategories);
        vehicleClassAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        vehicleClassSpinner.setAdapter(vehicleClassAdapter);

        vehicleConditionAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, carConditionCategories);
        vehicleConditionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        vehicleConditionSpinner.setAdapter(vehicleConditionAdapter);


        sensorDelay = SensorManager.SENSOR_DELAY_UI;
        sampleSlowRadioButton.setChecked(true);

        iv=(ImageView)findViewById(R.id.imageView);

        uploadFileButton = (Button)findViewById(R.id.upload_file_button);

        phoneModel = "";
        phoneName = "";

        phoneModel = android.os.Build.MODEL;
        phoneName = android.os.Build.BRAND;

        c = Calendar.getInstance();
        sdfString = "dd-MM-yy";
        sdf = new SimpleDateFormat(sdfString);
        timeStamp = sdf.format(c.getTime());
        fileExtension = ".txt";

        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        senRotationVestor = senSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        androidId = Secure.getString(this.getContentResolver(),Secure.ANDROID_ID);
        filename = ""+androidId+"_"+phoneName+"_"+phoneModel+"_"+timeStamp+fileExtension;

        resetUploadButton();
    }

    @Override
    protected void onPause() {
        super.onPause();
        senSensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        resetUploadButton();
        //senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Accelerometer blocks
    @Override
    public void onAccuracyChanged(Sensor arg0, int arg1) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        // TODO Auto-generated method stub
        Sensor mySensor = sensorEvent.sensor;

        //changed condition from locationLocked
        if(locationLocked){
            if (mySensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
                Float x = sensorEvent.values[0];
                Float y = sensorEvent.values[1];
                Float z = sensorEvent.values[2];

                writeToFile(x, y, z, speed, longitude, latitude, vehicleClass, vehicleAge, vehicleCondition);

                txtView.setText("x-linear: "+x.toString() + "\n y-linear: " + y.toString() +
                        "\n z-linear: " + z.toString()+ "\n speed: " + speed.toString()+
                        "\n longitude: " + longitude.toString()+ "\n latitude: " + latitude.toString()+
                        "\n x-gravity: "+ gravityX.toString() + "\n y-gravity: " + gravityY.toString() +
                        "\n z-gravity: " + gravityZ.toString());
            }

            if (mySensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
                gravityX = sensorEvent.values[0];
                gravityY = sensorEvent.values[1];
                gravityZ = sensorEvent.values[2];
            }
        }

    }

    // Location GPS blocks
    @Override
    public void onLocationChanged(Location location) {

        longitude =location.getLongitude();
        latitude=location.getLatitude();
        speed = location.getSpeed();
        accuracy = location.getAccuracy();


        if(locationLocked==false){
            locationLocked = true;
            Toast.makeText(getBaseContext(), "location locked", Toast.LENGTH_LONG).show();
        }

        String str = "Latitude: "+location.getLatitude()+" Longitude: "+location.getLongitude();

        //Toast.makeText(getBaseContext(), str, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(getBaseContext(), "Gps is turned off. Turn on and try again", Toast.LENGTH_LONG).show();
        locationLocked = false;
        toggle.setChecked(false);
    }

    @Override
    public void onProviderEnabled(String provider) {

        /******** Called when User on Gps  *********/

        Toast.makeText(getBaseContext(), "Gps turned on ", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        String item = parent.getItemAtPosition(position).toString();


        Spinner spinner = (Spinner) parent;
        if(spinner.getId() == R.id.vehicle_age)
        {
            vehicleAge = parent.getItemAtPosition(position).toString();
        }
        else if(spinner.getId() == R.id.vehicle_type)
        {
            vehicleClass = parent.getItemAtPosition(position).toString();
        }else if(spinner.getId() == R.id.vehicle_condition)
        {
            vehicleCondition = parent.getItemAtPosition(position).toString();
        }


        // Showing selected spinner item
        //Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    public void writeToFile(Float x, Float y, Float z, Float speed, Double longitude, Double latitude,
                            String vehicleClass, String vehicleAge, String vehicleCondition){
        try {
            FileOutputStream fileout=openFileOutput(filename, Context.MODE_APPEND);
            OutputStreamWriter outputWriter=new OutputStreamWriter(fileout);

            Long tsLong = System.currentTimeMillis();
            String ts = tsLong.toString();

            String line = ts+
                    "|"+x.toString()+"|"+y.toString()+"|"+z.toString()+
                    "|"+gravityX+"|"+gravityY+"|"+gravityZ+
                    "|"+ speed.toString()+"|"+accuracy.toString()+
                    "|"+longitude.toString()+"|"+latitude.toString()+"|"+vehicleClass+
                    "|"+vehicleAge+"|"+vehicleCondition+"\n";
            outputWriter.write(line);
            outputWriter.close();

            //display file saved message
//            Toast.makeText(getBaseContext(), "File saved successfully!",
//                    Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void startStopToggleClicked(View view) {
        // Is the toggle on?
        boolean on = ((ToggleButton) view).isChecked();

        if (on) {
            senSensorManager.registerListener(this, senAccelerometer , sensorDelay);
            senSensorManager.registerListener(this, senRotationVestor, sensorDelay);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, this);

            sampleMidRadioButton.setEnabled(false);
            sampleFastRadioButton.setEnabled(false);
            sampleSlowRadioButton.setEnabled(false);
            sampleSnailRadioButton.setEnabled(false);

            if(!locationLocked){
                Toast.makeText(getBaseContext(), "Getting GPS lock. Might take a while", Toast.LENGTH_LONG).show();
            }

        } else {
            senSensorManager.unregisterListener(this);
            sampleMidRadioButton.setEnabled(true);
            sampleFastRadioButton.setEnabled(true);
            sampleSlowRadioButton.setEnabled(true);
            sampleSnailRadioButton.setEnabled(true);
            resetUploadButton();
        }
    }



    public void onSampleRateRadioButtonClicked(View view){
        boolean checked = ((RadioButton) view).isChecked();
        sensorDelay = SensorManager.SENSOR_DELAY_FASTEST;

        switch (view.getId()){

            case R.id.sampleFastRadioButton:
                if(checked){
                    sensorDelay = SensorManager.SENSOR_DELAY_FASTEST;
                }
                break;

            case R.id.sampleMidRadioButton:
                if(checked){
                    sensorDelay = SensorManager.SENSOR_DELAY_GAME;
                }
                break;
            case R.id.sampleSlowRadioButton:
                if(checked){
                    sensorDelay = SensorManager.SENSOR_DELAY_UI;
                }
                break;
            case R.id.sampleSnailRadioButton:
                if(checked){
                    sensorDelay = SensorManager.SENSOR_DELAY_NORMAL;
                }
                break;

            default:

        }
    }




    public void writeClassToFile(String classification){
        try {
            outputStream = openFileOutput(filename, Context.MODE_APPEND);
            String line = "\n******" + classification + "******\n";
            outputStream.write(line.getBytes());
            //outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void goodRoadClicked(View view) {
        writeClassToFile(GOOD_ROAD);
        Toast.makeText(getBaseContext(),GOOD_ROAD,
                Toast.LENGTH_SHORT).show();
    }

    public void fairRoadClicked(View view) {
        writeClassToFile(FAIR_ROAD);
        Toast.makeText(getBaseContext(),FAIR_ROAD,
                Toast.LENGTH_SHORT).show();
    }

    public void badRoadClicked(View view) {
        writeClassToFile(BAD_ROAD);
        Toast.makeText(getBaseContext(),BAD_ROAD,
                Toast.LENGTH_SHORT).show();
    }

    public void cameraClicked(View view){

//        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//        startActivityForResult(intent, 0);

        dispatchTakePictureIntent();

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        try{
            Bitmap bp = (Bitmap) data.getExtras().get("data");
            iv.setImageBitmap(bp);

        }catch (Exception e){

        }

        galleryAddPic();

    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    String mCurrentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();

        return image;
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    // Read text from file
    public void readFile(View v) {
        //reading text from file
        try {
            FileInputStream fileIn=openFileInput(filename);
            InputStreamReader InputRead= new InputStreamReader(fileIn);

            char[] inputBuffer= new char[READ_BLOCK_SIZE];
            String s="";
            int charRead;

            while ((charRead=InputRead.read(inputBuffer))>0) {
                // char to string conversion
                String readstring=String.copyValueOf(inputBuffer,0,charRead);
                s +=readstring;
            }
            InputRead.close();
            Toast.makeText(getBaseContext(), s,Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void listFilesInDirectory(){
        String[] files = fileList();
        String fileList="";

        for (String element : files) {
            fileList = fileList + element +"\n";
        }

        //Toast.makeText(getBaseContext(), fileList, Toast.LENGTH_SHORT).show();
    }

    public int getNumberOfFilesInDirectory(){
        return fileList().length;
    }

    public File getFileAtIndex(int index){
        return new File(getFilesDir() + "/" + fileList()[index]);
    }


    public void postFile(View v){

        File fileToUpload = getFileAtIndex(fileList().length-1);

        //Toast.makeText(getBaseContext(), getFileAtIndex(fileList().length-1).toString(), Toast.LENGTH_SHORT).show();

        OkHTTPAsync fileUploadHandler = new OkHTTPAsync(fileToUpload, this);
        System.out.println((getFileAtIndex(fileList().length-1).length()/1024));
        System.out.println(fileToUpload.toString());

        try {
            fileUploadHandler.execute(getFileAtIndex(fileList().length-1));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public boolean fileDirectoryIsEmpty(){
        return(fileList().length==0);
    }

    public void resetUploadButton(){
        if(fileDirectoryIsEmpty()){
            uploadFileButton.setVisibility(View.GONE);
        }else {
            uploadFileButton.setVisibility(View.VISIBLE);
        }

        Toast.makeText(getBaseContext(), "Reset called", Toast.LENGTH_SHORT).show();

        uploadFileButton.setText(String.valueOf(getDirSize()/1024)+"KB to upload");
    }


    public String getTodayTimestamp(){
        return timeStamp;
    }

    public String getFileTimestamp(String fileName){
        return fileName.substring(fileName.length()-(sdfString.length()+fileExtension.length()),
                fileName.length()-fileExtension.length());
    }

    // Start the service
    public void startService(){
        startService(new Intent(this, UploadService.class));
    }

    // Stop the service
    public void stopService(View view) {
        stopService(new Intent(this, UploadService.class));
    }


    //non-critical functions. utility testing
    public void testButton(View v){
        startService();
    }


    public long getDirSize(){

        long totalSize = 0;

        for(int i=0; i<getNumberOfFilesInDirectory(); i++){
            totalSize += getFileAtIndex(i).length();
        }

        return totalSize;
    }


    @Override
    public void call() {
        resetUploadButton();
        //Toast.makeText(getBaseContext(), "Call called", Toast.LENGTH_SHORT).show();
    }
}
