package com.example.msocketv2;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.widget.EditText;
import android.widget.TextView;
import android.location.Criteria;
import android.location.LocationProvider;
import java.io.IOException;
import java.io.File;
import java.io.FileWriter;
import android.widget.Toast;
import android.os.Environment;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import org.apache.commons.math3.distribution.ExponentialDistribution;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.derby.impl.tools.sysinfo.Main;

import java.util.concurrent.ExecutionException;

public class RunTest  extends AsyncTask<Void, String, Void> {

    double download_m;
    double download_s;
    double upload_m ;
    double upload_s ;
    TextView out;
    LocationManager locationManager;
    Context context;
    String response;
    public RunTest(double dm, double ds, double um, double us, TextView o, LocationManager lm, Context c){
        download_m = dm;
        download_s = ds;
        upload_m = um;
        upload_s = us;
        out = o;
        locationManager = lm;
        Context context = c;
    }


    public void generateNoteOnSD(Context context, String sFileName, String sBody) {
        try {
            File root = new File(Environment.getExternalStorageDirectory(), "MSocketLogs");
            if (!root.exists()) {
                root.mkdirs();
            }
            File gpxfile = new File(root, sFileName);
            FileWriter writer = new FileWriter(gpxfile,true);
            writer.append(sBody + "\n\n");
            writer.flush();
            writer.close();
//            Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static Criteria createCoarseCriteria() {

        Criteria c = new Criteria();
        c.setAccuracy(Criteria.ACCURACY_COARSE);
        c.setAltitudeRequired(false);
        c.setBearingRequired(false);
        c.setSpeedRequired(false);
        c.setCostAllowed(true);
        c.setPowerRequirement(Criteria.POWER_HIGH);
        return c;

    }
    public static Criteria createFineCriteria() {

        Criteria c = new Criteria();
        c.setAccuracy(Criteria.ACCURACY_FINE);
        c.setAltitudeRequired(false);
        c.setBearingRequired(false);
        c.setSpeedRequired(false);
        c.setCostAllowed(true);
        c.setPowerRequirement(Criteria.POWER_LOW);
        return c;

    }


    protected Void doInBackground(Void... arg0) {
        System.out.println("inside the running test");
        if(MainActivity.test_running){

        }else{
            MainActivity.test_running = true;

            NormalDistribution download_dist = new NormalDistribution(download_m,download_s);
            NormalDistribution upload_dist = new NormalDistribution(upload_m,upload_s);
            ExponentialDistribution interval_dist = new ExponentialDistribution(1);

//
            boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
//            System.out.println(isGPSEnabled);
//            System.out.println(isNetworkEnabled);
//            msocketclientnonasync myclient = new msocketclientnonasync("192.168.0.9",5458);
            while (true){
//
                int download_size = (int) download_dist.sample();
                int upload_size = (int) upload_dist.sample();
                Double coin_flip = Math.random();
                String action = "";
                if(coin_flip < 0.5){
                    action = "upload";
                }else{
                    action = "download";
                }
//                locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER,1,1,);
//                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
                  Criteria criteria = new Criteria();
//                String provider = locationManager.getBestProvider(criteria,true);

//                String low = locationManager.getBestProvider(createFineCriteria(),true);

                Double longi = null;
                Double lati = null;
                if(!isGPSEnabled && !isNetworkEnabled){

                }else if(isNetworkEnabled){
//                    System.out.println("Network was enabled");
//                    String low = locationManager.getBestProvider(createCoarseCriteria(),false);
//                    System.out.println("this is the provider  " + low);
                    try{

                        Location l =  locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
                        if(l != null){
                            lati = l.getLatitude();
                            longi = l.getLongitude();
                        }

                    }catch (SecurityException e){
                        e.printStackTrace();

                    }
                    System.out.println("Got location from Network");
                }else if (isGPSEnabled){
//                    System.out.println("GPS was enabled");
//                    String low = locationManager.getBestProvider(createFineCriteria(),false);
//                    System.out.println("this is the provider  " + low);
                    try{

                        Location l =  locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);
                        if(l != null){
                            lati = l.getLatitude();
                            longi = l.getLongitude();
                        }

                    }catch (SecurityException e){
                        e.printStackTrace();

                    }
                    System.out.println("Got location from GPS");
                }

                msocketclientnonasync myclient = new msocketclientnonasync("18.218.2.225",5458);

                response = myclient.run_client(download_size,upload_size,action);
                if(longi != null && lati != null){
                    response = response + "[Location: (" + Double.toString(longi) + "," + Double.toString(lati) + ")]";
                }
//                System.out.printf("This is th extrnal storag state : " + Environment.getExternalStorageState());
                if(Environment.getExternalStorageState().equals("mounted") ){
//                    System.out.println("I am able to write");
                    generateNoteOnSD(context, "logs.txt",response);

                }
                publishProgress(response);

//                int wait_interval = (int) interval_dist.sample();
                int wait_interval = 5;
                System.out.println("this is the wait interval " + String.valueOf(wait_interval));
                try{
                    Thread.sleep(wait_interval * 1000);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }

                if(MainActivity.stop == true) {
                    MainActivity.test_running = false;
                    break;
                }


            }
        }

        return null;

    }

    protected void onProgressUpdate(String... values){
        super.onProgressUpdate(values);
        out.setText(values[0]);
    }
    @Override
    protected void onPostExecute(Void result) {
        out.setText("You have stopped the test. To start again press START TEST." );
        super.onPostExecute(result);
    }
}
