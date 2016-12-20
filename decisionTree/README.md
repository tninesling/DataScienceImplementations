# ID3 Decision Tree #

This folder contains an implementation of the base ID3 Decision Tree algorithm in Java.

To run the package, compile the main class with:
javac DecisionTree.java

Then you can run with:
java DecisionTree

The package currently trains the tree from the data/processed.cleveland.csv file
and makes predictions on the data/processed.switzerland.csv file.

The files containing the data to train the tree and to test are currently
hard-coded in the DecisionTree.java file. To change the files, call the readCsv
method on the location of the data set and the column number (0-indexed) of
the prediction column.

This code was written for an assignment in the CSC 273 Data Mining course at Hofstra University
