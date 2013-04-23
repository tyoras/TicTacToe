/**
 * 
 */
package yoan.game.tictactoe.game.engines.events;

import yoan.game.engines.events.EngineEvent;

/**
 * Evenement de communication entre les moteurs du jeu
 * @author yoan
 */
public class TttEngineEvent implements EngineEvent {
	/** identifiant de l'événement */
	private final Enum<?> code;
	/** données à transmettre par l'évenement */
	private Object[] data;
	
	protected TttEngineEvent(){
		code = null;
	}
	
	public TttEngineEvent(Enum<?> code, Object... data) {
		this.code = code;
		this.data = data;
	}
	
	/**
	 * @return the code
	 */
	public Enum<?> getCode(){
		return code;
	}

	/**
	 * @return the data
	 */
	public Object[] getData(){
		return data;
	}

	/**
	 * @param data the data to set
	 */
	public void setData(Object[] data){
		this.data= data;
	}
}