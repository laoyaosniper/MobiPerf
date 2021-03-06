#!/bin/bash
exec >> /var/log/mobiperf 2>&1
echo "####### Running /home/michigan_1/init/start.sh at `date` ########"

cd /home/michigan_1/mobiperf

dl_port=6001
ul_port=6002
config_port=6003
udpserver_port=31341

for i in Downlink Uplink ServerConfig UDPServer
do
	echo "Running $i ..."
	java -Xmx128M -jar $i.jar &
        sleep 1
done

echo "Verifying on ports ..."
for port in $dl_port $ul_port $config_port $udpserver_port
do
	is_up=$(netstat -atup | grep "$port" | wc -l)
	if [ $is_up == 0 ];then
		echo "Port $port doesn't start properly"
		echo "Try \"./stop.sh; ./start.sh\" one more time"
		exit 1
	fi
done
echo "Success deploying and running server"
