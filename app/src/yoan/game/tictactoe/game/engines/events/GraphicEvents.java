/**
 * 
 */
package yoan.game.tictactoe.game.engines.events;


/**
 * Evenements du graphic engine
 * @author yoan
 */
public enum GraphicEvents {
	/** Evenement inconnu */
	UNKNOWN(-1),
	/** Activation du bouton "Next" */
	ENABLE_NEXT_BUTTON(1),
	/** Fin d'un tour */
	FINISH(2),
	/** Refresh de la vue */
	REFRESH(3),
	/** Passage au tour suivant */
	SELECT_TURN(4),
	/** Arret du clignotement */
	STOP_BLINK(5),
	/** Victoire d'un joueur */
	WIN(6);
	
	/** Code numérique associée à un évenement */
	private int numCode;

    private GraphicEvents(int numCode) {
        this.numCode = numCode;
    }

    /**
     * Récupère le code numérique d'un évenement
     * @return code numérique
     */
    public int getnumCode() {
        return numCode;
    }

    /**
     * Récupère un évenement depuis son code
     * @param i : code
     * @return l'état correspondant au code, sinon UNKNOWN
     */
    public static GraphicEvents fromInt(int i) {
        for (GraphicEvents s : values()) {
            if (s.getnumCode() == i) {
                return s;
            }
        }
        return UNKNOWN;
    }
   
}