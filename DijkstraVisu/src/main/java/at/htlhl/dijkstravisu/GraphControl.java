package at.htlhl.dijkstravisu;

import com.brunomnsilva.smartgraph.graph.Edge;
import com.brunomnsilva.smartgraph.graph.Graph;
import com.brunomnsilva.smartgraph.graph.GraphEdgeList;
import com.brunomnsilva.smartgraph.graph.Vertex;

import java.util.*;


public class GraphControl {

    private Graph<VertexData, EdgeData> graph;
    private VertexData startVertex;
    private VertexData endVertex;

    public GraphControl() {
        graph = new GraphEdgeList<>();
        buildGraph();
    }

    private void buildGraph() {
        VertexData seattle = createCityAndInsert("Seattle");
        VertexData sanFrancisco = createCityAndInsert("San Francisco");
        VertexData losAngeles = createCityAndInsert("Los Angeles");
        VertexData riverside = createCityAndInsert("Riverside");
        VertexData phoenix = createCityAndInsert("Phoenix");
        VertexData chicago = createCityAndInsert("Chicago");
        VertexData boston = createCityAndInsert("Boston");
        VertexData newYork = createCityAndInsert("New York");
        VertexData atlanta = createCityAndInsert("Atlanta");
        VertexData miami = createCityAndInsert("Miami");
        VertexData dallas = createCityAndInsert("Dallas");
        VertexData houston = createCityAndInsert("Houston");
        VertexData detroit = createCityAndInsert("Detroit");
        VertexData philadelphia = createCityAndInsert("Philadelphia");
        VertexData washington = createCityAndInsert("Washington");
        graph.insertEdge(seattle, chicago, new EdgeData(1737));
        graph.insertEdge(seattle, sanFrancisco, new EdgeData(678));
        graph.insertEdge(sanFrancisco, riverside, new EdgeData(386));
        graph.insertEdge(sanFrancisco, losAngeles, new EdgeData(348));
        graph.insertEdge(losAngeles, riverside, new EdgeData(50));
        graph.insertEdge(losAngeles, phoenix, new EdgeData(357));
        graph.insertEdge(riverside, phoenix, new EdgeData(307));
        graph.insertEdge(riverside, chicago, new EdgeData(1704));
        graph.insertEdge(phoenix, dallas, new EdgeData(887));
        graph.insertEdge(phoenix, houston, new EdgeData(1015));
        graph.insertEdge(dallas, chicago, new EdgeData(805));
        graph.insertEdge(dallas, atlanta, new EdgeData(721));
        graph.insertEdge(dallas, houston, new EdgeData(225));
        graph.insertEdge(houston, atlanta, new EdgeData(702));
        graph.insertEdge(houston, miami, new EdgeData(968));
        graph.insertEdge(atlanta, chicago, new EdgeData(588));
        graph.insertEdge(atlanta, washington, new EdgeData(543));
        graph.insertEdge(atlanta, miami, new EdgeData(604));
        graph.insertEdge(miami, washington, new EdgeData(923));
        graph.insertEdge(chicago, detroit, new EdgeData(238));
        graph.insertEdge(detroit, boston, new EdgeData(613));
        graph.insertEdge(detroit, washington, new EdgeData(396));
        graph.insertEdge(detroit, newYork, new EdgeData(482));
        graph.insertEdge(boston, newYork, new EdgeData(190));
        graph.insertEdge(newYork, philadelphia, new EdgeData(81));
        graph.insertEdge(philadelphia, washington, new EdgeData(123));
    }

    private VertexData createCityAndInsert(String name) {
        VertexData city = new VertexData(name);
        graph.insertVertex(city);
        return city;
    }

    public List<VertexData> shortestPath(VertexData startVertex, VertexData endVertex) {

        Map<Vertex<VertexData>, Integer> distances = new HashMap<>();
        Map<Vertex<VertexData>, Vertex<VertexData>> predecessors = new HashMap<>();

        ArrayList<Vertex<VertexData>> unvisited = new ArrayList<>();

        // Distance und alle Vertexes in unprocessed stecken
        for(Vertex<VertexData> vertex: graph.vertices()) {
            distances.put(vertex, Integer.MAX_VALUE);
            predecessors.put(vertex, null);
            unvisited.add(vertex);
        }

        Vertex<VertexData> start = findVertex(startVertex);
        Vertex<VertexData> end = findVertex(endVertex);

        // Start Vertex distance auf 0 setzen
        distances.put(start, 0);

        while (!unvisited.isEmpty()) {
            Vertex<VertexData> currentVertex = getMinVertexDistance(unvisited, distances);

            // Soll die current von den unvisited entfernen
            unvisited.remove(currentVertex);

            // Wenn Ende erreicht wurde
            if (currentVertex.equals(end)) {
                break;
            }

            for(Edge<EdgeData, VertexData> edge: graph.incidentEdges(currentVertex)){
                Vertex<VertexData> neighor = graph.opposite(currentVertex, edge);

                // überspringen von beareiteten Knoten
                if (distances.containsKey(neighor)) {
                    continue;
                }

                int newDistance = distances.get(currentVertex) + edge.element().getDistance();
                if (newDistance < distances.get(neighor)) {
                    distances.put(neighor, newDistance);
                    predecessors.put(neighor, currentVertex);
                }
            }
        }

        List<VertexData> path = new ArrayList<>();
        for (Vertex<VertexData> i = end; i != null ; i = predecessors.get(i)) {
            path.add(i.element());
        }

        Collections.reverse(path);

        if (path.isEmpty() || !path.get(0).equals(start)) {
            return Collections.emptyList();
        }else {
            return path;
        }
    }

    private Vertex<VertexData> findVertex(VertexData vertex) {
        return graph.vertices().stream().filter(v -> v.equals(vertex)).findFirst().get();
    }

    private Vertex<VertexData> getMinVertexDistance(List<Vertex<VertexData>> vertices, Map<Vertex<VertexData>, Integer> distances) {
        Vertex<VertexData> minVertex = null;
        int minDistance = Integer.MAX_VALUE;

        for (Vertex<VertexData> vertex: vertices) {
            int distance = distances.get(vertex);
            if (distance < minDistance) {
                minDistance = distance;
                minVertex = vertex;
            }
        }
        return minVertex;
    }

    public Graph<VertexData, EdgeData> getGraph() {
        return graph;
    }
}
