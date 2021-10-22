package map;

import java.util.Comparator;

/**
 * A helper class for comparing the costs between two nodes
 */
public class NodeComparator implements Comparator<Node> {

    @Override
    public int compare(Node o1, Node o2) {
        double c1 = o1.getCost();
        double c2 = o2.getCost();
        if (c1 < c2) return -1;
        if (c1 > c2) return 1;
        return 0;
    }
}
