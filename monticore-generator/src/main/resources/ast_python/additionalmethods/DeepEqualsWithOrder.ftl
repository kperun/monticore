<#--
****************************************************************************
MontiCore Language Workbench, www.monticore.de
Copyright (c) 2017, MontiCore, All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice,
this list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice,
this list of conditions and the following disclaimer in the documentation
and/or other materials provided with the distribution.

3. Neither the name of the copyright holder nor the names of its
contributors may be used to endorse or promote products derived from this
software without specific prior written permission.

This software is provided by the copyright holders and contributors
"as is" and any express or implied warranties, including, but not limited
to, the implied warranties of merchantability and fitness for a particular
purpose are disclaimed. In no event shall the copyright holder or
contributors be liable for any direct, indirect, incidental, special,
exemplary, or consequential damages (including, but not limited to,
procurement of substitute goods or services; loss of use, data, or
profits; or business interruption) however caused and on any theory of
liability, whether in contract, strict liability, or tort (including
negligence or otherwise) arising in any way out of the use of this
software, even if advised of the possibility of such damage.
****************************************************************************
-->
${tc.signature("ast","astType")}
<#assign genHelper = glex.getGlobalVar("astHelper")>
<#assign astName = genHelper.getPlainName(astType)>
    <#if astType.getCDAttributes()?size == 0>
        return isinstance(_o, ${astName})
    <#else>
        comp = None
        if isinstance(_o, ${astName}):
            comp = _o
        else:
            return False
        if not self.equalAttributes(comp):
            return False
        <#list astType.getCDAttributes()  as attribute>
        <#assign attrName = genHelper.getJavaConformName(attribute.getName())>
            <#if genHelper.isOptionalAstNode(attribute)>
        # comparing ${attrName}
        if (self.${attrName} is not None == comp.${attrName} is not None) or\
            (self.${attrName} is not None and not self.${attrName}.deepEquals(comp.${attrName})):
            return False

        <#elseif genHelper.isAstNode(attribute)>
        # comparing ${attrName}
        if (self.${attrName} is None and comp.${attrName} is not None) or\
            (self.${attrName} is not None and not self.${attrName}.deepEquals(comp.${attrName})):
            return False

        <#elseif genHelper.isListAstNode(attribute)>
        # comparing ${attrName}
        if len(self.${attrName}) != len(comp.${attrName}):
            return False
        else:
        <#assign astChildTypeName = genHelper.getAstClassNameForASTLists(attribute)>
            if _forceSameOrder:
                for i in range(0,len(self.${attrName})):
                    if not self.${attrName}[i].deepEquals(comp.${attrName}[i]):
                        return False
            else:
                for elemThis in self.${attrName}:
                    matchFound = False
                    for elemThat in comp.${attrName}:
                        if elemThis.deepEquals(elemThat):
                            matchFound = True
                            break
                    if not matchFound:
                        return False
        </#if>
        </#list>
        return True
        </#if>

