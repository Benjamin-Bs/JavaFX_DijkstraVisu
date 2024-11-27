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

import java.util.function.Consumer;

public class GraphView extends BorderPane {

    private SmartGraphPanel<VertexData, EdgeData> smartGraphPanel;
    private ContentZoomScrollPane contentZoomScrollPane;
    private GraphControl graphControl;
    private VertexData startVertex;
    private VertexData endVertex;

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

        // TODO: das auf meiner Menü ändern
        smartGraphPanel.setOnContextMenuRequested(new EventHandler<ContextMenuEvent>() {
            @Override
            public void handle(ContextMenuEvent event) {
                System.out.println("ContextMenuEvent: " + event);

                SmartGraphVertexNode<VertexData> foundVertex = findVertex(event.getX(), event.getY());
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

        MenuItem selectStart = new MenuItem("Select as Start");
        selectStart.setOnAction(event -> {

            double x = contextMenu.getScene().getWindow().getX();
            double y = contextMenu.getScene().getWindow().getY();

            SmartGraphVertexNode<VertexData> selectedVertex = findVertex(x, y);
            if (selectedVertex != null) {
                startVertex = selectedVertex.getUnderlyingVertex().element();
                System.out.println("StartVertex: " + startVertex);
            }
        });
        MenuItem selectEnd = new MenuItem("Select as End");
        selectEnd.setOnAction(event -> {
            double x = contextMenu.getScene().getWindow().getX();
            double y = contextMenu.getScene().getWindow().getY();

            SmartGraphVertexNode<VertexData> selectedVertex = findVertex(x, y);
            if (selectedVertex != null) {
                endVertex = selectedVertex.getUnderlyingVertex().element();
                System.out.println("endVertex: " + endVertex);
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
            System.out.println("Hallo Dijkstra");
        }
    }

}
