package org.example.test;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UdpEarTestPort {

    public static void main(String[] args) {
    int port=9999;

    //Datagram Socket for UDP protocol
        try{
            DatagramSocket datagramSocket=new DatagramSocket(port);
            System.out.println("--- Day 1: Listening for UDP on port " + port + " ---");

            // We create a buffer (a bucket) to catch the incoming dat in its raw form
            // since date travels across network in byte form
            byte[] bytebuffer=new byte[1024];

           while(true)
           {
               DatagramPacket datagramPacket = new DatagramPacket(bytebuffer, bytebuffer.length);

               // This line pauses the code until a packet actually hits the port
               datagramSocket.receive(datagramPacket);

               //Convert the raw data into human-readable string
               String received=new String(datagramPacket.getData(),0,datagramPacket.getLength());

               System.out.println("SUCCESS! Caught a message: " + received);
               System.out.println("From: " + datagramPacket.getAddress().getHostAddress() + ":" + datagramPacket.getPort());
           }
        }catch (Exception e) {
            System.err.println("Could not open port. Is another program using it?");
            e.printStackTrace();
        }
    }


}
