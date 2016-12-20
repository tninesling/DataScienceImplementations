import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class KLPartition {
  List<String> points;
  Integer[][] adjacencyMatrix;
  ArrayList<String> partitionA, partitionB, aPrimeList, bPrimeList;
  ArrayList<Integer> gainList;
  Integer[] costs;

  public static void main(String[] args) {
    Integer[][] testMatrix = new Integer[6][6];
    List<String> points = Arrays.asList("a", "b", "c", "d", "e", "f");
    testMatrix[0] = new Integer[] {0, 1, 2, 3, 2, 4};
    testMatrix[1] = new Integer[] {1, 0, 1, 4, 2, 1};
    testMatrix[2] = new Integer[] {2, 1, 0, 3, 2, 1};
    testMatrix[3] = new Integer[] {3, 4, 3, 0, 4, 3};
    testMatrix[4] = new Integer[] {2, 2, 2, 4, 0, 2};
    testMatrix[5] = new Integer[] {4, 1, 1, 3, 2, 0};

    KLPartition klp = new KLPartition(points, testMatrix);
    klp.partition();
    klp.printPartitions();
  }

  public KLPartition(List<String> points, Integer[][] adjacencyMatrix) {
    this.points = points;
    this.adjacencyMatrix = adjacencyMatrix;
    this.partitionA = new ArrayList<String>();
    this.partitionB = new ArrayList<String>();
    this.aPrimeList = new ArrayList<String>();
    this.bPrimeList = new ArrayList<String>();
    this.gainList = new ArrayList<Integer>();
    this.costs = new Integer[adjacencyMatrix.length];
  }

  /**
   * Partitions a graph into two even partitions using the Kernighan-Lin
   * partitioning algorithm. The partitions are stored in the partitionA and
   * partitionB variables of the calling KLPartition object.
   */
  public void partition() {
    int totalGain = 1;
    int k = 0;
    do {
      initializePartitions();
      calculateCosts();

      for (int i = 0; i < adjacencyMatrix.length/2; i++) {
        setAsideMaxGainPoints();
        updateCosts();
      }

      k = numberToMaximizeTotalGain();
      totalGain = gainSumForFirstKValues(k);

      if (totalGain > 0) {
        swapFirstKValues(k);
      } else {
        swapFirstKValues(0);
      }

    } while (totalGain > 0);
  }

  /**
   * Initializes the values of the partitions. If partition A and partition B
   * are not empty, then this is not the first iteration, so we keep the
   * existing partitions as our starting partitions. The other lists and the
   * cost array are wiped clean at the beginning of each iteration.
   */
  public void initializePartitions() {
    if (partitionA.isEmpty() || partitionB.isEmpty()) {
      // Add the first n/2 points to partition A
      for (int i = 0; i < adjacencyMatrix.length/2; i++) {
        partitionA.add(points.get(i));
      }
      // Add the second n/2 points to partition B
      for (int i = adjacencyMatrix.length/2; i < adjacencyMatrix.length; i++) {
        partitionB.add(points.get(i));
      }
    }

    aPrimeList = new ArrayList<String>();
    bPrimeList = new ArrayList<String>();
    gainList = new ArrayList<Integer>();
    costs = new Integer[adjacencyMatrix.length];
  }

  /**
   * Calculates the costs for each point in the graph. For a point s in the
   * set, the cost of s is the difference of the external cost of s and the
   * internal cost of s. The external cost is the sum of the costs of s in the
   * other partition, and the internal cost is the sum of the costs of s within
   * its own partition.
   *
   * The costs are stored in the costs array of the calling KLPartition object.
   */
  public void calculateCosts() {
    for (String aVal : partitionA) {
      int externalCost = cost(aVal, partitionB);
      int internalCost = cost(aVal, partitionA);

      costs[points.indexOf(aVal)] = externalCost - internalCost;
    }

    for (String bVal : partitionB) {
      int externalCost = cost(bVal, partitionA);
      int internalCost = cost(bVal, partitionB);

      costs[points.indexOf(bVal)] = externalCost - internalCost;
    }
  }

  public int cost(String point, ArrayList<String> partition) {
    int cost = 0;
    int pointIndex = points.indexOf(point);

    for (String partitionVal : partition) {
      int partitionValIndex = points.indexOf(partitionVal);

      cost += adjacencyMatrix[pointIndex][partitionValIndex];
    }

    return cost;
  }

  /**
   * Finds the pair of a from partition A and b from partition B which
   * maximizes the gain calculation. For such a pair, a is moved from partition A
   * to aPrimeList and b is likewise moved from paritition B to bPrimeList so
   * they will not be considered again in later iterations. The gain value
   * for the pair is appended to gainList.
   */
  public void setAsideMaxGainPoints() {
    String aPrime = partitionA.get(0);
    String bPrime = partitionB.get(0);
    Integer maxGain = calculateGain(partitionA.get(0), partitionB.get(0));

    for (String aVal : partitionA) {
      for (String bVal : partitionB) {
        int gain = calculateGain(aVal, bVal);

        if (gain > maxGain) {
          maxGain = gain;
          aPrime = aVal;
          bPrime = bVal;
        }
      }
    }

    gainList.add(maxGain);

    aPrimeList.add(aPrime);
    partitionA.remove(aPrime);

    bPrimeList.add(bPrime);
    partitionB.remove(bPrime);
  }

  public int calculateGain(String pointA, String pointB) {
    int a = points.indexOf(pointA);
    int b = points.indexOf(pointB);

    return costs[a] + costs[b] - 2*adjacencyMatrix[a][b];
  }

  /**
   * Updates the costs array for the remaining values in partitions A and B
   * using the most recently added values to aPrimeList and bPrimeList
   */
  public void updateCosts() {
    // Get the most recent values of aPrime and bPrime
    String aPrime = aPrimeList.get(aPrimeList.size() - 1);
    String bPrime = bPrimeList.get(bPrimeList.size() - 1);

    int aPrimeIndex = points.indexOf(aPrime);
    int bPrimeIndex = points.indexOf(bPrime);

    for (String aVal : partitionA) {
      int aValIndex = points.indexOf(aVal);
      costs[aValIndex] += 2*adjacencyMatrix[aValIndex][aPrimeIndex] -
                          2*adjacencyMatrix[aValIndex][bPrimeIndex];
    }

    for (String bVal : partitionB) {
      int bValIndex = points.indexOf(bVal);
      costs[bValIndex] += 2*adjacencyMatrix[bValIndex][bPrimeIndex] -
                          2*adjacencyMatrix[bValIndex][aPrimeIndex];
    }
  }

  /**
   * Returns the number k such that the sum of the first k gain values in
   * gainList is maximized
   */
  public int numberToMaximizeTotalGain() {
    int k = 0;
    int maxTotalGain = gainList.get(0);
    int currentGainSum = 0;

    for (int i = 0; i < gainList.size(); i++) {
      currentGainSum += gainList.get(i);
      if (currentGainSum > maxTotalGain) {
        k = i;
      }
    }

    return k + 1;
  }

  /**
   * Swaps the first k values between partitions A and B, namely the first k
   * items in aPrimeList are sent to partition B and the first k items in
   * bPrimeList are sent to partition A. The remaining items are returned to
   * their original partition.
   */
  public void swapFirstKValues(int k) {
    for (int i = 0; i < k; i++) {
      partitionA.add(bPrimeList.remove(0));
      partitionB.add(aPrimeList.remove(0));
    }

    partitionA.addAll(aPrimeList);
    aPrimeList.clear();

    partitionB.addAll(bPrimeList);
    bPrimeList.clear();
  }

  public int gainSumForFirstKValues(int k) {
    int gainSum = 0;
    for (int i = 0; i < k; i++) {
      gainSum += gainList.get(i);
    }
    return gainSum;
  }

  public void printPartitions() {
    System.out.print("Partition A: ");
    for (String aVal : partitionA) {
      System.out.print(aVal + " ");
    }
    System.out.print("\nPartition B: ");
    for (String bVal : partitionB) {
      System.out.print(bVal + " ");
    }
    System.out.println();
  }
}
