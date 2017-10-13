package de.monticore.codegen.cd2python.ast.ast;

import com.google.common.collect.Lists;
import de.monticore.codegen.GeneratorHelper;
import de.monticore.codegen.cd2java.ast.AstAdditionalMethods;
import de.monticore.codegen.cd2java.ast.AstGeneratorHelper;
import de.monticore.codegen.cd2java.ast.CdDecorator;
import de.monticore.codegen.cd2java.visitor.VisitorGeneratorHelper;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.HookPoint;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.io.paths.IterablePath;
import de.monticore.symboltable.GlobalScope;
import de.monticore.types.TypesHelper;
import de.monticore.types.TypesPrinter;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;
import de.monticore.umlcd4a.cd4analysis._ast.ASTModifier;
import de.monticore.umlcd4a.symboltable.CDSymbol;
import de.monticore.umlcd4a.symboltable.CDTypeSymbol;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;
import groovyjarjarantlr.ANTLRException;

import java.util.List;
import java.util.Optional;


public class PythonCdDecorator extends CdDecorator{

    public PythonCdDecorator(GlobalExtensionManagement glex, GlobalScope symbolTable, IterablePath targetPath) {
        super(glex, symbolTable, targetPath);
    }

    /**
     * Adds getter for all attributes of ast classes
     *
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
            if (attribute.getType() == null)// in the case that no type is provided, it is an enum constant
                continue;
            String toParse = "public " + TypesPrinter.printType(attribute.getType()) + " "
                    + methodName + "() ;";
            boolean isBooleanReturn = TypesPrinter.printType(attribute.getType()).equals("boolean");
            HookPoint getMethodBody = new TemplateHookPoint("ast_python.additionalmethods.Get", clazz,
                    attribute.getName(),isBooleanReturn);
            replaceMethodBodyTemplate(clazz, toParse, getMethodBody);
        }
    }

    /**
     * Adds getter for all attributes of ast classes
     *
     * @param clazz
     * @param astHelper
     * @throws ANTLRException
     */
    protected void addSetter(ASTCDClass clazz, AstGeneratorHelper astHelper) {
        for (ASTCDAttribute attribute : clazz.getCDAttributes()) {
            String typeName = TypesHelper.printSimpleRefType(attribute.getType());
            if (!AstGeneratorHelper.generateSetter(clazz, attribute, typeName)) {
                continue;
            }
            String attributeName = attribute.getName();
            String methodName = GeneratorHelper.getPlainSetter(attribute);
            boolean isOptional = GeneratorHelper.isOptional(attribute);
            boolean isBooleanSetter = TypesPrinter.printType(attribute.getType()).equals("boolean");
            String toParse = "public void " + methodName + "("
                    + typeName + " " + attributeName + ") ;";
            HookPoint methodBody = new TemplateHookPoint("ast_python.additionalmethods.Set", clazz,
                    attribute, attributeName,isBooleanSetter);
            ASTCDMethod setMethod = replaceMethodBodyTemplate(clazz, toParse, methodBody);

            if (isOptional) {
                glex.replaceTemplate(ERROR_IFNULL_TEMPLATE, setMethod, new StringHookPoint(""));
            }

            if (isOptional) {
                toParse = "public boolean " + attributeName + "IsPresent() ;";
                methodBody = new StringHookPoint("  return " + attributeName + ".isPresent(); \n");
                replaceMethodBodyTemplate(clazz, toParse, methodBody);
            }
        }
    }

