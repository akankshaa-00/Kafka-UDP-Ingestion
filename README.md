# Kafka-UDP-Ingestion
A Kafka Source Connector that listen to a UDP port in accordance with required configurations and then forward the incoming messages to Kafka Queue to be consumed later by Consumer program.


Complete Runbook: Starting and Verifying the App
**Step 1: Start the Infrastructure**
Before checking or registering anything, ensure your Docker containers are spun up and running in the background:

`docker compose up -d`
**Step 2: Register the UDP Source Connector**
Submit your connector configuration to Kafka Connect. This tells the system to start listening on UDP port 9999:

`curl -X POST -H "Content-Type: application/json" -d '{"name": "udp-source-connector", "config": {"connector.class": "org.example.manager.UdpSourceConnector", "tasks.max": "1", "udp.port": "9999", "kafka.topic": "udp-data", "key.converter": "org.apache.kafka.connect.storage.StringConverter", "value.converter": "org.apache.kafka.connect.storage.StringConverter"}}' http://localhost:8083/connectors`

**Step 3: Verify the Connector Status**
Now that it's registered, verify that both the connector engine and its worker tasks have initialized cleanly without crashing:


`curl -s http://localhost:8083/connectors/udp-source-connector/status`
Look for "state":"RUNNING" inside the JSON response.

**Step 4: Open the Live Kafka Consumer (Terminal Window 1)**
Launch your real-time console consumer so you can watch messages land in the log partition. Leave this terminal window open; it will show a blinking cursor while it waits:


`docker compose exec kafka kafka-console-consumer --bootstrap-server localhost:9092 --topic udp-data --from-beginning`

**Step 5: Fire a Test Packet (Terminal Window 2)**
Open a separate terminal window and blast a mock JSON payload directly into your open UDP network socket:

`echo '{"deviceId": "sensor-01", "temperature": 23.4}' | nc -u -w1 localhost 9999`

Once executed, look back at Terminal Window 1—your text payload will instantly stream onto the screen.mpose exec kafka kafka-console-consumer --bootstrap-server localhost:9092 --topic udp-data --from-beginning