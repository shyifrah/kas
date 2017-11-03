@setlocal enableextensions
@setlocal enabledelayedexpansion
@echo off

::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:: extract KAS_HOME
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
set "BATCH_DIR=%~dp0"
pushd %BATCH_DIR%..
set "SCRIPT_HOME=%CD%"
popd
pushd %SCRIPT_HOME%


::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:: determine which JAR should be excluded from CLASS_PATH and construct it
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
for %%I in (%SCRIPT_HOME%\kas-) do (
  set "CLEAN_DIR_NAME=%%I"
  pushd !CLEAN_DIR_NAME!
  ant Clean
  popd
)

popd
exit /b
