/*******************************************************************************
 * MontiCore Language Workbench, www.monticore.de
 * Copyright (c) 2017, MontiCore, All rights reserved.
 *  
 * This project is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this project. If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.se_rwth.langeditor.language;

import java.util.Optional;
import java.util.function.Function;

import org.eclipse.swt.graphics.Image;

import com.google.common.collect.ImmutableSet;

import de.monticore.ast.ASTNode;

public class OutlineElementSet {
  
  public static class Builder {
    
    private final ImmutableSet.Builder<OutlineElement<?>> builder = ImmutableSet.builder();
    
    public <T extends ASTNode> void add(
        Class<T> ruleType,
        Function<T, String> nameGetter,
        Optional<Image> image) {
      builder.add(new OutlineElement<>(ruleType, nameGetter, image));
    }
    
    public OutlineElementSet build() {
      return new OutlineElementSet(builder.build());
    }
  }
  
  private final ImmutableSet<OutlineElement<?>> outlineElements;
  
  private OutlineElementSet(ImmutableSet<OutlineElement<?>> outlineElements) {
    this.outlineElements = outlineElements;
  }
  
  public static OutlineElementSet empty() {
    return new OutlineElementSet(ImmutableSet.of());
  }
  
  public static Builder builder() {
    return new Builder();
  }
  
  public boolean contains(Class<?> ruleType) {
    return outlineElements.stream()
        .map(OutlineElement::getRuleType)
        .anyMatch(ruleType::equals);
  }
  
  public <T extends ASTNode> String getName(T rule) {
    return getOutlineElement(rule.getClass())
        .flatMap(outlineElement -> outlineElement.getName(rule))
        .orElse("");
  }
  
  public Optional<Image> getImage(Class<?> ruleType) {
    Optional<OutlineElement<?>> outlineElement = getOutlineElement(ruleType);
    return outlineElement.map(OutlineElement::getImage).orElse(Optional.empty());
  }
  
  private Optional<OutlineElement<?>> getOutlineElement(Class<?> ruleType) {
    return outlineElements.stream()
        .filter(outlineElement -> ruleType.equals(outlineElement.getRuleType()))
        .findFirst();
  }
}
