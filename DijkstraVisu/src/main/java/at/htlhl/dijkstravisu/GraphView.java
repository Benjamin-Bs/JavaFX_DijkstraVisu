package at.htlhl.dijkstravisu;

import com.brunomnsilva.smartgraph.containers.ContentZoomScrollPane;
import com.brunomnsilva.smartgraph.graph.Vertex;
import com.brunomnsilva.smartgraph.graphview.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.layout.BorderPane;
import javafx.util.Duration;

import java.util.List;
import java.util.function.Consumer;

public class GraphView extends BorderPane {

    private SmartGraphPanel<VertexData, EdgeData> smartGraphPanel;
    private ContentZoomScrollPane contentZoomScrollPane;
    private GraphControl graphControl;
    private VertexData startVertex;
    private VertexData endVertex;

    // Koordinaten
    private double lastContextScreenX;
    private double lastContextScreenY;

    private SmartGraphVertexNode<VertexData> lastSelectedVertex;

    public GraphView(GraphControl graphControl) {
        super();
        this.graphControl = graphControl;

        SmartPlacementStrategy strategy = new SmartCircularSortedPlacementStrategy();
        smartGraphPanel = new SmartGraphPanel<>(graphControl.getGraph(), strategy);
        smartGraphPanel.setAutomaticLayout(true);

        contentZoomScrollPane = new ContentZoomScrollPane(smartGraphPanel);

        //borderPane = new BorderPane();
        setCenter(contentZoomScrollPane);


        // Create "Test"-Button
        Button testButton = new Button("Test");
        testButton.setOnAction(new TestEventHandler());

        // Create "Dijkstra"-Button
        // Soll dann die animation von einem knoten zum anderen zeigen und kürzesten weg ausrechnen
        Button dijkstraButton = new Button("Dijkstra");
        dijkstraButton.setOnAction(new DijkstraEventHandler());

        Button clearButton = new Button("Clear");
        clearButton.setOnAction(new ClearEventHandler());

        // Create ToolBar
        ToolBar toolBar = new ToolBar(testButton, dijkstraButton, clearButton);
        setTop(toolBar);
        smartGraphPanel.setVertexDoubleClickAction(graphVertex -> {
            graphVertex.setStyleClass("htlVertex");
        });

        // Enable Context on Vertex
        ContextMenu contextMenu = buildContextMenu();


        smartGraphPanel.setOnContextMenuRequested(new EventHandler<ContextMenuEvent>() {
            @Override
            public void handle(ContextMenuEvent event) {
                System.out.println("ContextMenuEvent: " + event);

                lastContextScreenX = event.getScreenX();
                lastContextScreenY = event.getScreenY();

//                System.out.println("lastContextScreenX: " + lastContextScreenX);
//                System.out.println("lastContextScreenY: " + lastContextScreenY);

                SmartGraphVertexNode<VertexData> foundVertex = findVertex(event.getX(), event.getY());
                lastSelectedVertex = foundVertex;

                if (foundVertex != null) {
                    contextMenu.show(foundVertex, event.getScreenX(), event.getScreenY());
                }
            }
        });
    }

    /**
     * IMPORTANT!
     * Should be called after scene is displayed, so we can initialize
     * the graph visualisation
     */
    public void initAfterVisible() {
        smartGraphPanel.init();
    }

    private ContextMenu buildContextMenu() {
        ContextMenu contextMenu = new ContextMenu();

        MenuItem selectStart = new MenuItem("Start Vertex");
        selectStart.setOnAction(event -> {
            if (lastSelectedVertex != null) {
                if (startVertex != null) {
                    SmartStylableNode oldStartNode = smartGraphPanel.getStylableVertex(graphControl.findVertex(startVertex));
                    if (oldStartNode != null) {
                        oldStartNode.setStyleClass("vertex");
                    }
                }
                startVertex = lastSelectedVertex.getUnderlyingVertex().element();
                System.out.println("startVertex: " + startVertex);

                lastSelectedVertex.setStyleClass("startVertex");

            } else {
                System.out.println("no startVertex selected");
            }
        });

        MenuItem selectEnd = new MenuItem("Select as End");
        selectEnd.setOnAction(event -> {
            if (lastSelectedVertex != null) {

                if (endVertex != null) {
                    SmartStylableNode oldEndNode = smartGraphPanel.getStylableVertex(graphControl.findVertex(endVertex));
                    if (oldEndNode != null) {
                        oldEndNode.setStyleClass("vertex");
                    }
                }

                endVertex = lastSelectedVertex.getUnderlyingVertex().element();
                System.out.println("endVertex: " + endVertex);

                lastSelectedVertex.setStyleClass("endVertex");

            } else {
                System.out.println("No endVertex selected");
            }
        });
        contextMenu.getItems().add(selectStart);
        contextMenu.getItems().add(selectEnd);
        return contextMenu;
    }

