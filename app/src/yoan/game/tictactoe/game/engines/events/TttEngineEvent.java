/**
 * 
 */
package yoan.game.tictactoe.game.engines.events;

import yoan.game.engines.events.EngineEvent;

/**
 * 
 * @author yoan
 */
public class TttEngineEvent implements EngineEvent {
	private final Enum<?> code;
	private Object[] data;
	
	protected TttEngineEvent(){
		code = null;
	}
	
	public TttEngineEvent(Enum<?> code, Object... data) {
		super();
		this.code = code;
		this.data = data;
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

	/**
	 * @return the code
	 */
	public Enum<?> getCode(){
		return code;
	}

}