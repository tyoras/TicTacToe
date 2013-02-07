/**
 * 
 */
package yoan.game.tictactoe.game.engines.events;


/**
 * @author yoan
 *
 */
public enum GraphicEvents {
	UNKNOWN(-1),
	ENABLE_NEXT_BUTTON(1),
	FINISH(2),
	REFRESH(3),
	SELECT_TURN(4),
	STOP_BLINK(5),
	WIN_STATE(6);
	
	private int mValue;

    private GraphicEvents(int value) {
        mValue = value;
    }

    public int getValue() {
        return mValue;
    }

    public static GraphicEvents fromInt(int i) {
        for (GraphicEvents s : values()) {
            if (s.getValue() == i) {
                return s;
            }
        }
        return UNKNOWN;
    }
    
}
