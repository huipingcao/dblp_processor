
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.*;

class edgeLabelUnitSmall{
	int labelID;
	double weight;
	String labelName;
	String labelValue;
	boolean labelHave;
	edgeLabelUnitSmall(){
		labelHave = false;
		weight = 1.0;
	}
};

public class XMLHandlerSmall extends DefaultHandler{

	private String COMMONFILE = "data/common_words.txt";
	private String DERICTION = "data/dblp0_Small_";
	private String NODEFILE = DERICTION+"nodes.txt";
	private String EDGEFILE = DERICTION+"edges.txt";
	private String KEYWORDFILE = DERICTION+"keywordID.txt";
	private String EDGELABEL = DERICTION+"edgelabel.txt";
	private String EDGEINFO = DERICTION+"edgeinfo.txt";
	private String NODECLEANFILE = DERICTION+"nodeclear.txt";
	private String NODEFORCHECK = DERICTION+"nodeforcheck.txt";
	private String ERRORF = DERICTION+"error.txt";
	private String NODENUM = DERICTION+"nodenum.txt";
	
	//private BufferedReader inCommonF = null;
	
	private BufferedWriter outError = null;
	
	private BufferedWriter outNodeF = null;
	private BufferedWriter outEdgeF = null;
	private BufferedWriter outKeyIDF = null;
	private BufferedWriter outEdgeLableF = null;
	private BufferedWriter outEdgeInfoF = null;
	private BufferedWriter outNodeCleanF = null;
	private BufferedWriter outNodeNum = null;
	//check part
	private BufferedWriter outNodeForCheck = null;
	//end of check part
	
	private List<Object> qNameList = null;
	
	private HashMap<Object, Integer> keywordIDMap = null;
	private HashMap<Object, edgeLabelUnitSmall> attributeMap = null;
	private HashMap<String, Integer> edgeLabelMap = null;
	private HashMap<Object, HashMap<Object, Integer>> findNodeIDMap = null;
	private List<Object> commonList = null;
	
	private List<String> stringListOneNode = new ArrayList<String>();		//the keywords (string) of one node
	private List<Integer> keyListInOneNode = new ArrayList<Integer>(); 		//the keywords (integer) of one node
	private List<Integer> cleanKeyListInOneNode = new ArrayList<Integer>();	//the keywords (integer) of one node after removing common words
	
	//private HashMap<Integer, List<Integer>> authorWithPaper = null;//from author keyword id to papers list
	
	public int nodeID = 0;//Start from 1
	public int edgeID = 0;
	private int keywordID = 1;
	private int edgeLableID = 1;
	
	private final int SPLIT=1;
	private final int NOTSPLIT=0;
	
	XMLHandlerSmall() {
		super();
	}
	
