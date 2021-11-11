package Helperclasses;

import org.graphstream.graph.Element;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.stream.file.FileSinkDOT;

import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class FileSinkDOTTreeVertex extends FileSinkDOT {


    @Override
    protected void exportGraph(Graph graph) {
        String graphId = graph.getId();
        AtomicLong timeId = new AtomicLong(0L);
        this.out.printf("\t%s%n", "layout = \"neato\"");
     /*   graph.attributeKeys().forEach((key) -> {
            this.graphAttributeAdded(graphId, timeId.getAndIncrement(), key, graph.getAttribute(key));
        });*/
        Iterator var5 = graph.iterator();

        while (var5.hasNext()) {
            Node node = (Node) var5.next();
            String nodeId = node.getId();
            this.out.printf("\t\"%s\" %s;%n", nodeId, this.outputAttributes(node));
        }

        graph.edges().forEach((edge) -> {
            String fromNodeId = edge.getNode0().getId();
            String toNodeId = edge.getNode1().getId();
            String attr = this.outputAttributes(edge);
            if (this.digraph) {
                this.out.printf("\t\"%s\" -> \"%s\"", fromNodeId, toNodeId);
                if (!edge.isDirected()) {
                    this.out.printf(" -> \"%s\"", fromNodeId);
                }
            } else {
                this.out.printf("\t\"%s\" -- \"%s\"", fromNodeId, toNodeId);
            }

            this.out.printf(" %s;%n", attr);
        });
    }


    @Override
    protected String outputAttributes(Element e) {
        if (e.getAttributeCount() == 0) {
            return "";
        } else {
            StringBuilder buffer = new StringBuilder("[");
            AtomicBoolean first = new AtomicBoolean(true);
            e.attributeKeys().forEach((key) -> {
                boolean quote = true;
                Object value = e.getAttribute(key);
                if (value instanceof Object[]) {
                    quote = false;
                }
                String key2;
                String value2 = "test";
                if (key.equals("ui.label")) {
                    key2 = "label";
                    value2 = (String) value;
                } else {
                    key2 = "pos";
                    Object[] array = (Object[]) e.getAttribute(key);
                    value2 = array[0] + "," + array[1];
                }

                // !quote, damit die Struktur zur überschriebenen Methode nicht geändert wird
                buffer.append(String.format("%s%s=%s%s%s", first.get() ? "" : ",", key2, !quote ? "\"" : "", value2, !quote ? "!\"" : ""));
                first.set(false);
            });
            return buffer.append(']').toString();
        }


    }


}
