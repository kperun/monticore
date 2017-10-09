package de.monticore.codegen.cd2python.ast;

import de.monticore.codegen.GeneratorHelper;
import de.monticore.codegen.cd2java.ast.AstGeneratorHelper;
import de.monticore.codegen.cd2java.visitor.VisitorGeneratorHelper;
import de.monticore.codegen.cd2python.ast.AstPythonGeneratorHelper;
import de.monticore.codegen.cd2java.ast.AstGeneratorHelper;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.io.paths.IterablePath;
import de.monticore.symboltable.GlobalScope;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.se_rwth.commons.Names;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

public class ASTPythonGenerator {

    private static final String PYTHON_EXTENSION = ".py";

    /**
     * Generates ast python files for the given class diagram AST
     *
     * @param glex - object for managing hook points, features and global
     * variables
     * @param astClassDiagram - class diagram AST
     * @param outputDirectory - target directory
     */
    public static void generate(GlobalExtensionManagement glex, GlobalScope globalScope, ASTCDCompilationUnit astClassDiagram,
                                File outputDirectory, IterablePath templatePath, boolean emfCompatible) {
        final String diagramName = astClassDiagram.getCDDefinition().getName();
        final GeneratorSetup setup = new GeneratorSetup(outputDirectory);
        setup.setModelName(diagramName);
        setup.setAdditionalTemplatePaths(templatePath.getPaths().stream().map(Path::toFile).collect(Collectors.toList()));
        AstPythonGeneratorHelper astHelper = new AstPythonGeneratorHelper(astClassDiagram, globalScope);
        glex.setGlobalValue("astHelper", astHelper);
        glex.setGlobalValue("pythonNameHelper", new PythonNamesHelper());
        setup.setGlex(glex);
        // we deactivate tracing in order to preserve the sensitive syntax of python
        setup.setTracing(false);

        final GeneratorEngine generator = new GeneratorEngine(setup);
        final String astPackage = astHelper.getAstPackage();
        final String visitorPackage = de.monticore.codegen.cd2java.ast.AstGeneratorHelper.getPackageName(astHelper.getPackageName(),
                VisitorGeneratorHelper.getVisitorPackageSuffix());

        for (ASTCDClass clazz : astClassDiagram.getCDDefinition().getCDClasses()) {
            final Path filePath = Paths.get(Names.getPathFromPackage(astPackage),
                    Names.getSimpleName(clazz.getName()) + PYTHON_EXTENSION);
            if (astHelper.isAstClass(clazz)) {
                generator.generate("ast_python.AstClass", filePath, clazz, clazz, astHelper.getASTBuilder(clazz));
            }
            else if (!AstGeneratorHelper.isBuilderClass(clazz)) {
                generator.generate("ast.Class", filePath, clazz);
            }
        }
        /*
        for (ASTCDInterface interf : astClassDiagram.getCDDefinition().getCDInterfaces()) {
            final Path filePath = Paths.get(Names.getPathFromPackage(astPackage),
                    Names.getSimpleName(interf.getName()) + PYTHON_EXTENSION);
            generator.generate("ast.AstInterface", filePath, interf, visitorPackage,
                    VisitorGeneratorHelper.getVisitorType(diagramName));
        }

        for (ASTCDEnum enm : astClassDiagram.getCDDefinition().getCDEnums()) {
            final Path filePath = Paths.get(Names.getPathFromPackage(astPackage),
                    Names.getSimpleName(enm.getName()) + PYTHON_EXTENSION);
            generator.generate("ast.AstEnum", filePath, enm);
        }
        */

    }

    private ASTPythonGenerator() {
        // noninstantiable
    }

}
