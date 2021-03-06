package timothyyudi.ahocorasick.controller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;

import timothyyudi.ahocorasick.model.Output;
import timothyyudi.ahocorasick.view.MainUI;

public class Utility {

	/**read keyword file from snort rules*/
	public HashSet<String> readKeyword(File f){
		Scanner scanner;
		HashSet<String> list = new HashSet<String>();

		try {
			scanner = new Scanner(f);
			while (scanner.hasNextLine()){
				String tempStr = scanner.nextLine().trim();
				list.add(tempStr); //ambil per spasi.
			}
			scanner.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		return list;
	}
	
	/**Read input string file as inputString*/
	public String readInputString(File f, Charset encoding) throws IOException {
		RandomAccessFile rf = new RandomAccessFile(f, "r");
		byte[] encoded = new byte[(int)rf.length()];
		rf.read(encoded);
		rf.close();
		return new String(encoded, encoding);
	}
	
	/**Write output to output.txt
	 * @throws UnsupportedEncodingException 
	 * @throws FileNotFoundException */
	public void writeOutput(ArrayList<Output> outputList) throws FileNotFoundException, UnsupportedEncodingException{
		PrintWriter writer = new PrintWriter("src/timothyyudi/ahocorasick/asset/AhoCorasickOutput.txt", "UTF-8");
		for (Output output : outputList) {
			writer.println("Found "+output.getOutputString()+" @line: "+output.getLineNumber()+"("+output.getOutputStartPoint()+"-"+output.getOutputEndPoint()+")");
		}
		writer.close();
	}
	
	public static void writeAhoCorasickTime(long ahoCorasickTime){
		MainUI.processingTimer = ahoCorasickTime;
	}
	
}
