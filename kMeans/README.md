# k-Means Clustering Algorithm #

The KMeans.R script is an implementation of the k-Means clustering algorithm.
The script defines a kmeans() function which takes parameters of a value k
for the number of clusters and a matrix of data which is to be clustered.
This implementation of the algorithm can handle a data table of any size, but
the implementation currently runs correctly for k values between 2 and 5, inclusive.

The algorithm clusters the data based on all columns passed in the data matrix.
Once the clusters are computed, they are plotted as a scatter plot where each
cluster is assigned a different color and the centroid for each cluster is
plotted as a star.

As additional information, the algorithm outputs the values of the centroids
for each cluster and the cluster vector which is a column vector corresponding
to the cluster number for each row in the input matrix. This vector can be used
to compute the clustering accuracy if you have a classification column in the
original data set.

The script clusters the data from iris.csv which can be found in the UCI
Machine Learning Repository here: https://archive.ics.uci.edu/ml/datasets/Iris
