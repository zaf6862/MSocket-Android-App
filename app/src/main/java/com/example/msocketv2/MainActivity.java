package com.example.msocketv2;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.gsm.SmsManager;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;


import static java.sql.DriverManager.println;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView out = (TextView) findViewById(R.id.output);
//        out.setText("you are done");
        Button button= (Button) findViewById(R.id.request_file);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                client myclient = new client("128.110.154.176", 9876, out);
//                myclient.execute();

                msocketclient myclient = new msocketclient("18.218.2.225",5458,out);
                myclient.execute();
            }
        });



    }



}
