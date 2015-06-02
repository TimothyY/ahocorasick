package timothyyudi.ahocorasick.model;

import java.util.HashMap;

public class CopyOfState {

	char stateContentCharacter;
	boolean fullWord;
	CopyOfState parent, failState;
	HashMap<String, CopyOfState> nextStateCollection;
	
	/**Called when root is created.*/
	public CopyOfState(){
		super();
		this.stateContentCharacter = '\u0000';
		this.fullWord = false;
		this.parent = this;
		this.nextStateCollection = new HashMap<String, CopyOfState>();
		this.failState = this;
	}
	
	/**Called each time a new state is created*/
	public CopyOfState(CopyOfState parent, char stateContentCharacter, CopyOfState failState){
		super();
		this.stateContentCharacter = stateContentCharacter;
		this.fullWord = false;
		this.parent = parent;
		this.nextStateCollection = new HashMap<String, CopyOfState>();
		this.failState = failState;
	}

	public char getStateContentCharacter() {
		return stateContentCharacter;
	}

	public void setStateContentCharacter(char stateContentCharacter) {
		this.stateContentCharacter = stateContentCharacter;
	}

	public boolean isFullWord() {
		return fullWord;
	}

	public void setFullWord(boolean fullWord) {
		this.fullWord = fullWord;
	}

	public CopyOfState getParent() {
		return parent;
	}

	public void setParent(CopyOfState parent) {
		this.parent = parent;
	}

	public CopyOfState getFailState() {
		return failState;
	}

	public void setFailState(CopyOfState failState) {
		this.failState = failState;
	}

	public HashMap<String, CopyOfState> getNextStateCollection() {
		return nextStateCollection;
	}

	public void setNextStateCollection(HashMap<String, CopyOfState> nextStateCollection) {
		this.nextStateCollection = nextStateCollection;
	}

}
