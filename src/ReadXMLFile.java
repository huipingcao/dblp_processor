

import java.io.FileInputStream;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * This is the main file to parse DBLP xml file
 * 
 * @author Huiping Cao
 * @date Dec. 9, 2013
 * Usage: java -Xmx8G -DentityExpansionLimit=1000000 -cp dblp.jar <type> <xml file>
 * E.g., 
 * java -Xmx8G -DentityExpansionLimit=1000000 -cp dblp.jar ReadXMLFile 0 data/dblp_2012_0907.xml
 * Detailed commands see "command.sh"
 * 
 * ************************************************************************************************************
 * Note: 
 * 1. The default heap size will not be sufficient. 
 * 2. Possible Errors: JAXP00010001: The parser has encountered more than "64000" entity expansions in this document; this is the limit imposed by the JDK.
 *    Solution 1: add "-DentityExpansionLimit=1000000" to the command line
 *    Solution 2: change property "entityExpansionLimit" in file "<JRE_HOME>/lib/jaxp.properties"
 * ************************************************************************************************************
 * Output graph file information, all the output files are associated with prefix "dblp<type_int>_<type_text>" 
 * 1. _edgeinfo.txt
 *    This file has the same number of rows as that in _edges.txt
 *    Each row: <edge id: int> <edge label's id: int>
 * 2. _edgelabel.txt
 *    Each row: <label id: int> <label: String>
 * 3. _edges.txt
 *    Each row: <edge id: int> <start node id: int> <end node id: int> <edge weight: float>
 * 4. _error.txt
 *    Denotes the nodes (or XML elements) that do not have any "key" string. 
 *    For example, labelName: sup keyStr:  nodeID :300332
 *    means that the element "sup" has node id 300332, but this node is not associated with any keyword
 * 5. _keywordID.txt
 *    Each row: <keyword id: int> <keyword: String>
 * 6. _nodeclear.txt
 *    This file contains the same number of rows as "_nodes.txt"
 *    Each row: similar to that of "_nodes.txt"
 *    Difference from "_nodes.txt":: the list of keyword IDs (get rid of the common words).
 *    E.g., for node with 3811562, 
 *    its keyword list in "dblp_data_graph/dblp_2013_1209/dblp0_Small_nodeclear.txt" is
 *    162626,10755392,1195947,3269783,6189621,1812803,36643,3553,15566,24578,3559,9229,175445,179,2351890,10755393,10755394
 *    its keyword list in "dblp_data_graph/dblp_2013_1209/dblp0_Small_nodes.txt" is
 *    162626,10755392,1195947,3269783,6189621,1812803,36643,3553,62,15566,28,7,24578,3559,9229,175445,179,2351890,10755393,10755394
 *    The "_nodeclear.txt" does not contain 62 (and), 28 (in), 7 (the), which are common words. 
 *    the correspondence from keyword id to keyword string can be found "dblp_data_graph/dblp_2013_1209/dblp0_Small_keywordID.txt" 
 * 7. _nodeforcheck.txt
 *    This file contains the same number of rows as "_nodes.txt"
 *    Each row: <node id: int> <node weight: float> <a list of keywords (string) associated to this node>
 *    Difference from "_nodes.txt": keyword is represented as strings
 * 8. _nodenum.txt
 *    This file contains one integer, the number of nodes in "_nodes.txt"
 *    <# of nodes: int>
 *    E.g., $ cat dblp_data_graph/dblp_2013_1209/dblp0_Small_nodenum.txt  
 *    3811563
 * 9. _nodes.txt
 *    Each row: <node id: int> <node weight: float> <a list of keyword IDs (in integer) associated to this node>
 *    The number of rows in this file should be the same to the integer in "_nodenum.txt"
 * 
 * All the types except type 4 (OnlyAuthor) has these 9 files.
 * For type 4 (OnlyAuthor), the program does not generate the following three output files: 
 * 1) _edgelabel.txt because the only edge label is co-authorship
 * 2) _edgeinfo.txt because the only edge label id is for "co-author"
 * 3) _nodeclear.txt because all the keywords are authors, and no keywords are common words
 * ************************************************************************************************************
 * 
 * To run a query, the useful files are
 * 3. _edges.txt
 * 6. _nodeclear.txt
 * 8. _nodenum.txt
 *  
 * ************************************************************************************************************
 * Different types of graphs:
 * 0 small:
 *    1) Node types: all the papers (from xml tags "article","inproceedings","proceedings","book","incollection","phdthesis","mastersthesis","www")
 *    2) Edge labels: "crossref","author", "editor" (see data/dblp0_Small_edgelabel.txt)
 * 1 bigroot:
 *   1) It has dblp as a node 
 *   2) It has "dblp" as one keyword
 * 2: BIGNOROOT
 *   1) It does not have dblp as a node, this graph is not well connected, NOT USEFUL
 *   2) It does not have "dblp as one keyword'
 * 3: SMALLWithAuthor
 *   1) Node types: all the papers and authors (type 0 (SMALL) union type 4 (OnlyAuthor))
 *   2) Edge labels: "crossref","author", "editor" (see data/dblp3_SmallAuthor_edgelabel.txt)
 * 4: OnlyAuthor
 *   1) Node types: all the authors (from xml tags: "author","editor")
 *   2) Edge labels: no edge label, only co-authorship
 * 5: PAPERAUTHOR
 *   1) Node types: same to SMALLWithAuthor (type 3) 
 *   2) Edge labels: same to SMALLWithAuthor (type 3)  (see data/dblp5_PaperAuthor_edgelabel.txt)
 *   3) 5 files are the same to SMALLWithAuthor (type 3): "_edgeinfo.txt", "_edgelabel.txt", "_edges.txt", "_error.txt", "_nodenum.txt"
 *   3) Keywords are less than the keywords for SMALLWithAuthor (type 3)
 *      4 files are different from SMALLWithAuthor (type 3): "_keywordID.txt", "_nodeclear.txt", "_nodeforcheck.txt", "_nodes.txt"
 *      The major different function is "characters()"
 *      
 * Example to show the differences between SMALL, OnlyAuthor, SMALLWithAuthor, PAPERAUTHOR
 *   Example data: data/data/dblp_2013_1209_part1.xml
 *   1. SMALL: data/dblp0_Small_nodenum.txt contains 9 nodes 
 *      For the elements with tags: www, article, inproceedings
 *   2. OnlyAuthor: data/dblp4_Author_nodenum.txt contains 8 nodes 
 *      for all the different authors (see data/dblp4_Author_keywordID.txt)
 *      E. F. Codd, Patrick A. V. Hall, Markus Tresch, C. J. Date, Michael Ley, Werner John, Dominik Ley, Joachim M&uuml;ller
 *   3. SMALLWithAuthor: data/dblp3_SmallAuthor_nodenum.txt contains 17 nodes
 *      With all the paper nodes and author nodes 
 *   4. PAPERAUTHOR: dblp5_PaperAuthor_nodenum.txt contains 17 nodes
 *      SAME to "data/dblp3_SmallAuthor_nodenum.txt"
 *      With all the paper nodes and author nodes 
 */
