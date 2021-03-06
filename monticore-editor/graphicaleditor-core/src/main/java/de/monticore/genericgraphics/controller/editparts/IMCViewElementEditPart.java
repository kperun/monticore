/*******************************************************************************
 * MontiCore Language Workbench, www.monticore.de
 * Copyright (c) 2017, MontiCore, All rights reserved.
 *  
 * This project is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this project. If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.monticore.genericgraphics.controller.editparts;

import java.util.Observer;

import org.eclipse.gef.EditPart;

import de.monticore.genericgraphics.model.graphics.IViewElement;


/**
 * Interface providing methods to handle an {@link IViewElement} in an
 * {@link EditPart}.
 * 
 * @author Tim Enger
 */
public interface IMCViewElementEditPart extends IMCGraphicalEditPart, Observer {
  
  /**
   * @return the {@link IViewElement}.
   */
  public IViewElement getViewElement();
  
  /**
   * Set the {@link IViewElement}.
   * 
   * @param ve the {@link IViewElement}.
   */
  public void setViewElement(IViewElement ve);
  
  /**
   * Should not be used, except you exactly know what you are doing. Use
   * {@link #getViewElement()} for accessing the {@link IViewElement}.
   * 
   * @return the view element for this class
   */
  public IViewElement createViewElement();
}