	public void startDocument() throws SAXException{
		try {
			outNodeF = CommonFunction.openFile(NODEFILE);
			outEdgeF = CommonFunction.openFile(EDGEFILE);
			outKeyIDF = CommonFunction.openFile(KEYWORDFILE);
			outEdgeLableF = CommonFunction.openFile(EDGELABEL);
			outEdgeInfoF = CommonFunction.openFile(EDGEINFO);
			outNodeCleanF = CommonFunction.openFile(NODECLEANFILE);
			outNodeForCheck = CommonFunction.openFile(NODEFORCHECK);
			outNodeNum = CommonFunction.openFile(NODENUM);
			outError = CommonFunction.openFile(ERRORF);
			commonList = CommonFunction.readCommonList(COMMONFILE);
			
			qNameList = new ArrayList<Object>();

			//test
			String keyword[] = {"article","inproceedings","proceedings","book","incollection","phdthesis","mastersthesis","www"};
			for(int i=0;i<keyword.length;i++)
				qNameList.add(keyword[i].toLowerCase());
			//end of test
			
			//authorWithPaper = new HashMap<Integer, List<Integer>>();
			keywordIDMap = new HashMap<Object, Integer>();
			attributeMap = new HashMap<Object, edgeLabelUnitSmall>();
			edgeLabelMap = new HashMap<String, Integer>();
			//initial edgeLabelMap
			String labelArray[] = {"crossref","author", "editor"};
			for(int i=0; i<labelArray.length;i++){
				edgeLabelMap.put(labelArray[i].toLowerCase(), i+1);
				CommonFunction.writeFile(outEdgeLableF, (i+1)+" "+labelArray[i].toLowerCase());
			}
			//end of initial edgeLabelMap
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
						keywordIDMap.put(tempStr, keywordID);
						try {
							CommonFunction.writeFile(outKeyIDF,keywordID+" "+tempStr);
						} catch (IOException e1) {
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
						CommonFunction.writeFile(outKeyIDF,keywordID+" "+tempStr);
					} catch (IOException e1) {
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
	 * @param mapStr this is label name: author or crossref
	 * @param inputStr this is the new key of this label
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
		//writeFile(outAuthorIDF, nodeID+" "+insertStr);
		//outAuthorIDF.write(nodeID+" "+insertStr);
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
		if(findNodeMapContain(mapStr,inputStr))
			return findNodeIDMap.get(mapStr).get(inputStr);
		return -1;	
	}
	


	
	public void startElement(String uri, String localName, String qName, Attributes attributes)
			throws SAXException {
		
		if(!qName.equalsIgnoreCase("dblp")){
			if(!attributeMap.containsKey(qName.toLowerCase())){
				edgeLabelUnitSmall newLabel = new edgeLabelUnitSmall();
				newLabel.labelID = edgeLableID;
				newLabel.labelHave = true;
				newLabel.labelName = qName;
				attributeMap.put(qName, newLabel);
			}
			else{
				attributeMap.get(qName).labelHave = true;
			}
			if(qNameList.contains(qName.toLowerCase())){
				stringListOneNode.clear();
			    keyListInOneNode.clear();
			    cleanKeyListInOneNode.clear();
				nodeID++;
				String crossrefKey = attributes.getValue("key");
				if(!crossrefKey.isEmpty()){
					try {
						insertFindNodeIDMap("crossref",crossrefKey);
					} catch (IOException e) {
						
						e.printStackTrace();
					}
				}
				insertKeywordMap(attributes.getValue(0),NOTSPLIT);
				for(int i=0;i<attributes.getLength();i++){
					String value = attributes.getValue(i);
					stringListOneNode.add(value);
					insertKeywordMap(value,NOTSPLIT);
					int keyid = keywordIDMap.get(value);
					keyListInOneNode.add(keyid);
					if(!commonList.contains(replaceSymbol(value))){
						cleanKeyListInOneNode.add(keyid);
					}
				}
			}
		}
	}

	public void endElement(String uri, String localName, String qName)
	throws SAXException {
		if(qNameList.contains(qName.toLowerCase())){
			try {
				if(keyListInOneNode.size()!=0 && cleanKeyListInOneNode.size()!=0){
					CommonFunction.writeFile(outNodeF,nodeID,keyListInOneNode);
					CommonFunction.writeFile(outNodeCleanF, nodeID, cleanKeyListInOneNode);
					
					//for check part
					CommonFunction.writeCheck(outNodeForCheck,nodeID, stringListOneNode);
					//end of check part
				}
				else{
					CommonFunction.writeCheck(outError,nodeID , stringListOneNode);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		//System.out.println("End Element :" + qName);
	}

	public void characters(char[] ch, int start, int length)
	throws SAXException {
		Iterator<Entry<Object, edgeLabelUnitSmall>> iter = attributeMap.entrySet().iterator();
		while(iter.hasNext()){
			@SuppressWarnings("rawtypes")
			Map.Entry entryBMap = (Map.Entry) iter.next(); 
			String label = (String)entryBMap.getKey(); 
			edgeLabelUnitSmall labelC = attributeMap.get(label);
			if(qNameList.contains(label.toLowerCase())){
				attributeMap.get(label).labelHave = false;
			}
			else{
				//int type=SPLIT;
				String keyStr = new String(ch, start,length);
				if(labelC.labelHave){
					int type = SPLIT;
					if(labelC.labelName.equalsIgnoreCase("author")){
						try {
							insertFindNodeIDMap("author", keyStr);
							type = NOTSPLIT;
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					if(labelC.labelName.equalsIgnoreCase("editor")){
						try {
							insertFindNodeIDMap("editor", keyStr);
							type = NOTSPLIT;
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					
					String labelStr = labelC.labelName.toLowerCase();
					//if(edgeLabelMap.containsKey(labelStr)){
					/*if(labelC.labelName.equalsIgnoreCase("journal")){
						System.out.println(keyStr);
					}*/
					if(labelC.labelName.equalsIgnoreCase("crossref")){
						int retInt = getIDFormFindNodeMap(labelStr, keyStr);
						if(retInt != -1){
							try {
								edgeID++;
								int labelid = edgeLabelMap.get(labelStr);
								CommonFunction.writeFile(outEdgeF,edgeID+" "+nodeID+" "+retInt+" "+labelC.weight);
								CommonFunction.writeFile(outEdgeInfoF, edgeID+" "+labelid);
							} catch (IOException e) {
								e.printStackTrace();
							}
							continue;
						}
					}
					else{
						if(!keyStr.trim().isEmpty()){
							
							insertKeywordMap(keyStr,type);
							//System.out.println(labelC.labelName+" "+keyStr);
							if(type == SPLIT){
								keyStr = replaceSymbol(keyStr);
								String[] temp;
								String delimiter = " ";
								temp = keyStr.split(delimiter);
								for(int i=0; i<temp.length;i++){
									stringListOneNode.add(temp[i]);
									int keyid = keywordIDMap.get(temp[i]);
									keyListInOneNode.add(keyid);
									if(!commonList.contains(replaceSymbol(temp[i]))){
										cleanKeyListInOneNode.add(keyid);
									}
								}
								
							}
							else{
								stringListOneNode.add(keyStr);
								//System.out.println("after "+keyStr);
								int keyid = keywordIDMap.get(keyStr);
								keyListInOneNode.add(keyid);
								if(!commonList.contains(replaceSymbol(keyStr))){
									cleanKeyListInOneNode.add(keyid);
								}
							}
						}
					}//end of else	
						
				}
				attributeMap.get(label).labelHave = false;
			}
		}
	}

	public void endDocument() throws SAXException {
		try {
			CommonFunction.writeFile(outNodeNum, nodeID+"");
			CommonFunction.closeFile(outNodeF);
			CommonFunction.closeFile(outEdgeF);
			CommonFunction.closeFile(outEdgeInfoF);
			CommonFunction.closeFile(outKeyIDF);
			CommonFunction.closeFile(outEdgeLableF);
			CommonFunction.closeFile(outNodeNum);
			CommonFunction.closeFile(outNodeCleanF);
			CommonFunction.closeFile(outNodeForCheck); 
			CommonFunction.closeFile(outError);
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("END Document");
	}
	
	
	
	
	
	
}