# Environmental sensors
The system is to monitor environmental information from sensors. Now, it supports temperature and humidity sensors.
The system supports multiple warehouses which receive information from sensors by UDP protocol.
The test coverage is 75% <a href="https://htmlpreview.github.io/?https://github.com/georgefungkp/Sensor/blob/main/TestConverageReport/index.html"> Report is here.</a>

<h2>Apache Artemis</h2>
The communication between Central Service and Warehouse Service is through ApacheMQ Artemis. You can find installation of <a href="https://activemq.apache.org/components/artemis/download/ApacheMQ"> ApacheMQ Artemis </a>
Local development is triggered by  
"C:\apache-artemis-2.37.0\george\bin\artemis" run

<h2> Sensors Communication</h2>

Sensors can use the following <a href="https://help.ubidots.com/en/articles/937233-sending-tcp-udp-packets-using-netcat#test-your-netcat-understanding-as-a-client-server"> tip</a> to communicate with Warehouse service

<h3> Sample message </h3>
Temperature Sensors: ncat -u localhost 3344<br>
Message format: sensor_id=t<int value>; value=<int value> e.g. sensor_id=t1; value=30

Humidity Sensors: ncat -u localhost 3355<br>
Message format: sensor_id=h<int value>; value=<int value> e.g. sensor_id=h1; value=40



