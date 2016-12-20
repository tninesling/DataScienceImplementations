import java.util.*;
import java.io.*;

public class DecisionTree<T> {
  TreeNode<T> root;

  public static void main(String[] args) {
    DecisionTree<String> dTree = new DecisionTree<String>();
    DataTable<String> dTable = dTree.readCsv("data/processed.cleveland.csv", 13);
    dTree.root = new TreeNode<String>();
    dTree.root.attributeNameHash = dTable.getAttributeNameHash();
    dTree.root = dTree.root.buildTree(dTable);

    int total = 0;
    int correct = 0;
    int wrong = 0;
    int falsePositives = 0;
    double accuracy = 0.0;
    double falsePositivePercent = 0.0;

    DataTable<String> resultTable = dTree.readCsv("data/processed.switzerland.csv", 13);
    List<Attribute<String>> testAttributes = resultTable.getAttributes();
    Attribute<String> targetAttribute = resultTable.getTargetAttribute();

    List<List<String>> lists = dTree.asInstanceList(testAttributes);
    for (List<String> list : lists) {
      String prediction = dTree.root.predict(list);
      System.out.print("Prediction: "+prediction+"\t Target Value: "+targetAttribute.get(lists.indexOf(list))+" ");
      if(prediction.equals(targetAttribute.get(lists.indexOf(list)))) {
        System.out.println("correct");
        correct++;
      } else {
        wrong++;
        if (prediction.equals("'present'")) {
          System.out.println("false positive");
          falsePositives++;
        } else {
          System.out.println("incorrect");
        }
      }
      total++;
    }
    accuracy = (double) correct / total;
    falsePositivePercent = (double) falsePositives / total;

    System.out.println("Total: " + total);
    System.out.println("Correct: " + correct);
    System.out.println("Wrong: " + wrong);
    System.out.println("False positives: " + falsePositives);
    System.out.println("Accuracy: " + (accuracy * 100));
    System.out.println("False positive %: " + (falsePositivePercent * 100));

    dTree.root.print();
  }

  // Converts format of a list of columns to a list of rows
  // Allows easy iteration through each individual instance during prediction phase
  public List<List<T>> asInstanceList(List<Attribute<T>> attrList) {
    List<List<T>> instanceList = new ArrayList<List<T>>();
    for (int i = 0; i < attrList.get(0).size(); i++) {
      // Create a new instance (row) and populate it with the corresponding
      // ith attribute value of each attribute column
      ArrayList<T> currentInstance = new ArrayList<T>();
      for (int j = 0; j < attrList.size(); j++) {
        currentInstance.add(attrList.get(j).get(i));
      }
      instanceList.add(currentInstance);
    }
    return instanceList;
  }

  public DataTable<String> readCsv(String fileName, int targetColNumber) {
    List<Attribute<String>> attributeList = new ArrayList<Attribute<String>>();

    Scanner sc = null;
    try {
      sc = new Scanner(new File(fileName));
    } catch (FileNotFoundException fnfe) {
      System.out.println("Cannot find file: " + fileName);
    }

    // Read first line and initialize attribute list with attribute names
    for (String attributeName : sc.nextLine().split(",")) {
      attributeList.add(new Attribute<String>(attributeName));
    }

    // Fill in attributes with remaining data
    int line = 0;
    while (sc.hasNext()) {
      String[] nextLine = sc.nextLine().split(",");
      for (int i = 0; i < attributeList.size(); i++) {
        Attribute<String> currentAttribute = attributeList.get(i);
        if (nextLine[i].equals("?")) {
          nextLine[i] = "";
        }
        currentAttribute.add(nextLine[i]);
      }
    }

    Attribute<String> targetAttribute = attributeList.remove(targetColNumber);

    return new DataTable<String>(attributeList, targetAttribute);
  }
}
