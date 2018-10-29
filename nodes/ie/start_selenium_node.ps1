
# turn off firewall
netsh advfirewall set allprofiles state off

cd "FileSystem::\\VBOXSVR\vagrant\selenium-ie"

# https://stackify.com/what-is-powershell/
$username = 'IEUser'
$password = 'Passw0rd!'

$securePassword = ConvertTo-SecureString $password -AsPlainText -Force
$credential = New-Object System.Management.Automation.PSCredential $username, $securePassword

Start-Process -FilePath "powershell" -Credential $credential -ArgumentList "java", "-cp", "selenium-server-standalone-3.14.0.jar", "org.openqa.grid.selenium.GridLauncherV3", "-role", "node", "-nodeConfig", "node.json", ">", "output.log", "2>&1"



