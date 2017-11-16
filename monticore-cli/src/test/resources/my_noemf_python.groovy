/*
 * ******************************************************************************
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
 * ******************************************************************************
 */

package de.monticore

import de.monticore.codegen.parser.Languages

// M1: configuration object "_configuration" prepared externally
Log.debug("--------------------------------", LOG_ID)
Log.debug("MontiCore", LOG_ID)
Log.debug(" - eating your models since 2005", LOG_ID)
Log.debug("--------------------------------", LOG_ID)
Log.debug("Grammar argument    : " + _configuration.getGrammarsAsStrings(), LOG_ID)
Log.debug("Grammar files       : " + grammars, LOG_ID)
Log.debug("Modelpath           : " + modelPath, LOG_ID)
Log.debug("Output dir          : " + out, LOG_ID)
Log.debug("Handcoded argument  : " + _configuration.getHandcodedPathAsStrings(), LOG_ID)
Log.debug("Handcoded files     : " + handcodedPath, LOG_ID)

// ############################################################
// M0 Incremental Generation
IncrementalChecker.initialize(out)
InputOutputFilesReporter.resetModelToArtifactMap()
globalScope = createGlobalScope(modelPath)

// M1: basic setup and initialization; enabling of reporting
Reporting.init(out.getAbsolutePath(), reportManagerFactory)
// ############################################################

// ############################################################
// the first pass processes all input grammars up to transformation to CD and storage of the resulting CD to disk
while (grammarIterator.hasNext()) {
    input = grammarIterator.next()
    if (force || !IncrementalChecker.isUpToDate(input, out, modelPath, templatePath, handcodedPath )) {
        IncrementalChecker.cleanUp(input)

        // M2: parse grammar
        astGrammar = parseGrammar(input)

        if (astGrammar.isPresent()) {
            astGrammar = astGrammar.get()

            // start reporting
            grammarName = Names.getQualifiedName(astGrammar.getPackage(), astGrammar.getName())
            Reporting.on(grammarName)
            Reporting.reportParseInputFile(input, grammarName)

            // M3: populate symbol table
            astGrammar = createSymbolsFromAST(globalScope, astGrammar)

            // M4: execute context conditions
            runGrammarCoCos(astGrammar, globalScope)

            // M7: transform grammar AST into Class Diagram AST
            astClassDiagram = transformAstGrammarToAstCd(glex, astGrammar, globalScope, handcodedPath)

            astClassDiagramWithST = createSymbolsFromAST(globalScope, astClassDiagram)

            // write Class Diagram AST to the CD-file (*.cd)
            storeInCdFile(astClassDiagramWithST, out)

            // M5 + M6: generate parser and wrapper
            generateParser(glex, astGrammar, globalScope, handcodedPath, out,true,Languages.PYTHON_2)

            // store result of the first pass
            storeCDForGrammar(astGrammar, astClassDiagramWithST)
        }
    }
}
// end of first pass
// ############################################################

// ############################################################
// the second pass
// do the rest which requires already created CDs of possibly
// local super grammars etc.
for (astGrammar in getParsedGrammars()) {
    // make sure to use the right report manager again
    Reporting.on(Names.getQualifiedName(astGrammar.getPackage(), astGrammar.getName()))
    reportGrammarCd(astGrammar, globalScope, out)

    astClassDiagram = getCDOfParsedGrammar(astGrammar)

    // M8: decorate Class Diagram AST
    decoratePythonCd(glex, astClassDiagram, globalScope, handcodedPath)

    // M?: generate symbol table
    // This is currently not available for Python as target.

    // M9: generate AST classes, grammar has to be provided to find the first rule
    generatePython(glex, globalScope, astClassDiagram, out, templatePath, astGrammar)

    Log.info("Grammar " + astGrammar.getName() + " processed successfully!", LOG_ID)

    // M10: flush reporting
    Reporting.flush(astGrammar)
}