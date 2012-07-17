set basedir=%~dp0

cp %basedir%\lib\ovation.jar %basedir%\Browser\release\modules\ext
cp %basedir%\lib\ovation.jar %basedir%\build\cluster\modules\ext
cp %basedir%\lib\ovation.jar %basedir%\DBConnectionProvider\release\modules\ext
cp %basedir%\lib\ovation.jar %basedir%\OvationAPI\release\modules\ext

cp %basedir%\lib\test-manager.jar %basedir%\TestManager\release\modules\ext

cp %basedir%\lib\interfaces.jar %basedir%\GlobalInterfaceLibrary\release\modules\ext\