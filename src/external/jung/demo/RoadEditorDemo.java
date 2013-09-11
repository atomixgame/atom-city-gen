/*
 * Copyright (c) 2003, the JUNG Project and the Regents of the University of
 * California All rights reserved.
 * 
 * This software is open-source under the BSD license; see either "license.txt"
 * or http://jung.sourceforge.net/license.txt for a description.
 * 
 */
package external.jung.demo;

import com.jme3.math.Vector3f;
import edu.uci.ics.jung.algorithms.layout.AbstractLayout;
import edu.uci.ics.jung.algorithms.layout.StaticLayout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.annotations.AnnotationControls;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.EditingModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ScalingControl;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Polygon;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.BorderFactory;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import external.jung.road.Edge3DInfo;
import external.jung.road.RoadGraphHelper;
import external.jung.road.Vertex3DInfo;
import org.apache.commons.collections15.functors.MapTransformer;
import org.apache.commons.collections15.map.LazyMap;

/**
 * Shows how to create a graph editor with JUNG. Mouse modes and actions are
 * explained in the help text. The application version of GraphEditorDemo
 * provides a File menu with an option to save the visible graph as a jpeg file.
 *
 * @author Tom Nelson
 *
 */
public class RoadEditorDemo extends JApplet {

    /**
     *
     */
    private static final long serialVersionUID = -2023243689258876709L;
    Graph<Vertex3DInfo, Edge3DInfo> graph;
    AbstractLayout<Vertex3DInfo, Edge3DInfo> layout;
    /**
     * the visual component and renderer for the graph
     */
    VisualizationViewer<Vertex3DInfo, Edge3DInfo> vv;
    String instructions =
            "<html>"
            + "</html>";
    private final JCheckBox layer1Check, layer2Check, layer3Check, layer4Check, layer5Check;
    private final RoadGraphHelper roadGraphHelper;

