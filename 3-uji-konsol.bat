@echo off
title Uji Konsol - Klinik Pratama
cd /d "%~dp0"

call "%~dp0tools\cari-java.bat"
if errorlevel 1 goto tanpajava

if exist out rmdir /s /q out
"%JAVAC%" -encoding UTF-8 --release 17 -d out -sourcepath src src\klinik\App.java src\klinik\UjiKonsol.java
if errorlevel 1 goto gagal

rem Menguji model, DAO, dan controller tanpa tampilan (data uji di folder sementara)
"%JAVA%" -cp out klinik.UjiKonsol
echo.
pause
goto :eof

:tanpajava
echo JDK 17 atau lebih baru tidak ditemukan. Jalankan dulu 1-cek-instal-java.bat
pause
goto :eof

:gagal
echo Kompilasi GAGAL. Baca pesan error di atas.
pause
