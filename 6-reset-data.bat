@echo off
title Reset Data - Klinik Pratama
cd /d "%~dp0"
echo Perintah ini MENGHAPUS semua data di folder "data"
echo dan mengembalikannya ke data contoh saat aplikasi dibuka lagi.
echo Cocok dipakai sebelum demo agar data rapi.
echo.
choice /c YN /m "Yakin reset data"
if errorlevel 2 goto batal
if exist data rmdir /s /q data
echo Data sudah direset. Jalankan 2-jalankan-aplikasi.bat
pause
goto :eof
:batal
echo Dibatalkan.
pause
