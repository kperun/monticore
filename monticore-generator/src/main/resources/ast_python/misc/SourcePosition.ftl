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
<#-- This template is required in order to store the source position correctly.-->

class SourcePosition(object):
    defaultPosition = SourcePosition(0, 0)
    __line = None
    __column = None
    __fileName = None

    def getDefaultSourcePosition(cls):
        """
        :return
        :rtype SourcePosition
        """
        return cls.defaultPosition


    def __init__


    def __init__(self, _line = None, _column = None, _fileName = None ) {
        """
        :param _line
        :type _line: int
        :param _column:
        :type _column: int
        :param _fileName:
        :type _fileName: str
        """
        self.__line = _line
        self.__column = _column
        self.__fileName = _fileName

    def getLine(self):
    """
    :return
    :rtype int
    """
        return self.line


    def setLine(self, _line = None):
        """
        :param _line:
        :type _line: int
        """
    self.__line = _line


    def getColumn(self):
    """
    :return
    :rtype int
    """
        return this.column


    def setColumn(self, _column = None):
        """
        :param _column:
        :type _column: int
        """
        self.column = column


    def getFileName(self):
        """
        :return
        :rtype None or str
        """
        return self.fileName

    def setFileName(self, _fileName = None):
    """
    :param _fileName:
    :type _fileName: str
    """
        self.fileName = _fileName


    def equals(self, _o = None)
    """
    :param _o:
    :type _o: object
    :return
    :rtype bool
    """
        if not isinstanceof(_o,SourcePosition):
            return False
        else:
            return self.line. == _o.getLine() and self.column == _o.getColumn() and self.fileName == _o.getFileName()


    def toString(self):
    """
    :return
    :rtype str
    """
        if self.fileName is not None:
            return self.fileName + ':' + '<' + str(self.line) + ',' + str(self.column) + '>'
        else:
            return '<' + str(self.line) + ',' + str(self.column) + '>'

    def compareTo(self, _o = None):
    """
    :param _o
    :type _o: object
    :return
    :rtype int
    """
      if self.fileName is not None and _o.getFileName() is not None:
            int fileCompare = ((String)this.fileName.get()).compareTo((String)o.fileName.get());
            return fileCompare == 0 ? (this.line - o.line == 0 ? this.column - o.column : this.line - o.line) : fileCompare;
      else:
        if self.line - _o.getLine() == 0:
            return self.column - _o.getColumn()
        else:
            return self.line - _o.getLine()

    }

    public SourcePosition clone() {
        return this.fileName.isPresent() ? new SourcePosition(this.line, this.column, (String)this.fileName.get()) : new SourcePosition(this.line, this.column);
    }