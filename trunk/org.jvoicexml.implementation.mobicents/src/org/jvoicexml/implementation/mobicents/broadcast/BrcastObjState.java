/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jvoicexml.implementation.mobicents.broadcast;

/**
 *
 * @author Shadowman
 */
public enum BrcastObjState 
{
     /**
     * broad cast object status
     */
    INITIAL,
    QUEUE,
    SELECT,
    INVITE,
    COMPLETE,
    FAILE_MEDIASERVER,
    UNKNOWN;
    public int getValue()
    {
        if(this == INITIAL) return 0;
        else if(this == QUEUE) return 1;        
        else if(this == SELECT) return 2;        
        else if(this == COMPLETE) return 3;                        
        else return -1;
    }
    public BrcastObjState getValue(int input)
    {
        if(this == INITIAL) return INITIAL;
        else if(this == QUEUE) return QUEUE;        
        else if(this == SELECT) return SELECT;        
        else if(this == COMPLETE) return COMPLETE;                        
        else return UNKNOWN;
    }
    
    public static BrcastObjState fromString(String value) {
    if (value != null) {
      for (BrcastObjState b : BrcastObjState.values()) {
        if (value.equalsIgnoreCase(b.name())) {
          return b;
        }
      }
    }
    return null;
  }
    
    public static void main(final String[] args) {
        
        System.out.println("BrcastObjState.fromString(\"0\"):" + BrcastObjState.fromString("0"));
        System.out.println("BrcastObjState.fromString(\"0\"):" + BrcastObjState.fromString("INITIAL"));
        System.out.println("BrcastObjState.fromString(\"0\"):" + BrcastObjState.fromString("INITIAL").name());

    }
   
}
