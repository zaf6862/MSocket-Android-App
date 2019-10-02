package com.example.msocketv2;
import android.media.effect.Effect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Context;
import android.location.LocationListener;
import android.location.LocationManager;
import org.apache.commons.math3.distribution.ExponentialDistribution;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.ode.nonstiff.ThreeEighthesFieldIntegrator;
import org.w3c.dom.Text;


public class MainActivity extends AppCompatActivity {

    static boolean stop = false;
    static boolean test_running = false;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final TextView out = (TextView) findViewById(R.id.output);
        final EditText download_mean = findViewById(R.id.download_mean);
        final EditText download_std = findViewById(R.id.download_std);
        final EditText upload_mean = findViewById(R.id.uplaod_mean);
        final EditText upload_std = findViewById(R.id.upload_std);
        final Button start_button= (Button) findViewById(R.id.start_button);
        final Button stop_button = (Button) findViewById(R.id.stop_button);

        start_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stop = false;
                double download_m;
                double download_s;
                double upload_m;
                double upload_s;
                if(download_mean.getText().toString().trim().length() == 0){
                    download_m = 1 * 1000000;
//                    Toast.makeText(getApplicationContext(), "No value for download mean added so using the default value", Toast.LENGTH_SHORT);
                }else{
                    download_m = Double.parseDouble(download_mean.getText().toString());
                    download_m = download_m * 1000000;
                }

                if(download_std.getText().toString().trim().length() == 0){
                    download_s =   0.1 * 1000000;
//                    Toast.makeText(getApplicationContext(), "No value for download mean added so using the default value", Toast.LENGTH_SHORT);
                }else{
                    download_s = Double.parseDouble(download_std.getText().toString());
                    download_s = download_s * 1000000;

                }

                if(upload_mean.getText().toString().trim().length() == 0){
                    upload_m = 1 * 1000000;
//                    Toast.makeText(getApplicationContext(), "No value for download mean added so using the default value", Toast.LENGTH_SHORT);
                }else{
                    upload_m = Double.parseDouble(upload_mean.getText().toString());
                    upload_m = upload_m * 1000000;
                }

                if(upload_std.getText().toString().trim().length() == 0){
                    upload_s=  0.1 * 1000000;
                }else{
                    upload_s = Double.parseDouble(upload_std.getText().toString());
                    upload_s = upload_s * 1000000;
                }

                Context c = getApplicationContext();
                LocationManager locationManager = (LocationManager) getSystemService(getApplicationContext().LOCATION_SERVICE);

                RunTest runtest = new RunTest(download_m,download_s,upload_m,upload_s,out,locationManager,c);
                runtest.execute();

            }
        });

        stop_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("stop is true now");
                stop = true;
            }
        });



    }



}
