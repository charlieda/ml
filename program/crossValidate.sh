#!/bin/bash

# partition
# for i in {0..9}; do
#   mkdir "../train/$i"
#   for f in ../train/*$i.txt
#   do
#     mv "$f" "../train/$i/"
#   done
# done

javac *.java


# for each of the 10 possible test sets
for i in {0..9};
do
  # train on all other sets
  dir=""
  for j in {0..9};
  do
    if [ "$i" -ne "$j" ] ; then
      dir="$dir ../train/$j"
    fi
  done
  java TrainClassifier $dir
  # test on this set
  CORRECT=0
  TOTAL=0
  for f in ../train/$i/spam*.txt
  do
    TOTAL=$((TOTAL+1))
    if [ "$(java filter $f)" = "spam" ] ; then
      CORRECT=$((CORRECT+1))
    fi
  done
  for f in ../train/$i/ham*.txt
  do
    TOTAL=$((TOTAL+1))
    if [ "$(java filter $f)" = "ham" ] ; then
      CORRECT=$((CORRECT+1))
    fi
  done
  # output number of correctly classified instances / total number of instances
  echo "Fold $i          $CORRECT out of $TOTAL"
done