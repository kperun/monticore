package de.monticore.codegen.cd2python.ast;

import de.se_rwth.commons.JavaNamesHelper;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class PythonNamesHelper {
    public static final String PREFIX_WHEN_WORD_IS_RESERVED = "r__";

    public static String javaAttribute(String in) {
        if (in != null && in.length() > 0) {
            String out = (in.substring(0, 1).toLowerCase() + in.substring(1)).intern();
            return getNonReservedName(out);
        } else {
            return in;
        }
    }

    public PythonNamesHelper() {
    }

    public static String getNonReservedName(String name) {
        return PythonReservedWordReplacer.getNonReservedName(name);
    }

    private static class PythonReservedWordReplacer {
        private static Set<String> goodNames = null;

        private PythonReservedWordReplacer() {
        }

        public static String getNonReservedName(String name) {
            if (goodNames == null) {
                goodNames = new HashSet();
                goodNames.addAll(Arrays.asList("and", "del", "from","not","while","as",
                        "elif","global","or","with","assert","else","if","pass","yield","break",
                        "except","import","print","class","exec","in","raise","continue",
                        "finally","is","return","def","for","lambda","try"));
            }

            if (name == null) {
                return null;
            } else {
                return goodNames.contains(name) ? ("r__" + name).intern() : name.intern();
            }
        }
    }
}
