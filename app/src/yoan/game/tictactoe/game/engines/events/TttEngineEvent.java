/**
 * 
 */
package yoan.game.tictactoe.game.engines.events;

import yoan.game.engines.events.EngineEvent;

/**
 * 
 * @author yoan
 */
public class TttEngineEvent extends EngineEvent {
	private String data;

	public TttEngineEvent(String data) {
		setData(data);
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}
}