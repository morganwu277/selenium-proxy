Edge under Win10
===
#Win 10 
https://app.vagrantup.com/chingc/boxes/win10
or .... using https://github.com/joefitzgerald/packer-windows to do windows packer yourself.

## 1. needs set local policy
https://stackoverflow.com/questions/41461217/microsoftwebdriver-fails-when-constructing-while-running-under-teamcity-agents

## 2. needs to switch `UAC notify` to be off in Control Pannel

## 3. needs to setup auto login for IEUser
https://www.networkcomputing.com/networking/how-enable-windows-10-auto-login/402273242

## 4. save init_state snapshot
```bash
vagrant snapshot save "init_state" --force ; vagrant snapshot restore --provision init_state
```

# install web driver 16.16299

Ref: https://developer.microsoft.com/en-us/microsoft-edge/tools/webdriver/    
use 16.16299 version

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
