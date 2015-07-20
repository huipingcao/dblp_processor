

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

//import graphIndex.KSearchGraph;


import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.*;


class edgeLabelUnitAuthor{
	int labelID;
	double weight;
	String labelName;
	String labelValue;
	boolean labelHave;
	edgeLabelUnitAuthor(){
		labelHave = false;
		weight = 1.0;
	}
};
public class XMLHandlerOnlyAuthor extends DefaultHandler{

	private String COMMONFILE = "data/common_words.txt";
	private String DERICTION = "data/dblp4_Author_";
	private String NODEFILE = DERICTION+"nodes.txt";
	private String EDGEFILE = DERICTION+"edges.txt";
	private String NODEFORCHECK = DERICTION+"nodeforcheck.txt";
	private String ERRORF = DERICTION+"error.txt";
	private String NODENUM = DERICTION+"nodenum.txt";
	private String KEYWORDFILE = DERICTION+"keywordID.txt";
	
	private BufferedWriter outError = null;
	
	private BufferedWriter outNodeF = null;
	private BufferedWriter outEdgeF = null;
	private BufferedWriter outNodeNumF = null;
	private BufferedWriter outKeyIDF = null;
	//check part
	private BufferedWriter outNodeForCheck = null;
	//end of check part
	
	private List<Object> qNameList = null;
	private List<Object> commonList = null;
	
	private boolean author = false;
	private boolean haveYear = false;
	
	private int totalNum = 0;
	
	private HashMap<Object, Integer> keywordIDMap = null;
	private String year = "";
	
	private List<String> authorList = new ArrayList<String>();
	
	private HashMap<Integer, List<Integer>> edgeMap = new HashMap<Integer, List<Integer>> ();
	
	public int nodeID = 0;//Start from 1
	public int authorID = 0;
	public int edgeID = 0;
	private int keywordID = 0;
	
	//private int maxEdge = 0;
	
	private final int SPLIT=1;
	private final int NOTSPLIT=0;
	
	

	private StringBuffer str = null;
	
