package Kreans;

import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;

import java.util.ArrayList;
import java.util.List;


class Block extends Pane {

    int value;
    int state;
    int posX, posY;
    private double width, height;

    Block(int value, int posX, int posY, double width, double height) {

        this.value = value;
        this.posX = posX;
        this.posY = posY;
        this.width = width;
        this.height = height;
        this.state = 0;

        setPrefHeight(height);
        setPrefWidth(width);

        setOnMouseClicked(this::handleMouseClick);
        draw();
    }


    private void handleMouseClick(MouseEvent event) {

        System.out.println("Clicked!");
    }

    void draw() {

        if (state == 0) setStyle("-fx-background-color: rgb(255,255,255);");
        else if (state == 1) setStyle("-fx-background-color: rgba(235,240,191,0.87);");
        else if (state == 2) setStyle("-fx-background-color: rgba(222,227,181,0.76);");
        else setStyle("-fx-background-color: rgba(227,45,37,0.76);");

        List<Line> lines = new ArrayList<>();

        if ((value & 1) == 1) lines.add(new Line(0, 0, this.width, 0));
        if (((value >> 1) & 1) == 1) lines.add(new Line(width, 0, width, height));
        if (((value >> 2) & 1) == 1) lines.add(new Line(0, height, width, height));
        if (((value >> 3) & 1) == 1) lines.add(new Line(0, 0, 0, height));


        getChildren().clear();
        for (Line line : lines)
            getChildren().add(line);
    }
}