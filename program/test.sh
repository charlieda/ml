#!/bin/bash

javac *.java

echo
echo "Sample Set"

java TrainClassifier ../automark/sampletrain 
java filter ../automark/sampletest/1.txt
java filter ../automark/sampletest/2.txt

echo
echo "3cool Set"
java TrainClassifier ../automark/train3cool
java filter ../automark/test3cool/1.txt
java filter ../automark/test3cool/2.txt

echo
echo "Part 2 Set"
java TrainClassifier ../train
time java filter ../automark2/sampletest/1.txt

TOTAL=20
SPAM=0
HAM=0
for i in {1..20}; do
  echo $i
  if [ "$(java filter "../train/ham$i.txt")" = "ham" ] ; then
    HAM=$((HAM+1))
  fi
  if [ $( java filter "../train/spam$i.txt" ) = "spam" ] ; then
    SPAM=$((SPAM+1))
  fi
done

echo "Spam: $SPAM / $TOTAL correct"
echo " Ham: $HAM / $TOTAL correct"

