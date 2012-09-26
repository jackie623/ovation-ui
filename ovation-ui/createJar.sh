mkdir -p jarDir

for ARG in $*
do
    ABSPATH=$(cd "$(dirname "$ARG")"; pwd)/`basename "$ARG"`
    CP=$ABSPATH:$CP
    cd jarDir
    jar -xf $ABSPATH
    echo "Unpacking" $ABSPATH
    cd ..
done

javac -cp $CP UpdateJars/src/ovation/Update_3.java


touch manifest.txt
echo "Main-Class: ovation.Update_3\nSchema-Version: 1" > manifest.txt

cp UpdateJars/src/ovation/Update_3.class jarDir/ovation/
cd jarDir

jar -cfm Update_3.jar ../manifest.txt *

cp Update_3.jar ..
cd ..

rm -rf jarDir
#rm -f manifest.txt