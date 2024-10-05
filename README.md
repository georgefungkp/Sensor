# Sensor

Installation of <a href="https://activemq.apache.org/components/artemis/download/ApacheMQ"> ApacheMQ Artemis </a>

Starting of Apache Artemis by 
"C:\apache-artemis-2.37.0\george\bin\artemis" run

A Sample of Sensors

Client side should use the following <a href="https://help.ubidots.com/en/articles/937233-sending-tcp-udp-packets-using-netcat#test-your-netcat-understanding-as-a-client-server"> tip</a> to communicate with Ware house service

Temperature Sensors: ncat -u localhost 3344
Message format: sensor_id=t<int value>; value=<int value> e.g. sensor_id=t1; value=30

Humidity Sensors: ncat -u localhost 3355
Message format: sensor_id=h<int value>; value=<int value> e.g. sensor_id=h1; value=40



