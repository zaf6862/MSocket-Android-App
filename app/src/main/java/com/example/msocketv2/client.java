package com.example.msocketv2;
import android.os.AsyncTask;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.text.DecimalFormat;



public class client extends AsyncTask<Void, Void, Void> {
    private static  int    serverport;
    private static  String serverip;
    private String response = "";
    private TextView textResponse;
    private static DecimalFormat df = new DecimalFormat("0.00##");
    private int TOTAL_ROUND = 1;
    private int numOfBytes = 10000000;
    private double[] throughput;
    private double[] download_time;

    client(String addr, int port, TextView textResponse, int numbytes, int numrounds) {
        serverip = addr;
        serverport = port;
        numOfBytes=numbytes;
        TOTAL_ROUND=numrounds;
        throughput=new double[numrounds];
        download_time=new double[numrounds];
        this.textResponse = textResponse;
    }


    protected Void doInBackground(Void... arg0) {

        Socket socket = null;

        try {
            socket = new Socket(InetAddress.getByName(serverip), serverport);
            OutputStream os = socket.getOutputStream();
            InputStream is = socket.getInputStream();
            int rd = 0;
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
                download_time[rd]=elapsed;
                throughput[rd]= (numOfBytes * 8) / 1000.0 / elapsed;

                rd++;

            }
            double sum1=0;
            double sum2=0;
            for(int i=0;i<throughput.length;i++){
                sum1= sum1 + throughput[i];
                sum2 = sum2 + download_time[i];

            }

            response = response + "[Average Download Time:] " + Double.toString(sum2/TOTAL_ROUND)+ " ms" + "\n";
            response = response + "[Average Throughput:] " +  Double.toString(sum1/TOTAL_ROUND).substring(0,6) + " mbps" + "\n";

            os.write(-1);
            os.flush();

        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            response = "UnknownHostException: " + e.toString();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            response = "IOException: " + e.toString();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        textResponse.setText(response);
        super.onPostExecute(result);
    }

}
