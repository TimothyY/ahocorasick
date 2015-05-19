package timothyyudi.ahocorasick.controller;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
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
	
	/**A function to match input string against constructed AhoCorasick trie*/
	public void patternMatching(String inputString){
		currState = root;
		lineNumberCounter=1;
		columnNumberCounter=0;
		
		for (int i = 0; i < inputString.length(); i++) { //as long as there is an input
			
			columnNumberCounter++;
			if(inputString.charAt(i)=='\n'){
				lineNumberCounter++;
				columnNumberCounter=1;
			}
			
			algoStart=System.currentTimeMillis();
			
			while (goTo(currState, inputString.charAt(i))==null&&!currState.equals(root)) { //repeat fail function as long goTo function is failing
				currState= failFrom(currState);
			}
			if(goTo(currState, inputString.charAt(i))!=null){
				currState = goTo(currState, inputString.charAt(i)); //set the current node to the result of go to function
				prepareOutput(currState,lineNumberCounter, columnNumberCounter);
			}
			algoEnd=System.currentTimeMillis();
			ahoCorasickTimeFragment=algoEnd-algoStart;
			ahoCorasickTimeTotal+=ahoCorasickTimeFragment;
		}
		writeAhoCorasickTime(ahoCorasickTimeTotal);
	}
	
	/**A function to move from 1 node of a trie to the others based on next input character*/
	private State goTo(State node, char nextInputChar){
		return node.getNextStateCollection().get(Character.toString(nextInputChar));
	}
	
	/**A function to move from 1 node of a trie to it's fail node*/
	private State failFrom(State node){
		tempOutputStr="";
		return node.getFailState();
	}
	
	/**Prepare AhoCorasick goto function/ successful state of AhoCorasick trie*/
	public void prepareGoToFunction(ArrayList<String> keywords){
		//State currNode = root;
		for (int i = 0; i < keywords.size(); i++) {
			enterKeyword(keywords.get(i));
		}
	}
	
	/**insert keywords to trie*/
	private void enterKeyword(String keyword){
		currState = root;
		keywordInsertionCounter = 0;

		while(keywordInsertionCounter<keyword.length() && goTo(currState, keyword.charAt(keywordInsertionCounter))!=null){ //while state already exist then go there.
			currState = goTo(currState, keyword.charAt(keywordInsertionCounter));
			keywordInsertionCounter++;
		}
	
		while(keywordInsertionCounter<keyword.length() && goTo(currState, keyword.charAt(keywordInsertionCounter))==null){ //while state doesnt exist then create new node and go there
			currState.getNextStateCollection().put(Character.toString(keyword.charAt(keywordInsertionCounter)), new State(currState, keyword.charAt(keywordInsertionCounter), root));
			currState = goTo(currState, keyword.charAt(keywordInsertionCounter));
			if(keywordInsertionCounter==keyword.length()-1){
				currState.setFullWord(true);
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
	private void prepareOutput(State currNode,int lineNumber, int endPoint){
		if(currNode.isFullWord()==true){//jika currNode = fullword
			traceAncestorAndPrint(currNode, lineNumber, endPoint);//telusuri sampai atas dan cetak
		}
		
		if(!failFrom(currNode).equals(root)){//jika state tersebut punya fail node yang bukan root
			if(failFrom(currNode).isFullWord()==true){//jika failState == fullword
				traceAncestorAndPrint(failFrom(currNode), lineNumber, endPoint);//telusuri failState sampai atas dan cetak
			}
		}
	}
	
	/**Trace the ancestor of a node and print it sequentially*/
	private void traceAncestorAndPrint(State state, int lineNumber, int endPoint){
		while(!state.equals(root)){
			tempOutputStr=tempOutputStr+state.getStateContentCharacter();
			state=state.getParent();
		}
		reversedTempOutputStr = new StringBuilder(tempOutputStr).reverse().toString();
		outputMap.put(reversedTempOutputStr+"l"+lineNumber+"c"+endPoint, new Output(reversedTempOutputStr, lineNumber, endPoint-reversedTempOutputStr.length(), endPoint));
	}
	
	/**Write output to output.txt
	 * @throws UnsupportedEncodingException 
	 * @throws FileNotFoundException */
	public void writeOutput() throws FileNotFoundException, UnsupportedEncodingException{
		PrintWriter writer = new PrintWriter("src/timothyyudi/ahocorasick/asset/AhoCorasickOutput.txt", "UTF-8");
		for (Output output : outputMap.values()) {
			writer.println("Found "+output.getOutputString()+" @line: "+output.getLineNumber()+"("+output.getOutputStartPoint()+"-"+output.getOutputEndPoint()+")");
		}
		writer.close();
	}
	
	public void writeAhoCorasickTime(long ahoCorasickTime){
		System.out.println("[TRUE] AhoCorasick Time: "+ahoCorasickTime+" ms");
	}
}
