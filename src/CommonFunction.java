



import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CommonFunction {

	public static BufferedWriter openFile(String filename) throws IOException{
		FileWriter fstream = new FileWriter(filename);
  		BufferedWriter out = new BufferedWriter(fstream);
  		return out;
  	}
	
	
	
	public static void writeFile(BufferedWriter out,String inputStr) throws IOException{
		out.write(inputStr);
		out.write("\r\n");
	}
	
	
	public static BufferedReader readFile(String filename) throws IOException{
		FileInputStream infstream = new FileInputStream(filename);
		DataInputStream in = new DataInputStream(infstream);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
  		return br;
  	}
	
	
	public static void writeFile(BufferedWriter out, int nodeid, List<Integer> inputList) throws IOException{
		if(inputList!=null){
			Iterator<Integer> iter = inputList.iterator();
			out.write(nodeid+" 1.0");
			if(iter.hasNext()){
				out.write(" "+iter.next());
			}
			while(iter.hasNext()){
				out.write(","+iter.next());
			}
			out.write("\r\n");
		}
	}
	
	public static void writeCheck(BufferedWriter out, int nodeid, List<String> inputList) throws IOException{
		if(inputList!=null){
			Iterator<String> iter = inputList.iterator();
			out.write(nodeid+" 1.0");
			if(iter.hasNext()){
				out.write(" "+iter.next());
			}
			while(iter.hasNext()){
				out.write(","+iter.next());
			}
			out.write("\r\n");
		}
	}
	
	//close a file that is open for reading
	public static void closeFile(BufferedReader in) throws IOException{
		in.close();
	}
	
	//close a file that is open for writing
	public static void closeFile(BufferedWriter out) throws IOException{
		out.close();
	}
	
	
	
	
	
	/**
	 * Read the file with common words
	 * @throws IOException
	 */
	public static List<Object> readCommonList(String commonwordFile) 
			throws IOException
   {
		List<Object> commonList = new ArrayList<Object>();
		BufferedReader inCommonF = readFile(commonwordFile);
		String strLine;
		while ((strLine = inCommonF.readLine()) != null)   {
		  	if(strLine.startsWith("#"))
		  		continue;
		  	commonList.add(strLine);
		}
		closeFile(inCommonF);
		return commonList;
	}
	
	
	
	
}


