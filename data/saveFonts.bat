:: BatchGotAdmin
:-------------------------------------
REM  --> Check for permissions
    IF "%PROCESSOR_ARCHITECTURE%" EQU "amd64" (
>nul 2>&1 "%SYSTEMROOT%\SysWOW64\cacls.exe" "%SYSTEMROOT%\SysWOW64\config\system"
) ELSE (
>nul 2>&1 "%SYSTEMROOT%\system32\cacls.exe" "%SYSTEMROOT%\system32\config\system"
)

REM --> If error flag set, we do not have admin.
if '%errorlevel%' NEQ '0' (
    echo Requesting administrative privileges...
    goto UACPrompt
) else ( goto gotAdmin )

:UACPrompt
    echo Set UAC = CreateObject^("Shell.Application"^) > "%temp%\getadmin.vbs"
    set params= %*
    echo UAC.ShellExecute "cmd.exe", "/c ""%~s0"" %params:"=""%", "", "runas", 1 >> "%temp%\getadmin.vbs"

    "%temp%\getadmin.vbs"
    del "%temp%\getadmin.vbs"
    exit /B

:gotAdmin
    pushd "%CD%"
    CD /D "%~dp0"
:--------------------------------------  
@echo off
REM  --> Store all file names in temp file
REM  --> dir /B %1 > dir_file.txt
REM  --> Loop through all file names
for /f "tokens=* delims= " %%I in (dir_file.txt) do (
  gawk -f awk_script_file.awk %%I
  echo %1\%%I
  REM  --> Only do stuff if right file extension
  if "%%~xI" == ".ttf" (
    REM  --> Copy the file to the windows font directory
    copy "%1\%%I" "%WINDIR%\Fonts\%%I"
    REM  --> Register the font in the windows regedit thing
    REG ADD "HKEY_LOCAL_MACHINE\SOFTWARE\Microsoft\Windows NT\CurrentVersion\Fonts" /v "%1\%%I" /t REG_SZ /d "%%~nxI" /f
	echo %1\%%I was added
  )
  if "%%~xI" == ".otf" (
    REM  --> Copy the file to the windows font directory
    copy "%1\%%I" "%WINDIR%\Fonts\%%I"
    REM  --> Register the font in the windows regedit thing
    REG ADD "HKEY_LOCAL_MACHINE\SOFTWARE\Microsoft\Windows NT\CurrentVersion\Fonts" /v "%1\%%I" /t REG_SZ /d "%%~nxI" /f
	echo %1\%%I was added
  )
  if "%%~xI" == ".fnt" (
    REM  --> Copy the file to the windows font directory
    copy "%1\%%I" "%WINDIR%\Fonts\%%I"
    REM  --> Register the font in the windows regedit thing
    REG ADD "HKEY_LOCAL_MACHINE\SOFTWARE\Microsoft\Windows NT\CurrentVersion\Fonts" /v "%1\%%I" /t REG_SZ /d "%%~nxI" /f
	echo %1\%%I was added
  )
)

