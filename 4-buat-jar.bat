@echo off
title Buat File JAR - Klinik Pratama
cd /d "%~dp0"

call "%~dp0tools\cari-java.bat"
if errorlevel 1 goto tanpajava

if exist out rmdir /s /q out
"%JAVAC%" -encoding UTF-8 --release 17 -d out -sourcepath src src\klinik\App.java src\klinik\UjiKonsol.java
if errorlevel 1 goto gagal

if not exist dist mkdir dist
"%JAR%" --create --file dist\KlinikPratama.jar --main-class klinik.App -C out .
if errorlevel 1 goto gagal

echo.
echo Berhasil: dist\KlinikPratama.jar
echo Jalankan dengan double-click file tersebut.
echo (Data aplikasi versi JAR disimpan di folder dist\data)
pause
goto :eof

:tanpajava
echo JDK 17 atau lebih baru tidak ditemukan. Jalankan dulu 1-cek-instal-java.bat
pause
goto :eof

:gagal
echo Proses GAGAL. Baca pesan error di atas.
pause
