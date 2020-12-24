// Written by Thomas Bastis
// This code performs a DFS on a graph, and prints out the preorder ordering
// of the nodes in the graph, as well as the type of each edge.
// "type" referring to one of tree, back, forward or cross.

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;

class Main {

    /** Takes as input to stdin a sequence of integers representing a valid directed graph, and
     * prints to stdout the preorder ordering of the nodes of that graph, as well as the type of
     * each edge in the graph. <br>
     * Precondition: The first line of input is two numbers which indicate the number of nodes and 
     * edges, respectively. Then, there is a number of next lines of input equal to the number of 
     * edges, which each contain two numbers x y, which signifies an edge from node x to node y. */
    public static void main(String args[]) throws java.io.IOException {
        Scanner scan= new Scanner(System.in);
        Node[] nodes= new Node[scan.nextInt()];
        Edge[] edges= new Edge[scan.nextInt()];
        for (int i= 0; i < nodes.length; i++ ) {
            nodes[i]= new Node(i);
        }

        for (int i= 0; i < edges.length; i++ ) {
            Node n= nodes[scan.nextInt()];
            Edge e= new Edge(n, nodes[scan.nextInt()]);
            edges[i]= e;
            n.addNeighbor(e);
        }
        scan.close();

        int[] ordering= dfs(nodes, edges.length);

        for (int i= 0; i < nodes.length - 1; i++ ) { // below could be optimized with StringBuilder
            System.out.print(ordering[i] + " ");
        }
        System.out.print(ordering[nodes.length - 1] + "\n");

        for (int i= 0; i < edges.length; i++ ) {
            Edge e= edges[i];
            System.out.print(e.num1.number + " " + e.num2.number + " " + e.type + "\n");
        }

    }

    /** Performs a depth first search on the given array of Nodes, while also calculating the types
     * of each Edge in the graph. */
    public static int[] dfs(Node[] nodes, int numEdges) {
        int preorder= 1, postorder= 1;
        int minUnvisited= 0; // The number of the minimum unvisited node
        int[] result= new int[nodes.length];

        Stack<Edge> stack= new Stack<>();
        stack.add(new Edge(null, nodes[0])); // Add a placeholder edge to begin the dfs

        while (!stack.isEmpty()) {
            Edge e= stack.peek();
            Node n1= e.num1;
            Node n2= e.num2;

            if (e.traversed) { // If we are returning from having already traversed down this edge
                if (e.type == 't') {
                    n2.postorder= postorder; // Give nodes from tree edges their post ordering.
                    postorder++ ;
                }
                stack.pop();
                if (stack.isEmpty() && minUnvisited < nodes.length) { // if the DFS isn't complete
                    stack.add(new Edge(null, nodes[minUnvisited])); // add the minUnvisited value
                }
                continue;
            }

            if (!n2.visited) { // if not visited
                n2.visited= true; // mark as visited

                if (n2.number == minUnvisited) { // update the minimum unvisited node if needed.
                    minUnvisited++ ;
                    // find the smallest unvisited node
                    while (minUnvisited < nodes.length && nodes[minUnvisited].visited) {
                        minUnvisited++ ;
                    }
                }
                result[preorder - 1]= n2.number; // add to list of discovered nodes
                n2.preorder= preorder; // give a preorder number
                preorder++ ;
                e.type= 't'; // placeholder edges marked also, so their nodes get postorder numbers

                List<Edge> neighbors= n2.getNeighbors();
                for (int i= neighbors.size() - 1; i >= 0; i-- ) { // iterate in reverse so stack
                                                                  // processes in correct order
                    Edge neighbor= neighbors.get(i);
                    stack.add(neighbor); // add all of the edges leaving n.
                }

            } else { // we've already seen this node
                if (n2.preorder > n1.preorder) { // This node is 'forward' in the graph
                    e.type= 'f';
                } else {
                    if (n2.postorder > 0) { // This node is ''across' the graph
                        e.type= 'c';
                    } else { // This node is 'backwards' in the graph
                        e.type= 'b';
                    }
                }
            }
            e.traversed= true; // Remember that we've already processed this edge
        }
        return result;
    }

    /** A represention of a node in a graph */
    static class Node {
        /** The integer identifier of this node */
        int number;
        /** The preorder traversal number of this node, as found during dfs */
        int preorder;
        /** The postorder traversal number of this node, as found during dfs */
        int postorder;
        /** = this node has been visited during the execution of dfs */
        boolean visited;
        /** All nodes connected to this node via a directed edge from this node to that node. */
        ArrayList<Edge> neighbors;

        /** An instance of a Node */
        protected Node(int number) {
            this.number= number;
            neighbors= new ArrayList<>();
        }

        /** returns the neighbors of this node */
        public ArrayList<Edge> getNeighbors() {
            return neighbors;
        }

        /** Adds neighbor as a new neighbor of this node */
        public void addNeighbor(Edge neighbor) {
            neighbors.add(neighbor);
        }
    }

    /** A representation of an edge in a directed graph. */
    static class Edge {
        /** The tail of the edge */
        Node num1;
        /** The head of the edge */
        Node num2;
        /** The type of the edge, if known */
        Character type;
        /** = this edge and all of its decendents been travered during the execution of dfs */
        boolean traversed;

        /** An instance of an edge. */
        protected Edge(Node num1, Node num2) {
            this.num1= num1;
            this.num2= num2;
        }
    }
}
