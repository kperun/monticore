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
--><#--
  Generates a python class method
  
  @params    ASTCDMethod     $ast
  @result    
-->
    ${tc.signature("ast", "astType")}
    <#assign genHelper = glex.getGlobalVar("astHelper")>
    <#if genHelper.isAbstract(ast, astType) || genHelper.isAbstractMethod(ast)>
    @abstractmethod
    </#if>
    <#if genHelper.isStaticMethod(ast)>
    @classmethod
    </#if>
    # generated by template ast_python.ClassMethod
    def ${genHelper.printModifier(ast)}${ast.getName()}(<#if genHelper.isStaticMethod(ast)>cls<#else>self</#if><#if genHelper.hasParameters(ast)>,</#if>${genHelper.printParametersDeclaration(ast)}):
    <#if !genHelper.isAbstract(ast, astType)>
        ${tc.includeArgs("ast.EmptyMethodBody", [ast, astType])}
    <#else>
        pass
    </#if>