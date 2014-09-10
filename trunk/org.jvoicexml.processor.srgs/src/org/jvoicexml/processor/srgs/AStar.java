package org.jvoicexml.processor.srgs;

import java.util.Collection;
import java.util.PriorityQueue;
import java.util.Set;

public class AStar {
    private PriorityQueue<AStarNode> openList;
    private Set<AStarNode> closedList;

    public AStar() {
        openList = new PriorityQueue<AStarNode>();
        closedList = new java.util.HashSet<AStarNode>();
    }

    public void search(final AStarNode startNode, final AStarNode endNode) {
        openList.offer(startNode);

        do {
            AStarNode current = openList.poll();
            if (current.costsTo(endNode) == 0.0d) {
                // Found
                return;
            }
            closedList.add(current);
            expand(current);
        } while (!openList.isEmpty());
    }

    private void expand(AStarNode node) {
        Collection<AStarNode> successors = node.getSuccessors();
        for (AStarNode successor : successors) {
            if (closedList.contains(successor)) {
                continue;
            }
            double tentativeG = node.getCostsFromStart() + node.costsTo(successor);
            if (openList.contains(successor)
                    && tentativeG >= successor.getCostsFromStart()) {
                continue;
            }
            successor.setPredecessor(node);
            successor.setCostsFromStart(tentativeG);
            double f = tentativeG + successor.getHeuristicCosts();
            successor.setPriority(f);
            
        }
    }
}