public class ReadXMLFile {
	
	private final static int SMALL = 0;				/*  3 Edges labels: crossref, author, editor, file prefix "dblp0_Small_" */
	private final static int BIGROOT = 1;			/* 34 Edges labels: article, author, title, ..., address; file prefix "dblp1_bigroot_"*/
	private final static int BIGNOROOT = 2;			/* 34 Edges labels: article, author, title, ..., address; file prefix "dblp2_BigNoRoot_" */
	private final static int PaperAuthorKeywordAll = 3; 	/*  3 Edges labels: crossref, author, editor; file prefix: dblp3_PaperAuthorKeywordAll_*/
	private final static int OnlyAuthor = 4;		/* no Edge label file; file prefix: dblp4_Author_*/
	private final static int PaperAuthorKeywordLess = 5;		/* same edges labels and node # to 3 (SMALLWithAuthor); file prefix: dblp5_PaperAuthorKeywordLess_*/

	private static String input = "data/dblp_2012_0907.xml";
	private static int type = SMALL;
	
	public static void main(String argv[]) {
 
	   try{
		   String xmlfile = input;
		    if(argv.length==1)
		    	type = Integer.parseInt(argv[0]);
		    else if(argv.length==2){
		    	type = Integer.parseInt(argv[0]);
		    	xmlfile = argv[1].toString();
		    	System.out.println(xmlfile);
		    }else{
		    	System.out.println("Usage: java -cp dblp.jar ReadXMLFile <type (0-5)> <xmlfile>");
		    }
		    System.out.println("Parsing file "+xmlfile+"...");
		    
		    long ts = System.currentTimeMillis();
			XMLReader xr = XMLReaderFactory.createXMLReader();
		    switch(type){
		    case SMALL: //0
				XMLHandlerSmall handlerSmall = new XMLHandlerSmall();
				xr.setContentHandler( handlerSmall );
				xr.setErrorHandler( handlerSmall );
				xr.parse( new InputSource(new FileInputStream(xmlfile) ));
				break;
		    case BIGROOT:	//1
		    	XMLHandlerBigRoot handlerRoot = new XMLHandlerBigRoot();
				xr.setContentHandler( handlerRoot );
				xr.setErrorHandler( handlerRoot );
				xr.parse( new InputSource(new FileInputStream(xmlfile) ));
				break;
		    case BIGNOROOT:	//2
				XMLHandlerNoRoot handlerNORoot = new XMLHandlerNoRoot();
				xr.setContentHandler( handlerNORoot );
				xr.setErrorHandler( handlerNORoot );
				xr.parse( new InputSource(new FileInputStream(xmlfile) ));
				break;
		    case PaperAuthorKeywordAll:	//3
		    	XMLHandlerPaperAuthorKeywordAll handlerSmallAuthor = new XMLHandlerPaperAuthorKeywordAll();
				xr.setContentHandler( handlerSmallAuthor );
				xr.setErrorHandler( handlerSmallAuthor );
				xr.parse( new InputSource(new FileInputStream(xmlfile) ));
				break;
		    case OnlyAuthor:	//4
		    	XMLHandlerOnlyAuthor handlerOnlyAuthor = new XMLHandlerOnlyAuthor();
		    	xr.setContentHandler( handlerOnlyAuthor );
				xr.setErrorHandler( handlerOnlyAuthor );
				xr.parse( new InputSource(new FileInputStream(xmlfile) ));
				break;
		    case PaperAuthorKeywordLess:	//5
		    	XMLHandlerPaperAuthorKeywordLess handlerpaperAuthor = new XMLHandlerPaperAuthorKeywordLess();
		    	xr.setContentHandler( handlerpaperAuthor );
				xr.setErrorHandler( handlerpaperAuthor );
				xr.parse( new InputSource(new FileInputStream(xmlfile) ));
				break;
		    }
			System.out.println("Finish parsing file "+xmlfile);
			long te = System.currentTimeMillis();
			System.out.println("ALL DONE, time used="+(te-ts) + " ms = "+ ((te-ts)/1000) + "sec");
			
		}catch(Exception e){
			System.out.println( e.getMessage());
			e.printStackTrace();
		}
 
   }
 
}
