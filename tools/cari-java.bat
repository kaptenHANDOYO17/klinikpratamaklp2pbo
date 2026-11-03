@echo off
rem ============================================================
rem  Mencari JDK versi 17 atau lebih baru di laptop ini.
rem  Hasil: variabel JAVAC, JAVA, JAR berisi lokasi programnya.
rem  File ini dipanggil oleh file .bat lain (tidak perlu dibuka sendiri).
rem ============================================================
setlocal
set "KETEMU="

rem 1) javac yang sudah terdaftar di PATH
for /f "delims=" %%p in ('where javac 2^>nul') do (
    if not defined KETEMU call :cek "%%~dpp"
)

rem 2) folder JAVA_HOME
if not defined KETEMU if defined JAVA_HOME call :cek "%JAVA_HOME%\bin\"

rem 3) lokasi instalasi JDK yang umum (termasuk JDK unduhan IntelliJ di .jdks)
for /d %%d in ("%USERPROFILE%\.jdks\*" "%ProgramFiles%\Eclipse Adoptium\*" "%ProgramFiles%\Java\*" "%ProgramFiles%\Microsoft\jdk*" "%ProgramFiles%\Zulu\*" "%ProgramFiles%\Amazon Corretto\*" "%ProgramFiles%\BellSoft\*" "%ProgramFiles%\OpenJDK\*") do (
    if not defined KETEMU call :cek "%%~d\bin\"
)

rem 4) JDK bawaan Android Studio
if not defined KETEMU call :cek "%ProgramFiles%\Android\Android Studio\jbr\bin\"

if not defined KETEMU (
    endlocal
    exit /b 1
)
endlocal & set "JAVAC=%KETEMU%javac.exe" & set "JAVA=%KETEMU%java.exe" & set "JAR=%KETEMU%jar.exe"
exit /b 0


:cek
rem %~1 = folder bin yang diakhiri tanda \
if not exist "%~1javac.exe" exit /b 0
set "VER="
set "MAJOR=0"
for /f "tokens=2" %%v in ('""%~1javac.exe" -version 2^>^&1"') do set "VER=%%v"
if not defined VER exit /b 0
for /f "delims=.-+" %%m in ("%VER%") do set /a "MAJOR=%%m" 2>nul
if %MAJOR% GEQ 17 set "KETEMU=%~1"
exit /b 0
