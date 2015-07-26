package timothyyudi.ahocorasick.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import timothyyudi.ahocorasick.model.Output;
import timothyyudi.ahocorasick.model.State;

public class AhoCorasick {

	State root = new State();
	State currState;
	int keywordInsertionCounter, lineNumberCounter, columnNumberCounter;
	String tempOutputStr = "";
	String reversedTempOutputStr = "";
	HashMap<String, Output> outputMap = new HashMap<>();
	long ahoCorasickTimeTotal=0;
	long ahoCorasickTimeFragment=0;
	long algoStart, algoEnd;
	
	public static ArrayList<Output> outputList = new ArrayList<>();
	
	/**A function to match input string against constructed AhoCorasick trie*/
	public void patternMatching(String inputString){
		currState = root;
		lineNumberCounter=1;
		columnNumberCounter=1;
		int inputStringLength = inputString.length();
		
//		algoStart=System.nanoTime();
		for (int i = 0; i < inputStringLength; i++) { //as long as there is an input
			
			columnNumberCounter++;
			if(inputString.charAt(i)=='\n'){
				lineNumberCounter++;
				columnNumberCounter=1;
			}
			
			algoStart=System.nanoTime();
			while (goTo(currState, Character.toString(inputString.charAt(i)))==null&&!currState.equals(root)) { //repeat fail function as long goTo function is failing
				currState= failFrom(currState);
			}
			if(goTo(currState, Character.toString(inputString.charAt(i)))!=null){
				currState = goTo(currState, Character.toString(inputString.charAt(i))); //set the current node to the result of go to function
				prepareOutput(currState,lineNumberCounter, columnNumberCounter);
			}
			algoEnd=System.nanoTime();
			ahoCorasickTimeFragment=algoEnd-algoStart;
			ahoCorasickTimeTotal+=ahoCorasickTimeFragment;

		}
		Utility.writeAhoCorasickTime(ahoCorasickTimeTotal);
//		algoEnd=System.nanoTime();
//		Utility.writeAhoCorasickTime(algoEnd-algoStart);
		
	}
	
	/**A function to move from 1 node of a trie to the others based on next input character*/
	private State goTo(State node, String nextInputChar){
		return node.getNextStateCollection().get(nextInputChar);
	}
	
	/**A function to move from 1 node of a trie to it's fail node*/
	private State failFrom(State node){
		tempOutputStr="";
		return node.getFailState();
	}
	
	/**Prepare AhoCorasick goto function/ successful state of AhoCorasick trie*/
	public void prepareGoToFunction(HashSet<String> keywords){
		for (String string : keywords) {
			enterKeyword(string);
		}
	}
	
	/**insert keywords to trie*/
	private void enterKeyword(String keyword){
		currState = root;
		keywordInsertionCounter = 0;

		while(keywordInsertionCounter<keyword.length() && goTo(currState, Character.toString(keyword.charAt(keywordInsertionCounter)))!=null){ //while state already exist then go there.
			currState = goTo(currState, Character.toString(keyword.charAt(keywordInsertionCounter)));
			keywordInsertionCounter++;
		}
	
		while(keywordInsertionCounter<keyword.length() && goTo(currState, Character.toString(keyword.charAt(keywordInsertionCounter)))==null){ //while state doesnt exist then create new node and go there
			currState.getNextStateCollection().put(Character.toString(keyword.charAt(keywordInsertionCounter)), new State(currState, Character.toString(keyword.charAt(keywordInsertionCounter)), root));
			currState = goTo(currState, Character.toString(keyword.charAt(keywordInsertionCounter)));
			if(keywordInsertionCounter==keyword.length()-1){
				currState.setFullKeyword(keyword);
			}
			keywordInsertionCounter++;
		}
	}
	
	/**Create the fail fall back state of AhoCorasick trie*/
	public void prepareFailFromFunction(){
		LinkedList<State> queue = new LinkedList<State>(); //a linked list is needed for BFS
		
		for (State state : root.getNextStateCollection().values()) {
			queue.add(state);
			state.setFailState(root);
		}
		
		State tempState;
		
		while(!queue.isEmpty()){
			tempState = queue.pop(); //pop node and get the childrens
			for (State state: tempState.getNextStateCollection().values()) { //implementation differ based on nextStateCollection data structure
				queue.add(state);
				currState=failFrom(tempState);
				while(goTo(currState, state.getStateContentCharacter())==null&&!currState.equals(root)){ //while fail 
					currState = failFrom(currState); //current state = failState	
				}//exit while when found a match from goTo of a failState or when it reach root
				if(goTo(currState, state.getStateContentCharacter())!=null){
					state.setFailState(goTo(currState, state.getStateContentCharacter()));
				}
			}
		}
	}
	
	/**prepare output for the matching keywords found*/
	private void prepareOutput(State state,int lineNumber, int endColumnNumber){
		if(state.getFullKeyword()!=null){//jika currNode = fullword
			outputList.add(new Output(state.getFullKeyword(), lineNumber, endColumnNumber-(state.getFullKeyword().length()), endColumnNumber-1));
		}
		
		while(!failFrom(state).equals(root)){//jika state tersebut punya fail node yang bukan root
			state = failFrom(state);
			if(state.getFullKeyword()!=null){//jika failState == fullword
				outputList.add(new Output(state.getFullKeyword(), lineNumber, endColumnNumber-(state.getFullKeyword().length()), endColumnNumber-1));
			}
		}
	}
	
}
