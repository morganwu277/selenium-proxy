
# turn off firewall
netsh advfirewall set allprofiles state off

cd "FileSystem::\\VBOXSVR\vagrant\selenium-ie"

# https://stackify.com/what-is-powershell/
Start-Process -FilePath "powershell" -ArgumentList "java -cp selenium-server-standalone-3.14.0.jar org.openqa.grid.selenium.GridLauncherV3 -role node -nodeConfig node.json > output.log 2>&1 " 




