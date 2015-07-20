
This small project is to parse DBLP XML file, to generate graph files in plain text. 

1. Input data file
   1) The DBLP XML file from the DBLP web site.
   2) dblp.dtd, which is put at the same folder of the jar file by default.  

2. Output data files
   It can generate five types of output graph files. The types are at ReadXMLFile.java. 
   For each type of graph, it generates about 7 files. 
   The detailed explanation of the differences of the five types and the 9 output files are at the header comments in the main file "ReadXMLFile.java".

3. To use the code in this project, run the following steps (all the commands are tested under Ubuntu and Mac OS)
   1). build the jar file
       $ ant build
       Note: if you do not have ant installed, please install ant first. 

   2) Run the processor (Main file: ReadXMLFile.java)
      $ java -cp dblp.jar ReadXMLFile <type> <xml file>
      E.g., 
      $ java -cp dblp.jar ReadXMLFile 1 data/dblp_2012_0907.xml
      Note: A folder called "data" which contains the file "common_words.txt" needs to exist (by default, the distribution of this project contains this).
            The output text files are written to this "data" folder.
      
      More detailed commands see "commands.sh". 
   
4. Data archiving (Cao group at NMSU)
   After running the commands,  copy the output txt files in the data folder to folder "dblp_data_graph"
   Commit the new data to the server. 


5. To run a query, the useful files are
1) _edges.txt
2) _nodeclear.txt
3) _nodenum.txt