    /**
     * create an instance of a simple graph with popup controls to create a
     * graph.
     *
     */
    public RoadEditorDemo() {
        roadGraphHelper = new RoadGraphHelper();
        this.graph = roadGraphHelper.createRoadGraph();
        this.layout = new StaticLayout<Vertex3DInfo, Edge3DInfo>(graph, new Dimension(600, 600));
        this.roadGraphHelper.setLayout(layout);
        
        vv = new VisualizationViewer<Vertex3DInfo, Edge3DInfo>(layout);
        vv.setBackground(Color.white);

        vv.getRenderContext().setVertexLabelTransformer(MapTransformer.<Vertex3DInfo, String>getInstance(
                LazyMap.<Vertex3DInfo, String>decorate(new HashMap<Vertex3DInfo, String>(), new ToStringLabeller<Vertex3DInfo>())));
        vv.getRenderContext().setEdgeLabelTransformer(MapTransformer.<Edge3DInfo, String>getInstance(
                LazyMap.<Edge3DInfo, String>decorate(new HashMap<Edge3DInfo, String>(), new ToStringLabeller<Edge3DInfo>())));
        vv.setVertexToolTipTransformer(vv.getRenderContext().getVertexLabelTransformer());
        vv.getRenderContext().setEdgeShapeTransformer(new EdgeShape.Line<Vertex3DInfo, Edge3DInfo>());

        Container content = getContentPane();
        final GraphZoomScrollPane panel = new GraphZoomScrollPane(vv);
        content.add(panel);


        final EditingModalGraphMouse<Vertex3DInfo, Edge3DInfo> graphMouse =
                new EditingModalGraphMouse<Vertex3DInfo, Edge3DInfo>(vv.getRenderContext(), roadGraphHelper.vertexFactory, roadGraphHelper.edgeFactory);

        // the EditingGraphMouse will pass mouse event coordinates to the
        // vertexLocations function to set the locations of the vertices as
        // they are created
//        graphMouse.setVertexLocations(vertexLocations);
        vv.setGraphMouse(graphMouse);
        vv.addKeyListener(graphMouse.getModeKeyListener());

        graphMouse.setMode(ModalGraphMouse.Mode.EDITING);

        final ScalingControl scaler = new CrossoverScalingControl();
        JButton plus = new JButton("+");
        plus.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                scaler.scale(vv, 1.1f, vv.getCenter());
            }
        });
        JButton minus = new JButton("-");
        minus.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                scaler.scale(vv, 1 / 1.1f, vv.getCenter());
            }
        });

        JButton help = new JButton("Build");
        help.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //edgeAngle();
                roadGraphHelper.resetExtraDraw();
                roadGraphHelper.vertexIntersect();
                vv.repaint();

            }
        });

        AnnotationControls<Vertex3DInfo, Edge3DInfo> annotationControls =
                new AnnotationControls<Vertex3DInfo, Edge3DInfo>(graphMouse.getAnnotatingPlugin());
        JPanel controls = new JPanel();
        controls.add(plus);
        controls.add(minus);
        JComboBox modeBox = graphMouse.getModeComboBox();
        controls.add(modeBox);
        controls.add(annotationControls.getAnnotationsToolBar());
        controls.add(help);

        JSlider slider2 = new JSlider();
        slider2.setBorder(BorderFactory.createTitledBorder("Size"));
        slider2.setMajorTickSpacing(20);
        slider2.setMinorTickSpacing(5);
        slider2.setPaintTicks(true);
        controls.add(slider2);
        slider2.addChangeListener(new SliderListener());

        JPanel checkBoxes = new JPanel();
        //checkBoxes.setLayout(new GridLayout(1, 5));
        layer1Check = new JCheckBox("Layer1");
        layer1Check.setSelected(true);

        layer2Check = new JCheckBox("Layer2");
        layer2Check.setSelected(true);

        layer3Check = new JCheckBox("Layer3");
        layer3Check.setSelected(true);

        layer4Check = new JCheckBox("Layer4");
        layer4Check.setSelected(true);
        layer5Check = new JCheckBox("Layer5");
        layer5Check.setSelected(true);
        checkBoxes.add(layer1Check);
        checkBoxes.add(layer2Check);
        checkBoxes.add(layer3Check);
        checkBoxes.add(layer4Check);
        checkBoxes.add(layer5Check);
        JPanel twoLine = new JPanel();
        twoLine.setLayout(new GridLayout(2, 1));
        content.add(twoLine, BorderLayout.SOUTH);
        twoLine.add(controls);
        twoLine.add(checkBoxes);
        // 
        vv.addPostRenderPaintable(new PaintRoad());
        vv.addPreRenderPaintable(new PaintRoadUnder());
        vv.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                //edgeAngle();
                roadGraphHelper.resetExtraDraw();
                roadGraphHelper.vertexIntersect();
                vv.repaint();

            }
        });

    }

    class CheckBoxListener implements ItemListener {

        public void itemStateChanged(ItemEvent e) {
            Object source = e.getItemSelectable();

            vv.repaint();
            if (source == layer1Check) {
            } else if (source == layer2Check) {
            } else if (source == layer3Check) {
            } else if (source == layer4Check) {
            }
        }
    }

    class SliderListener implements ChangeListener {

        public void stateChanged(ChangeEvent e) {
            JSlider source = (JSlider) e.getSource();
            if (!source.getValueIsAdjusting()) {
                roadGraphHelper.size = (int) source.getValue();
                //edgeAngle();
                roadGraphHelper.resetExtraDraw();
                roadGraphHelper.vertexIntersect();
                vv.repaint();
            }
        }
    }

    class PaintRoad implements VisualizationViewer.Paintable {

        public void paint(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            if (layer1Check.isSelected()) {
                for (Vector3f v : roadGraphHelper.ipointList) {
                    Color oldColor = g.getColor();
                    g.setColor(Color.YELLOW);
                    g.drawOval(Math.round(v.x) - 4, Math.round(v.z) - 4, 8, 8);
                    g.setColor(oldColor);
                }
            }

            if (layer2Check.isSelected()) {
                for (Vector3f v : roadGraphHelper.pointList) {
                    Color oldColor = g.getColor();
                    g.setColor(Color.BLACK);
                    g.drawOval(Math.round(v.x) - 2, Math.round(v.z) - 2, 4, 4);
                    g.setColor(oldColor);
                }
            }

            if (layer3Check.isSelected()) {
                for (Pair<Vector3f> pair : roadGraphHelper.lineList) {
                    Color oldColor = g.getColor();
                    g.setColor(Color.BLUE);
                    int x1 = Math.round(pair.getFirst().x);
                    int y1 = Math.round(pair.getFirst().z);
                    int x2 = Math.round(pair.getSecond().x);
                    int y2 = Math.round(pair.getSecond().z);

                    Stroke oldStroke = g2.getStroke();
                    float dash[] = {10.0f};
                    g2.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_BUTT,
                            BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f));
                    g.drawLine(x1, y1, x2, y2);
                    g.setColor(oldColor);
                    g2.setStroke(oldStroke);
                }
            }

            if (layer4Check.isSelected()) {
                for (Pair<Vector3f> pair : roadGraphHelper.orderLineList) {
                    Color oldColor = g.getColor();

                    int i = roadGraphHelper.orderLineList.indexOf(pair);

                    int colorStep = 1;

                    int x1 = Math.round(pair.getFirst().x);
                    int y1 = Math.round(pair.getFirst().z);
                    int x2 = Math.round(pair.getSecond().x);
                    int y2 = Math.round(pair.getSecond().z);

                    Stroke oldStroke = g2.getStroke();
                    float dash[] = {10.0f};
                    //g.setColor(new Color(i * colorStep, 255 - i * colorStep, 0));
                    g.setColor(Color.CYAN);
                    g.drawString(String.valueOf(i), x2 + 20, y2 + 10);
                    g.drawOval(x2 + 20 - 8, y2 + 5 - 8, 16, 16);
                    g2.setStroke(new BasicStroke(8.0f, BasicStroke.CAP_BUTT,
                            BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f));

                    g.drawLine(x1, y1, x2, y2);
                    g.setColor(oldColor);
                    g2.setStroke(oldStroke);
                }

            }

            if (layer5Check.isSelected()) {
                for (Pair<Vector3f> pair : roadGraphHelper.outLineList) {
                    Color oldColor = g.getColor();
                    int i = roadGraphHelper.outLineList.indexOf(pair);


                    int x1 = Math.round(pair.getFirst().x);
                    int y1 = Math.round(pair.getFirst().z);
                    int x2 = Math.round(pair.getSecond().x);
                    int y2 = Math.round(pair.getSecond().z);


                    g.setColor(Color.MAGENTA);
                    g.drawLine(x1, y1, x2, y2);
                    g.setColor(oldColor);

                }

            }
        }

        public boolean useTransform() {
            return true;
        }
    }

    Color colorIndex(int v) {
        switch (v) {
            case 1:
                return Color.BLUE;
            case 2:
                return Color.CYAN;
        }
        return Color.BLACK;

    }

    class PaintRoadUnder implements VisualizationViewer.Paintable {

        public void paint(Graphics g) {
            for (ArrayList<Vector3f> crossVecList : roadGraphHelper.crossRoadMap.values()) {
                Polygon p = new Polygon();
                for (Vector3f v : crossVecList) {
                    p.addPoint(Math.round(v.x), Math.round(v.z));
                }
                g.setColor(Color.lightGray);
                g.fillPolygon(p);
            }
        }

        public boolean useTransform() {
            return true;
        }
    }

    /**
     * a driver for this demo
     */
    @SuppressWarnings("serial")
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        final RoadEditorDemo demo = new RoadEditorDemo();

        JMenu menu = new JMenu("File");

        JPopupMenu.setDefaultLightWeightPopupEnabled(false);
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(menu);
        frame.setJMenuBar(menuBar);
        frame.getContentPane().add(demo);
        frame.pack();
        frame.setVisible(true);
    }
}
