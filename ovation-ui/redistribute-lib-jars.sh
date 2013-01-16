#!/bin/bash

function force_copy {
	mkdir -p $(dirname $2)
    if [ -f $2 ] && [ $1 != $2 ]; then
	echo "Removing file"
	rm -f $2
    fi
    cp $1 $2
}

if [ -d `dirname $0`/build/cluster/modules/ext ]; then
    force_copy `dirname $0`/lib/ovation.jar `dirname $0`/build/cluster/modules/ext/ovation.jar
fi

force_copy `dirname $0`/lib/ovation.jar `dirname $0`/Browser/release/modules/ext/ovation.jar
force_copy `dirname $0`/lib/ovation.jar `dirname $0`/DBConnectionProvider/release/modules/ext/ovation.jar
force_copy `dirname $0`/lib/ovation.jar `dirname $0`/OvationAPI/release/modules/ext/ovation.jar

force_copy `dirname $0`/lib/test-manager.jar `dirname $0`/TestManager/release/modules/ext/test-manager.jar

force_copy `dirname $0`/lib/interfaces.jar `dirname $0`/GlobalInterfaceLibrary/release/modules/ext/interfaces.jar

