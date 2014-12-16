/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006-2014 JVoiceXML group - http://jvoicexml.sourceforge.net
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Library General Public
 *  License as published by the Free Software Foundation; either
 *  version 2 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Library General Public License for more details.
 *
 *  You should have received a copy of the GNU Library General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package org.jvoicexml.implementation.talkinghead.view;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JPanel;

/**
 * Panel, with centered and scaled Background Image.
 * 
 * @author Matthias Mettel
 * @author Markus Ermuth
 * @author Alex Krause
 * 
 * @version $LastChangedRevision$
 * @since 0.7.3
 */
public class JImagePanel extends JPanel {
  /**
   * Serial version of the component.
   */
  private static final long serialVersionUID = 5023121431163558178L;
  
  /**
   * Background image of the panel.
   */
  private Image backgroundImage;

  /**
   * Constructor, which sets default values of the attribtues.
   */
  public JImagePanel() {
    super();

    backgroundImage = null;
  }

  /**
   * Sets the Background Image of the panel and repaints it.
   * 
   * @param img Background Image of the panel
   */
  public final void setImage(final Image img) {
    backgroundImage = img;
    repaint();
  }

  /**
   * Paints the Component inclusive the Background Image.
   */
  @Override
  protected final void paintComponent(final Graphics g) {
    super.paintComponent(g);
    if (backgroundImage != null) {
      //Get Dimensions
      Dimension panelDimension = getSize();
      Dimension imageDimension =
          new Dimension(backgroundImage.getWidth(null),
                        backgroundImage.getHeight(null));
      
      //Calculate x
      double resWidth =
          panelDimension.getWidth() / imageDimension.getWidth();
      double resHeight =
          panelDimension.getHeight() / imageDimension.getHeight();
      
      //Calculate Sizes
      int targetWidth = 0;
      int targetHeight = 0;
      
      if (resWidth < resHeight) {
        targetWidth = (int) panelDimension.getWidth();
        targetHeight = (int) (resWidth *  imageDimension.getHeight());       
      } else {
        targetWidth = (int) (resHeight * imageDimension.getWidth());
        targetHeight = (int) panelDimension.getHeight();
      }
      
      //Calculate Position
      int targetX = (int) (panelDimension.getWidth()
          / 2.0 - targetWidth / 2.0);
      int targetY = (int) (panelDimension.getHeight()
          / 2.0 - targetHeight / 2.0);
      
      //Draw
      g.drawImage(backgroundImage,
                  targetX,
                  targetY,
                  targetWidth,
                  targetHeight,
                  null);

    }
  }
}
