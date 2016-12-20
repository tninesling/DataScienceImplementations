import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TreeNode<T> {
  public static HashMap<String,Integer> attributeNameHash;
  String nodePrediction;
  String attributeName;
  T attributeValue;
  List<TreeNode<T>> children;

  public TreeNode() {
    this.nodePrediction = "";
    this.attributeName = "";
    this.attributeValue = null;
    this.children = new ArrayList<TreeNode<T>>();
  }

  public TreeNode(String attributeName, T attributeValue) {
    this.nodePrediction = "";
    this.attributeName = attributeName;
    this.attributeValue = attributeValue;
    this.children = new ArrayList<TreeNode<T>>();
  }

  public TreeNode buildTree(DataTable<T> data) {
    // Each node will have a prediction value which is the majority vote
    // of all values of the target attribute
    // The prediction will only be used at leaf nodes
    this.nodePrediction = data.targetMajority().toString();

    // If the data table is empty or all target values are the same, we have a leaf node
    // Otherwise, we filter the table and add the children to this node
    if (!data.attributesEmpty() && !data.allTargetsSame()) {
      Attribute<T> nextAttr = data.maxInfoGainAttribute();
      List<T> attrValues = nextAttr.getDistinctValues();

      for (T value : attrValues) {
        String attributeName = nextAttr.getAttributeName();
        // Create a new node for each value of the attribute
        TreeNode<T> newNode = new TreeNode(attributeName, value);
        // Filter the table on this child's attribute
        DataTable<T> filteredData = data.filterAttributes(data.indexOfAttribute(attributeName), value);

        children.add(newNode);
        // Continue building this branch of the tree with the filtered table
        newNode.buildTree(filteredData);
      }
    }
    return this;
  }

  public String predict(List<T> attrList) {
    String prediction = nodePrediction;

    // If the attribute list is empty or there are no children, we are at a leaf node
    // Otherwise, look for a matching child node
    if (!attrList.isEmpty() && !children.isEmpty()) {
      for (TreeNode<T> node : children) {
        // Get the index of this node's attribute from the attribute name hash
        int attributeNumber = attributeNameHash.get(node.attributeName);

        // If the child's value matches the value in the attribute list,
        // set the prediction to the prediction of that child
        if (attrList.get(attributeNumber).equals(node.attributeValue)) {
          prediction = node.predict(attrList);
        }
      }
    }

    return prediction;
  }

  public void preorder() {
    System.out.print(attributeName + ":" + attributeValue.toString() + " " + nodePrediction + " ");
    for (TreeNode<T> child : children) {
      child.preorder();
    }
  }

  public void print() {
    print("", true);
  }

  private void print(String prefix, boolean isTail) {
    // The default label will be root if the attribute value is null
    String nodeLabel = "root";

    // We want non-root nodes to display the name of the attribute and the
    // value they have for that attribute
    if (attributeValue != null) {
      nodeLabel = attributeName + ":" + attributeValue.toString();
    }

    // Leaf nodes will also display the target prediction at that node
    if (children.isEmpty()) {
      nodeLabel += "─" + nodePrediction;
    }
    // Print this node with only 1 fork if it is the last child
    System.out.println(prefix + (isTail ? "└── " : "├── ") + nodeLabel);

    // Call print for each child
    for (int i = 0; i < children.size() - 1; i++) {
      children.get(i).print(prefix + (isTail ? "    " : "│   "), false);
    }
    // For the last child, call print with true to designate as last child
    if (children.size() > 0) {
      children.get(children.size() - 1)
              .print(prefix + (isTail ?"    " : "│   "), true);
    }
  }
}
