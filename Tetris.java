package com.example.assignment3_tetris;

import android.graphics.Point;

//import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

//2 = border, 1 = locked pieces, 3 = falling (controlled) piece, -1 = erased row (dont need to account for in GUI)
//2 threads: USE BLOCKS - updates the screen without user touching, user touches the screen, need to return from DRAW_SCREEN
public class Tetris {
    /** DEBUG **/
    private final static boolean DEBUG = false;

    /** colors **/
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    /**add ""static"" to Point[][][] pieces for testing printing purposes **/
    private static final Point[][][] pieces = {     //Point randomPoint = pieces[shape] [rotationNum] [Point(x,y)]
            //int x_1 = randomPoint.x
            //double x_2 = randomPoint.getX()   -- not needed
            {
                    //Z     --first parameter
                    {new Point(0,0), new Point(1,0), new Point(0,1), new Point(-1,1)},       //row --second parameter
                    {new Point(0,0), new Point(0,-1), new Point(1,0), new Point(1,1)},       //Point(x.y) --third parameter
                    {new Point(0,0), new Point(1,0), new Point(0,1), new Point(-1,1)},       //use Point.x to get x value
                    {new Point(0,0), new Point(0,-1), new Point(1,0), new Point(1,1)}        //Point.getX() returns a double
            },
            {
                    //S
                    {new Point(0,0), new Point(0,1), new Point(1,1), new Point(-1,0)},
                    {new Point(0,0), new Point(0,1), new Point(1,0), new Point(1,-1)},
                    {new Point(0,0), new Point(0,1), new Point(1,1), new Point(-1,0)},
                    {new Point(0,0), new Point(0,1), new Point(1,0), new Point(1,-1)}
            },
            {
                    //I
                    {new Point(0,0), new Point(0,1), new Point(0,2), new Point(0,-1)},
                    {new Point(0,0), new Point(1,0), new Point(2,0), new Point(-1,0)},
                    {new Point(0,0), new Point(0,1), new Point(0,2), new Point(0,-1)},
                    {new Point(0,0), new Point(1,0), new Point(2,0), new Point(-1,0)}
            },
            {
                    //L
                    {new Point(0,0), new Point(0,1), new Point(0,-1), new Point(1,-1)},
                    {new Point(0,0), new Point(1,0), new Point(-1,0), new Point(-1,-1)},
                    {new Point(0,0), new Point(0,1), new Point(0,-1), new Point(-1,1)},
                    {new Point(0,0), new Point(1,0), new Point(-1,0), new Point(1,1)}
            },
            {
                    //J
                    {new Point(0,0), new Point(0,1), new Point(0,-1), new Point(-1,-1)},
                    {new Point(0,0), new Point(1,0), new Point(-1,0), new Point(-1,1)},
                    {new Point(0,0), new Point(0,1), new Point(0,-1), new Point(1,1)},
                    {new Point(0,0), new Point(1,0), new Point(-1,0), new Point(1,-1)}
            },
            {
                    //T
                    {new Point(0,0), new Point(-1,0), new Point(1,0), new Point(0,-1)},
                    {new Point(0,0), new Point(0,1), new Point(-1,0), new Point(0,-1)},
                    {new Point(0,0), new Point(-1,0), new Point(1,0), new Point(0,1)},
                    {new Point(0,0), new Point(0,1), new Point(0,-1), new Point(1,0)}
            },
            {
                    //O    - box
                    {new Point(0,0), new Point(1,0), new Point(1,1), new Point(0,1)},       //all the same bc boxes have no rotation
                    {new Point(0,0), new Point(1,0), new Point(1,1), new Point(0,1)},
                    {new Point(0,0), new Point(1,0), new Point(1,1), new Point(0,1)},
                    {new Point(0,0), new Point(1,0), new Point(1,1), new Point(0,1)}
            }
    };
    //X is column | Y is row
    public static Point refPoint;
    public static ArrayList<Integer> nextPieceArr = new ArrayList<Integer>();
    public static int currentPiece;           //Z, S, I, L, J, T, O
    public static int rotationNum;            //0, 1, 2, 3, ... (looped)
    public static boolean game = true;
    public static int[][] board = new int[12][23];  // accounts for border on left/right side & border on bottom
    //10 playable in X, 22 playable in Y
    public static int nextPiece = -1;
    public static int holdPiece = -1;
    public static boolean holdPieceCounter = false;
    public static int score = 0;
    public static int rowMultiplier = 0;
    // creates a new piece, and updates the location to the top. also indicates the next piece
    public static void getNewPiece(){
        refPoint = new Point(5,1);  //since board is 10 horizontally, 5 is halfway point
        rotationNum = 0;                  //since first piece has no rotation value
        currentPiece = nextPiece;         //gets the next piece
        nextPieceArr.remove(0);         //removes the taken piece out of buffer
        fillPieceBuffer();
        nextPiece = nextPieceArr.get(0);       //updates the next piece
        if(DEBUG){System.out.println("DEBUG: getNewPiece() currentPiece " + currentPiece);}
        if(DEBUG){System.out.println("DEBUG: getNewPiece() nextPiece " + nextPiece);}
    }

