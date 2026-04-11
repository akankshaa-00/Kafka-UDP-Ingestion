package org.example;

import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.source.SourceTask;
import org.example.config.UdpSourceConfig;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class UdpSourceTask extends SourceTask {

    private DatagramSocket socket;
    private String topic;
    private int port;


    @Override
    public String version() {
        return "";
    }

    @Override
    public void start(Map<String, String> props) {

        UdpSourceConfig udpconfig=new UdpSourceConfig(props);
        this.port= udpconfig.getInt(UdpSourceConfig.PORT_CONFIG);
        this.topic= udpconfig.getString(UdpSourceConfig.TOPIC_CONFIG);

        try{
            this.socket=new DatagramSocket(port);
            // Set a timeout so poll() doesn't block forever
            this.socket.setSoTimeout(1000);
        } catch (Exception e) {
            throw new RuntimeException("Failed to open UDP socket", e);
        }
    }


    @Override
    public List<SourceRecord> poll() throws InterruptedException {
        List<SourceRecord> sourceRecords=new ArrayList<>();
        byte[] buffer = new byte[1024];

        DatagramPacket packet=new DatagramPacket(buffer, buffer.length);

        try{
            socket.receive(packet);
            String message= new String(packet.getData(),packet.getLength());

            // Wrap the message for Kafka
            SourceRecord record = new SourceRecord(
                    Collections.singletonMap("port", port), // Source Partition
                    Collections.singletonMap("offset", System.currentTimeMillis()), // Source Offset
                    topic, null, null, null, null, message);

            sourceRecords.add(record);
            return sourceRecords;
        } catch (java.net.SocketTimeoutException e) {
            return null; // No data right now, tell Kafka to try again later
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void stop() {
    if(socket!=null)
    {
        socket.close();
    }
    }
}
