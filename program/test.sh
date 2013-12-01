#!/bin/bash

javac *.java

java TrainClassifier ../automark/sampletrain
java filter ../automark/sampletest/1.txt
java filter ../automark/sampletest/2.txt

java TrainClassifier ../automark/train3cool
java filter ../automark/test3cool/1.txt
java filter ../automark/test3cool/2.txt

java TrainClassifier ../train
time java filter ../automark2/sampletest/1.txt