    // rotates left or right, as indicated by -1 or 1
    public static void rotatePiece(int num){
        int rotateVal = rotationNum + num;
        if(rotateVal > 3){
            rotateVal = 0;
        }
        else if(rotateVal < 0 ){
            rotateVal = 3;
        }

        if(!didCollideWalls(refPoint.x, refPoint.y, rotateVal)) {
            if(!isBlockedNextRow(currentPiece, rotateVal, refPoint.x, refPoint.y)) {
                rotationNum = rotateVal;
                refPoint.x = pieces[currentPiece][rotationNum][0].x + refPoint.x;
                refPoint.y = pieces[currentPiece][rotationNum][0].y + refPoint.y;
            }
        }
    }

    //returns true if the piece did collide
    public static boolean didCollideWalls(int x, int y, int rotationOffset){
        //rotationOffset will use rotateVal as the parameter to check if it collided
        Point a = pieces[currentPiece][rotationOffset][0];
        Point b = pieces[currentPiece][rotationOffset][1];
        Point c = pieces[currentPiece][rotationOffset][2];
        Point d = pieces[currentPiece][rotationOffset][3];

        int temp_X;
        int temp_Y;
        /* checks for rotation */
        temp_X = a.x + x;
        temp_Y = a.y + y;
        if(temp_X == 0 || temp_X == 11 || temp_Y == 22){

            return true;
        }
        if(board[temp_X][temp_Y] == 2 || board[temp_X][temp_Y] == 1){
            return true;
        }

        temp_X = b.x + x;
        temp_Y = b.y + y;
        if(temp_X == 0 || temp_X == 11 || temp_Y == 22){
            return true;
        }
        if(board[temp_X][temp_Y] == 2 || board[temp_X][temp_Y] == 1){
            return true;
        }

        temp_X = c.x + x;
        temp_Y = c.y + y;
        if(temp_X == 0 || temp_X == 11 || temp_Y == 22){
            return true;
        }
        if(board[temp_X][temp_Y] == 2 || board[temp_X][temp_Y] == 1){
            return true;
        }

        temp_X = d.x + x;
        temp_Y = d.y + y;
        if(temp_X == 0 || temp_X == 11 || temp_Y == 22) {
            return true;
        }
        if(board[temp_X][temp_Y] == 2 || board[temp_X][temp_Y] == 1){
            return true;
        }

        return false;
    }

    //initialize the board (ONLY ONCE)
    public static void initBoard(){
        for(int i = 0; i < 12; i++){
            for(int j = 0; j < 23; j++){
                board[i][j] = 0;        //initializing everything to be 0
            }
        }
        for(int i = 0; i < 12; i++){
            board[i][22] = 2;           //2 is wall
        }
        for(int i = 0; i < 23; i++){
            board[0][i] = 2;            //2 is wall
            board[11][i] = 2;
        }
    }

