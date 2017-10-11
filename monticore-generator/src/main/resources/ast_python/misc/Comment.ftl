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
<#-- This template is required for correct storage of comments in the ast.
     Caution: This file is not automatically generated and has to be adjusted in the case the grammar is changed.
-->

from SourcePosition import SourcePosition

class Comment(object):
    """
    Class represents a comment (contains the comment and the start- and end-position)
    """
    start = SourcePosition.getDefaultSourcePosition()
    end = SourcePosition.getDefaultSourcePosition()
    text = None

    def __init__(self, _text = None):
        """
        :param _text:
        :type _text: str
        """
        self.setText(text)

    def getSourcePositionEnd(self):
        """
        :return
        :rtype SourcePosition
        """
        return self.end

    def setSourcePositionEnd(self, _end = None):
        """
        :param _end
        :type _end: SourcePosition
        """
        this.end = end

    def getSourcePositionStart(self):
        """
        :return
        :rtype SourcePosition
        """
        return self.start

    def setSourcePositionStart(self, _start = None):
        """
        :param _start
        :type _start: SourcePosition
        """
        self.start = start

    def toString(self):
        """
        :return
        :rtype str
        """
        return self.text

    def getText(self):
        """
        :return
        :rtype str
        """
        return self.text

    def setText(self, _text = None):
        """
        :param _text
        :type _text: str
        """
        self.text = _text

    def equals(self, _o = None):
        """
        :param _o
        :type _o: object
        """
        if isinstanceof(_o,Comment):
            return self.text == _o.getText
        return False



