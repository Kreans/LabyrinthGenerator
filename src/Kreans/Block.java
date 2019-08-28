package Kreans;

import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;

import java.util.ArrayList;
import java.util.List;


class Block extends Pane {


    int value;
    private int state;
    private int posX;
    private int posY;
    private double width, height;

    static class Wall{
        static int up = 0b0001;
        static int right = 0b0010;
        static int down = 0b0100;
        static int left = 0b1000;
    }

    Block(int value, int posX, int posY, double width, double height) {

        this.value = value; // block's border
        this.posX = posX;
        this.posY = posY;
        this.width = width; // in px
        this.height = height;// in px
        this.state = 0; // 0 - non visited, 1- in watch list, 2-visited, 3- marked as first block

        setPrefHeight(height);
        setPrefWidth(width);

        setOnMouseClicked(this::handleMouseClick);
        draw();
    }

    int getPosX() {
        return this.posX;
    }

    int getPosY() {
        return this.posY;
    }

    void changeState() {
        this.state = (state + 1) % 3;
    }
    void setToVisted(){
        this.state = 2;
    }

    boolean isVisited() {
        return this.state != 0;
    }

    void setStart(boolean start, boolean isVisited) {

        int state = !isVisited ? 0 : 2;
        this.state = start ? 3 : state;
        draw();
    }

    private void handleMouseClick(MouseEvent event) {

        Controller.setClickedBlock(this);
    }

    void draw() {

        if (state == 0) setStyle("-fx-background-color: rgb(255,255,255);");
        else if (state == 1) setStyle("-fx-background-color: rgba(235,240,191,0.87);");
        else if (state == 2) setStyle("-fx-background-color: rgba(222,227,181,0.76);");
        else setStyle("-fx-background-color: rgba(227,45,37,0.76);");

        List<Line> lines = new ArrayList<>();

        //conver border value to walls
        if ((value & 1) == 1) lines.add(new Line(0, 0, this.width, 0));
        if (((value >> 1) & 1) == 1) lines.add(new Line(width, 0, width, height));
        if (((value >> 2) & 1) == 1) lines.add(new Line(0, height, width, height));
        if (((value >> 3) & 1) == 1) lines.add(new Line(0, 0, 0, height));

        getChildren().clear();
        for (Line line : lines)
            getChildren().add(line);
    }

}