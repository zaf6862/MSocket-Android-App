package com.example.msocketv2;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.distribution.ExponentialDistribution;



public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView out = (TextView) findViewById(R.id.output);
        final EditText bytes=findViewById(R.id.numbytes);
        final EditText rounds=findViewById(R.id.numrounds);

        Button button= (Button) findViewById(R.id.request_bytes);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String bytes_str=bytes.getText().toString();
                String rounds_str=rounds.getText().toString();
                int numbytes=Integer.parseInt(bytes_str);
                int numrounds=Integer.parseInt(rounds_str);

                double mean_upload_size = 100000000;
                double std_upload_size  = 30000000;
                NormalDistribution normdist = new NormalDistribution(mean_upload_size,std_upload_size);
                numbytes = (int) normdist.sample();
                for(int i=0;i<2;i++){
                    numbytes = (int) normdist.sample();
                    msocketclient myclient = new msocketclient("18.218.2.225",5458,out,numbytes,numrounds);
                    myclient.execute();

                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }


            }
        });



    }



}
