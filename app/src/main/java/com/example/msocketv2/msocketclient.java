package com.example.msocketv2;

//import static org.junit.Assert.fail;

import android.os.AsyncTask;
import android.widget.TextView;

import org.apache.commons.math3.ode.nonstiff.ThreeEighthesFieldIntegrator;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import edu.umass.cs.msocket.FlowPath;
import edu.umass.cs.msocket.MSocket;
import edu.umass.cs.msocket.mobility.MobilityManagerClient;

import java.util.Calendar;
import java.util.Date;

public class msocketclient  extends AsyncTask<Void, Void, Void> {

    private static  int    serverport;
    private static  String serverip;
    private String response = "";
    private TextView textResponse;
    private static DecimalFormat df = new DecimalFormat("0.00##");
    private int download_size;
    private int upload_size;
    private String action;
    private static int TOTAL_ROUND = 1;

    msocketclient(String addr, int port, TextView textResponse, int download_s,int upload_s, String act) {
        action = act;
        serverip = addr;
        serverport = port;
        download_size = download_s;
        upload_size = upload_s ;
        this.textResponse = textResponse;

    }


    protected Void doInBackground(Void... arg0) {

        try{
            System.out.println("client has been called");
                Date currentTime = Calendar.getInstance().getTime();
                String time = currentTime.toString();

                response = response + "[" + time + "] \t" ;
                MSocket ms = new MSocket(InetAddress.getByName(serverip), serverport);
//                ms.addFlowPath();
                OutputStream os = ms.getOutputStream();
                InputStream is = ms.getInputStream();
                // wait for 2 seconds for all connections
//              try {
//                  Thread.sleep(2000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
                if(action == "download"){
                    Thread.sleep(10000);
                    int rd = 0;
                    String[] names= new String[2];
                    names[0] = "WiFi";
                    names[1] = "Cellular";
                    for (int i = 0; i < ms.getActiveFlowPaths().size(); i++) {
                        FlowPath currfp = ms.getActiveFlowPaths().get(i);
                        System.out.println("Flowpath id=" + currfp.getFlowPathId()+ "\n " + names[i] + " ip=" + currfp.getLocalEndpoint().toString());
                        response = response + "[" + names[i] +" ip=" + currfp.getLocalEndpoint().toString() + "]" + "\t";
                    }
                    byte[] b = new byte[download_size];
                    int numSent = download_size;
                    System.out.println("[Client:] To read " + numSent + " bytes data from input stream...");
                    ByteBuffer dbuf = ByteBuffer.allocate(4);
                    dbuf.putInt(numSent);
                    byte[] bytes = dbuf.array();

                    int numRead;
                    int totalRead = 0;

                    long start = System.currentTimeMillis();

                    os.write(bytes);
                    do {
                        numRead = is.read(b);
                        if (numRead >= 0)
                            totalRead += numRead;
                    } while (totalRead < numSent);
                    long elapsed = System.currentTimeMillis() - start;
//                        System.out.println("[Download Time :] " + elapsed + " ms");
//                        System.out.println("[Throughput:] " + df.format((numOfBytes * 8) / 1000.0 / elapsed) + " mbps");
//                        download_time[rd]=elapsed;
//                        throughput[rd]= (numOfBytes * 8) / 1000.0 / elapsed;


                    response = response + "[Download Time: " + Double.toString(elapsed)+ " ms" + "]" + "\t";
                    response = response + "[Throughput: " + df.format((download_size * 8) / 1000.0 / elapsed) + " mbps" + "]" +"\t";
                    os.write(-1);
                    os.flush();

                    ms.close();
                    System.out.println("this is the response \n\n" + response + "\n\n");
                    System.out.println("Socket closed");
//                MobilityManagerClient.shutdownMobilityManager();
                }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        textResponse.setText(response);
        super.onPostExecute(result);
    }

}
