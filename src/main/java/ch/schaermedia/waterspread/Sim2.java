/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.waterspread;

import javafx.geometry.Point2D;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.event.MouseEvent;

/**
 *
 * @author Quentin
 */
public class Sim2 extends PApplet {

    private final int VALUE_LAYER_IDX = 0;
    private final int INBOUND_LAYER_IDX = 1;
    private final int OUTBOUND_LAYER_IDX = 2;

    private final int TOP_IDX = 0;
    private final int RIGHT_IDX = 1;
    private final int BOTTOM_IDX = 2;
    private final int LEFT_IDX = 3;
    private final int TOTAL_IDX = 4;

    private final Point2D[] DIRECTIONS = new Point2D[4];

    private int cols;
    private int rows;

    private int insX = 100;
    private int insY = 100;

    private int counter = 0;

    private float zoom;

    //grids: [layer][x][y]
    private float[][][] grid;

    @Override
    public void draw() {

        background(0);

        PGraphics gr = createGraphics(cols, rows);
        gr.beginDraw();
        gr.loadPixels();

        for (int x = 0; x < cols; x++) {
            for (int y = 0; y < rows; y++) {
            }
        }
        for (int x = 0; x < cols; x++) {
            for (int y = 0; y < rows; y++) {
            }
        }

        gr.updatePixels();
        gr.endDraw();

        scale(zoom);
        image(gr, 0, 0);
    }

    @Override
    public void mouseDragged(MouseEvent event) {
        if (mouseX < 0 || mouseX >= width || mouseY < 0 || mouseY >= height) {
            return;
        }
//        int mx = (int) Math.floor(mouseX / zoom);
//        int my = (int) Math.floor(mouseY / zoom);
    }

    public void mousePressed() {
        if (mouseX < 0 || mouseX >= width || mouseY < 0 || mouseY >= height) {
            return;
        }
//        int mx = (int) Math.floor(mouseX / zoom);
//        int my = (int) Math.floor(mouseY / zoom);

    }

    @Override
    public void settings() {
        size(800, 800);
    }

    @Override
    public void setup() {
        cols = 50;
        rows = 50;

        grid = new float[cols*3][rows*3][3];

        frameRate(60);
    }

    public static void main(String[] args) {
        PApplet.main(Sim2.class, args);
    }

}
