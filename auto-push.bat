@echo off
setlocal EnableExtensions EnableDelayedExpansion

title Kaizoku-o-craft - Auto GitHub Push

set "PROJECT=C:\MCreatorWorkspaces\kaizoku_o_craft"
set "BRANCH=main"
set "INTERVAL=10"

echo ========================================
echo   Auto GitHub Push
echo ========================================
echo Klasor: %PROJECT%
echo Kontrol: %INTERVAL% saniye
echo ========================================
echo.

cd /d "%PROJECT%"

if errorlevel 1 (
    echo [HATA] Proje klasorune girilemedi.
    pause
    exit /b 1
)

:LOOP

rem -------------------------------------------------
rem Remote bilgilerini guncelle
rem -------------------------------------------------
git fetch origin %BRANCH% >nul 2>&1

rem -------------------------------------------------
rem Degisiklik var mi?
rem -------------------------------------------------
git status --porcelain > "%TEMP%\kaizoku_git_status.txt"

set "CHANGED=0"

for /f "delims=" %%A in ("%TEMP%\kaizoku_git_status.txt") do (
    set "CHANGED=1"
)

if "!CHANGED!"=="1" (

    echo.
    echo [%time:~0,8%] Degisiklik algilandi.

    rem -------------------------------------------------
    rem Minecraft calisma dosyalarini Git'ten hariç tut
    rem -------------------------------------------------
    git add .gitignore >nul 2>&1

    rem -------------------------------------------------
    rem Geri kalan degisiklikleri ekle
    rem -------------------------------------------------
    git add . >nul 2>&1

    rem -------------------------------------------------
    rem Commit
    rem -------------------------------------------------
    git diff --cached --quiet

    if errorlevel 1 (

        git commit -m "Auto update %date% %time%"

        if errorlevel 1 (
            echo [HATA] Commit basarisiz.
            goto WAIT
        )

    ) else (

        echo Commit edilecek degisiklik yok.
        goto WAIT
    )

    rem -------------------------------------------------
    rem Remote bizden ilerideyse:
    rem Local surumu remote'un yerine koy.
    rem
    rem Bu projede bunu istiyoruz cunku
    rem local workspace = gercek kaynak.
    rem -------------------------------------------------

    git push --force-with-lease origin %BRANCH%

    if errorlevel 1 (

        echo.
        echo [HATA] GitHub push basarisiz.
        echo.
        echo Remote ile local arasinda fark var.
        echo Force-with-lease reddedildiyse GitHub degismis olabilir.
        echo.
        
    ) else (

        echo.
        echo [OK] GitHub push basarili.
        echo.
    )
)

:WAIT

del "%TEMP%\kaizoku_git_status.txt" >nul 2>&1

timeout /t %INTERVAL% /nobreak >nul

goto LOOP