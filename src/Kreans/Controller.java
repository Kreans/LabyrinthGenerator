package Kreans;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.util.*;


public class Controller implements Initializable {

    @FXML
    Pane pane;

    @FXML
    Button createButton;

    private static int size = 21;   //  The board is square
    private int deleyedTime = 25;   //  time in ms
    private Block[][] blocks;       //  list of all labyrinth's blocks


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        clearBoard(); // initialize board
    }

    private void create() throws InterruptedException {     // create labyrinth on board

        LinkedList<Block> seeList = new LinkedList<>();     //List for visited blocks
        Random r = new Random(System.currentTimeMillis());  //init random

        int startX = r.nextInt(size);
        int startY = r.nextInt(size);

        seeList.add(blocks[startX][startY]); // rand first block
        seeList.getFirst().state++;          // set first block as visited

        while (!seeList.isEmpty()) {        // end while visited list is empty


            Block currentBlock = seeList.get(r.nextInt(seeList.size()));
            Platform.runLater(currentBlock::draw);                          // to change gui from another thread

            List<Block> neighbours = getNeighbours(currentBlock.posX, currentBlock.posY); // neighbours of current block

            int counter = 0;

            while (neighbours.size() > 0) {

                counter++;
                if (counter == size) break;
                Thread.sleep(this.deleyedTime);


                int index = neighbours.size() != 1 ? r.nextInt(neighbours.size()) : 0; // rand neighbour block
                seeList.addLast(neighbours.get(index)); // add neighbour block to visited list
                neighbours.get(index).state++; // set neighbour state as visited


                Block finalCurrentBlock = currentBlock;
                Block finalNeighbours = neighbours.get(index);
                removeWalls(finalCurrentBlock, finalNeighbours);
                Platform.runLater(() -> {
                    finalCurrentBlock.draw();
                    finalNeighbours.draw();
                });

                currentBlock = neighbours.get(index); // set next block as current
                neighbours = getNeighbours(currentBlock.posX, currentBlock.posY); // get neighbours of new current block
            }

            Thread.sleep(this.deleyedTime);

            //  if current block hasn't got any non-visited neighbour, remove it from visited list and change state to created
            if (getNeighbours(currentBlock.posX, currentBlock.posY).size() == 0) {
                currentBlock.state++;
                Platform.runLater(currentBlock::draw);
                seeList.remove(currentBlock);
            }

        }


    }

    private List<Block> getNeighbours(int x, int y) {   //  get non-visited neighbours

        List<Block> neighbours = new ArrayList<>();

        // check state if is non-visited
        if (x - 1 >= 0 && blocks[x - 1][y].state == 0) neighbours.add(blocks[x - 1][y]);

        if (x + 1 < size && blocks[x + 1][y].state == 0) neighbours.add(blocks[x + 1][y]);

        if (y - 1 >= 0 && blocks[x][y - 1].state == 0) neighbours.add(blocks[x][y - 1]);

        if (y + 1 < size && blocks[x][y + 1].state == 0) neighbours.add(blocks[x][y + 1]);

        return neighbours;
    }

    private void removeWalls(Block from, Block to) {
        //    on the start every block have 4 walls: up,left,down,right, to make connection between blocks,
        //    remove one wall for ech block

        int directionX = from.posX - to.posX;
        int directionY = from.posY - to.posY;

        //  walls
        int up = 1;
        int right = 2;
        int down = 4;
        int left = 8;


        if (directionX == -1) { // go right
            from.value -= right;
            to.value -= left;
        } else if (directionX == 1) { // go left
            from.value -= left;
            to.value -= right;
        } else if (directionY == -1) { // go down
            from.value -= down;
            to.value -= up;

        } else if (directionY == 1) { // go up
            from.value -= up;
            to.value -= down;
        }

    }


    public void createButtonClick(ActionEvent actionEvent) {


        clearBoard();
        createButton.setDisable(true);
        new Thread(() -> {
            try {
                create();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            finally {
                Platform.runLater(()->createButton.setDisable(false));

            }
        }).start();

    }

    private void clearBoard() {

        blocks = new Block[size][size];

        double height = pane.getPrefHeight();
        double width = pane.getPrefWidth();

        pane.getChildren().clear();

        for (int x = 0; x < size; ++x) {
            for (int y = 0; y < size; ++y) {
                Block block = new Block(15, x, y, width / size, height / size);
                block.setTranslateX(x * width / size);  //  move block to correct positionX
                block.setTranslateY(y * height / size); //  move block to correct positionY
                blocks[x][y] = block;                   // add to table of blocks
                pane.getChildren().add(block);          // add to Gui

            }
        }
    }
}
