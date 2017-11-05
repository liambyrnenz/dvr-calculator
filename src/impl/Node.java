package impl;

import java.util.*;
import java.util.stream.Collectors;

/**
 * NWEN 243 Lab 4
 * Liam Byrne (byrneliam2)
 * 300338518
 *
 * @author aliahmed (template)
 */
public class Node {

    private char key;                                  // the node key
    private int x, y;                                  // the positions to draw the node
    private HashMap<Character, Integer> neighbours;    // the list of neighbours
    private RoutingTable table;

    /**
     * @param n: the node key
     * @param xp: the x position
     * @param yp: the y position
     */
    public Node(char n, int xp, int yp) {
        this.key = n;
        this.x = xp;
        this.y = yp;
        this.neighbours = new HashMap<>();
    }

    /**
     * Initialises the routing table.
     */
    public void setupRoutingTable(List<Node> nodes) {
        this.table = new RoutingTable(nodes.size() - 1, neighbours.size());
        char[] dests = new char[nodes.size() - 1]; int destCount = 0;
        List<Character> neighbourList = neighbours.keySet().stream().sorted().collect(Collectors.toList());
        // for each destination
        for (Node dest : nodes) {
            if (dest == this) continue;
            int[] row = new int[neighbours.size()];
            int count = 0;
            // get the neighbours of this node and add link values
            // iff this node is a direct neighbour of it
            // Integer.MAX_VALUE if no direct link
            for (Character c : neighbourList) {
                Node route = new DistanceVectorRouter.DVUtils(nodes).find(c);
                if (route == dest)
                    row[count++] = neighbours.get(route.getKey());
                else row[count++] = Integer.MAX_VALUE;
            }
            table.addRow(row);
            dests[destCount++] = dest.getKey();
        }
        // simply set the destinations with the previously built array
        table.setDestinations(dests);
        // manually build a neighbour array to avoid type casting errors
        char[] neighs = new char[nodes.size()];
        for (int i = 0; i < neighbourList.size(); i++) neighs[i] = neighbourList.get(i);
        table.setNeighbours(neighs);
    }

    /**
     * Updates the routing table in this node by analysing the given one and replacing
     * any values that are now obsolete.
     */
    public void updateRoutingTable(Node fromNode, int fromDist, RoutingTable fromTable) {
        // for each destination in the given table
        for (int d = 0; d < fromTable.destinationSize(); d++) {
            // for each neighbour in this row
            for (int n = 0; n < fromTable.neighbourSize(); n++) {
                // skip unfilled table cells
                if (fromTable.getValue(d, n) == Integer.MAX_VALUE) continue;
                // get the value that exists in the cell
                int val = fromTable.getValue(d, n);
                char dest = fromTable.getDestinationAt(d);
                // overwrite the value present iff the new value is smaller
                table.setValueAt(dest, fromNode.getKey(),
                        Math.min(table.getValueAt(dest, fromNode.getKey()),
                                fromDist + val));
            }
        }
    }

    /**
     * Simulate the addition of a new link by adding the new neighbour and re-initialising
     * the node. This does not affect the node as greatly as it may seem, since the initialising
     * process basically sets up the table again (a requirement since we have a new node.)
     * @param key character key of the node
     * @param cost cost of the direct link
     * @param nodes parameter for re-initialising
     */
    public void simulateNewLink(char key, int cost, List<Node> nodes) {
        addNeighbour(key, cost);
        setupRoutingTable(nodes);
    }

    /**
     * @param key:the neighbour node key
     * @param cost: the cost of the link
     */
    public void addNeighbour(char key, int cost) {
        this.neighbours.put(key, cost);
    }

    /* =========================================================================================== */

    public char getKey() {
        return key;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public HashMap<Character, Integer> getNeighbours() {
        return neighbours;
    }

    public RoutingTable getTable() {
        return table;
    }
}
