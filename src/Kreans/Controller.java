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

    private static int size = 15;   //  The board is square
    private int delayTime = 50;   //  time in ms
    private Block[][] blocks;       //  list of all labyrinth's blocks
    private static Block clickedBlock = null;
    private static boolean isDrawing = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        clearBoard(); // initialize board
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

    private void changeStateBoard(){ // after drawing set all blocks as visited
        for (int x = 0; x < size; ++x) {
            for (int y = 0; y < size; ++y) {
                int finalX = x;
                int finalY = y;
                Platform.runLater(() -> {
                    blocks[finalX][finalY].setToVisted();
                    blocks[finalX][finalY].draw();
                });
            }

        }
    }

    private void drawLabyrinth() throws InterruptedException {     // create labyrinth on board

        LinkedList<Block> seeList = new LinkedList<>();     //List for visited blocks
        Random r = new Random(System.currentTimeMillis());

        // set as start block selected block or random coords
        int startX = clickedBlock == null ? r.nextInt(size) : clickedBlock.getPosX();
        int startY = clickedBlock == null ? r.nextInt(size) : clickedBlock.getPosY();

        seeList.add(blocks[startX][startY]);
        seeList.getFirst().changeState();          // set first block as visited
        int visitedBlocksCounter = 1;  // count visit block to end algorithm faster

        while (!seeList.isEmpty() && visitedBlocksCounter != size*size) {        // end while visited list is empty

            Block currentBlock = seeList.get(r.nextInt(seeList.size()));
            Platform.runLater(currentBlock::draw);                          // to change gui from another thread

            List<Block> neighbours = getNeighbours(currentBlock.getPosX(), currentBlock.getPosY()); // neighbours of current block
            int counter = 0;

            while (neighbours.size() > 0) {

                counter++;
                if (counter == size) break;    //extra stop condition to e

                Thread.sleep(this.delayTime);
                int index = neighbours.size() != 1 ? r.nextInt(neighbours.size()) : 0; // rand neighbour block
                seeList.addLast(neighbours.get(index)); // add neighbour block to visited list
                visitedBlocksCounter++;
                neighbours.get(index).changeState(); // set neighbour state as visited

                Block finalCurrentBlock = currentBlock;
                Block finalNeighbours = neighbours.get(index);
                removeWalls(finalCurrentBlock, finalNeighbours);
                Platform.runLater(() -> {
                    finalCurrentBlock.draw();
                    finalNeighbours.draw();
                });

                currentBlock = neighbours.get(index); // set next block as current
                neighbours = getNeighbours(currentBlock.getPosX(), currentBlock.getPosY()); // get neighbours of new current block
            }

            Thread.sleep(this.delayTime);

            //  if current block hasn't got any non-visited neighbour, remove it from visited list and change state to created
            if (getNeighbours(currentBlock.getPosX(), currentBlock.getPosY()).size() == 0) {
                currentBlock.changeState();
                Platform.runLater(currentBlock::draw);
                seeList.remove(currentBlock);

            }
        }
    }

    private List<Block> getNeighbours(int x, int y) {   //  get non-visited block's neighbours

        List<Block> neighbours = new ArrayList<>();

        // check state if is non-visited
        if (x - 1 >= 0 && !blocks[x - 1][y].isVisited())
            neighbours.add(blocks[x - 1][y]);

        if (x + 1 < size && !blocks[x + 1][y].isVisited())
            neighbours.add(blocks[x + 1][y]);

        if (y - 1 >= 0 && !blocks[x][y - 1].isVisited())
            neighbours.add(blocks[x][y - 1]);

        if (y + 1 < size && !blocks[x][y + 1].isVisited())
            neighbours.add(blocks[x][y + 1]);

        return neighbours;
    }

    private void removeWalls(Block from, Block to) {
        //    on the start every block have 4 walls: up,left,down,right,
        //    remove one wall for ech block to make connection between blocks,

        int directionX = from.getPosX() - to.getPosX();
        int directionY = from.getPosY() - to.getPosY();

        if (directionX == -1) { // go right
            from.value -= Block.Wall.right;
            to.value -= Block.Wall.left;

        } else if (directionX == 1) { // go left
            from.value -= Block.Wall.left;
            to.value -= Block.Wall.right;

        } else if (directionY == -1) { // go down
            from.value -= Block.Wall.down;
            to.value -= Block.Wall.up;

        } else if (directionY == 1) { // go up
            from.value -= Block.Wall.up;
            to.value -= Block.Wall.down;
        }

    }

    public void createButtonClick(ActionEvent actionEvent) {

        clearBoard();
        createButton.setDisable(true);
        isDrawing = true;
        new Thread(() -> {
            try {
                drawLabyrinth();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                Platform.runLater(() -> createButton.setDisable(false));
                isDrawing = false;
                clickedBlock = null;
                changeStateBoard();
            }
        }).start();

    }

    static void setClickedBlock(Block block) {  // click on board

        if (!isDrawing) {

            if (clickedBlock == block) { // clicked selected block
                clickedBlock.setStart(false, block.isVisited());
                clickedBlock = null;

            } else {

                if (clickedBlock != null) // change block's state previously selected block
                    clickedBlock.setStart(false, block.isVisited());

                clickedBlock = block;
                clickedBlock.setStart(true, block.isVisited());
            }
        }
    }
}