	public void startDocument() throws SAXException{
		//graph = new KSearchGraph();
		try {
			outNodeF = openFile(NODEFILE);
			outEdgeF = openFile(EDGEFILE);
			outKeyIDF = openFile(KEYWORDFILE);
			/*outEdgeLableF = openFile(EDGELABEL);
			outEdgeInfoF = openFile(EDGEINFO);
			outNodeCleanF = openFile(NODECLEANFILE);*/
			outNodeForCheck = openFile(NODEFORCHECK);
			outNodeNumF = openFile(NODENUM);
			outError = openFile(ERRORF);
			
			commonList = CommonFunction.readCommonList(COMMONFILE);
			
			qNameList = new ArrayList<Object>();

			//qName
			String keyword[] = {"article","inproceedings","proceedings","book","incollection","phdthesis","mastersthesis","www"};
			for(int i=0;i<keyword.length;i++)
				qNameList.add(keyword[i].toLowerCase());
			//end of qName
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * From keyword, remove useless symbols
	 * @param inputStr
	 * @return
	 */
	private String replaceSymbol(String inputStr){
		return inputStr.replace(",", "").replace(".","").replace("'", "").replace("\"", "");
	}

	
	/**
	 * Insert keyword to map
	 * @param inputStr
	 * @param split
	 * @return
	 */
	public boolean insertKeywordMap(String inputStr, int split){
		if(keywordIDMap==null)
			keywordIDMap = new HashMap<Object, Integer>();
		if(!inputStr.isEmpty()){
			if(split==SPLIT){
				String insertStr = replaceSymbol(inputStr);
				String[] temp;
				String delimiter = " ";
				temp = insertStr.split(delimiter);
				for(int i=0;i<temp.length;i++){
					String tempStr = temp[i];
					if(!keywordIDMap.containsKey(tempStr)){
						keywordID++;
						keywordIDMap.put(tempStr, keywordID);
						try {
							writeFile(outKeyIDF,keywordID+" "+tempStr);
							writeFile(outNodeF,keywordID+" 1.0 "+keywordID);
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
				}
			}
			else if(split==NOTSPLIT){
				String insertStr = inputStr;
				String tempStr = insertStr;
				if(!keywordIDMap.containsKey(tempStr)){
					keywordID++;
					keywordIDMap.put(tempStr, keywordID);
					try {
						writeFile(outKeyIDF,keywordID+" "+tempStr);
						writeFile(outNodeF,keywordID+" 1.0 "+keywordID);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
			return true;
		}
		return false;
	}

	
	XMLHandlerOnlyAuthor() {
		super();
	}

	/*private KSearchGraph graph = null;
	
	public KSearchGraph getGraph(){
		return graph;
	}*/
	
	
	public void startElement(String uri, String localName, String qName, Attributes attributes)
	throws SAXException {
		
		str = new StringBuffer();  
		if(qNameList.contains(qName.toLowerCase())){
			authorList.clear();
			author = false;
			year = "";
			haveYear=false;
		}
		if(qName.toLowerCase().equals("author") || qName.toLowerCase().equals("editer")){
			author = true;
		}
		if(qName.toLowerCase().equals("year")){
			haveYear = true;
		}
	}

	public void endElement(String uri, String localName, String qName)
	throws SAXException {
		try {
			String key = str.toString();
			if(qName.toLowerCase().equals("author")){
				authorList.add(key);
				insertKeywordMap(key, NOTSPLIT);
			}
			if(qNameList.contains(qName.toLowerCase())){
				if(authorList.size() != 0){
					for(int i=0;i<authorList.size();i++){
						String keyStr = authorList.get(i);
						if(keyStr.equals("Wei Wang")){
							totalNum = totalNum+authorList.size()-1;
						}
						int srcid =keywordIDMap.get(keyStr);
						//int srcid = authorList.get(i);
						for(int j=i+1;j<authorList.size();j++){
							int tgtid = keywordIDMap.get(authorList.get(j));
							List<Integer> srcnodeList = edgeMap.get(srcid);
							List<Integer> tgtnodeList = edgeMap.get(tgtid);
							if(srcnodeList == null){
								srcnodeList = new ArrayList<Integer>();
								edgeMap.put(srcid, srcnodeList);
							}
							if(tgtnodeList == null){
								tgtnodeList = new ArrayList<Integer>();
								edgeMap.put(tgtid, tgtnodeList);
							}
							if(!srcnodeList.contains(tgtid) && !tgtnodeList.contains(srcid)){
								edgeID ++;
								if(year.equals("")){
									year = "1.0";
								}
								writeFile(outEdgeF,edgeID+" "+srcid+" "+tgtid+" "+year);
								srcnodeList.add(tgtid);
								tgtnodeList.add(srcid);
							}
							
						}
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void characters(char[] ch, int start, int length)
	throws SAXException {
		str.append(ch, start, length);  
		/*if(author){
			String tag = "author";
			int stIdx = ch.toString().indexOf("<"+tag);
			int edIdx = keyStr.indexOf("</"+tag);
			edIdx = keyStr.indexOf('>', edIdx) + 1;
			
			keyStr = keyStr.substring(stIdx, edIdx);//new String(ch, start, length);
			if(keyStr.equals("J")){
				test = true;
			}
			author = false;
			//insertKeywordMap(keyStr, NOTSPLIT);
			//authorID = keywordIDMap.get(keyStr);
			//StringBuffer buffer = new StringBuffer();
			//authorList.add(buffer);
			
		}*/
		if(haveYear){
			year = new String(ch, start, length);
			haveYear = false;
		}
			
		
	}

	public static String getElement(String xmlStr, String tag) {
	    int stIdx = xmlStr.indexOf("<"+tag);
	    if ( stIdx == -1 ) return "";
	    stIdx = xmlStr.indexOf('>', stIdx) + 1;
	    int edIdx = xmlStr.indexOf("</"+tag);
	    return xmlStr.substring(stIdx, edIdx);
	}
	
	
	public void endDocument() throws SAXException {
		try {
			writeFile(outNodeNumF, keywordID+"");
			closeFile(outNodeF);
			closeFile(outEdgeF);
			//closeFile(outEdgeInfoF); //this is not needed because the only edge label is coauthorship
			closeFile(outKeyIDF);
			//closeFile(outEdgeLableF); //this is not needed because the only edge label is coauthorship
			closeFile(outNodeNumF);
			closeFile(outNodeForCheck); 
			closeFile(outError);
			System.out.println("total number of Stefano Quer "+ totalNum);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("END Document");
	}
	
	
	public BufferedReader readFile(String filename) throws IOException{
		FileInputStream infstream = new FileInputStream(filename);
		DataInputStream in = new DataInputStream(infstream);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
  		return br;
  	}
	
	public BufferedWriter openFile(String filename) throws IOException{
		FileWriter fstream = new FileWriter(filename);
  		BufferedWriter out = new BufferedWriter(fstream);
  		return out;
  	}
	public void writeFile(BufferedWriter out,String inputStr) throws IOException{
		out.write(inputStr);
		out.write("\r\n");
	}
	
	public void writeFile(BufferedWriter out, int nodeid, List<Integer> inputList) throws IOException{
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
	
	public void writeCheck(BufferedWriter out, int nodeid, List<String> inputList) throws IOException{
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
	
	public void closeFile(BufferedReader in) throws IOException{
		in.close();
	}
	public void closeFile(BufferedWriter out) throws IOException{
		out.close();
	}
	
}