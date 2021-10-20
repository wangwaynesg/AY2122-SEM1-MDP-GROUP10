package com.example.mdptest;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class Grid extends View {
    private static final int numberOfColumns = 20;
    private static final int numberOfRows = 20;
    private static final int cellWidth = 40;
    private static final int cellHeight = 40;

    private static final Paint blackPaint = new Paint();
    private static final Paint greyPaint = new Paint();
    private static final Paint whitePaint = new Paint();

    public Grid(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);

        blackPaint.setColor(Color.BLACK);
        greyPaint.setColor(Color.LTGRAY);
        whitePaint.setColor(Color.WHITE);
    }

    @Override
    public void onDraw(Canvas canvas) {

        // Draw the background boxes for the grid
        for (int i = 0; i < numberOfColumns; i++) {
            for (int j = 0; j < numberOfRows; j++) {
                canvas.drawRect(i * cellWidth, j * cellHeight,
                        (i + 1) * cellWidth, (j + 1) * cellHeight,
                        greyPaint);
            }
        }

        // Draw vertical lines
        for (int i = 1; i < numberOfColumns; i++) {
            canvas.drawLine(i * cellWidth, 0, i * cellWidth, numberOfRows * cellHeight, blackPaint);
        }

        // Draw horizontal lines
        for (int i = 1; i < numberOfRows; i++) {
            canvas.drawLine(0, i * cellHeight, numberOfColumns * cellWidth, i * cellHeight, blackPaint);
        }

        // Draw vertical grid axis
        for (int i = 0; i < numberOfRows; i++) {
            canvas.drawText(String.valueOf(i), cellWidth * 10 + 5, cellHeight * i + 15, whitePaint);
        }

        for (int i = 0; i < numberOfColumns; i++) {
            canvas.drawText(String.valueOf(i), cellWidth * i + 5, cellHeight * 10 + 15, whitePaint);
        }
    }
}
