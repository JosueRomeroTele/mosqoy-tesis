echo "Warning running: sudo rm -r /var/sw2/sw2.jar"
echo "For deployment information, go to: https://docs.spring.io/spring-boot/docs/current/reference/html/deployment.html#deployment-systemd-service"
sudo systemctl stop sw2.service
sudo rm -r /var/sw2/sw2.jar
sudo git pull
sudo mvn package
sudo cp target/sw2-0.0.1-SNAPSHOT.jar /var/sw2/sw2.jar
sudo systemctl enable sw2.service
sudo systemctl start sw2.service
echo "Script finished"
sudo systemctl status sw2.service
echo "Wait..."
sleep 6
sudo systemctl status sw2.service
