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

package org.jvoicexml.talkinghead.avatar;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import javax.imageio.ImageIO;

import org.jdom2.Element;
import org.jvoicexml.talkinghead.animations.model.Animation;

/**
 * Model of the avatar with images and animation definitions.
 * 
 * @author Matthias Mettel
 * @author Markus Ermuth
 * @author Alex Krause
 *
 * @version $LastChangedRevision$
 * @since 0.7.3
 */
public class Avatar {
    /**
     * Images of the Avatar.
     */
    private LinkedList<Image> animationImages;

    /**
     * Animation Definitions of the Avatar.
     */
    private HashMap<String, Animation> animations;

    /**
     * Constructor to generate an empty Avatar.
     */
    public Avatar() {
        // Generate Animations & Image List
        animations = new HashMap<String, Animation>();
        animationImages = new LinkedList<Image>();
    }

    /**
     * Constructor to load avatar definition from given XMLFile.
     * 
     * @param avatarXMLElement
     *            XML-Element with avatar informations
     * @throws IOException
     *             Failed to read data from the xml-element
     */
    public Avatar(final Element avatarXMLElement) throws IOException {
        // Generate Empty Avatar
        this();

        // Load XML-File
        load(avatarXMLElement);
    }

    /**
     * Method to read Avatar Definition from given XML-File.
     * 
     * @param avatarXMLElement
     *            XML-Element with avatar informations
     * @throws IOException
     *             Failed to read data from the xml-element
     */
    public final void load(final Element avatarXMLElement) throws IOException {
        // Check Parameter
        if (avatarXMLElement == null) {
            throw new IllegalArgumentException("Avatar element cannot be null");
        }

        // Read Images
        Element imageElement = avatarXMLElement.getChild("images");
        List<Element> images = imageElement.getChildren();
        for (Element image : images) {
            String imgName = image.getAttributeValue("src");

            animationImages.add(ImageIO.read(new File(imgName)));
        }

        // Read Animations
        List<Element> animationElements = avatarXMLElement.getChild(
                "animations").getChildren("animation");
        for (Element animation : animationElements) {
            // Generate Animation Set
            Animation anim = new Animation();

            // Read animation
            anim.read(animation);

            // Add Animation
            animations.put(anim.getID(), anim);
        }
    }

    /**
     * get access to the Avatar images.
     * 
     * @param idx
     *            index of the Image
     * @return the specific image
     */
    public final Image getImage(final int idx) {
        return animationImages.get(idx);
    }

    /**
     * get access to the Avatar animations.
     * 
     * @param id
     *            of the Animation
     * @return the specific Animation
     */
    public final Animation getAnimation(final String id) {
        return animations.get(id);
    }

    /**
     * get access to the Avatar animations to iterate over all existing
     * Animations.
     * 
     * @return Iterator of the Animation HashMap
     */
    public final Iterator<Entry<String, Animation>> getAnimationsIterator() {
        return animations.entrySet().iterator();
    }
}
