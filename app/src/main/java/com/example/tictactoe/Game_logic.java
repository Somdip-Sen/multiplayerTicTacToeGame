package com.example.tictactoe;

public class Game_logic {
    int[][] board;
    Game_logic()
    {
        board = new int[3][3];
        this.reset();
    }
    protected void reset()
    {
        for(int i=0;i<=2;i++)
            for(int j=0; j<=2;j++)
                board[i][j]=0;
    }
    protected boolean alter(int col, int row, int player)
    {
        if (board[row][col]==0)
        {
            board[row][col] = player;
            return true;
        }
        else
            return false;
    }
}
