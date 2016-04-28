;This file will be executed next to the application bundle image
;I.e. current directory will contain folder PDR with application files
[Setup]
AppId={{cz.muni.physics.pdr.app}}
AppName=PDR
AppVersion=1.0
AppVerName=PDR 1.0
AppPublisher=Masaryk University
AppComments=PDR
AppCopyright=Copyright (C) 2016
;AppPublisherURL=http://java.com/
;AppSupportURL=http://java.com/
;AppUpdatesURL=http://java.com/
DefaultDirName={pf}\PDR
DisableStartupPrompt=Yes
DisableDirPage=No
DisableProgramGroupPage=No
DisableReadyPage=No
DisableFinishedPage=No
DisableWelcomePage=No
DefaultGroupName=PDR
;Optional License
LicenseFile=
;WinXP or above
MinVersion=0,5.1 
OutputBaseFilename=PDR-1.0
Compression=lzma
SolidCompression=yes
PrivilegesRequired=admin
SetupIconFile=PDR\PDR.ico
UninstallDisplayIcon={app}\PDR.ico
UninstallDisplayName=PDR
WizardImageStretch=Yes
WizardSmallImageFile=PDR-setup-icon.bmp   
ArchitecturesInstallIn64BitMode=


[Languages]
Name: "english"; MessagesFile: "compiler:Default.isl"

[Files]
Source: "PDR\PDR.exe"; DestDir: "{app}"; Flags: ignoreversion
Source: "PDR\*"; DestDir: "{app}"; Flags: ignoreversion recursesubdirs createallsubdirs

[Icons]
Name: "{group}\PDR"; Filename: "{app}\PDR.exe"; IconFilename: "{app}\PDR.ico"; Check: returnTrue()
Name: "{commondesktop}\PDR"; Filename: "{app}\PDR.exe";  IconFilename: "{app}\PDR.ico"; Check: returnFalse()


[Run]
Filename: "{app}\PDR.exe"; Parameters: "-Xappcds:generatecache"; Check: returnFalse()
Filename: "{app}\PDR.exe"; Description: "{cm:LaunchProgram,PDR}"; Flags: nowait postinstall skipifsilent; Check: returnTrue()
Filename: "{app}\PDR.exe"; Parameters: "-install -svcName ""PDR"" -svcDesc ""Tool for retrieving photometric data of stellar objects"" -mainExe ""PDR.exe""  "; Check: returnFalse()

[UninstallRun]
Filename: "{app}\PDR.exe "; Parameters: "-uninstall -svcName PDR -stopOnUninstall"; Check: returnFalse()

[Code]
function returnTrue(): Boolean;
begin
  Result := True;
end;

function returnFalse(): Boolean;
begin
  Result := False;
end;

function InitializeSetup(): Boolean;
begin
// Possible future improvements:
//   if version less or same => just launch app
//   if upgrade => check if same app is running and wait for it to exit
//   Add pack200/unpack200 support? 
  Result := True;
end;  
