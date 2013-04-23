package yoan.game.tictactoe.game.constantes;

/**
 * Enum servants au différents états du jeu et des cases
 * @author yoan
 */
public enum State {
    UNKNOWN(-3),
    WIN(-2),
    EMPTY(0),
    PLAYER1(1),
    PLAYER2(2);

    /** Code numérique associée à un état */
    private int code;

    private State(int code) {
    	this.code = code;
    }

    /**
     * Récupère le code numérique d'un état
     * @return code numérique
     */
    public int getCode() {
        return code;
    }

    /**
     * Récupère un état depuis son code
     * @param i : code
     * @return l'état correspondant au code, sinon EMPTY
     */
    public static State fromInt(int i) {
        for (State s : values()) {
            if (s.getCode() == i) {
                return s;
            }
        }
        return EMPTY;
    }
    
    /**
     * Récupère l'état associé à un joueur
     * @param player : identifiant du joueur
     * @return l'état associé, sinon EMPTY
     */
    public static State getPlayerState(String player) {
    	State retour = EMPTY;
    	if (ConstValues.JOUEUR_HUMAIN_1.equals(player)) {
    		retour = State.PLAYER1;
    	} else if (ConstValues.JOUEUR_HUMAIN_2.equals(player) || ConstValues.JOUEUR_ORDI.equals(player)) {
    		retour = State.PLAYER2;
    	}
        return retour;
    }
}