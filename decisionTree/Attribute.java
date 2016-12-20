import java.util.ArrayList;
import java.util.List;

public class Attribute<T> {
  public String attributeName;
  public List<T> values;

  public Attribute() {
    this.attributeName = "";
    values = new ArrayList<T>();
  }

  public Attribute(String attributeName) {
    this.attributeName = attributeName;
    values = new ArrayList<T>();
  }

  public Attribute(String attributeName, List<T> values) {
    this.attributeName = attributeName;
    this.values = values;
  }

  public boolean allEqual() {
    boolean allEqual = true;
    for (int i = 0; i < values.size() && allEqual; i++) {
      allEqual = allEqual && values.get(0).equals(values.get(i));
    }
    return allEqual;
  }

  public List<T> getDistinctValues() {
    List<T> distinctValues = new ArrayList<T>();
    for (T val : values) {
      if (!distinctValues.contains(val)) {
        distinctValues.add(val);
      }
    }
    return distinctValues;
  }

  public List<T> getValues() {
    return this.values;
  }

  public String getAttributeName() {
    return this.attributeName;
  }

  public T get(int i) {
    return values.get(i);
  }

  public int size() {
    return values.size();
  }

  public void add(T item) {
    values.add(item);
  }

  public void print() {
    for (T item : values) {
      System.out.print(item.toString() + " ");
    }
    System.out.println();
  }
}
