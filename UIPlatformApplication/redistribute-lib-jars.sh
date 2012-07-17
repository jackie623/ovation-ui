if [ -d `dirname $0`/build/cluster/modules/ext ]; then
    cp `dirname $0`/lib/ovation.jar `dirname $0`/build/cluster/modules/ext
fi

cp `dirname $0`/lib/ovation.jar `dirname $0`/Browser/release/modules/ext
cp `dirname $0`/lib/ovation.jar `dirname $0`/DBConnectionProvider/release/modules/ext
cp `dirname $0`/lib/ovation.jar `dirname $0`/OvationAPI/release/modules/ext

cp `dirname $0`/lib/test-manager.jar `dirname $0`/TestManager/release/modules/ext

cp `dirname $0`/lib/interfaces.jar `dirname $0`/GlobalInterfaceLibrary/release/modules/ext/