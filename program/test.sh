#!/bin/bash

javac *.java

echo
echo "Sample Set"

java TrainClassifier ../automark/sampletrain
time java filter ../automark/sampletest/1.txt
time java filter ../automark/sampletest/2.txt

echo
echo "3cool Set"
java TrainClassifier ../automark/train3cool
time java filter ../automark/test3cool/1.txt
time java filter ../automark/test3cool/2.txt

echo
echo "Part 2 Set"
java TrainClassifier ../train
time java filter ../automark2/sampletest/1.txt
for i in {1..10}; do
  time java filter "../train/ham$i.txt"
  time java filter "../train/spam$i.txt"
done

