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
<#-- Copyright -->
${tc.defineHookPoint("JavaCopyright")}
</@compress><#list cd.getTypes() as type>
from ${type} import ${type}
</#list>

class ${genHelper.getVisitorType()}:
    """
     * Default AST-visitor for the {@code ${genHelper.getCdName()}} language.<br/>
     * <br/>
     * <b>Running a visitor</b>: Starting a traversal of an AST with root {@code astNode} is as simple as calling {@code handle(astNode)}. Note that the visitor only handles nodes of language {@code ${genHelper.getCdName()}}.<br/>
     * <br/>
     * <b>Implementing a visitor:</b><br/>
     * You should never use {@code this}, but always make use of {@link #getRealThis()}. This ensures that the visitor can be reused by composition.<br/>
     * <br/>
     * <ul>
     *   <li><b>Visiting nodes</b>: You may override the {@code visit(node)} and {@code endVisit(node)} methods to do something at specific AST-nodes.<br/><br/></li>
     *   <li><b>Traversal</b>: You may override the {@code traverse(node)} methods, if you want to change the climb down strategy for traversing children (e.g. ordering the children). Be aware of the underlying double-dispatch mechanism: probably you want to call {@code childNode.accept(getRealThis())} and <b>not</b> {@code handle(childNode)}<br/><br/></li>
     *   <li><b>Handling of nodes</b>: You may override the {@code handle(node)} methods, if you want to change its default implementation (depth-first iteration): {@code visit(node); traverse(node); endVisit(node);}<br/><br/></li>
     * </ul>
     * <b>Special node type {@code ASTNode}:</b><br/>
     * Visitors do not provide handle or traverse methods for {@code ASTNode},
     * because handling and traversal are defined in the language depending node
     * types. However, an {@link ${genHelper.getInheritanceVisitorType()}} visits and
     * endVisits each node as {@code ASTNode}. Due to composition of all kinds of
     * visitors we must define the methods here in the main visitor interface.
     *
     * @see ${genHelper.getASTNodeBaseType()}#accept(${genHelper.getVisitorType()} visitor)
    """


    def setRealThis( _realThis = None):
        """
        Sets the visitor to use for handling and traversing nodes.
        This method is not implemented by default and visitors intended for reusage
        in other languages should override this method together with
        {@link #getRealThis()} to make a visitor composable.
        RealThis is used to allow visitor composition, where a delegating visitor
        utilizes this setter to set another visitor as the handle/traversal
        controller. If this method is not overridden by the language developer,
        the visitor still can be reused, by implementing this method in a
        decorator.
        :param realThis the real instance to use for handling and traversing nodes.
        @see ${genHelper.getCdName()}DelegatorVisitor
        """
        raise Exception("0xA7011${genHelper.getGeneratedErrorCode(ast)} The setter for realThis is not implemented. You might want to implement a wrapper class to allow setting/getting realThis.")

    def getRealThis():
        """
        By default this method returns {@code this}. Visitors intended for reusage
        in other languages should override this method together with
        {@link #setRealThis(${genHelper.getVisitorType()})} to make a visitor
        composable.
        See {@link #setRealThis(${genHelper.getVisitorType()})} for more information.
        @see #setRealThis(${genHelper.getVisitorType()})
        @see ${genHelper.getCdName()}DelegatorVisitor
        """
        return self

    # ------------------------------------------------------------------------


    def visit( _node = None):
        """
        Python does not allow overloading of methods, thus a special visit method is used which
        delegates according to the type.

        :param _node: the node that is entered
        """
        <#list cd.getTypes() as type>
        if isinstnace( _node , ${type} ):
            visit${type}(_node)
        </#list>

    def traverse( _node = None):
        """
        Python does not allow overloading of methods, thus a special visit method is used which
        delegates according to the type.

        :param _node: the node that is entered
        """
        <#list cd.getTypes() as type>
        if isinstnace( _node , ${type} ):
            traverse${type}(_node)
        </#list>


    def endVisit( _node = None):
        """
        Python does not allow overloading of methods, thus a special visit method is used which
        delegates according to the type.

        :param _node: the node that is left
        """
        <#list cd.getTypes() as type>
        if isinstnace( _node , ${type} ):
            endVisit${type}(_node)
        </#list>

    # ------------------------------------------------------------------------


    <#list cd.getTypes() as type>
    <#if type.isClass() || type.isInterface() >
    <#assign astName = type.getName()>

    def visit${astName}( _node = None ):
        pass

    def endVisit${astName}( _node = None ):
        pass

    def handle${astName}( _node = None ):
        getRealThis().visit(_node)
        <#if type.isInterface() || type.isEnum()>
        // no traverse() for interfaces and enums, only concrete classes are traversed
        <#elseif type.isAbstract() >
        // no traverse() for abstract classes, only concrete subtypes are traversed
        <#else>
        getRealThis().traverse(_node);
        </#if>
        getRealThis().endVisit(_node);
    </#if>

    <#if type.isClass() && !type.isAbstract()>
    def traverse${astName}( _node = None ):
        # One might think that we could call traverse(subelement) immediately,
        # but this is not true for interface-types where we do not know the
        # concrete type of the element.
        # Instead we double-dispatch the call, to call the correctly typed
        # traverse(...) method with the elements concrete type.
        <#list type.getAllVisibleFields() as field>
            <#if genHelper.isAstNode(field) || genHelper.isOptionalAstNode(field) >
                <#assign attrGetter = genHelper.getPlainGetter(field)>
        if (_node.${attrGetter}() is not None):
            _node.${attrGetter}().accept(getRealThis())
            <#elseif genHelper.isListAstNode(field)>
                <#assign attrGetter = genHelper.getPlainGetter(field)>
                <#assign astChildTypeName = genHelper.getAstClassNameForASTLists(field)>
        for node in _node.${attrGetter}():
            node.accept(getRealThis())
            </#if>
        </#list>

    </#if>
    </#list>

