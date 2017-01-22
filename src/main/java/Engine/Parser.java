package Engine;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class Parser {
    public static class GraphNode {
        int data;
        List<GraphNode> edges;

        public GraphNode(int x) {
            this.data = x;
            edges = new LinkedList();
        }

        public void addEdge(GraphNode to) {
            edges.add(to);
        }

        static HashSet<GraphNode> visitedNode = new HashSet<>();

        public static boolean hasPath(GraphNode start, GraphNode end) {
            List<GraphNode> edges = start.edges;

            for (GraphNode gn : edges) {
                if (visitedNode.contains(gn))
                    continue;
                if (gn == end)
                    return true;
                visitedNode.add(gn);

                return hasPath(gn, end);
            }

            return false;
        }

        public static void main(String[] args) {
            GraphNode n1 = new GraphNode(10);
            GraphNode n2 = new GraphNode(15);
            GraphNode n3 = new GraphNode(20);
            GraphNode n4 = new GraphNode(25);
            GraphNode n5 = new GraphNode(30);
            GraphNode n6 = new GraphNode(35);

            n1.addEdge(n2);
            n2.addEdge(n3);
            n3.addEdge(n4);
            n4.addEdge(n5);
            n6.addEdge(n5);
            System.out.println(hasPath(n1,n5));
            System.out.println(hasPath(n1,n6));

            ArrayList<String> result = permutations("abc");

            for (String str : result)
                System.out.println(str);

            stringCombination("ab", 2, "a");
        }


    }

    public static ArrayList<String> permutations(String str) {
        return per("", str, new ArrayList<String>());
    }

    public static ArrayList<String> per(String prefix, String str, ArrayList<String> ls) {
        if (str.isEmpty()) {
            ls.add(prefix + str);
        } else {
            for (int i=0; i<str.length(); i++) {
                per(prefix+str.charAt(i), str.substring(0, i)+str.substring(i+1, str.length()), ls);
            }
        }

        return ls;
    }

    public static void stringCombination(String str, int n, String start) {
        if(start.length() >= n) {
            System.out.println(start);
        } else {
            for (char x : str.toCharArray()) {
                stringCombination(str, n, start + x);
            }
        }
    }
}
