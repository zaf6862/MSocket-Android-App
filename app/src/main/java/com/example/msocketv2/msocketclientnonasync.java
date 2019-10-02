package com.example.msocketv2;

import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import edu.umass.cs.msocket.FlowPath;
import edu.umass.cs.msocket.MSocket;

public class msocketclientnonasync {
    private static  int    serverport;
    private static  String serverip;
    private String response = "";
    private static DecimalFormat df = new DecimalFormat("0.00##");
    private static int TOTAL_ROUND = 1;

    msocketclientnonasync(String addr, int port) {
        serverip = addr;
        serverport = port;
    }

    public String run_client(int download_size, int upload_size, String action){
        try{
            Date currentTime = Calendar.getInstance().getTime();
            String time = currentTime.toString();
            response="";
            response = response + "[" + time + "] \t" ;
            response = response + "[Action : " + action + "] ";
            MSocket ms = new MSocket(InetAddress.getByName(serverip), serverport);
            OutputStream os = ms.getOutputStream();
            InputStream is = ms.getInputStream();
            if(action.equals("download")){
                String indicate_action_to_server = "d";
                byte[] act_in_bytes = indicate_action_to_server.getBytes();
                os.write(act_in_bytes);
                response = response + "[Download size: " + String.valueOf(download_size) + "] ";
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
                System.out.println("[Client:] To download " + numSent + " bytes data from input stream...");
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
                response = response + "[Download Time: " + Double.toString(elapsed)+ " ms" + "]" + "\t";
                response = response + "[Throughput: " + df.format((download_size * 8) / 1000.0 / elapsed) + " mbps" + "]" +"\t";
//                os.write(-1);
                os.flush();
                try{
                    ms.close();
                }catch (IOException e){

                }

                System.out.println("this is the download response \n\n" + response + "\n\n");

            }else if(action.equals("upload")){
                String indicate_action_to_server = "u";
                byte[] act_in_bytes = indicate_action_to_server.getBytes();
                os.write(act_in_bytes);
                response = response + "[Upload size: " + String.valueOf(upload_size) + "] ";
                int rd = 0;
                String[] names= new String[2];
                names[0] = "flowpath-1";
                names[1] = "flowpath-2";
                for (int i = 0; i < ms.getActiveFlowPaths().size(); i++) {
                    FlowPath currfp = ms.getActiveFlowPaths().get(i);
                    System.out.println("Flowpath id=" + currfp.getFlowPathId()+ "\n " + names[i] + " ip=" + currfp.getLocalEndpoint().toString());
                    response = response + "[" + names[i] +" ip=" + currfp.getLocalEndpoint().toString() + "]" + "\t";
                }

                int numSent = upload_size;
                System.out.println("[Client:] To upload " + numSent + " bytes data to the server...");
                ByteBuffer dbuf = ByteBuffer.allocate(4);
                dbuf.putInt(numSent);
                byte[] bytes = dbuf.array();
                os.write(bytes);
                byte[] b = new byte[upload_size];
                new Random().nextBytes(b);
                long start = System.currentTimeMillis();
                try {
                    os.write(b);
                    os.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                long elapsed = System.currentTimeMillis() - start;

                System.out.println("[" + time + "] Data upload finished. It takes " + elapsed + " ms");
                response = response + "[Upload Time: " + Double.toString(elapsed)+ " ms" + "]" + "\t";
                response = response + "[Throughput: " + df.format((upload_size * 8) / 1000.0 / elapsed) + " mbps" + "]" +"\t";
                System.out.println("this is the uplaod response \n\n" + response + "\n\n");
                try{
                    ms.close();
                    System.out.println("socket closed");
                }catch (IOException e){

                }
            }


        } catch (Exception e) {
        }
        return response;
    }
}
