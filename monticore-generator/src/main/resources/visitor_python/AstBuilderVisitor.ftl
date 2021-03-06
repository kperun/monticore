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
--><@compress>
${tc.signature("astType", "astPackage", "cd")}
<#assign genHelper = glex.getGlobalVar("visitorHelper")>
<#assign pythonHelper = glex.getGlobalVar("pythonVisitorHelper")>
<#-- Copyright -->
${tc.defineHookPoint("JavaCopyright")}
</@compress><#list cd.getTypes() as type><#if type.isClass() && !type.isAbstract()>
from ${type} import ${type}
</#if></#list>
from antlr4 import * <#-- required in order to interact with the parse tree. -->


# generated by template visitor_python.AstBuilderVisitor
class AstBuilderVisitor(ParseTreeVisitor):
    """
    This visitor can be used to generate, from a given compilation unit, the corresponding AST of the model.
    """

    <#list cd.getTypes() as type>
    <#if type.isClass() && !type.isAbstract()>
    <#assign astName = type.getName()>
    def visit${astName}(self, ctx):
        <#-- first collect all values from the parse tree -->
        <#list type.getAllVisibleFields() as field >
            <#if pythonHelper.isListNode(field)>
        _${field.getName()} = list()
        if callable(ctx.${field.getName()}):
            for node in ctx.${field.getName()}():
                <#if pythonHelper.hasSubRule(field,cd)>
                _${field.getName()}.append(self.visit(node))
                <#else>
                _${field.getName()}.append(node)
                </#if>
        else:
            for node in ctx.${field.getName()}:
                <#if pythonHelper.hasSubRule(field,cd)>
                _${field.getName()}.append(self.visit(node))
                <#else>
                _${field.getName()}.append(node)
                </#if>
            <#else>
                <#if pythonHelper.hasSubRule(field,cd)>
        if callable(ctx.${field.getName()}):
            _${field.getName()} = self.visit(ctx.${field.getName()}()) if ctx.${field.getName()}() is not None else None
        else:
            _${field.getName()} = self.visit(ctx.${field.getName()})
                <#else>
        if callable(ctx.${field.getName()}):
            _${field.getName()} = ctx.${field.getName()}() if ctx.${field.getName()}() is not None else None
        else:
            _${field.getName()} = ctx.${field.getName()}
                </#if>
            </#if>
        </#list>

        <#-- now create a new object of the corresponding type -->
        ret = ${astName}(<#list type.getAllVisibleFields() as field>_${field.getName()}<#if (field?index < type.getAllVisibleFields()?size -1)>, </#if></#list>)
        # update the source position
        ret.setSourcePositionStart(ctx.start.line,ctx.start.column)
        ret.setSourcePositionEnd(ctx.stop.line,ctx.stop.column)
        return ret

    </#if>
    </#list>