    /**
     * Adds common ast methods to the all classes in the class diagram
     *
     * @param clazz - each entry contains a class diagram class and a respective
     * builder class
     * @param astHelper
     * @throws ANTLRException
     */
    protected void addAdditionalMethods(ASTCDClass clazz,
                                        AstGeneratorHelper astHelper) {
        if (astHelper.isAstClass(clazz)) {
            AstAdditionalMethods additionalMethod = AstAdditionalMethods.accept;
            String visitorTypeFQN = VisitorGeneratorHelper.getQualifiedVisitorType(
                    astHelper.getPackageName(), astHelper.getCdName());
            String methodSignatur = String.format(additionalMethod.getDeclaration(), visitorTypeFQN);
            replaceMethodBodyTemplate(clazz, methodSignatur, new TemplateHookPoint(
                    "ast_python.additionalmethods.Accept"));

            // node needs to accept visitors from all super languages
            for (CDSymbol cdSym : astHelper.getAllSuperCds(astHelper.getCd())) {
                String superGrammarName = Names.getSimpleName(cdSym.getFullName());
                String visitorType = superGrammarName + "Visitor";
                String visitorPackage = VisitorGeneratorHelper.getVisitorPackage(cdSym.getFullName());

                additionalMethod = AstAdditionalMethods.accept;
                String superVisitorTypeFQN = visitorPackage + "." + visitorType;
                methodSignatur = String.format(additionalMethod.getDeclaration(), superVisitorTypeFQN);
                replaceMethodBodyTemplate(clazz, methodSignatur, new TemplateHookPoint(
                        "ast_python.additionalmethods.AcceptSuper", clazz, astHelper.getQualifiedCdName(),
                        visitorTypeFQN, superVisitorTypeFQN));
            }
        }

        Optional<ASTModifier> modifier = clazz.getModifier();
        String plainClassName = GeneratorHelper.getPlainName(clazz);
        Optional<CDTypeSymbol> symbol = astHelper.getCd().getType(plainClassName);
        if (!symbol.isPresent()) {
            Log.error("0xA1062 CdDecorator error: Can't find symbol for class " + plainClassName);
        }

        replaceMethodBodyTemplate(clazz, AstAdditionalMethods.deepEqualsWithOrder.getDeclaration(),
                new TemplateHookPoint("ast.additionalmethods.DeepEqualsWithOrder"));

        replaceMethodBodyTemplate(clazz, AstAdditionalMethods.deepEquals.getDeclaration(),
                new StringHookPoint("return deepEquals(o, true);\n"));

        replaceMethodBodyTemplate(clazz,
                AstAdditionalMethods.deepEqualsWithCommentsWithOrder.getDeclaration(),
                new TemplateHookPoint("ast.additionalmethods.DeepEqualsWithComments"));

        replaceMethodBodyTemplate(clazz,
                AstAdditionalMethods.deepEqualsWithComments.getDeclaration(),
                new StringHookPoint("return deepEqualsWithComments(o, true);\n"));

        replaceMethodBodyTemplate(clazz, AstAdditionalMethods.equalAttributes.getDeclaration(),
                new TemplateHookPoint("ast.additionalmethods.EqualAttributes"));

        replaceMethodBodyTemplate(clazz, AstAdditionalMethods.equalsWithComments.getDeclaration(),
                new TemplateHookPoint("ast.additionalmethods.EqualsWithComments"));

        replaceMethodBodyTemplate(clazz, AstAdditionalMethods.get_Children.getDeclaration(),
                new TemplateHookPoint("ast.additionalmethods.GetChildren", clazz, symbol.get()));

        replaceMethodBodyTemplate(clazz, AstAdditionalMethods.remove_Child.getDeclaration(),
                new TemplateHookPoint("ast_python.additionalmethods.RemoveChild", clazz, symbol.get()));

        replaceMethodBodyTemplate(clazz, AstAdditionalMethods.getBuilder.getDeclaration(),
                new StringHookPoint("return new Builder();\n"));

        String stringToParse = String.format(AstAdditionalMethods.deepClone.getDeclaration(),
                plainClassName);
        replaceMethodBodyTemplate(clazz, stringToParse,
                new StringHookPoint("return deepClone(_construct());\n"));

        stringToParse = String.format(AstAdditionalMethods.deepCloneWithOrder.getDeclaration(),
                plainClassName, plainClassName);
        replaceMethodBodyTemplate(clazz, stringToParse,
                new TemplateHookPoint("ast.additionalmethods.DeepCloneWithParameters"));

        if (modifier.isPresent() && modifier.get().isAbstract()) {
            stringToParse = String.format(AstAdditionalMethods._construct.getDeclaration(), "abstract "
                    + plainClassName);
            cdTransformation.addCdMethodUsingDefinition(clazz, stringToParse);
        }
        else {
            stringToParse = String.format(AstAdditionalMethods._construct.getDeclaration(),
                    plainClassName);
            replaceMethodBodyTemplate(clazz, stringToParse,
                    new StringHookPoint("return new " + plainClassName + "();\n"));
        }
    }

}
