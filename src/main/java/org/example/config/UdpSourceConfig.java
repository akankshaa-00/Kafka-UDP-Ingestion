package org.example.config;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;

import java.util.Map;

public class UdpSourceConfig extends AbstractConfig {

    public static final String PORT_CONFIG = "udp.port";
    public static final String TOPIC_CONFIG = "kafka.topic";
    public static final String RETRY_TOPIC_CONFIG = "kafka.retry.topic";
    public static final String DEAD_LETTER_TOPIC = "dlq.kafka.topic";



    public UdpSourceConfig(Map<String, ?> props) {
        super(conf(), props);
    }

    public static ConfigDef conf() {
        return new ConfigDef()
                .define(PORT_CONFIG, ConfigDef.Type.INT, 9999, ConfigDef.Importance.HIGH, "UDP port to listen on")
                .define(TOPIC_CONFIG, ConfigDef.Type.STRING, "udp_data", ConfigDef.Importance.HIGH, "Target Kafka topic")
                .define(RETRY_TOPIC_CONFIG, ConfigDef.Type.STRING, "retry", ConfigDef.Importance.HIGH, "Retry Kafka topic")
                .define(DEAD_LETTER_TOPIC, ConfigDef.Type.STRING, "dead_letter", ConfigDef.Importance.HIGH, "Dead Letter Kafka topic");


    }

}
