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
        # generated by template ast_python.additionalmethods.EqualsWithComments
        comp = None
        if isinstance(_o, ${astName}):
            comp = _o
        else:
            return False

        if not self.equalAttributes(comp):
            return False

    <#-- list astType.getCDAttributes()  as attribute> 
      <#assign attributeName = genHelper.getJavaConformName(attribute.getName())>
      <#if !genHelper.isAstNode(attribute)>
	    # comparing ${attributeName}
	    <#if genHelper.isPrimitive(attribute.getType())>
	    if not self.${attributeName} == comp.${attributeName}:
	        return False
	    <#else>
	    if (self.${attributeName} is None and comp.${attributeName} is not None) or \
	        (self.${attributeName} is not None and not self.${attributeName}.equals(comp.${attributeName})):
	        return False
	    </#if>
	  </#if>  
    </#list --> 
        # comparing comments
        if len(self.get_PreComments()) == len(comp.get_PreComments()):
            one = self.get_PreComments()
            two = comp.get_PreComments()
            for i in range(0,len(self.get_PreComments())):
                if not one[i].equals(two[i]):
                    return False
        else:
            return False

        if len(self.get_PostComments()) == len(comp.get_PostComments()):
            one = self.get_PostComments()
            two = comp.get_PostComments()
            for i in range(0,len(self.get_PostComments())):
                if not one[i].equals(two[i]):
                    return False
        else:
            return False

        return True
