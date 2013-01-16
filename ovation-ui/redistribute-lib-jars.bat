set basedir=%~dp0

if exist %basedir%\build\cluster\modules\ext ( copy /Y %basedir%\lib\ovation.jar %basedir%\build\cluster\modules\ext )

mkdir %basedir%\Browser\release\modules\ext
copy /Y %basedir%\lib\ovation.jar %basedir%\Browser\release\modules\ext

mkdir %basedir%\DBConnectionProvider\release\modules\ext
copy /Y %basedir%\lib\ovation.jar %basedir%\DBConnectionProvider\release\modules\ext

mkdir %basedir%\OvationAPI\release\modules\ext
copy /Y %basedir%\lib\ovation.jar %basedir%\OvationAPI\release\modules\ext

mkdir %basedir%\TestManager\release\modules\ext
copy /Y %basedir%\lib\test-manager.jar %basedir%\TestManager\release\modules\ext

mkdir %basedir%\GlobalInterfaceLibrary\release\modules\ext\
copy /Y %basedir%\lib\interfaces.jar %basedir%\GlobalInterfaceLibrary\release\modules\ext\