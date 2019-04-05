package com.example.msocketv2;

//import static org.junit.Assert.fail;

import android.os.AsyncTask;
import android.widget.TextView;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.text.DecimalFormat;


import edu.umass.cs.msocket.FlowPath;
import edu.umass.cs.msocket.MSocket;
import edu.umass.cs.msocket.mobility.MobilityManagerClient;

public class msocketclient  extends AsyncTask<Void, Void, Void> {

    private static  int    serverport;
    private static  String serverip;
    private String response = "";
    private TextView textResponse;
    private static DecimalFormat df = new DecimalFormat("0.00##");
    private static final int TOTAL_ROUND = 1;
    private static int numOfBytes = 10000000;


    msocketclient(String addr, int port, TextView textResponse) {
        serverip = addr;
        serverport = port;

        this.textResponse = textResponse;
    }


    protected Void doInBackground(Void... arg0) {

        try{
                MSocket ms = new MSocket(InetAddress.getByName(serverip), serverport);
                ms.addFlowPath();

                OutputStream os = ms.getOutputStream();
                InputStream is = ms.getInputStream();

                // wait for 2 seconds for all connections
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                int rd = 0;
                String[] names= new String[2];
                names[0] = "wifi";
                names[1] = "Cellular";
                for (int i = 0; i < ms.getActiveFlowPaths().size(); i++) {
                    FlowPath currfp = ms.getActiveFlowPaths().get(i);

                    System.out.println("Flowpath id=" + currfp.getFlowPathId()+ "\n " + names[i] + " ip=" + currfp.getLocalEndpoint().toString());
//                    response = response + "Flowpath id=" + currfp.getFlowPathId() + "\n" + names[i] +" ip=" + currfp.getLocalEndpoint().toString() + "\n";
                    response = response + names[i] +" ip=" + currfp.getLocalEndpoint().toString() + "\n";
                }
                byte[] b = new byte[numOfBytes];
                while (rd < TOTAL_ROUND) {

                    int numSent = numOfBytes;
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
                    System.out.println("[Download Time :] " + elapsed + " ms");
                    System.out.println("[Throughput:] " + df.format((numOfBytes * 8) / 1000.0 / elapsed) + " mbps");


                    response = response + "[Download Time:] " + Long.toString(elapsed)+ " ms" + "\n";
                    response = response + "[Throughput:] " + df.format((numOfBytes * 8) / 1000.0 / elapsed) + " mbps" + "\n";

                    rd++;

                }

                os.write(-1);
                os.flush();

                ms.close();
                System.out.println("Socket closed");
                MobilityManagerClient.shutdownMobilityManager();

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
