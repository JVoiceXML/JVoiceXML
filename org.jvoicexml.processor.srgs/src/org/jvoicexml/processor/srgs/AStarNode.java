package org.jvoicexml.processor.srgs;

import java.util.Collection;

public interface AStarNode extends Comparable<AStarNode> {
    void setPredecessor(AStarNode predecessor);

    Collection<AStarNode> getSuccessors();
    
    double getCostsFromStart();
    
    void setCostsFromStart(double costs);
    
    double costsTo(AStarNode successor);
    
    double getHeuristicCosts();
    
    public void setPriority(double priority);
    
    double getPriority();
}
