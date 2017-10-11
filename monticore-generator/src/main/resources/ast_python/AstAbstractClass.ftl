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
<#--
  Generates a Python class

  @params    ASTCDClass $ast
             ASTCDClass $astBuilder
  @result    mc.javadsl.JavaDSL.CompilationUnit

-->
${tc.signature("visitorPackage", "visitorType")}
<#assign genHelper = glex.getGlobalVar("astHelper")>
from abc import ABCMeta, abstractmethod

class ${ast.getName()}(object):
    """
    This is an abstract class aka. interface.
    """
    __metaclass__ = ABCMeta

    @abstractmethod
    def deepClone():
        """
        :return a copy of self
        :rtype ${ast.getName()}
        """
        pass

    @abstractmethod
    def equals(_o = None):
        """
        :param _o: an object
        :type _o: object
        :return True if equal, otherwise False.
        :rtype bool
        """
        pass

    @abstractmethod
    def equalsWithComments(_o = None):
        """
        :param _o: an object
        :type _o: object
        :return True if equal, otherwise False.
        :rtype bool
        """
        pass

    @abstractmethod
    def deepEquals(Object o):
        """
        :param _o: an object
        :type _o: object
        :return True if equal, otherwise False.
        :rtype bool
        """
        pass

    @abstractmethod
    def deepEquals(_o = None, _forceSameOrder=False):
        """
        :param _o: an object
        :type _o: object
        :param _forceSameOrder: enforces the everything has to be in the same order
        :type _forceSameOrder: bool
        :return True if equal, otherwise False.
        :rtype bool
        """
        pass

    @abstractmethod
    def deepEqualsWithComments(_o = None):
        """
        :param _o: an object
        :type _o: object
        :return True if equal, otherwise False.
        :rtype bool
        """
        pass

    @abstractmethod
    def deepEqualsWithComments(_o = None, _forceSameOrder = None):
        """
        :param _o: an object
        :type _o: object
        :param _forceSameOrder: enforces the everything has to be in the same order
        :type _forceSameOrder: bool
        :return True if equal, otherwise False.
        :rtype bool
        """
        pass

    @abstractmethod
    def accept(_visitor=None):
        """
        :param _visitor: a single visitor object
        :type _visitor: ${visitorType}
        """
        pass

    <#-- generate all methods -->
    <#list ast.getCDMethods() as method>
    ${tc.includeArgs("ast_python.ClassMethod", [method, ast])}
    </#list>


