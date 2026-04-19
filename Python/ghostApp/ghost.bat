@echo off
cd /d "%~dp0"

echo Installing PyInstaller...
py -m pip install pyinstaller --quiet
if %errorlevel% neq 0 (
    echo Failed to install PyInstaller. Make sure Python is installed.
    pause
    exit /b 1
)

echo.
echo Building ghost_pet.exe ...
py -m PyInstaller --noconsole --onefile --add-data "ghost.gif;." --name "ghost_pet" ghost_pet.py

echo.
if exist "dist\ghost_pet.exe" (
    echo Build successful!
    echo Your exe is ready at: dist\ghost_pet.exe
    echo Copy it together with the GIF to wherever you want.
) else (
    echo Build failed. Check the errors above.
)

pause