/**
 * 
 */
package yoan.game.tictactoe.activities;

/**
 * @author yoan
 */
public enum Navigation{
	ACTION_QUIT(1, "QUIT");
	
	private int resultCode;
	
	private String action;

	/**
	 * @param code
	 */
	private Navigation(int code, String action){
		this.resultCode= code;
		this.action = action;
	}

	/**
	 * @return the resultCode
	 */
	public int getResultCode(){
		return resultCode;
	}

	/**
	 * @return the action
	 */
	public String getAction(){
		return action;
	}
}