/*
 * ******************************************************************************
 * MontiCore Language Workbench
 * Copyright (c) 2015, MontiCore, All rights reserved.
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
 * ******************************************************************************
 */

package de.monticore.symboltable.resolving;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import de.monticore.symboltable.SymbolKind;
import de.monticore.symboltable.Symbol;

/**
 * TODO: Write me!
 *
 * @author Pedram Mir Seyed Nazari
 *
 */
// TODO PN Problem, wenn unterschiedliche Symbole selben Kind nutzen. Welcher Adapter wird dann genutzt?
//         Statt Kinds eher Klassen nutzen? D.h. From extends Symbol (gleiches für To)
public abstract class CommonAdaptedResolvingFilter<S extends Symbol>
    extends CommonResolvingFilter<S> implements AdaptedResolvingFilter<S> {

  private final SymbolKind sourceKind;
  
  /**
   * @param targetSymbolClass
   * @param targetKind
   */
  public CommonAdaptedResolvingFilter(SymbolKind sourceKind, Class<S> targetSymbolClass, SymbolKind targetKind) {
    super(targetSymbolClass, targetKind);
    this.sourceKind = sourceKind;
  }

  public SymbolKind getSourceKind() {
    return sourceKind;
  }

  protected abstract S createAdapter(Symbol s);

  @Override
  public Optional<S> filter(ResolvingInfo resolvingInfo, String symbolName, List<Symbol> symbols) {
    final List<S> resolvedSymbols = new ArrayList<>();

    final Collection<ResolvingFilter<? extends Symbol>> filtersWithoutAdapters =
        ResolvingFilter.getFiltersForTargetKind(resolvingInfo.getResolvingFilters(), getSourceKind())
            .stream()
            .filter(resolvingFilter -> !(resolvingFilter instanceof AdaptedResolvingFilter))
            .collect(Collectors.toSet());

    for (ResolvingFilter<? extends Symbol> resolvingFilter : filtersWithoutAdapters) {

      Optional<? extends Symbol> optSymbol = resolvingFilter.filter(resolvingInfo, symbolName, symbols);

      // TODO PN Remove this whole if-statement, if adaptors should be created eager.
      if (optSymbol.isPresent()) {
        resolvedSymbols.add(createAdapter(optSymbol.get()));
      }
    }

    return ResolvingFilter.getResolvedOrThrowException(resolvedSymbols);
  }

  @Override
  public Collection<S> filter(ResolvingInfo resolvingInfo, List<Symbol> symbols) {
    // TODO PN override implementation
    return super.filter(resolvingInfo, symbols);
  }

  public static Collection<CommonAdaptedResolvingFilter<? extends Symbol>> getFiltersForSourceKind
      (Collection<ResolvingFilter<? extends Symbol>> resolvingFilters, SymbolKind sourceKind) {

    return resolvingFilters.stream()
      .filter(resolvingFilter -> (resolvingFilter instanceof CommonAdaptedResolvingFilter)
          && ((CommonAdaptedResolvingFilter) resolvingFilter).getSourceKind().isKindOf(sourceKind))
      .map(resolvingFilter -> (CommonAdaptedResolvingFilter<? extends Symbol>) resolvingFilter)
      .collect(Collectors.toCollection(LinkedHashSet::new));
  }

  @Override
  public String toString() {
    return CommonAdaptedResolvingFilter.class.getSimpleName() + " [" + sourceKind.getName() + " -> " +
        getTargetKind().getName() + "]";
  }
}
