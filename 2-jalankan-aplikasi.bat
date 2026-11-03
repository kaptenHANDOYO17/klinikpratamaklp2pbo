@echo off
title Klinik Pratama
cd /d "%~dp0"

call "%~dp0tools\cari-java.bat"
if errorlevel 1 goto tanpajava

echo [1/2] Mengompilasi kode program...
if exist out rmdir /s /q out
"%JAVAC%" -encoding UTF-8 --release 17 -d out -sourcepath src src\klinik\App.java src\klinik\UjiKonsol.java
if errorlevel 1 goto gagal

echo [2/2] Menjalankan aplikasi... (jendela hitam ini biarkan terbuka)
echo       Login awal: admin / admin123
"%JAVA%" -cp out klinik.App
goto :eof

:tanpajava
echo JDK 17 atau lebih baru tidak ditemukan.
echo Jalankan dulu file 1-cek-instal-java.bat
pause
goto :eof

:gagal
echo.
echo Kompilasi GAGAL. Baca pesan error di atas.
pause
