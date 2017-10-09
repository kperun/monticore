package de.monticore.codegen.cd2python.ast;

import de.monticore.codegen.GeneratorHelper;
import de.monticore.symboltable.GlobalScope;
import de.monticore.types.TypesPrinter;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDType;

import java.util.Optional;

public class AstPythonGeneratorHelper extends GeneratorHelper {

    protected static final String AST_BUILDER = "Builder_";

    public AstPythonGeneratorHelper(ASTCDCompilationUnit topAst, GlobalScope symbolTable) {
        super(topAst, symbolTable);
    }

    public String getAstAttributeValue(ASTCDAttribute attribute, ASTCDType clazz) {
        return getAstAttributeValue(attribute);
    }

    public String getAstAttributeValue(ASTCDAttribute attribute) {
        if (attribute.getValue().isPresent()) {
            return attribute.printValue();
        }
        if (isOptional(attribute)) {
            return "None";
        }
        String typeName = TypesPrinter.printType(attribute.getType());
        if (isListType(typeName)) {
            return "list()";
        }
        if (isMapType(typeName)) {
            return "dict()";
        }
        return "";
    }

    public String getAstAttributeValueForBuilder(ASTCDAttribute attribute) {
        if (isOptional(attribute)) {
            return "None";
        }
        return getAstAttributeValue(attribute);
    }

    public String getAstPackage() {
        return getPackageName(getPackageName(), getAstPackageSuffix());
    }

    public static String getAstPackageSuffix() {
        return GeneratorHelper.AST_PACKAGE_SUFFIX;
    }

    public Optional<ASTCDClass> getASTBuilder(ASTCDClass clazz) {
        return getCdDefinition().getCDClasses().stream()
                .filter(c -> c.getName().equals(getNameOfBuilderClass(clazz))).findAny();
    }

    public static String getNameOfBuilderClass(ASTCDClass astClass) {
        return AST_BUILDER + getPlainName(astClass);
    }

    public static String getSuperClass(ASTCDClass astcdClass){
        if (astcdClass.getSuperclass().isPresent()){
            return astcdClass.getSuperclass().get().toString();
        }
        // this null is catched in the template
        return null;
    }


    /**
     * The are no actual modifiers in python but some guidelines. In order to mark something as private,
     * we extend the name by __, e.g., __isPrivate
     * @param astcdAttribute a single attribute object
     * @return the modifier if required
     */
    public static String printModifier(ASTCDAttribute astcdAttribute){
        if(astcdAttribute.getModifier().isPresent() && astcdAttribute.getModifier().get().isPrivate()){
            return "__";
        }
        else{
            return "";
        }
    }

}