    //returns true if there are pieces in the next row
    public static boolean isBlockedNextRow(int currPiece, int rotationOffset, int x_Position, int y_Position){
        Point a = pieces[currPiece][rotationOffset][0];
        Point b = pieces[currPiece][rotationOffset][1];
        Point c = pieces[currPiece][rotationOffset][2];
        Point d = pieces[currPiece][rotationOffset][3];

        int temp_X = a.x + x_Position;
        int temp_Y = a.y + y_Position;
        if(temp_Y == 23){
            return true;
        }
        if(board[temp_X][temp_Y] == 2 || board[temp_X][temp_Y] == 1){
            return true;
        }

        temp_X = b.x + x_Position;
        temp_Y = b.y + y_Position;
        if(temp_Y == 23){
            return true;
        }
        if(board[temp_X][temp_Y] == 2 || board[temp_X][temp_Y ] == 1){
            return true;
        }

        temp_X = c.x + x_Position;
        temp_Y = c.y + y_Position;
        if(temp_Y == 23){
            return true;
        }
        if(board[temp_X][temp_Y] == 2 || board[temp_X][temp_Y] == 1){
            return true;
        }

        temp_X = d.x + x_Position;
        temp_Y = d.y + y_Position;
        if(temp_Y == 23){
            return true;
        }
        if(board[temp_X][temp_Y] == 2 || board[temp_X][temp_Y] == 1){
            return true;
        }

        return false; //if no pieces are in the next row
    }

    //called in conjunction with isBlockedNextRow
    public static void lockPieces(int currPiece, int rotationVal, int x_Position, int y_Position){
        for(int i = 0; i < 4; i++){
            int x = pieces[currPiece][rotationVal][i].x;
            int y = pieces[currPiece][rotationVal][i].y;
            board[x+x_Position][y+y_Position-1] = 1;
        }
        holdPieceCounter = false;
        getNewPiece();
    }

    //returns if true a row is filled
    public static boolean isRowFilled(int row){
        for(int i = 1; i < 11; i++){
            if(board[i][row] != 1){
                return false;
            }
        }
        return true;
    }

    //clears a row (by making it -1) ACTUALLY THIS IS USELESS LMAO
    public static void clearRow(int row){
        for(int i = 1; i < 11; i++){
            board[i][row] = -1;
        }
    }

    //shifts everything down by 1
    public static void shiftDown(int startingY){
        for(int y = startingY; y > 1; y--){
            for(int x = 1; x < 11; x++){
                board[x][y] = board[x][y-1];
            }
        }
        for(int x = 1; x < 11; x++){
            board[x][0] = 0;
        }
    }

    //move a piece left/right, left would be -1, right would be 1
    public static void movePieceLeftRight(int leftOrRight){
        if(!didCollideWalls(refPoint.x + leftOrRight, refPoint.y, rotationNum)){
            refPoint.x = refPoint.x + leftOrRight;
        }
    }

    //run this function after every time a new piece is created (aka a piece is locked)
    public static boolean checkGameOver(){
        if(isBlockedNextRow(currentPiece, rotationNum, refPoint.x, refPoint.y)){
            return true;
        }
        return false;
    }

    //function to check for full rows, if there are full rows, then deletes the row to make it -1 and shifts everything down by 1
    public static void checkForFullRows(){
        for(int i = 22; i > 1; i--){
            if(isRowFilled(i)){
                clearRow(i);    //actually useless
                shiftDown(i);
                rowMultiplier++;
                i++;
            }
        }
        score += rowMultiplier*1000;
        rowMultiplier = 0;
    }

    //run this function if the user presses down
    public static void moveDown(){
        if(!isBlockedNextRow(currentPiece, rotationNum, refPoint.x, refPoint.y)){
            refPoint.y = refPoint.y+1;
        }
    }

    //updates the board every time you move
    public static void updateBoard(){
        for(int y = 0; y < 22; y++){
            for(int x = 1; x < 11; x++){
                if(board[x][y] == 3){
                    board[x][y] = 0;
                }
            }
        }
        board[pieces[currentPiece][rotationNum][0].x + refPoint.x][pieces[currentPiece][rotationNum][0].y + refPoint.y] = 3;
        board[pieces[currentPiece][rotationNum][1].x + refPoint.x][pieces[currentPiece][rotationNum][1].y + refPoint.y] = 3;
        board[pieces[currentPiece][rotationNum][2].x + refPoint.x][pieces[currentPiece][rotationNum][2].y + refPoint.y] = 3;
        board[pieces[currentPiece][rotationNum][3].x + refPoint.x][pieces[currentPiece][rotationNum][3].y + refPoint.y] = 3;
    }

