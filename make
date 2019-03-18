#!/bin/bash

PRG=libpdfdm

LIB_PDF=/dm/dmJava/lib/libpdfdm/lib/itextpdf-5.3.4.jar
LIB_XML=/dm/dmJava/lib/libpdfdm/lib/xmlworker-1.2.1.jar
LIB_DM=/dm/dmJava/lib/libjdm/pack/libjdm.jar
LIBS=$LIB_PDF:$LIB_XML

# -----------------------------------------------------------------------------

pack () {
  rm -fR classes
  mkdir classes
  javac -cp $LIBS -d classes src/es/dm/*.java
  if [ -e pack/tmp ]
  then
    rm -fR pack/tmp
  fi
  mkdir pack/tmp
  cd pack/tmp
  jar xvf $LIB_PDF
  jar xvf $LIB_XML
  cp -fR ../../classes/* ./
  rm -fR META-INF
  mkdir META-INF
  echo 'Manifest-Version: 1.0' > META-INF/MANIFEST.MF
  echo 'Created-By: ºDeme' >> META-INF/MANIFEST.MF
#  echo 'Main-Class: Main' >> META-INF/MANIFEST.MF
  echo ''>> META-INF/MANIFEST.MF
  jar cvfm ../$PRG.jar META-INF/MANIFEST.MF *
  cd ../..
  rm -fR pack/tmp
}

case $1 in
c*)
  javac -cp $LIBS -d classes src/es/dm/*.java
  javac -cp $LIBS:$LIB_DM:classes -d tests/classes tests/src/*.java
  ;;
x*)
  javac -cp $LIBS -d classes src/es/dm/*.java
  javac -cp $LIBS:$LIB_DM:classes -d tests/classes tests/src/*.java
  cp tests/src/asterisk.png tests/classes/asterisk.png
  java -cp $LIBS:$LIB_DM:classes:tests/classes Main
  ;;
pack*)
  pack
  ;;
run*)
  pack
  cp tests/src/asterisk.png tests/classes/asterisk.png
  java -cp pack/$PRG.jar:$LIB_DM:tests/classes Main
  ;;
doc*)
  javadoc -quiet -cp $LIBS -d api -sourcepath src es.dm
  ;;
*)
  echo $1: Unknown option
  ;;
esac


