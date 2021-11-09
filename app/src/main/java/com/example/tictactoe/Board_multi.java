package com.example.tictactoe;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;

import java.util.Random;

public class Board_multi extends View {
    private final int boardColor, player1Color, player2Color;
    private final Paint paint = new Paint();
    private int cellsize;
    protected int row = 9, col = 9 ; // garbage value
    Game_logic game;
    protected int player,  match_winner = 0;
    protected volatile int player_start = 9;
    protected boolean win = false, sent = true;
    protected int score1,score2;
    boolean got_winner = false;
    protected int flag = 0;
    protected boolean myTurn = false, is_server, correct = false;
    private static final String TAG = "Board_multi";
    private MediaPlayer player_click, opponent_click;



    public Board_multi(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        score1 = 0; score2 = 0;
        game = new Game_logic();
        //retrieving attributes value from attrs.xml file
        TypedArray arr = context.getTheme().obtainStyledAttributes(attrs,R.styleable.Board, 0,0);

        try {
            boardColor = arr.getInteger(R.styleable.Board_boardColor, 0);
            player1Color = arr.getInteger(R.styleable.Board_player1color, 0);
            player2Color = arr.getInteger(R.styleable.Board_player2color, 0);

        }
        finally {
            arr.recycle();
        }
        player_click = MediaPlayer.create(context, R.raw.player_click);

    }


    // board measurement
    @Override
    protected void onMeasure(int width, int height)
    {
        super.onMeasure(width,height);

        int dimension = Math.min(getMeasuredHeight(),getMeasuredWidth()) * 80/100; // 80 % of width
        setMeasuredDimension(dimension,dimension);
        cellsize = dimension /3;
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        paint.setStyle(Paint.Style.STROKE); // draw line
        paint.setAntiAlias(true);
        drawBoard(canvas);
        drawTurn(canvas);
    }

    protected String getLocation(){
        String location = Integer.toString(row) + Integer.toString(col);
        return location;
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent e)
    {
        Log.d(TAG, "onTouchEvent: " + myTurn);

        if (myTurn) {
            if (!win) {
                Log.d(TAG, "onTouchEvent: running");
                float x_position = e.getX();
                float y_position = e.getY();
                int action = e.getAction();
                if (action == MotionEvent.ACTION_DOWN) {
                    row = (int) Math.ceil(x_position / cellsize) - 1;
                    col = (int) Math.ceil(y_position / cellsize) - 1;
                }
                if (game.alter(row, col, player)) {
                    player_click.start();
                    invalidate();
                    sent = false;
                    return true;
                }
            }
        }
        return false;
    }

    private void drawTurn(Canvas canvas)
    {
        for(int i=0;i<=2;i++)
            for(int j=0; j<=2;j++) {
                if (!(game.board[i][j] == 0)) {
                    if (game.board[i][j] == 1) {
                        drawPlayer1(canvas, i, j);
                    } else {
                        drawPlayer2(canvas, i, j);
                    }
                }
            }
        check_win(canvas);
    }


    protected void drawBoard(Canvas canvas)
    {
        //draw the checkboard
        paint.setColor(boardColor);
        paint.setStrokeWidth(18);
        for(int i=0;i<=3;i++) {
            canvas.drawLine(0, i * cellsize, canvas.getWidth()+5000, i * cellsize, paint); // horizontal line
            canvas.drawLine(i * cellsize, 0, i * cellsize, canvas.getWidth()+5000, paint); // vartical line
        }

    }

    protected void drawPlayer1(Canvas canvas , int col, int row)
    {
        paint.setColor(player1Color);
        paint.setStrokeWidth(10);
        canvas.drawLine(cellsize*row+60, cellsize*col+60, cellsize*(row+1)-60,cellsize*(col+1)-60,paint);
        canvas.drawLine(cellsize*(row+1)-60, cellsize*(col)+60, cellsize*row+60,cellsize*(col+1)-60,paint);

    }

    protected void drawPlayer2(Canvas canvas, int col, int row)
    {
        paint.setColor(player2Color);
        paint.setStrokeWidth(10);
        canvas.drawCircle(row*cellsize+cellsize/2,col*cellsize+cellsize/2, cellsize/2 - 45,paint);
    }
    protected void game_reset()
    {
        game.reset();
        match_winner = 0;
        got_winner = false;
        sent = true;
        flag = 0;
    }
    protected void check_win(Canvas canvas)
    {
        for (int i=0;i<=2;i++) {
            if (game.board[i][0]!=0 &&game.board[i][0] == game.board[i][1] && game.board[i][0] == game.board[i][2]) { //row-wise win
                match_winner = game.board[i][0];
                draw_win_line('r', i, canvas);
                break;
            }
            else if (game.board[0][i]!=0 &&game.board[0][i] == game.board[1][i] && game.board[0][i] == game.board[2][i]) {//column wise win
                match_winner = game.board[0][i];
                draw_win_line('c', i, canvas);
                break;
            }
            else
            {
                //diagonal win
                if (game.board[0][0]!=0 && game.board[0][0] == game.board[1][1] && game.board[2][2] == game.board[1][1]) {
                    match_winner = game.board[1][1];
                    draw_win_line('d', 1, canvas);
                    break;
                }
                else if (game.board[1][1]!=0 && game.board[0][2] == game.board[1][1] && game.board[1][1] == game.board[2][0]) {
                    match_winner = game.board[1][1];
                    draw_win_line('d', 2, canvas);
                    break;
                }
            }
        }

    }
    protected void draw_win_line(char pattern, int number, Canvas canvas) {
        if (match_winner == 1) paint.setColor(player1Color);
        else paint.setColor(player2Color);

        paint.setStrokeWidth(14);

        // pattern -> row/column/diagonal wise number ->which row/col/diagonal
        switch (pattern)
        {
            case 'r':
                canvas.drawLine(20,number*cellsize+cellsize/2, canvas.getWidth()-20,number*cellsize+cellsize/2, paint);
                invalidate();
                break;
            case 'c':
                canvas.drawLine(number*cellsize+cellsize/2,0,number*cellsize+cellsize/2, canvas.getHeight(),paint);
                invalidate();
                break;
            case 'd':
                if (number == 1)  canvas.drawLine(20, 20, canvas.getWidth() - 20, canvas.getHeight() - 20, paint);
                else  canvas.drawLine(canvas.getWidth()-20,20,20,canvas.getHeight()-20,paint);
                invalidate();
        }

        got_winner = true;
        flag++;

    }
}

