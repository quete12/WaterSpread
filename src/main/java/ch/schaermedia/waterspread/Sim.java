/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.waterspread;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.event.MouseEvent;

/**
 *
 * @author Quentin
 */
public class Sim extends PApplet {

    private final int VALUE_LAYER_IDX = 0;
    private final int INBOUND_LAYER_IDX = 1;
    private final int OUTBOUND_LAYER_IDX = 2;
    private final int AVERAGE_LAYER_IDX = 3;

    private int cols;
    private int rows;

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
                calculate(x, y);
            }
        }
        for (int x = 0; x < cols; x++) {
            for (int y = 0; y < rows; y++) {
                update(x, y);
                renderToGraphic(gr, x, y);
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
        int mx = (int) Math.floor(mouseX / zoom);
        int my = (int) Math.floor(mouseY / zoom);

        int radius = 3;

        for (int x = mx - radius; x <= mx + radius; x++) {
            for (int y = my - radius; y <= my + radius; y++) {

                if (x < 0 || x >= cols || y < 0 || y >= rows) {
                    continue;
                }
                if (event.getButton() == LEFT) {
                    grid[INBOUND_LAYER_IDX][x][y] += 50;
                } else {
                    float lower = grid[VALUE_LAYER_IDX][x][y] - 50;
                    if (lower < 0) {
                        lower = 0;
                    }
                    grid[VALUE_LAYER_IDX][x][y] = lower;
                }

            }
        }

    }

    public void mousePressed() {
        if (mouseX < 0 || mouseX >= width || mouseY < 0 || mouseY >= height) {
            return;
        }
        int mx = (int) Math.floor(mouseX / zoom);
        int my = (int) Math.floor(mouseY / zoom);
        grid[INBOUND_LAYER_IDX][mx][my] += 50;

    }

    private void renderToGraphic(PGraphics g, int x, int y) {
        //float value = (toUpdate[VALUE_LAYER_IDX][x][y]  + toCalculate[VALUE_LAYER_IDX][x][y]) / 2;
        float value = grid[VALUE_LAYER_IDX][x][y];
        int idx = x + y * cols;
        if (value < 0) {
            // Render pixels with a negative (invalid) value in blue
            g.pixels[idx] = color(0, 0, 255);
        } //else if (value - grid[AVERAGE_LAYER_IDX][x][y] < 0) {
        // For debugging render pixels with a lower than average value in red
        //            g.pixels[idx] = color(255, 0, 0);
        //}
        else {
            // Render the pixel in a rage from black to grey
            g.pixels[idx] = color(value);
        }
    }

    private void update(int x, int y) {
        // Adds the value flowing in from other cells and subtracts outwards flow
        grid[VALUE_LAYER_IDX][x][y] = grid[VALUE_LAYER_IDX][x][y] + grid[INBOUND_LAYER_IDX][x][y] - grid[OUTBOUND_LAYER_IDX][x][y];
        // Reset in and out flow
        grid[INBOUND_LAYER_IDX][x][y] = 0;
        grid[OUTBOUND_LAYER_IDX][x][y] = 0;
    }

    private void calculate(int x, int y) {

        float value = grid[VALUE_LAYER_IDX][x][y];

        float average;
        float tospread = 0;
        int range = 2;
        average = calcAverage(x, y, range);
        grid[AVERAGE_LAYER_IDX][x][y] = average;
        tospread = value - average;
        range++;
        //tospread = (float) (tospread > 0 ? tospread : value > diffTotal ? diffTotal * 0.1 : 0);
        if (tospread <= 0) {
            return;
        }

        float totalFlow = 0;

        float lowValue = Float.MAX_VALUE;
        int numLowest = 0;
        int[] lowX = new int[8];
        int[] lowY = new int[8];
        for (int nx = -1; nx <= 1; nx++) {
            for (int ny = -1; ny <= 1; ny++) {
//                if (nx != 0 && ny != 0) {
//                    continue;
//                }
                if (nx == 0 && ny == 0) {
                    continue;
                }
                int cx = x + nx;
                int cy = y + ny;
                if (cx < 0 || cy < 0 || cx > cols - 1 || cy > rows - 1) {
                    continue;
                }
                if (grid[VALUE_LAYER_IDX][cx][cy] < lowValue) {
                    lowValue = grid[VALUE_LAYER_IDX][cx][cy];
                    numLowest = 0;
                    lowX = new int[8];
                    lowY = new int[8];

                    lowX[numLowest] = cx;
                    lowY[numLowest] = cy;
                    numLowest++;
                } else if (grid[VALUE_LAYER_IDX][cx][cy] == lowValue) {
                    lowX[numLowest] = cx;
                    lowY[numLowest] = cy;
                    numLowest++;
                }
            }
        }
        float flow = (float) (tospread / numLowest);
        for (int i = 0; i < numLowest; i++) {
            grid[INBOUND_LAYER_IDX][lowX[i]][lowY[i]] += flow;
            totalFlow += flow;

        }
        grid[OUTBOUND_LAYER_IDX][x][y] = totalFlow;
    }

    private float calcAverage(int centerX, int centerY, int range) {
        float sum = 0;
        int tiles = 0;
        for (int x = -range; x <= range; x++) {
            for (int y = -range; y <= range; y++) {
                int cx = centerX + x;
                int cy = centerY + y;
                if (cx < 0 || cy < 0 || cx > cols - 1 || cy > rows - 1) {
                    continue;
                }
                sum += grid[VALUE_LAYER_IDX][cx][cy];
                tiles++;
            }
        }
        return sum / tiles;
    }

    @Override
    public void settings() {
        size(800, 800);
    }

    @Override
    public void setup() {
        cols = 150;
        rows = 150;
        zoom = (float) Math.sqrt((width * height) / (cols * rows));

        System.out.println("zoom: " + zoom);

        grid = new float[4][cols][rows];

        frameRate(60);
    }

    public static void main(String[] args) {
        PApplet.main(Sim.class,
                args);
    }

}
