package at.htlhl.dijkstravisu;

import com.brunomnsilva.smartgraph.containers.ContentZoomScrollPane;
import com.brunomnsilva.smartgraph.graph.Vertex;
import com.brunomnsilva.smartgraph.graphview.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ToolBar;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.layout.BorderPane;

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


        // Create ToolBar
        ToolBar toolBar = new ToolBar(testButton, dijkstraButton);
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
                return;
            }

            List<VertexData> path = graphControl.shortestPath(startVertex, endVertex);

            if (path.isEmpty()) {
                System.out.println("No path between " + startVertex + " and " + endVertex + " found");
            } else {
                System.out.println("shortestPath: " + path);
            }

        }
    }

}
