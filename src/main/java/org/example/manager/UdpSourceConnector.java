package org.example.manager;

import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.source.SourceConnector;
import org.example.UdpSourceTask;
import org.example.config.UdpSourceConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UdpSourceConnector extends SourceConnector {

    Map<String,String> configProps;

    @Override
    public void start(Map<String, String> props) {
        // The manager reads the settings and stores them
        this.configProps = props;
    }

    @Override
    public Class<? extends Task> taskClass() {
        // Telling Kafka which class is the "Worker"
        return UdpSourceTask.class;
    }

    @Override
    public List<Map<String, String>> taskConfigs(int maxTasks) {
        // Even if Kafka asks for 10 tasks, we only return 1
        // because only one process can listen to a UDP port at a time!
        List<Map<String, String>> configs = new ArrayList<>();
        configs.add(configProps);
        return configs;
    }

    @Override
    public void stop() {

    }

    @Override
    public ConfigDef config() {
        // Return the definition we wrote in our Config class
        return UdpSourceConfig.conf();
    }

    @Override
    public String version() {
        return "1.0";
    }
}
