package org.example;

import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.source.SourceTask;
import org.example.config.UdpSourceConfig;
import org.example.exception.InvalidPacketException;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class UdpSourceTask extends SourceTask {

    private DatagramSocket socket;
    private String topic;
    private int port;

    private String mainTopic;
    private String retryTopic;
    private String dlqTopic;



    @Override
    public String version() {
        return "";
    }

    @Override
    public void start(Map<String, String> props) {
    //The start() method is called exactly once when the task is initialized.
        UdpSourceConfig udpconfig=new UdpSourceConfig(props);
        this.port= udpconfig.getInt(UdpSourceConfig.PORT_CONFIG);
        this.topic= udpconfig.getString(UdpSourceConfig.TOPIC_CONFIG);
        this.dlqTopic=udpconfig.getString(UdpSourceConfig.DEAD_LETTER_TOPIC);
        this.retryTopic=udpconfig.getString(UdpSourceConfig.RETRY_TOPIC_CONFIG);

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
        //The poll() method is called repeatedly in a loop by the Kafka Connect framework.
        byte[] buffer = new byte[1024];

        DatagramPacket packet=new DatagramPacket(buffer, buffer.length);

        try{
            //
            socket.receive(packet);

            String message = new String(packet.getData(), 0, packet.getLength()).trim();
            byte[] data=packet.getData();


            //Implemented Retry as well as DeadLetter Queue here
            // Check packet layout
            if (data == null || packet.getLength() == 0 || message.isEmpty()) {
                sourceRecords.add(saveIntoSourceRecord(port, dlqTopic, "Empty or Null Packet"));
                // Log explicitly so we can track issues in docker compose logs
                System.err.println("WARN: Received empty UDP packet. Routing to DLQ: " + dlqTopic);
                return sourceRecords;
            }
            if (message.contains("RETRY")) {
                System.out.println("Routing message to Retry Topic: " + message);
                sourceRecords.add(saveIntoSourceRecord(port, retryTopic, message));
                return sourceRecords;
            }

            // Standard happy-path execution
            System.out.println("Successfully ingested UDP packet: " + message);
            sourceRecords.add(saveIntoSourceRecord(port, topic, message));
            return sourceRecords;

        } catch (java.net.SocketTimeoutException e) {
            // Sockets timeout normally every 1 second if nobody writes; return null to yield control
            return null;
        } catch (Exception e) {
            // FIX: Print the stack trace so bugs don't fail silently!
            System.err.println("ERROR: Error handling UDP packet processing workflow!");
            e.printStackTrace();
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

    public SourceRecord saveIntoSourceRecord(int port,String topic,String message)
    {
        SourceRecord record = new SourceRecord(
                Collections.singletonMap("port", port), // Source Partition
                Collections.singletonMap("offset", System.currentTimeMillis()), // Source Offset
                topic, null, null, null, null, message);

        return record;
    }
}
