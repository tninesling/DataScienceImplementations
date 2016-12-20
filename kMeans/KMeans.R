# Implementation of k-means clustering method
# k should be less than 9 since there are 8 color options for plotting

kmeans <- function(k, table) {
  # Returns a table containing k centroids
  # The first centroid is chosen randomly from the table
  # The remaining k-1 are chosen to maximize the distance between them
  initialCentroids <- function(k, table) {
    # Generate a matrix where each entry M[i,j] is the Euclidean distance from point i to point j
    numberOfPoints <- dim(table)[1]
    distMat <- matrix(0, nrow = numberOfPoints, ncol = numberOfPoints)
    for (i in 1:numberOfPoints) {
      for (j in i:numberOfPoints) {
        distMat[i,j] <- dist(rbind(table[i,], table[j,]), method = "euclidean")
        distMat[j,i] <- distMat[i,j]
      }
    }

    # Randomly select a point from the data to be the first centroid
    firstCentroidIndex <- ceiling(runif(1, min = 0, max = length(x)))
    # Initialize the centroids matrix where each row will contain one centroid
    centroids <- matrix(0, ncol = dim(table)[2])
    centroids[1,] <- table[firstCentroidIndex,]
    distVec <- distMat[firstCentroidIndex,]

    # Select k-1 more centroids, maximizing the distance of each centroid to the previously selected ones
    for (i in 2:k) {
      centroids <- rbind(centroids, table[distVec == max(distVec)])

      # Update the distance vector to include the distances to this centroid
      centroidSelector <- (table[,1] == centroids[i,1])
      for (j in 2:dim(centroids)[2]) {
        centroidSelector <- centroidSelector & (table[,j] == centroids[i,j])
      }

      # Add the distance vector for the last centroid to current distVec so we maximize distance from all previous centroids (only 2 here)
      # The distance matrix doesn't need to be transposed because it is symmetric
      distVec <- distVec + distMat[centroidSelector]
    }

    centroids
  }

  # Returns a matrix where each entry M[i,j] is the distance from point i in table to centroid j
  distancesToCentroids <- function(table, centroids) {
    # Calculate the distance from every point in the table to each centroid
    distMat <- matrix(0, nrow = dim(table)[1], ncol = dim(centroids)[1])
    for (i in 1:dim(table)[1]) {
      for (j in 1:dim(centroids)[1]) {
        distMat[i,j] <- dist(rbind(table[i,], centroids[j,]), method = "euclidean")
      }
    }
    distMat
  }

  # Returns a vector of cluster labels for each point in the table
  # Each point is assigned to the cluster belonging to the nearest centroid
  assignClusters <- function(distMat) {
    # Create a parallel array containing the cluster label for each entry of the table
    clusters <- 0
    for (i in 1:dim(distMat)[1]) {
      # Get the logical vector which selects the nearest centroid
      nearestCentroid <- distMat[i,] == min(distMat[i,])
      # Convert the location in the logical vector to a numerical value
      # This value is the cluster label - where cluster i is around centroid i
      clusters[i] <- seq(from = 1, to = k, by = 1)[nearestCentroid]
    }
    clusters
  }

  # Returns a table of updated centroids
  # Each centroid is updated to be the mean value of its cluster
  updateCentroids <- function(k, table, clusters) {
    # Update centroids for each cluster
    updatedCentroids <- matrix(0, nrow = k, ncol = dim(table)[2])
    for (i in 1:k) {
      # Select all entries in the table assigned to cluster i
      clusterTable <- table[(clusters == i),]
      for (j in 1:dim(updatedCentroids)[2]) {
        updatedCentroids[i,j] <- mean(clusterTable[,j])
      }
    }
    updatedCentroids
  }


  centroids <- initialCentroids(k, table)
  repeat {
    oldCentroids <- centroids
    clusters <- assignClusters(distancesToCentroids(table, oldCentroids))
    centroids <- updateCentroids(k, table, clusters)

    if (Reduce((function(x1, x2) x1 && x2), centroids == oldCentroids)) {
      break
    }
  }

  plot(table, col = clusters, pch = 19)
  points(centroids, col = "black", pch = 8)

  print("Centroids:")
  print(centroids)
  print("Cluster vector:")
  print(clusters)

  clusters
}

iris <- read.csv("iris.csv", header=FALSE)
irisTable <- as.matrix(iris[1:4])
kmeans(3, irisTable)
