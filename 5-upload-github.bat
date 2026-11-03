@echo off
title Upload ke GitHub - Klinik Pratama
cd /d "%~dp0"

where git >nul 2>nul
if errorlevel 1 goto tanpagit

rem ---------- identitas pembuat commit (sekali saja) ----------
git config --global user.name >nul 2>nul
if errorlevel 1 goto isinama
goto cekemail
:isinama
set /p "GNAMA=Nama Anda untuk commit: "
git config --global user.name "%GNAMA%"

:cekemail
git config --global user.email >nul 2>nul
if errorlevel 1 goto isiemail
goto cekrepo
:isiemail
set /p "GEMAIL=Email akun GitHub Anda: "
git config --global user.email "%GEMAIL%"

rem ---------- siapkan repository lokal ----------
:cekrepo
if exist ".git" goto commit
git init -b main

:commit
echo.
git add .
git status --short
echo.
set "PESAN=Update aplikasi Klinik Pratama"
set /p "PESAN=Pesan commit [Enter = %PESAN%]: "
git commit -m "%PESAN%"

rem ---------- alamat repository GitHub ----------
git remote get-url origin >nul 2>nul
if errorlevel 1 goto isiremote
goto push
:isiremote
echo.
echo Buat dulu repository KOSONG di https://github.com/new
echo (jangan centang "Add a README file"), lalu salin URL-nya.
set /p "URL=Tempel URL repository, contoh https://github.com/namaanda/klinik-pratama.git : "
git remote add origin "%URL%"

:push
git branch -M main
git push -u origin main
if errorlevel 1 echo Push GAGAL. Cek URL repository dan login GitHub (biasanya muncul jendela login di browser).
echo.
pause
goto :eof

:tanpagit
echo Git belum terpasang. Jalankan dulu 1-cek-instal-java.bat
pause
