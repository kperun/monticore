package de.monticore.codegen.cd2python.ast.visitor;

import de.monticore.umlcd4a.symboltable.CDFieldSymbol;
import de.monticore.umlcd4a.symboltable.CDSymbol;

public class PythonVisitorGeneratorHelper {


    /**
     * Indicates whether the handed over field symbol has a type which is a reference to a different class, i.e., an
     * object. This is required in order to the the information, if the "visit" has to be called.
     * @param cdFieldSymbol a single cdFieldSymbol object
     * @param cdSymbol a single cdSymbol object.
     * @return true if the visit method should be called
     */
    public static boolean hasSubRule(CDFieldSymbol cdFieldSymbol, CDSymbol cdSymbol){
        if (cdFieldSymbol.getType().getActualTypeArguments().size() > 0){
            return cdSymbol.getType(cdFieldSymbol.getType().getActualTypeArguments().get(0).getType().getName()).isPresent();
        }
        return false;
    }

    /**
     * Indicates whether the handed over symbol uses a collection which has to be represented as a list in python.
     * @param cdFieldSymbol a single field symbol
     * @return true if collection, otherwise false.
     */
    public static boolean isListNode(CDFieldSymbol cdFieldSymbol){
        if (cdFieldSymbol.getType().getActualTypeArguments().size() > 0){
            return true;
        }
        return false;
    }

}
