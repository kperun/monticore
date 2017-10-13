package de.monticore.codegen.cd2python.ast.ast;

import com.google.common.collect.Lists;
import de.monticore.codegen.GeneratorHelper;
import de.monticore.codegen.cd2java.ast.AstGeneratorHelper;
import de.monticore.codegen.cd2java.ast.CdDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.HookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.io.paths.IterablePath;
import de.monticore.symboltable.GlobalScope;
import de.monticore.types.TypesPrinter;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import groovyjarjarantlr.ANTLRException;

import java.util.List;


public class PythonCdDecorator extends CdDecorator{

    public PythonCdDecorator(GlobalExtensionManagement glex, GlobalScope symbolTable, IterablePath targetPath) {
        super(glex, symbolTable, targetPath);
    }

    /**
     * Adds getter for all attributes of ast classes
     *
     * @param interf
     * @param astHelper
     * @throws ANTLRException
     */
    protected void addGetter(ASTCDClass clazz, AstGeneratorHelper astHelper) {
        List<ASTCDAttribute> attributes = Lists.newArrayList(clazz.getCDAttributes());
        // attributes.addAll(astHelper.getAttributesOfExtendedInterfaces(clazz));
        for (ASTCDAttribute attribute : attributes) {
            if (GeneratorHelper.isInherited(attribute)) {
                continue;
            }
            String methodName = GeneratorHelper.getPlainGetter(attribute);
            if (clazz.getCDMethods().stream()
                    .filter(m -> methodName.equals(m.getName()) && m.getCDParameters().isEmpty()).findAny()
                    .isPresent()) {
                continue;
            }
            if (attribute.getType() == null)// in the case that it no type is provided, it is an enum constant
                continue;
            String toParse = "public " + TypesPrinter.printType(attribute.getType()) + " "
                    + methodName + "() ;";
            HookPoint getMethodBody = new TemplateHookPoint("ast_!aspython.additionalmethods.Get", clazz,
                    attribute.getName());
            replaceMethodBodyTemplate(clazz, toParse, getMethodBody);
        }
    }

}
