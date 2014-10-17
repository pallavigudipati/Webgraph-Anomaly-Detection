import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Graph {

    private HashMap<Integer, List<Integer>> graphList = new HashMap<Integer, List<Integer>>();

    static public void main(String arg[]) {
        Graph graph = new Graph();
        graph.loadGraph("dataset-2/web-Stanford.txt");
    }

    public void loadGraph(String fileName) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            String line = null;
            int i = 0;
            while ((line = reader.readLine()) != null) {
                System.out.println(i);
                i++;
                String[] parts = line.split("\t");
                int fromVertex = Integer.parseInt(parts[0]);
                int toVertex = Integer.parseInt(parts[1]);
                if (graphList.containsKey(fromVertex)) {
                    graphList.get(fromVertex).add(toVertex);
                } else {
                    List<Integer> toVertices = new ArrayList<Integer>();
                    toVertices.add(toVertex);
                    graphList.put(fromVertex, toVertices);
                }
            }
            reader.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
