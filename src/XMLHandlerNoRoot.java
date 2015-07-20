

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
import java.util.Map;
import java.util.Map.Entry;

//import graphIndex.KSearchGraph;



import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.*;


class edgeLabelUnitNoRoot{
	int labelID;
	double weight;
	String labelName;
	String labelValue;
	boolean labelHave;
	edgeLabelUnitNoRoot(){
		labelHave = false;
		weight = 1.0;
	}
};
public class XMLHandlerNoRoot extends DefaultHandler{

	private String folder = "data/dblp2_BigNoRoot";
	private int ORIGINAL = 0;
	private int CLEAN = 1;
	private int CHECK = 2;
	private String NODEFILE = folder+"nodes.txt";
	private String EDGEFILE = folder+"edges.txt";
	private String KEYWORDFILE = folder+"keywordID.txt";
	private String EDGELABEL = folder+"edgelabel.txt";
	private String EDGEINFO = folder+"edgeinfo.txt";
	private String NODECLEANFILE = folder+"nodeclear.txt";
	private String NODEFORCHECK = folder+"nodeforcheck.txt";
	private String COMMONFILE = "data/common_words.txt";
	private String ERRORF = folder+"error.txt";
	private String NODENUM = folder+"nodenum.txt";
	
	private BufferedWriter outError = null;
	
	private BufferedWriter outNodeF = null;
	private BufferedWriter outEdgeF = null;
	private BufferedWriter outKeyIDF = null;
	private BufferedWriter outEdgeLableF = null;
	private BufferedWriter outEdgeInfoF = null;
	private BufferedWriter outNodeCleanF = null;
	private BufferedWriter outNodeNumF = null;
	//check part
	private BufferedWriter outNodeForCheck = null;
	//end of check part
	
	private List<Object> qNameList = null;
	
	private HashMap<Object, Integer> keywordIDMap = null;
	private HashMap<Object, edgeLabelUnitNoRoot> edgeLabelIDMap = null;
	private HashMap<Object, HashMap<Object, Integer>> findNodeIDMap = null;
	private List<Object> commonList = null;
	//private int rootID = 0;
	public int nodeID = 1;
	public int edgeID = 1;
	private int articleID = 1;
	private int keywordID = 1;
	private int edgeLableID = 1;
	
	
	private final int SPLIT=1;
	private final int NOTSPLIT=0;
	
	private String keyInArticle = "";
	private List<edgeLabelUnitNoRoot> adjList = new ArrayList<edgeLabelUnitNoRoot>();
	
