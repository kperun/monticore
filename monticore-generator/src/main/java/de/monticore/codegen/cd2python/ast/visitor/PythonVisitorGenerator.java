package de.monticore.codegen.cd2python.ast.visitor;

import de.monticore.codegen.cd2java.ast.AstGeneratorHelper;
import de.monticore.codegen.cd2java.visitor.VisitorGeneratorHelper;
import de.monticore.codegen.cd2python.ast.visitor.PythonVisitorGeneratorHelper;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.symboltable.GlobalScope;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.umlcd4a.symboltable.CDSymbol;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PythonVisitorGenerator {

    private final static String LOGGER_NAME = PythonVisitorGenerator.class.getName();

    /**
     * Generates the different visitor default implementations for the given class
     * diagram.
     */
    public static void generate(GlobalExtensionManagement glex, GlobalScope globalScope,
                                ASTCDCompilationUnit astClassDiagram,
                                File outputDirectory) {
        final GeneratorSetup setup = new GeneratorSetup(outputDirectory);
        VisitorGeneratorHelper visitorHelper = new VisitorGeneratorHelper(astClassDiagram, globalScope);
        glex.setGlobalValue("visitorHelper", visitorHelper);
        glex.setGlobalValue("pythonVisitorHelper",new PythonVisitorGeneratorHelper());
        setup.setGlex(glex);
        setup.setTracing(false);//we need to deactivate it since python is syntax sensitive
        final GeneratorEngine generator = new GeneratorEngine(setup);
        final String diagramName = astClassDiagram.getCDDefinition().getName();
        final CDSymbol cd = visitorHelper.getCd();

        final String astPackage = VisitorGeneratorHelper.getPackageName(visitorHelper.getPackageName(),
                AstGeneratorHelper.getAstPackageSuffix());

        final String visitorPackage = visitorHelper.getVisitorPackage();
        String path = Names.getPathFromPackage(visitorPackage);

        // simple visitor interface
        final Path simpleVisitorFilePath = Paths.get(path, visitorHelper.getVisitorType() + ".py");
        generator.generate("visitor_python.SimpleVisitor", simpleVisitorFilePath, astClassDiagram,
                astClassDiagram.getCDDefinition(), astPackage, cd);
        // and the ast builder
        final Path astBuilderVisitorFilePath = Paths.get(path, "AstBuilderVisitor" + ".py");
        generator.generate("visitor_python.AstBuilderVisitor", astBuilderVisitorFilePath , astClassDiagram,
                astClassDiagram.getCDDefinition(), astPackage, cd);

        Log.trace(LOGGER_NAME, "Generated python visitors for the diagram: " + diagramName);
    }

    private PythonVisitorGenerator() {
        // noninstantiable
    }
}
