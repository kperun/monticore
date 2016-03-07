/*
 * ******************************************************************************
 * MontiCore Language Workbench
 * Copyright (c) 2016, MontiCore, All rights reserved.
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

package de.monticore.codegen.symboltable;

import static de.monticore.codegen.GeneratorHelper.getSimpleTypeNameToGenerate;
import static de.se_rwth.commons.Names.getSimpleName;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;

import de.monticore.generating.GeneratorEngine;
import de.monticore.io.paths.IterablePath;
import de.monticore.languages.grammar.MCGrammarSymbol;
import de.se_rwth.commons.Names;

/**
 * @author Pedram Mir Seyed Nazari
 */
public class CommonModelingLanguageGenerator implements ModelingLanguageGenerator {

  private final SymbolTableGeneratorHelper genHelper;
  private final IterablePath handCodedPath;
  private final GeneratorEngine genEngine;

  public CommonModelingLanguageGenerator(GeneratorEngine genEngine, SymbolTableGeneratorHelper genHelper, IterablePath handCodedPath) {
    this.genEngine = genEngine;
    this.genHelper = genHelper;
    this.handCodedPath = handCodedPath;
  }

  @Override
  public void generate(MCGrammarSymbol grammarSymbol, Collection<String> ruleNames) {
    final String className = getSimpleTypeNameToGenerate(getSimpleName(grammarSymbol.getFullName() + "Language"),
        genHelper.getTargetPackage(), handCodedPath);

    final Path filePath = Paths.get(Names.getPathFromPackage(genHelper.getTargetPackage()), className + ".java");
    genEngine.generate("symboltable.ModelingLanguage", filePath, grammarSymbol.getAstNode().get(), className, ruleNames);
  }
}