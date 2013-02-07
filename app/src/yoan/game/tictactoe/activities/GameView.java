package yoan.game.tictactoe.activities;

import java.util.Random;

import yoan.game.tictactoe.R;
import yoan.game.tictactoe.game.constantes.State;
import yoan.game.tictactoe.game.context.TttContext;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Looper;
import android.os.Message;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class GameView extends View {

    public static final long FPS_MS = 1000/2;

    private static final int MARGIN = 4;
    private static final int MSG_BLINK = 1;

    private final Handler mHandler = new Handler(Looper.getMainLooper(), new MyHandler());

    private final Rect mSrcRect = new Rect();
    private final Rect mDstRect = new Rect();

    private int mSxy;
    private int mOffetX;
    private int mOffetY;
    private Paint mWinPaint;
    private Paint mLinePaint;
    private Paint mBmpPaint;
    private Bitmap mBmpPlayer1;
    private Bitmap mBmpPlayer2;
    private Drawable mDrawableBg;

    private ICellListener mCellListener;

    //TODO [Ttt] retirer tout ce qui n'est pas nécessaire de la GameView
    //TODO [Ttt] améliorer le design
    //TODO [Ttt] faire une version multilingue
    private int mWinCol = -1;
    private int mWinRow = -1;
    private int mWinDiag = -1;

    private boolean mBlinkDisplayOff;
    private final Rect mBlinkRect = new Rect();



    public interface ICellListener {
        abstract void onCellSelected();
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        requestFocus();
        
        State[] data = TttContext.getContext().getData();
        
        mDrawableBg = getResources().getDrawable(R.drawable.lib_bg);
        setBackgroundDrawable(mDrawableBg);

        mBmpPlayer1 = getResBitmap(R.drawable.lib_cross);
        mBmpPlayer2 = getResBitmap(R.drawable.lib_circle);

        if (mBmpPlayer1 != null) {
            mSrcRect.set(0, 0, mBmpPlayer1.getWidth() -1, mBmpPlayer1.getHeight() - 1);
        }

        mBmpPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        mLinePaint = new Paint();
        mLinePaint.setColor(0xFFFFFFFF);
        mLinePaint.setStrokeWidth(5);
        mLinePaint.setStyle(Style.STROKE);

        mWinPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mWinPaint.setColor(0xFFFF0000);
        mWinPaint.setStrokeWidth(10);
        mWinPaint.setStyle(Style.STROKE);

        for (int i = 0; i < data.length; i++) {
        	data[i] = State.EMPTY;
        }

        //si on est dans un éditeur (ex: le layout editor d'eclipse)
        if (isInEditMode()) {
            //on affiche un état aléatoire du jeu
            Random rnd = new Random();
            for (int i = 0; i < data.length; i++) {
            	data[i] = State.fromInt(rnd.nextInt(3));
            }
        }
    }


    public void setCellListener(ICellListener cellListener) {
        mCellListener = cellListener;
    }
    
    /** Sets winning mark on specified column or row (0..2) or diagonal (0..1). */
    public void setFinished(int col, int row, int diagonal) {
        mWinCol = col;
        mWinRow = row;
        mWinDiag = diagonal;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        State[] data = TttContext.getContext().getData();
        int selectedCell = TttContext.getContext().getSelectedCell();
        State selectedValue = TttContext.getContext().getSelectedValue();

        int sxy = mSxy;
        int s3  = sxy * 3;
        int x7 = mOffetX;
        int y7 = mOffetY;

        for (int i = 0, k = sxy; i < 2; i++, k += sxy) {
            canvas.drawLine(x7    , y7 + k, x7 + s3 - 1, y7 + k     , mLinePaint);
            canvas.drawLine(x7 + k, y7    , x7 + k     , y7 + s3 - 1, mLinePaint);
        }

        for (int j = 0, k = 0, y = y7; j < 3; j++, y += sxy) {
            for (int i = 0, x = x7; i < 3; i++, k++, x += sxy) {
                mDstRect.offsetTo(MARGIN+x, MARGIN+y);

                State v;
                if (selectedCell == k) {
                    if (mBlinkDisplayOff) {
                        continue;
                    }
                    v = selectedValue;
                } else {
                    v = data[k];
                }

                switch(v) {
                case PLAYER1:
                    if (mBmpPlayer1 != null) {
                        canvas.drawBitmap(mBmpPlayer1, mSrcRect, mDstRect, mBmpPaint);
                    }
                    break;
                case PLAYER2:
                    if (mBmpPlayer2 != null) {
                        canvas.drawBitmap(mBmpPlayer2, mSrcRect, mDstRect, mBmpPaint);
                    }
                    break;
                default:
                	break;
                }
            }
        }

        if (mWinRow >= 0) {
            int y = y7 + mWinRow * sxy + sxy / 2;
            canvas.drawLine(x7 + MARGIN, y, x7 + s3 - 1 - MARGIN, y, mWinPaint);

        } else if (mWinCol >= 0) {
            int x = x7 + mWinCol * sxy + sxy / 2;
            canvas.drawLine(x, y7 + MARGIN, x, y7 + s3 - 1 - MARGIN, mWinPaint);

        } else if (mWinDiag == 0) {
            // diagonal 0 is from (0,0) to (2,2)

            canvas.drawLine(x7 + MARGIN, y7 + MARGIN,
                    x7 + s3 - 1 - MARGIN, y7 + s3 - 1 - MARGIN, mWinPaint);

        } else if (mWinDiag == 1) {
            // diagonal 1 is from (0,2) to (2,0)

            canvas.drawLine(x7 + MARGIN, y7 + s3 - 1 - MARGIN,
                    x7 + s3 - 1 - MARGIN, y7 + MARGIN, mWinPaint);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // Keep the view squared
        int w = MeasureSpec.getSize(widthMeasureSpec);
        int h = MeasureSpec.getSize(heightMeasureSpec);
        int d = w == 0 ? h : h == 0 ? w : w < h ? w : h;
        setMeasuredDimension(d, d);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        int sx = (w - 2 * MARGIN) / 3;
        int sy = (h - 2 * MARGIN) / 3;

        int size = sx < sy ? sx : sy;

        mSxy = size;
        mOffetX = (w - 3 * size) / 2;
        mOffetY = (h - 3 * size) / 2;

        mDstRect.set(MARGIN, MARGIN, size - MARGIN, size - MARGIN);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();

        if (action == MotionEvent.ACTION_DOWN) {
            return true;

        } else if (action == MotionEvent.ACTION_UP) {
            int x = (int) event.getX();
            int y = (int) event.getY();

            int sxy = mSxy;
            x = (x - MARGIN) / sxy;
            y = (y - MARGIN) / sxy;

            if (isEnabled() && x >= 0 && x < 3 && y >= 0 & y < 3) {
            	State[] data = TttContext.getContext().getData();
                int selectedCell = TttContext.getContext().getSelectedCell();
                State selectedValue = TttContext.getContext().getSelectedValue();
                
            	int cell = x + 3 * y;
                State currentPlayer = TttContext.getContext().getCurrentPlayer();
                State state = cell == selectedCell ? selectedValue : data[cell];
                state = state == State.EMPTY ? currentPlayer : State.EMPTY;

                stopBlink();

                TttContext.getContext().setSelectedCell(cell);
                TttContext.getContext().setSelectedValue(state);
                mBlinkDisplayOff = false;
                mBlinkRect.set(MARGIN + x * sxy, MARGIN + y * sxy,
                               MARGIN + (x + 1) * sxy, MARGIN + (y + 1) * sxy);

                if (state != State.EMPTY) {
                    // Start the blinker
                    mHandler.sendEmptyMessageDelayed(MSG_BLINK, FPS_MS);
                }

                if (mCellListener != null) {
                    mCellListener.onCellSelected();
                }
            }

            return true;
        }

        return false;
    }

    public void stopBlink() {
        int selectedCell = TttContext.getContext().getSelectedCell();
        State selectedValue = TttContext.getContext().getSelectedValue();
        
        boolean hadSelection = selectedCell != -1 && selectedValue != State.EMPTY;
        TttContext.getContext().setSelectedCell(-1);
        TttContext.getContext().setSelectedValue(State.EMPTY);
        if (!mBlinkRect.isEmpty()) {
            invalidate(mBlinkRect);
        }
        mBlinkDisplayOff = false;
        mBlinkRect.setEmpty();
        mHandler.removeMessages(MSG_BLINK);
        if (hadSelection && mCellListener != null) {
            mCellListener.onCellSelected();
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle b = new Bundle();

        Parcelable s = super.onSaveInstanceState();
        b.putParcelable("gv_super_state", s);

        b.putBoolean("gv_en", isEnabled());

        State[] data = TttContext.getContext().getData();
        
        int[] d = new int[data.length];
        for (int i = 0; i < d.length; i++) {
            d[i] = data[i].getValue();
        }
        b.putIntArray("gv_data", d);

        b.putInt("gv_sel_cell", TttContext.getContext().getSelectedCell());
        b.putInt("gv_sel_val",  TttContext.getContext().getSelectedValue().getValue());
        b.putInt("gv_curr_play", TttContext.getContext().getCurrentPlayer().getValue());
        b.putInt("gv_winner", TttContext.getContext().getWinner().getValue());

        b.putInt("gv_win_col", mWinCol);
        b.putInt("gv_win_row", mWinRow);
        b.putInt("gv_win_diag", mWinDiag);

        b.putBoolean("gv_blink_off", mBlinkDisplayOff);
        b.putParcelable("gv_blink_rect", mBlinkRect);

        return b;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
    	
    	State[] data = TttContext.getContext().getData();
        
        if (!(state instanceof Bundle)) {
            // Not supposed to happen.
            super.onRestoreInstanceState(state);
            return;
        }

        Bundle b = (Bundle) state;
        Parcelable superState = b.getParcelable("gv_super_state");

        setEnabled(b.getBoolean("gv_en", true));

        int[] d = b.getIntArray("gv_data");
        if (d != null && d.length == data.length) {
            for (int i = 0; i < d.length; i++) {
            	data[i] = State.fromInt(d[i]);
            }
        }

        int selectedCell = b.getInt("gv_sel_cell", -1);
        TttContext.getContext().setSelectedCell(selectedCell);
        State selectedValue = State.fromInt(b.getInt("gv_sel_val", State.EMPTY.getValue()));
        TttContext.getContext().setSelectedValue(selectedValue);
        State currentPlayer = State.fromInt(b.getInt("gv_curr_play", State.EMPTY.getValue()));
        TttContext.getContext().setCurrentPlayer(currentPlayer);
        State winner = State.fromInt(b.getInt("gv_winner", State.EMPTY.getValue()));
        TttContext.getContext().setWinner(winner);

        mWinCol = b.getInt("gv_win_col", -1);
        mWinRow = b.getInt("gv_win_row", -1);
        mWinDiag = b.getInt("gv_win_diag", -1);

        mBlinkDisplayOff = b.getBoolean("gv_blink_off", false);
        Rect r = b.getParcelable("gv_blink_rect");
        if (r != null) {
            mBlinkRect.set(r);
        }

        // let the blink handler decide if it should blink or not
        mHandler.sendEmptyMessage(MSG_BLINK);

        super.onRestoreInstanceState(superState);
    }

    /**
     * Récupère un bitmap dans les ressources à partir de son ID
     * @param bmpResId : identifiant de bitmap
     * @return bitmap
     */
    private Bitmap getResBitmap(int bmpResId) {
        Options opts = new Options();
        opts.inDither = false;

        Resources res = getResources();
        Bitmap bmp = BitmapFactory.decodeResource(res, bmpResId, opts);

        if (bmp == null && isInEditMode()) {
            // BitmapFactory.decodeResource doesn't work from the rendering
            // library in Eclipse's Graphical Layout Editor. Use this workaround instead.

            Drawable d = res.getDrawable(bmpResId);
            int w = d.getIntrinsicWidth();
            int h = d.getIntrinsicHeight();
            bmp = Bitmap.createBitmap(w, h, Config.ARGB_8888);
            Canvas c = new Canvas(bmp);
            d.setBounds(0, 0, w - 1, h - 1);
            d.draw(c);
        }

        return bmp;
    }
    
    
    //-----

    private class MyHandler implements Callback {
        public boolean handleMessage(Message msg) {
            if (msg.what == MSG_BLINK) {
            	int selectedCell = TttContext.getContext().getSelectedCell();
                State selectedValue = TttContext.getContext().getSelectedValue();
                if (selectedCell >= 0 && selectedValue != State.EMPTY && mBlinkRect.top != 0) {
                    mBlinkDisplayOff = !mBlinkDisplayOff;
                    invalidate(mBlinkRect);

                    if (!mHandler.hasMessages(MSG_BLINK)) {
                        mHandler.sendEmptyMessageDelayed(MSG_BLINK, FPS_MS);
                    }
                }
                return true;
            }
            return false;
        }
    }
}