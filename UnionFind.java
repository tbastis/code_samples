// Written by Thomas Bastis
// This code implements the Union-Find data structure. The main function uses it to calculate the
// minimum number of edges needed to fully connect a given undirected graph.

import java.util.Scanner;

class Main {

    /** An instance of a Union-Find data structure. */
    static class UnionFind {

        /** Representation of all the trees in the graph. The number at index i is the number of the
         * parent node of node i (indicating they are connected), or is i if i is the root of its
         * own tree. */
        private int[] trees;

        /** The number of disjoint trees in the graph */
        private int disjoint;

        /** Constructor: an instance with v initial nodes, hence v intial disjoint trees. */
        public UnionFind(int v) {
            trees= new int[v];
            disjoint= v;
            for (int i= 0; i < v; i++ ) {
                trees[i]= i;
            }
        }

        /** Find the root of the tree containing node v */
        public int find(int v) {
            while (v != trees[v]) { // v is not the root of v's tree
                v= trees[v]; // v becomes the parent node of v
            }
            return v;
        }

        /** Sets the root of v1 to be the new parent node of the root of v2 (or does nothing if v1
         * and v2 have the same root). */
        public void union(int v1, int v2) {
            int V1root= find(v1);
            int V2root= find(v2);
            if (V1root == V2root) return;
            trees[V2root]= V1root;
            disjoint-- ;
        }

        /** returns the number of disjoint trees in the graph. */
        public int disjoint() {
            return disjoint;
        }
    }

    /** Takes as input to stdin a sequence of integers representing a valid undirected graph, and
     * prints to stdout the minimum number of edges required to be added to connect all nodes. <br>
     * Precondition: The first line of input should be two numbers providing the number of nodes and
     * edges in the graph, respectively. Then, there is a number of next lines of input equal to the
     * number of edges, which each contain two numbers x y, which signifies an edge from node x to
     * node y. */
    public static void main(String args[]) throws java.io.IOException {
        Scanner scan= new Scanner(System.in);
        UnionFind graph= new UnionFind(scan.nextInt()); // initialize union-find with number of
                                                        // nodes
        int edges= scan.nextInt();
        for (int i= 0; i < edges; i++ ) { // union all nodes who share an edge in the graph
            graph.union(scan.nextInt(), scan.nextInt());
        }
        scan.close();
        System.out.println(graph.disjoint() - 1); // The answer is the number of remaining
                                                  // disjoint components minus 1, because
                                                  // we can connect one of the components to
                                                  // all of the other ones using 1 edge each 
                                                  // between them.
    }
}
