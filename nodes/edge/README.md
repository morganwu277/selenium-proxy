Edge under Win10
===
#Win 10 
https://app.vagrantup.com/chingc/boxes/win10
or .... using https://github.com/joefitzgerald/packer-windows to do windows packer yourself.

## (optional)1. needs set local policy
https://stackoverflow.com/questions/41461217/microsoftwebdriver-fails-when-constructing-while-running-under-teamcity-agents
use `secpol.msc` to enter into.
```
Press and Hold the Windows key and Press R
In the run dialog box, Type secpol.msc and Press Enter (Opens Local Security Policy)
On the left pane, Navigate to Security Settings > Local Policies >Security Options
Locate User Account Control Admin Approval Mode for the Built-in Administrator account on the right pane. Double Click it to open its properties
Select Enabled in the Local Security Setting tab and Click Ok
Now Restart your computer and check if it works now
```

## (optional)2. needs to switch `UAC notify` to be off in Control Pannel

## 3. needs to setup auto login for IEUser
use `netplwiz` command.
https://www.networkcomputing.com/networking/how-enable-windows-10-auto-login/402273242

## 4. install jre

## 5. save init_state snapshot
```bash
vagrant snapshot save "init_state" --force ; vagrant snapshot restore --provision init_state
```
## 6. For IE
### 1. IEDriverServer
https://github.com/SeleniumHQ/selenium/wiki/InternetExplorerDriver
needs to create `HKEY_LOCAL_MACHINE\SOFTWARE\Wow6432Node\Microsoft\Internet Explorer\Main\FeatureControl\FEATURE_BFCACHE`, and then create a `DWORD` key, as `iexplore.exe`=`0`.
### 2. OS settings
1. auto login
2. Vagrantfile settings
```bash
  config.vm.communicator = "winrm"
  config.vm.guest = "windows"

  config.winrm.username = "IEUser"
  config.winrm.password = "Passw0rd!"
  config.winrm.timeout = 180

  config.ssh.username = "IEUser"
  config.ssh.password = "Passw0rd!"
  config.ssh.extra_args = "cmd"  # cmd or powershell
```

```bash
  config.vm.provision "shell" do |s|
    s.path = "./start_selenium_node.ps1"
    s.powershell_elevated_interactive = "true"
    s.privileged = "true"
  end
```
3. WINRM settings in CMD
https://www.vagrantup.com/docs/boxes/base.html
4. firewall settings
https://www.vagrantup.com/docs/boxes/base.html#additional-winrm-1-1-configuration
5. using the proteted mode level in all zone, either ON or OFF, but should be the same
https://github.com/seleniumQuery/seleniumQuery/wiki/seleniumQuery-and-IE-Driver#change-the-protected-mode-in-all-zones
6. Win10 Optimization
http://www.xitonghe.com/jiaocheng/Windows10-3467.html
7. Win8 Optimization
https://www.iplaysoft.com/windows8-you-hua-ji-qiao.html

# install web driver 16.16299

Ref: https://developer.microsoft.com/en-us/microsoft-edge/tools/webdriver/    
for `chingc/win10` vbox, use 16.16299 version

or 
```bash
DISM.exe /Online /Add-Capability /CapabilityName:Microsoft.WebDriver~~~~0.0.1.0
```

# get windows os build version
```powershell
PS C:\> [System.Environment]::OSVersion.Version

Major  Minor  Build  Revision
-----  -----  -----  --------
6      1      7601   65536
```
or
```powershell
(Get-ItemProperty -Path "HKLM:\SOFTWARE\Microsoft\Windows NT\CurrentVersion" -Name ReleaseId).ReleaseId
```
# change account username
```bash
netplwiz
```
