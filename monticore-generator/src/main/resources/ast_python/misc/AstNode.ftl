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
<#-- This template is required in order to generate the AST superclass in python. -->
from Comment import Comment

class ASTNode(object):
    """
    Foundation interface for all AST-classes.
    """

    def deepClone(self, _result = Node ):
        """
        Performs a deep clone of this ASTNode and all of its successors
        :return Clone of current ASTNode with a parent which is equal to null
        """
        assert (_result is not None),\
            "0xA4040 The argument ASTNode of the 'deepClone' method must not be null."

        _result.setSourcePositionStart(self.getSourcePositionStart().clone())
        _result.setSourcePositionEnd(self.getSourcePositionEnd().clone())

        for  x in self.getPreComments():
            _result.getPreComments().add(Comment(x.getText()))

        for x in self.getPostComments())
            _result.getPostComments().add(Comment(x.getText()))

        return _result

    def equalAttributes(self, _o = None):
        """
        :param _o: an arbitrary object
        :type _o: object
        :return True if attributes equal, otherwise False.
        :rtype bool
        """
        if _o is None:
            return False
        raise CompareNotSupportedException("0xA4041 Method equalAttributes is not implemented properly in class: " + type(_o.getClass()))


    def equalsWithComments(self, _o = None ):
        """
        :param _o: a single object
        :type _o: object
        :return True, if equal with comments.
        :rtype bool
        """
        if _o is None:
            return False
        raise CompareNotSupportedException("0xA4042 Method equalsWithComments is not implemented properly in class: "+ type(_o.getClass()))

    def deepEquals(self, _o = None, _forceSameOrder = False ):
        """
        Compare this object to another Object. Do not take comments into account.
        :param _o: the object to compare this node to
        :type _o: object
        :param _forceSameOrder: consider the order in ancestor lists, even if these  lists are of stereotype <tt>&lt;&lt;unordered&gt;&gt;</tt> in the grammar.
        :type _forceSameOrder: bool
        :return True, if deep equality is given, otherwise false
        """
        if _o is None:
            return False
        raise CompareNotSupportedException("0xA4045 Method deepEquals is not implemented properly in class: " + type(_o))


    def deepEqualsWithComments(self, _o = None, _forceSameOrder = False ):
        """
        Compare this object to another Object. Take comments into account. This
        method returns the same value as <tt>deepEqualsWithComment(Object o, boolean forceSameOrder)</tt> method
        when using the default value for forceSameOrder of each Node.
        :param _o: a single object
        :type _o: object
        :param _forceSameOrder: force the same order of elements
        :type _forceSameOrder: bool
        :return True if equal, otherwise False.
        :bool
        """
        if _o is None:
            return False
        raise CompareNotSupportedException("0xA4046 Method deepEqualsWithComments is not implemented properly in class: " + type(_o));



    def deepClone(self):
        """
        Performs a deep clone of this ASTNode and all of its successors
        :return Clone of current ASTNode with a parent which is equal to null
        :rtype ASTNode
        """
        pass

    def getSourcePositionEnd(self);
        """
        Returns the start position of this ASTNode
        :return start position of this ASTNode
        :rtype SourcePosition
        """
        pass

    def setSourcePositionEnd(self, _end = None );
        """
        Sets the end position of this ASTNode
        :param _end end position of this ASTNode
        :type _end SourcePosition
        """
        pass

    def getSourcePositionStart(self);
        """
        Returns the end source position of this ASTNode
        :return end position of this ASTNode
        :rtype SourcePosition
        """
        pass

    def setSourcePositionStart(self, _start = None );
        """
        Sets the start position of this ASTNode
        :param _start: start position of this ASTNode
        :type _start: SourcePosition
        """
        pass

    def get_PreComments(self);
        """
        Returns list of all comments which are associated with this ASTNode and are
        prior to the ASTNode in the input file
        :return list of comments
        :rtype list(Comment)
        """
        pass

    def setPreComments(self, _precomments = None );
        """
        Sets list of all comments which are associated with this ASTNode and are
        prior to the ASTNode in the input file
        :param _precomments: list of comments
        :param _precomments: list(Comment)
        """
        pass


    def getPostComments(self);
        """
        Returns list of all comments which are associated with this ASTNode and can
        be found after the ASTNode in the input file
        :return list of comments
        :rtype list(Comment)
        """
        pass



    def setPostComments(self, _postcomments = None );
        """
        Sets list of all comments which are associated with this ASTNode and can be
        found after the ASTNode in the input file.
        :param _postcomments list of comments
        :type _postcomments: list(Comment)
        """
        pass

    def getChildren(self):
        """
        :return a collection of all child nodes of this node
        :rtype list(ASTNode)
        """
        pass

    def removeChild(self, _child = None );
        """
        This method removes the reference from this node to a child node, no matter in which attribute it is stored.
        :param _child: the target node of the reference to be removed
        :type _child: ASTNode
        """
        pass


    def setEnclosingScope(self, _enclosingScope = None );
        """
        Sets the enclosing scope of this ast node.
        :param _enclosingScope: the enclosing scope of this ast node
        :type _enclosingScope: Scope
        """
        pass


    def getEnclosingScope(self):
        """
        :return the enclosing scope of this ast node
        :rtype Scope or None
        """
        pass

    def enclosingScopeIsPresent(self):
        """
        :return true if the enclosing scope is present
        :rtype bool
        """
        pass

    def setSymbol(self, _symbol = None):
        """
        Sets the corresponding symbol of this ast node.
        :param _symbol: the corresponding symbol of this ast node.
        :type _symbol: Symbol
        """
        pass


    def getSymbol(self):
        """
        :return the corresponding symbol of this ast node.
        :rtype Symbol
        """
        pass


    def symbolIsPresent(self):
        """
        :return true if the symbol is present
        :rtype bool
        """
        pass


    def setSpannedScope(self, _spannedScope = None ):
        """
        Sets the spanned scope of this ast node.
        :param _spannedScope: spannedScope the spanned scope of this ast node
        :type _spannedScope: Scope
        """
        pass


    def getSpannedScope(self):
        """
        Returns the spanning scope.
        :return the spanned scope of this ast node.
        :rtype Scope or Subtype
        """
        return None

    def spannedScopeIsPresent(self):
        """
        Indicates whether a spanned scope is present.
        :return True if present, otherwise False.
        :rtype bool
        """
        pass


class CompareNotSupportedException(Exception):
    """
    pass