    //prints the board
    public static void printBoard(){
        for(int y = 0; y < 23; y++){
            for(int x = 0; x < 12; x++){
                if(board[x][y] == 2){
                    System.out.print(ANSI_RED + board[x][y] + "  " + ANSI_RESET);
                }
                else if(board[x][y] == 1){
                    System.out.print(ANSI_GREEN + board[x][y] + "  " + ANSI_RESET);
                }
                else if(board[x][y] == 3){
                    System.out.print(ANSI_BLUE + board[x][y] + "  " + ANSI_RESET);
                }
                else if(board[x][y] == 0){
                    System.out.print(ANSI_BLACK + board[x][y] + "  " + ANSI_RESET);
                }
                //System.out.print(board[x][y] + "  ");
            }
            System.out.println();
        }
        System.out.println(ANSI_PURPLE + "Current piece: " + pieceName(currentPiece) + ANSI_RESET);
        System.out.println(ANSI_YELLOW + "Next piece: " + pieceName(nextPiece) + ANSI_RESET);
        System.out.println(ANSI_CYAN + "Held piece: " + pieceName(holdPiece) + ANSI_RESET);
    }

    //function to hold the piece
    public static void holdPiece(){
        if(holdPiece == -1){
            holdPiece = currentPiece;
            getNewPiece();
        }
        else if(!holdPieceCounter){
            int temp = holdPiece;
            holdPiece = currentPiece;
            currentPiece = temp;
            refPoint = new Point(5, 1);
            updateBoard();
            holdPieceCounter = true;
        }
    }

    //function to drop immediately
    public static void dropImmediately(){
        boolean loopDrop = true;
        while(loopDrop) {
            if (!isBlockedNextRow(currentPiece, rotationNum, refPoint.x, refPoint.y)) {
                refPoint.y = refPoint.y + 1;
            } else {
                loopDrop = false;
            }
        }
    }

    //function to return string of the piece
    public static String pieceName(int piece){
        if(piece == 0){
            return "S";
        }
        else if(piece == 1){
            return "Z";
        }
        else if(piece == 2){
            return "I";
        }
        else if(piece == 3){
            return "J";
        }
        else if(piece == 4){
            return "L";
        }
        else if(piece == 5){
            return "T";
        }
        else if(piece == 6){
            return "O";
        }
        return "NO PIECE HELD";
    }

    //fills the piece buffer
    public static void fillPieceBuffer(){
        if(nextPieceArr.isEmpty()){
            Collections.addAll(nextPieceArr, 0, 1, 2, 3, 4, 5, 6);          //adds everything back into the list
            Collections.shuffle(nextPieceArr);                                        //shuffles the elements inside
            nextPiece = nextPieceArr.get(0);
            if(DEBUG){System.out.println("DEBUG: fillPieceBuffer() nextPiece " + nextPiece);}
        }
    }

    //function to run the game
    public static void gameRun(){
        Scanner sc = new Scanner(System.in);
        fillPieceBuffer();
        getNewPiece();
        initBoard();
        updateBoard();
        printBoard();
        while(game){
            String c = sc.nextLine();
            if(c.equals("a")){
                movePieceLeftRight(-1);
            }
            else if(c.equals("d")){
                movePieceLeftRight(1);
            }
            else if(c.equals("s")){
                moveDown();
            }
            else if(c.equals("q")){
                rotatePiece(1);
            }
            else if(c.equals("e")){
                rotatePiece(-1);
            }
            else if(c.equals("h")){
                holdPiece();
            }
            else if(c.equals(" ")){
                dropImmediately();
            }
            else if(c.equals("EXIT")){
                break;
            }
            if(isBlockedNextRow(currentPiece, rotationNum, refPoint.x, refPoint.y)){
                lockPieces(currentPiece, rotationNum, refPoint.x, refPoint.y);
                checkForFullRows();
                if(checkGameOver()){
                    game = false;
                }
            }
            updateBoard();
            printBoard();
        }
        System.out.println("DONE");
    }

}