	public void startDocument() throws SAXException{
		try {
			outNodeF = openFile(NODEFILE);
			outEdgeF = openFile(EDGEFILE);
			outKeyIDF = openFile(KEYWORDFILE);
			outEdgeLableF = openFile(EDGELABEL);
			outEdgeInfoF = openFile(EDGEINFO);
			outNodeCleanF = openFile(NODECLEANFILE);
			outNodeForCheck = openFile(NODEFORCHECK);
			outNodeNumF = openFile(NODENUM);
			outError = openFile(ERRORF);
			
			commonList = CommonFunction.readCommonList(COMMONFILE);
			
			articleID = -1;
			
			qNameList = new ArrayList<Object>();

			//test
			String keyword[] = {"article","inproceedings","proceedings","book","incollection","phdthesis","mastersthesis","www"};
			for(int i=0;i<keyword.length;i++)
				qNameList.add(keyword[i].toLowerCase());
			//end of test
			
			keywordIDMap = new HashMap<Object, Integer>();
			edgeLabelIDMap = new HashMap<Object, edgeLabelUnitNoRoot>();
			//nodeAdjacent = new TreeMap<Integer, String>(); 
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
				String[] temp;
				String delimiter = " ";
				temp = inputStr.split(delimiter);
				if(temp.length>1){
					for(int i=0;i<temp.length;i++){
						String tempStr = replaceSymbol(temp[i]);
						if(!keywordIDMap.containsKey(tempStr)){
							keywordIDMap.put(tempStr, keywordID);
							try {
								writeFile(outKeyIDF,keywordID+" "+tempStr);
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							keywordID++;
						}
					}
				}
				else{
					if(!keywordIDMap.containsKey(inputStr)){
						keywordIDMap.put(inputStr, keywordID);
						try {
							writeFile(outKeyIDF,keywordID+" "+inputStr);
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						keywordID++;
					}
				}
			}
			else if(split==NOTSPLIT){
				String insertStr = inputStr;
				String tempStr = insertStr;
				if(!keywordIDMap.containsKey(tempStr)){
					keywordIDMap.put(tempStr, keywordID);
					try {
						writeFile(outKeyIDF,keywordID+" "+tempStr);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					keywordID++;
				}
			}
			return true;
		}
		return false;
	}

	void showFindNodeIDMap(){
		if(findNodeIDMap == null)
			System.out.println("empty!!!");
		else{
			Iterator<Entry<Object,HashMap<Object, Integer>>> iter = findNodeIDMap.entrySet().iterator();
			while(iter.hasNext()){
				  @SuppressWarnings("rawtypes")
				  Map.Entry entryBMap = (Map.Entry) iter.next(); 
				  String srcMap = (String) entryBMap.getKey();
				  HashMap<Object, Integer> listMap = findNodeIDMap.get(srcMap);
				  Iterator<Entry<Object,Integer>> iterSecond = listMap.entrySet().iterator();
				  System.out.print(srcMap+":");
				  while(iterSecond.hasNext()){
					  @SuppressWarnings("rawtypes")
					  Map.Entry entryBMapSecond = (Map.Entry) iterSecond.next();
					  String keyStr = (String) entryBMapSecond.getKey();
					  int id = listMap.get(keyStr);
					  System.out.print("("+keyStr+","+id+")");
				  }
				  System.out.println("");
			}
		}
	}
	/**
	 * Insert element to findNodeIDMap
	 * @param mapStr
	 * @param inputStr
	 * @return false if this sting in the corresponding map already, else true
	 * @throws IOException
	 */
	boolean insertFindNodeIDMap(String mapStr, String inputStr) throws IOException{
		if(findNodeIDMap == null)
			findNodeIDMap = new HashMap<Object,HashMap<Object, Integer>>();
		HashMap<Object,Integer> nodeIDMap = findNodeIDMap.get(mapStr);
		if(nodeIDMap != null){
			if(nodeIDMap.containsKey(inputStr)){
				return false;
			}
			else{
				nodeIDMap.put(inputStr, nodeID);
			}
		}
		else{
			nodeIDMap = new HashMap<Object, Integer>();
			nodeIDMap.put(inputStr, nodeID);
			findNodeIDMap.put(mapStr, nodeIDMap);
		}
		return true;
	}
	
	/**
	 * Check map contains element or not
	 * @param mapStr
	 * @param inputStr
	 * @return
	 */
	boolean findNodeMapContain(String mapStr, String inputStr){
		if(findNodeIDMap == null)
			return false;
		//String insertStr = replaceSymbol(inputStr);
		HashMap<Object,Integer> nodeIDMap = findNodeIDMap.get(mapStr);
		if(nodeIDMap != null){
			if(nodeIDMap.containsKey(inputStr)){
				return true;
			}
		}
		return false;	
	}
	
	/**
	 * Get node id from findNodeMap
	 * @param mapStr
	 * @param inputStr
	 * @return -1 if can not find
	 */
	int getIDFormFindNodeMap(String mapStr, String inputStr){
		//String insertStr = replaceSymbol(inputStr);
		if(findNodeMapContain(mapStr,inputStr))
			return findNodeIDMap.get(mapStr).get(inputStr);
		return -1;	
	}
	
	/**
	 * Function to generate keyword list(STRING) for check file
	 * @param keywordStr
	 * @param split
	 * @param check
	 * @return
	 */
	public String generateNode2Keyword(String keywordStr, int split, int type){
		String retStr = null;
		if(type == ORIGINAL){
			if(split == SPLIT){
				if(!keywordStr.trim().isEmpty()){
					//nodeID++;
					retStr = nodeID+" 1.0";
					String[] temp;
					String delimiter = " ";
					temp = keywordStr.split(delimiter);
					if(temp.length>1){
						retStr += " "+keywordIDMap.get(replaceSymbol(temp[0]));
						for(int i=1;i<temp.length;i++){
							Object add = keywordIDMap.get(replaceSymbol(temp[i]));
							if(add != null){
								retStr += ","+(Integer)add;
							}
						}
					}
					else{
						retStr += " "+keywordIDMap.get(keywordStr);
					}
				}
			}
			else if(split == NOTSPLIT){
				if(!keywordStr.trim().isEmpty()){
					//nodeID++;
					retStr = nodeID+" 1.0";
					Object add = keywordIDMap.get(keywordStr);
					if(add != null){
						retStr += " "+add;
					}
				}
			}
		}
		else if(type == CHECK){
			if(split == SPLIT){
				if(!keywordStr.trim().isEmpty()){
					//nodeID++;
					retStr = nodeID+" 1.0";
					String[] temp;
					String delimiter = " ";
					temp = keywordStr.split(delimiter);
					if(temp.length>1){
						retStr += " "+replaceSymbol(temp[0]);
						for(int i=1;i<temp.length;i++){
							retStr += ","+replaceSymbol(temp[i]);
						}
					}
					else{
						retStr += " "+keywordStr;
					}
				}
			}
			else if(split == NOTSPLIT){
				if(!keywordStr.trim().isEmpty()){
					//nodeID++;
					retStr = nodeID+" 1.0 "+keywordStr;
				}
			}
		}
		else if(type==CLEAN){
			if(split == SPLIT){
				if(!keywordStr.trim().isEmpty()){
					//nodeID++;
					retStr = nodeID+" 1.0";
					String[] temp;
					String delimiter = " ";
					temp = keywordStr.split(delimiter);
					if(temp.length>1){
						if(!commonList.contains(replaceSymbol(temp[0])))
							retStr += " "+keywordIDMap.get(replaceSymbol(temp[0]));
						for(int i=1;i<temp.length;i++){
							if(keywordIDMap.containsKey(replaceSymbol(temp[i])))
								if(!commonList.contains(replaceSymbol(temp[i])))
									retStr += ","+keywordIDMap.get(replaceSymbol(temp[i]));
						}
					}
					else{
						if(!commonList.contains(keywordStr))
							retStr += " "+keywordIDMap.get(keywordStr);
						/*for(int i=1;i<temp.length;i++){
							if(keywordIDMap.containsKey(temp[i]))
								if(!commonList.contains(temp[i]))
									retStr += ","+keywordIDMap.get(temp[i]);
						}*/
					}
				}
			}
			else if(split == NOTSPLIT){
				if(!keywordStr.trim().isEmpty()){
					//nodeID++;
					retStr = nodeID+" 1.0";
					Object add = keywordIDMap.get(keywordStr);
					if(add != null)
						retStr += " "+add;
				}
			}
		}
		return retStr;
	}
	
	
	XMLHandlerNoRoot() {
		super();
	}

	public void startElement(String uri, String localName, String qName, Attributes attributes)
			throws SAXException {
		
		if(!qName.equalsIgnoreCase("dblp")){
			if(!edgeLabelIDMap.containsKey(qName)){
				edgeLabelUnitNoRoot newLabel = new edgeLabelUnitNoRoot();
				newLabel.labelID = edgeLableID;
				newLabel.labelHave = true;
				newLabel.labelName = qName;
				edgeLabelIDMap.put(qName, newLabel);
				try {
					writeFile(outEdgeLableF, edgeLableID+" "+qName);
					edgeLableID++;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			else{
				edgeLabelIDMap.get(qName).labelHave = true;
			}
			if(qNameList.contains(qName.toLowerCase())){
				adjList.clear();
				String crossrefKey = attributes.getValue("key");
				if(!crossrefKey.isEmpty()){
					try {
						insertFindNodeIDMap("crossref",crossrefKey);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			    keyInArticle = attributes.getValue(0);
				insertKeywordMap(attributes.getValue(0),NOTSPLIT);
				for(int i=1;i<attributes.getLength();i++){
					keyInArticle += " "+attributes.getValue(i);
					insertKeywordMap(attributes.getValue(i),NOTSPLIT);
				}
				articleID = nodeID;
				
			}
		}
	}

	public void endElement(String uri, String localName, String qName)
	throws SAXException {
		if(qNameList.contains(qName.toLowerCase())){
			try {
				if(keyInArticle!=null){
					//System.out.println(adjList.size());
					if(adjList.size()!=0){
						//nodeID++;
						String outputStr = generateNode2Keyword(keyInArticle, SPLIT,CLEAN);
						
						writeFile(outNodeF,outputStr);
						//for clean part
						if(outputStr!=null)
							writeFile(outNodeCleanF,outputStr);
						else{
							writeFile(outError,"qName: "+qName+" keyStr: "+keyInArticle+"nodeID :"+nodeID);
						}
						//end of clean part
						//for clean part
						outputStr = generateNode2Keyword(keyInArticle, SPLIT,CHECK);
						if(outputStr!=null)
							writeFile(outNodeForCheck,outputStr);
						else{
							writeFile(outError,"qName: "+qName+" keyStr: "+keyInArticle+"nodeID :"+nodeID);
						}
						//end of check part
						nodeID++;

						if(nodeID % 10000 ==0){
							System.out.println(nodeID);
						}
						//add edges part
						Iterator<edgeLabelUnitNoRoot> iter = adjList.iterator();
						while(iter.hasNext()){
							
							edgeLabelUnitNoRoot labelC = iter.next();
							String keyStr = labelC.labelValue;
							int type = SPLIT;
							if(labelC.labelName.equalsIgnoreCase("author") || labelC.labelName.equalsIgnoreCase("editor")){
								type = NOTSPLIT;
							}
							if(labelC.labelName.equalsIgnoreCase("crossref")){
								int retInt = getIDFormFindNodeMap("crossref", keyStr);
								if(retInt != -1){
									try {
										writeFile(outEdgeF,edgeID+" "+articleID+" "+retInt+" "+labelC.weight);
										writeFile(outEdgeInfoF, edgeID+" "+labelC.labelID);
										edgeID++;
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									//refNum++;
									continue;
								}
							}
							else{
								insertKeywordMap(keyStr,type);
								try {
									if(insertFindNodeIDMap(labelC.labelName,keyStr)){
										outputStr = generateNode2Keyword(keyStr, type, ORIGINAL);
										writeFile(outEdgeF,edgeID+" "+articleID+" "+nodeID+" "+labelC.weight);
										writeFile(outEdgeInfoF, edgeID+" "+labelC.labelID);
										//edgeID++;
										if(outputStr!=null){
											writeFile(outNodeF,outputStr);
											//for clean part
											outputStr = generateNode2Keyword(keyStr, type,CLEAN);
											if(outputStr!=null)
												writeFile(outNodeCleanF,outputStr);
											else{
												writeFile(outError,"labelName: "+labelC.labelName+" keyStr: "+keyStr+"nodeID :"+nodeID);
											}
											//end of clean part
											//for check part
											outputStr = generateNode2Keyword(keyStr, type,CHECK);
											if(outputStr!=null)
												writeFile(outNodeForCheck,outputStr);
											else{
												writeFile(outError,"labelName: "+labelC.labelName+" keyStr: "+keyStr+"nodeID :"+nodeID);
											}
											//end of check part
											nodeID++;
										}
										else{
											writeFile(outError,"labelName: "+labelC.labelName+" keyStr: "+keyStr+"nodeID :"+nodeID);
										}
										
										if(nodeID % 10000 ==0){
											System.out.println("nodeId="+nodeID);
										}
									} 
									//this author is in the keyword map already
									else{
											int authorID = getIDFormFindNodeMap(labelC.labelName,keyStr);
											writeFile(outEdgeF,edgeID+" "+articleID+" "+authorID+" "+labelC.weight);
											writeFile(outEdgeInfoF, edgeID+" "+labelC.labelID);
											edgeID++;
										} 
								}
								catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}//end of else
							//nodeID ++;
						}
						//end of adding edges part
					}
				}
				else{
					writeFile(outError,"qName: "+qName+" keyStr: "+keyInArticle+"nodeID :"+nodeID);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void characters(char[] ch, int start, int length)
	throws SAXException {
		Iterator<Entry<Object, edgeLabelUnitNoRoot>> iter = edgeLabelIDMap.entrySet().iterator();
		while(iter.hasNext()){
			@SuppressWarnings("rawtypes")
			Map.Entry entryBMap = (Map.Entry) iter.next(); 
			String label = (String)entryBMap.getKey(); 
			edgeLabelUnitNoRoot labelC = edgeLabelIDMap.get(label);
			if(qNameList.contains(label.toLowerCase())){
				edgeLabelIDMap.get(label).labelHave = false;
			}
			else{
				String keyStr = new String(ch, start, length);
				if(labelC.labelHave){
					if(labelC.labelName.equalsIgnoreCase("crossref")){
						int retInt = getIDFormFindNodeMap("crossref", keyStr);
						if(retInt != -1){
							adjList.add(labelC);
						}
					}
					else{
						adjList.add(labelC);
					}
					labelC.labelValue = keyStr;
				}
				edgeLabelIDMap.get(label).labelHave = false;
			}
		}
	}

	public void endDocument() throws SAXException {
		try {
			nodeID--;
			writeFile(outNodeNumF, nodeID+"");
			closeFile(outNodeF);
			closeFile(outEdgeF);
			closeFile(outEdgeInfoF);
			closeFile(outKeyIDF);
			closeFile(outEdgeLableF);
			closeFile(outNodeNumF);
			closeFile(outNodeCleanF);
			closeFile(outNodeForCheck); 
			closeFile(outError);
			
			edgeID--;
			//System.out.println("REF NUMBER !!!!!!!!!!!!!!!!!!!!!!! "+refNum);
			/*Iterator<Entry<Integer,String>> iter = nodeAdjacent.entrySet().iterator();
			writeFile(outNodeAdjacentF, (nodeID)+" "+(edgeID));
			while(iter.hasNext()){
				  @SuppressWarnings("rawtypes")
				  Map.Entry entryBMap = (Map.Entry) iter.next(); 
				  int srcid = (Integer) entryBMap.getKey();
				  String list = nodeAdjacent.get(srcid);
				  writeFile(outNodeAdjacentF, srcid+" "+list);
				  //writeFile(outNodeAdjacentF, list);
			}*/
			//closeFile(outNodeAdjacentF);
			//showFindNodeIDMap();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
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
	public void closeFile(BufferedReader in) throws IOException{
		in.close();
	}
	public void closeFile(BufferedWriter out) throws IOException{
		out.close();
	}
	
}