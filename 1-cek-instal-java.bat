@echo off
title Cek dan Instal Kebutuhan - Klinik Pratama
cd /d "%~dp0"

echo ==================================================
echo   1. CEK JAVA (JDK 17 atau lebih baru)
echo ==================================================
call "%~dp0tools\cari-java.bat"
if errorlevel 1 goto instaljava
echo [OK] JDK ditemukan di: "%JAVAC%"
"%JAVAC%" -version
goto cekgit

:instaljava
echo [BELUM ADA] JDK 17+ belum terpasang di laptop ini.
echo.
where winget >nul 2>nul
if errorlevel 1 goto javamanual
choice /c YN /m "Instal Eclipse Temurin JDK 21 sekarang (butuh internet)"
if errorlevel 2 goto javamanual
winget install --id EclipseAdoptium.Temurin.21.JDK -e --accept-source-agreements --accept-package-agreements
echo.
echo Instalasi JDK selesai. TUTUP jendela ini, lalu jalankan file ini SEKALI LAGI untuk memastikan.
goto cekgit

:javamanual
echo Silakan unduh JDK 21 dari: https://adoptium.net/temurin/releases/?version=21
echo Pilih Windows x64 - JDK - file .msi, lalu centang "Set JAVA_HOME" saat instalasi.

:cekgit
echo.
echo ==================================================
echo   2. CEK GIT (untuk upload ke GitHub)
echo ==================================================
where git >nul 2>nul
if errorlevel 1 goto instalgit
echo [OK] Git ditemukan:
git --version
goto cekvscode

:instalgit
echo [BELUM ADA] Git belum terpasang.
where winget >nul 2>nul
if errorlevel 1 goto gitmanual
choice /c YN /m "Instal Git sekarang (butuh internet)"
if errorlevel 2 goto gitmanual
winget install --id Git.Git -e --accept-source-agreements --accept-package-agreements
echo Setelah selesai, tutup lalu buka ulang VS Code agar Git terbaca.
goto cekvscode

:gitmanual
echo Unduh Git dari: https://git-scm.com/download/win

:cekvscode
echo.
echo ==================================================
echo   3. EKSTENSI VS CODE: "Extension Pack for Java"
echo ==================================================
where code >nul 2>nul
if errorlevel 1 goto vscodemanual
choice /c YN /m "Pasang Extension Pack for Java di VS Code sekarang"
if errorlevel 2 goto selesai
call code --install-extension vscjava.vscode-java-pack
goto selesai

:vscodemanual
echo Buka VS Code - menu Extensions (Ctrl+Shift+X) - cari "Extension Pack for Java" - Install.

:selesai
echo.
echo Selesai. Berikutnya jalankan: 2-jalankan-aplikasi.bat
echo.
pause
