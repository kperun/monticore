package de.monticore.emf._ast;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.eclipse.emf.common.util.Enumerator;

public enum ConstantsASTENode implements Enumerator {
  // Literal Object DEFAULT
  DEFAULT(0, "DEFAULT", "DEFAULT");
  
  public static final int DEFAULT_VALUE = 0;
  
  private static final ConstantsASTENode[] VALUES_ARRAY =
      new ConstantsASTENode[] {
          DEFAULT,
      };
  
  public static final List<ConstantsASTENode> VALUES = Collections.unmodifiableList(Arrays
      .asList(VALUES_ARRAY));
  
  // Returns the literal with the specified literal value.
  public static ConstantsASTENode get(String literal) {
    for (int i = 0; i < VALUES_ARRAY.length; ++i) {
      ConstantsASTENode result = VALUES_ARRAY[i];
      if (result.toString().equals(literal)) {
        return result;
      }
    }
    return null;
  }
  
  // Returns the literal with the specified name.
  public static ConstantsASTENode getByName(String name) {
    for (int i = 0; i < VALUES_ARRAY.length; ++i) {
      ConstantsASTENode result = VALUES_ARRAY[i];
      if (result.getName().equals(name)) {
        return result;
      }
    }
    return null;
  }
  
  // Returns the literal with the specified integer value.
  public static ConstantsASTENode get(int value) {
    switch (value) {
      case DEFAULT_VALUE:
        return DEFAULT;
        
    }
    return null;
  }
  
  private final int value;
  
  private final String name;
  
  private final String literal;
  
  private ConstantsASTENode(int value, String name, String literal) {
    this.value = value;
    this.name = name;
    this.literal = literal;
  }
  
  public int getValue() {
    return value;
  }
  
  public String getName() {
    return name;
  }
  
  public String getLiteral() {
    return literal;
  }
  
  @Override
  public String toString() {
    return literal;
  }
  
}
