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
package de.monticore.genericgraphics.controller.commands.connections;

import org.eclipse.gef.commands.Command;

import de.monticore.genericgraphics.model.graphics.IEdgeViewElement;
import de.monticore.genericgraphics.view.figures.connections.MCBendpoint;


/**
 * {@link Command} for deleting bendpoints in {@link IEdgeViewElement
 * IEdgeViewElements}.
 * 
 * @author Tim Enger
 */
public class ConnectionDeleteBendpointCommand extends Command {
  
  /** Link that contains the bendpoint. */
  private IEdgeViewElement ve;
  
  /** Index where the bendpoint is located in the link's bendpoin list. */
  private int index;
  
  /** Point in the diagram where the bendpoint is located. */
  private MCBendpoint oldBP;
  
  /**
   * Constructor
   * 
   * @param ve The IConnectionViewElement from which the bendpoint is removed
   * @param index The index from which the bendpoint is removed
   */
  public ConnectionDeleteBendpointCommand(IEdgeViewElement ve, int index) {
    this.ve = ve;
    this.index = index;
  }
  
  /**
   * Only execute if IConnectionViewElement constraints is not null and index is
   * valid.
   */
  @Override
  public boolean canExecute() {
    return (ve != null) && (ve.getConstraints().size() > index);
  }
  
  /**
   * Remove the bendpoint from the IConnectionViewElement.
   */
  @Override
  public void execute() {
    oldBP = ve.getConstraints().get(index);
    ve.removeConstraint(index);
    ve.notifyObservers();
  }
  
  /**
   * Reinsert the bendpoint in the IConnectionViewElement.
   */
  @Override
  public void undo() {
    ve.addConstraint(index, oldBP);
    ve.notifyObservers();
  }
  
}