    private SmartGraphVertexNode<VertexData> findVertex(double x, double y) {
        for (Vertex<VertexData> v : graphControl.getGraph().vertices()) {
            SmartStylableNode smartStylableNode = smartGraphPanel.getStylableVertex(v);
            if (smartStylableNode instanceof SmartGraphVertexNode) {
                SmartGraphVertexNode<VertexData> smartGraphVertexNode = (SmartGraphVertexNode) smartStylableNode;
                if (smartGraphVertexNode.getBoundsInParent().contains(x, y)) {
                    return smartGraphVertexNode;
                }
            }
        }
        return null;
    }

    private class TestEventHandler implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent actionEvent) {
            System.out.println("Test clicked ... " + actionEvent.getSource());
            //smartGraphPanel.getStylableVertex().setStyleClass("htlVertex");
        }
    }

    private class DijkstraEventHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent actionEvent) {

            System.out.println("startVertex: " + startVertex);
            System.out.println("endVertex: " + endVertex);

            if (startVertex == null || endVertex == null) {
                System.out.println("startVertex or endVertex is null");

                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Error");
                alert.setHeaderText("Start or End Vertex Missing");
                alert.setContentText("Please select both a start vertex and an end vertex.");
                alert.showAndWait();

                return;
            }


            List<VertexData> path = graphControl.shortestPath(startVertex, endVertex);

            if (path.isEmpty()) {
                System.out.println("No path between " + startVertex + " and " + endVertex + " found");

                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Path not found");
                alert.setHeaderText("No Path found");
                alert.setContentText("There is no valid path between " + startVertex + " and " + endVertex + ".");
                alert.showAndWait();

            } else {
                System.out.println("shortestPath: " + path);

                double totalDistance = 0;
                for (int i = 0; i < path.size() - 1; i++) {
                    VertexData current = path.get(i);
                    VertexData next = path.get(i + 1);
                    // Hole die Kante und addiere deren Distanz
                    EdgeData edge = graphControl.getEdge(current, next);
                    if (edge != null) {
                        totalDistance += edge.getDistance(); // Kanten-Distanz zu den Gesamtkosten addieren
                    }
                }
                System.out.println("Total Distance: " + totalDistance + " km");

                // Format path as a string
                StringBuilder pathString = new StringBuilder();
                for (VertexData vertex : path) {
                    pathString.append(vertex.getName()).append(" -> ");
                }
                // Remove the last " -> "
                if (pathString.length() > 4) {
                    pathString.setLength(pathString.length() - 4);
                }

                animatePath(path);

                // Alert for shortest path found
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Shortest Path Found");
                alert.setHeaderText("Shortest Path from " + startVertex.getName() + " to " + endVertex.getName());
                alert.setContentText("Path: " + pathString.toString() + "\nTotal Distance: " + totalDistance + " km\n\nClick OK to continue.");
                alert.showAndWait();

            }

        }

        private void animatePath(List<VertexData> path) {
            Timeline timeline = new Timeline();
            timeline.setCycleCount(1);

            int delay = 0;
            for (int i = 0; i < path.size(); i++) {
                VertexData current = path.get(i);

                // Highlight current vertex
                KeyFrame keyFrame = new KeyFrame(Duration.seconds(delay), event -> {
                    Vertex<VertexData> vertex = graphControl.findVertex(current);
                    SmartStylableNode stylableNode = smartGraphPanel.getStylableVertex(vertex);
                    if (stylableNode != null) {
                        stylableNode.setStyleClass("highlight-vertex");
                    }
                });

                timeline.getKeyFrames().add(keyFrame);

                // Highlight edge to next vertex
                if (i < path.size() - 1) {

                    VertexData next = path.get(i + 1);
                    KeyFrame edgeKeyFrame = new KeyFrame(Duration.seconds(delay + 1), event -> {
                        SmartStylableNode edgeNode = smartGraphPanel.getStylableEdge(graphControl.findEdge(current, next));
                        if (edgeNode != null) {
                            edgeNode.setStyleClass("highlight-edge");
                        }
                    });
                    timeline.getKeyFrames().add(edgeKeyFrame);
                }

                delay += 2; // Delay for each step
            }

            // Start the animation
            timeline.play();
        }

    }

    private class ClearEventHandler implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent actionEvent) {

            startVertex = null;
            endVertex = null;

            graphControl.getGraph().edges().forEach(edge -> {
                SmartStylableNode edgeNode = smartGraphPanel.getStylableEdge(edge.element());
                if (edgeNode != null) {
                    edgeNode.setStyleClass("edge");
                }
            });

            graphControl.getGraph().vertices().forEach(vertex -> {
                SmartStylableNode vertexNode = smartGraphPanel.getStylableVertex(vertex.element());
                if (vertexNode != null) {
                    vertexNode.setStyleClass("vertex");
                }
            });

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Clear");
            alert.setHeaderText("Clear");
            alert.setContentText("Clear Graph");
            alert.showAndWait();

            System.out.println("All selections cleared");

        }

    }

}
