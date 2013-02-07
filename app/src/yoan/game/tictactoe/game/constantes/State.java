/**
 * 
 */
package yoan.game.tictactoe.game.constantes;

/**
 * @author yoan
 *
 */
public enum State {
    UNKNOWN(-3),
    WIN(-2),
    EMPTY(0),
    PLAYER1(1),
    PLAYER2(2);

    private int mValue;

    private State(int value) {
        mValue = value;
    }

    public int getValue() {
        return mValue;
    }

    public static State fromInt(int i) {
        for (State s : values()) {
            if (s.getValue() == i) {
                return s;
            }
        }
        return EMPTY;
    }
    
    public static State fromString(String s) {
    	State retour = EMPTY;
    	if (ConstValues.JOUEUR_HUMAIN_1.equals(s)) {
    		retour = State.PLAYER1;
    	} else if (ConstValues.JOUEUR_HUMAIN_2.equals(s) || ConstValues.JOUEUR_ORDI.equals(s)) {
    		retour = State.PLAYER2;
    	}
        return retour;
    }
}