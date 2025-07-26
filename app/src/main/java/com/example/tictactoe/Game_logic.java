package com.example.tictactoe;
import android.util.Log;

public class Game_logic {
    protected int[][] board;
    private static final String TAG = "Game_logic";
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

    protected boolean alter(int col, int row, int player) {
        try {
            if (board[row][col] == 0) {
                board[row][col] = player;
                return true;
            } else
                return false;
        }
        catch(Exception e){
            Log.e(TAG, "alter: ", e);
        }
        return false;
    }

}
