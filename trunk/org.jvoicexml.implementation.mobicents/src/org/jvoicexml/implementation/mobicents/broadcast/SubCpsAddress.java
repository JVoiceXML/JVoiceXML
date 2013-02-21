/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jvoicexml.implementation.mobicents.broadcast;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Shadowman
 */
 public class SubCpsAddress
    {
        public List<String> addresses= new ArrayList();
        public String toString()
        {
            final StringBuilder str = new StringBuilder();
            str.append(SubCpsAddress.class.getCanonicalName());
            str.append('[');
            str.append(addresses);
            str.append(']');
            return str.toString();
        }
    }
