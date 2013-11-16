#!/bin/bash

commands=( "java filter ../automark/sampletrain ../automark/sampletest/1.txt" "java filter ../automark/sampletrain ../automark/sampletest/2.txt" "java filter ../automark/train3cool ../automark/test3cool/1.txt" "java filter ../automark/train3cool ../automark/test3cool/2.txt")
outputs=( "spam" "ham" "ham" "ham")

javac *.java

for i in {0..3}
do
	if [ $(${commands[$i]}) = ${outputs[$i]} ] ; then
		echo "PASS"
	else
		echo "FAIL"
	fi
done

