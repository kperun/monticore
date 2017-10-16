package de.monticore.codegen.cd2python.ast.ast;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import de.monticore.codegen.GeneratorHelper;
import de.monticore.codegen.cd2java.ast.AstAdditionalMethods;
import de.monticore.codegen.cd2java.ast.AstGeneratorHelper;
import de.monticore.codegen.cd2java.ast.CdDecorator;
import de.monticore.codegen.cd2java.visitor.VisitorGeneratorHelper;
import de.monticore.codegen.mc2cd.TransformationHelper;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.HookPoint;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.io.paths.IterablePath;
import de.monticore.symboltable.GlobalScope;
import de.monticore.types.TypesHelper;
import de.monticore.types.TypesPrinter;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.*;
import de.monticore.umlcd4a.symboltable.CDSymbol;
import de.monticore.umlcd4a.symboltable.CDTypeSymbol;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;
import groovyjarjarantlr.ANTLRException;

import java.util.*;


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
                if(attribute.getModifier().isPresent() && attribute.getModifier().get().isStatic()) {
                    methodBody = new StringHookPoint("  return cls." + attributeName + " is not None\n");
                }else{
                    methodBody = new StringHookPoint("  return self." + attributeName + " is not None\n");
                }
                replaceMethodBodyTemplate(clazz, toParse, methodBody);
            }
        }
    }

    /**
     * Adds common ast methods to the all classes in the class diagram.
     *
     * @param clazz - each entry contains a class diagram class and a respective
     * builder class
     * @param astHelper a generator helper with python specific helper functions
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
                new TemplateHookPoint("ast_python.additionalmethods.DeepEqualsWithOrder"));

        replaceMethodBodyTemplate(clazz,
                AstAdditionalMethods.deepEqualsWithCommentsWithOrder.getDeclaration(),
                new TemplateHookPoint("ast_python.additionalmethods.DeepEqualsWithComments"));

        replaceMethodBodyTemplate(clazz, AstAdditionalMethods.equalAttributes.getDeclaration(),
                new TemplateHookPoint("ast_python.additionalmethods.EqualAttributes"));

        replaceMethodBodyTemplate(clazz, AstAdditionalMethods.equalsWithComments.getDeclaration(),
                new TemplateHookPoint("ast_python.additionalmethods.EqualsWithComments"));

        replaceMethodBodyTemplate(clazz, AstAdditionalMethods.get_Children.getDeclaration(),
                new TemplateHookPoint("ast_python.additionalmethods.GetChildren", clazz, symbol.get()));

        replaceMethodBodyTemplate(clazz, AstAdditionalMethods.remove_Child.getDeclaration(),
                new TemplateHookPoint("ast_python.additionalmethods.RemoveChild", clazz, symbol.get()));

        replaceMethodBodyTemplate(clazz, AstAdditionalMethods.getBuilder.getDeclaration(),
                new StringHookPoint("return Builder()\n"));

        String stringToParse = String.format(AstAdditionalMethods.deepClone.getDeclaration(),
                plainClassName);
        replaceMethodBodyTemplate(clazz, stringToParse,
                new TemplateHookPoint("ast_python.additionalmethods.DeepCloneWithParameters"));

    }

    /**
     * Creates a node factory for the corresponding set of nodes as stored in the compilation unit
     *
     * @param cdCompilationUnit a single compilation unit with several classes.
     * @param nativeClasses a list of native classes, i.e., those classes which were present in the CD
     * @param astHelper a helper object
     * @throws ANTLRException
     */
    protected void addNodeFactoryClass(ASTCDCompilationUnit cdCompilationUnit,
                                       List<ASTCDClass> nativeClasses, AstGeneratorHelper astHelper) {

        // Add factory-attributes for all ast classes
        Set<String> astClasses = new LinkedHashSet<>();
        nativeClasses.stream()
                .forEach(e -> astClasses.add(GeneratorHelper.getPlainName(e)));

        ASTCDClass nodeFactoryClass = createNodeFactoryClass(cdCompilationUnit, nativeClasses,
                astHelper, astClasses);

        // We only modify the path to the template which is used to print the factory
        glex.replaceTemplate(CLASS_CONTENT_TEMPLATE, nodeFactoryClass, new TemplateHookPoint(
                "ast_python.AstNodeFactory", nodeFactoryClass, nativeClasses));

    }

    /**
     * Adds all required methods to the factory.
     * @param clazz a single class object.
     * @param nodeFactoryClass the factory class object
     * @param astHelper a helper containing additional information
     */
    protected void addMethodsToNodeFactory(ASTCDClass clazz, ASTCDClass nodeFactoryClass,
                                           AstGeneratorHelper astHelper) {
        if (!clazz.getModifier().isPresent() || clazz.getModifier().get().isAbstract()) {
            return;
        }
        String className = GeneratorHelper.getPlainName(clazz);
        String toParse = "";
        // No create methods with parameters
        if (clazz.getCDAttributes().isEmpty()) {
            return;
        }

        toParse = "public static " + className + " create" + className + "() ;";

        Optional<ASTCDMethod> astMethod = cdTransformation.addCdMethodUsingDefinition(
                nodeFactoryClass, toParse);
        Preconditions.checkArgument(astMethod.isPresent());
        ASTCDMethod createMethod = astMethod.get();

        toParse = "protected " + className + " doCreate" + className + "() ;";
        astMethod = cdTransformation.addCdMethodUsingDefinition(
                nodeFactoryClass, toParse);
        Preconditions.checkArgument(astMethod.isPresent());
        ASTCDMethod doCreateMethod = astMethod.get();

        StringBuilder paramCall = new StringBuilder();
        List<ASTCDAttribute> parameters = Lists.newArrayList();
        String del = "";
        List<ASTCDAttribute> inheritedAttributes = Lists.newArrayList();
        for (ASTCDAttribute attr : clazz.getCDAttributes()) {
            if (GeneratorHelper.isInherited(attr)) {
                inheritedAttributes.add(attr);
                continue;
            }
            ASTCDParameter param = CD4AnalysisNodeFactory.createASTCDParameter();
            ASTType type = attr.getType();
            if (TypesHelper.isOptional(type)) {
                type = TypesHelper.getSimpleReferenceTypeFromOptional(type);
            }
            else {
                parameters.add(attr);
            }
            param.setType(type);
            String pythonAttrName = AstPythonGeneratorHelper.getPythonConformName(attr.getName());
            param.setName(pythonAttrName );
            ASTCDParameter doParam = param.deepClone();
            createMethod.getCDParameters().add(param);
            doCreateMethod.getCDParameters().add(doParam);
            paramCall.append(del + "_"+pythonAttrName );
            del = DEL;
        }

        for (ASTCDAttribute attr : inheritedAttributes) {
            ASTCDParameter param = CD4AnalysisNodeFactory.createASTCDParameter();
            ASTType type = attr.getType();
            if (TypesHelper.isOptional(type)) {
                type = TypesHelper.getSimpleReferenceTypeFromOptional(type);
            }
            else {
                parameters.add(attr);
            }
            param.setType(type);
            String pythonAttrName  = AstPythonGeneratorHelper.getPythonConformName(attr.getName());
            param.setName(pythonAttrName );
            ASTCDParameter doParam = param.deepClone();
            createMethod.getCDParameters().add(param);
            doCreateMethod.getCDParameters().add(doParam);
            paramCall.append(del + "_" + pythonAttrName );
            del = DEL;
        }

        // create() method
        glex.replaceTemplate("ast_python.ParametersDeclaration", createMethod, new TemplateHookPoint(
                "ast_python.ConstructorParametersDeclaration"));
        glex.replaceTemplate(EMPTY_BODY_TEMPLATE, createMethod, new TemplateHookPoint(
                "ast_python.factorymethods.CreateWithParams", clazz, className, paramCall.toString()));

        // doCreate() method
        glex.replaceTemplate("ast_python.ParametersDeclaration", doCreateMethod, new TemplateHookPoint(
                "ast_python.ConstructorParametersDeclaration"));
        glex.replaceTemplate(EMPTY_BODY_TEMPLATE, doCreateMethod, new TemplateHookPoint(
                "ast_python.factorymethods.DoCreateWithParams", clazz, className, paramCall.toString()));

        if (parameters.size() != createMethod.getCDParameters().size()) {
            glex.replaceTemplate(ERROR_IFNULL_TEMPLATE, createMethod, new TemplateHookPoint(
                    "ast_python.factorymethods.ErrorIfNull", parameters));
        }
        if (parameters.size() != doCreateMethod.getCDParameters().size()) {
            glex.replaceTemplate(ERROR_IFNULL_TEMPLATE, doCreateMethod, new TemplateHookPoint(
                    "ast_python.factorymethods.ErrorIfNull", parameters));
        }

    }
}
