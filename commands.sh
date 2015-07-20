
echo "Parse TEST file: data/dblp_2013_1209_part1.xml"
java -Xmx8G -DentityExpansionLimit=1000000 -cp dblp.jar ReadXMLFile 0 data/dblp_2013_1209_part1.xml >log_dblp_2013_1209_part1_type0.txt
java -Xmx8G -DentityExpansionLimit=1000000 -cp dblp.jar ReadXMLFile 1 data/dblp_2013_1209_part1.xml >log_dblp_2013_1209_part1_type1.txt
java -Xmx8G -DentityExpansionLimit=1000000 -cp dblp.jar ReadXMLFile 2 data/dblp_2013_1209_part1.xml >log_dblp_2013_1209_part1_type2.txt
java -Xmx8G -DentityExpansionLimit=1000000 -cp dblp.jar ReadXMLFile 3 data/dblp_2013_1209_part1.xml >log_dblp_2013_1209_part1_type3.txt
java -Xmx8G -DentityExpansionLimit=1000000 -cp dblp.jar ReadXMLFile 4 data/dblp_2013_1209_part1.xml >log_dblp_2013_1209_part1_type4.txt
java -Xmx8G -DentityExpansionLimit=1000000 -cp dblp.jar ReadXMLFile 5 data/dblp_2013_1209_part1.xml >log_dblp_2013_1209_part1_type5.txt

echo "Parse data/dblp_2012_0907.xml"
java -Xmx8G -DentityExpansionLimit=1000000 -cp dblp.jar ReadXMLFile 0 data/dblp_2012_0907.xml
java -Xmx8G -DentityExpansionLimit=1000000 -cp dblp.jar ReadXMLFile 1 data/dblp_2012_0907.xml
java -Xmx8G -DentityExpansionLimit=1000000 -cp dblp.jar ReadXMLFile 2 data/dblp_2012_0907.xml
java -Xmx8G -DentityExpansionLimit=1000000 -cp dblp.jar ReadXMLFile 3 data/dblp_2012_0907.xml
java -Xmx8G -DentityExpansionLimit=1000000 -cp dblp.jar ReadXMLFile 4 data/dblp_2012_0907.xml
java -Xmx8G -DentityExpansionLimit=1000000 -cp dblp.jar ReadXMLFile 5 data/dblp_2012_0907.xml

echo "Parse data/dblp_2013_1209.xml"
java -Xmx8G -DentityExpansionLimit=1000000 -cp dblp.jar ReadXMLFile 0 data/dblp_2013_1209.xml > log_dblp_2013_1209_type0.txt
java -Xmx8G -DentityExpansionLimit=1000000 -cp dblp.jar ReadXMLFile 1 data/dblp_2013_1209.xml > log_dblp_2013_1209_type1.txt
java -Xmx8G -DentityExpansionLimit=1000000 -cp dblp.jar ReadXMLFile 2 data/dblp_2013_1209.xml > log_dblp_2013_1209_type2.txt
java -Xmx8G -DentityExpansionLimit=1000000 -cp dblp.jar ReadXMLFile 3 data/dblp_2013_1209.xml > log_dblp_2013_1209_type3.txt
java -Xmx8G -DentityExpansionLimit=1000000 -cp dblp.jar ReadXMLFile 4 data/dblp_2013_1209.xml > log_dblp_2013_1209_type4.txt
java -Xmx8G -DentityExpansionLimit=1000000 -cp dblp.jar ReadXMLFile 5 data/dblp_2013_1209.xml > log_dblp_2013_1209_type5.txt

echo "Parse data/dblp_2014_0421.xml"
java -Xmx8G -DentityExpansionLimit=1000000 -cp dblp.jar ReadXMLFile 0 data/dblp_2014_0421.xml > log_dblp_2014_0421_type0.txt
java -Xmx8G -DentityExpansionLimit=1000000 -cp dblp.jar ReadXMLFile 1 data/dblp_2014_0421.xml > log_dblp_2014_0421_type1.txt
java -Xmx8G -DentityExpansionLimit=1000000 -cp dblp.jar ReadXMLFile 2 data/dblp_2014_0421.xml > log_dblp_2014_0421_type2.txt
java -Xmx8G -DentityExpansionLimit=1000000 -cp dblp.jar ReadXMLFile 3 data/dblp_2014_0421.xml > log_dblp_2014_0421_type3.txt
java -Xmx8G -DentityExpansionLimit=1000000 -cp dblp.jar ReadXMLFile 4 data/dblp_2014_0421.xml > log_dblp_2014_0421_type4.txt
java -Xmx8G -DentityExpansionLimit=1000000 -cp dblp.jar ReadXMLFile 5 data/dblp_2014_0421.xml > log_dblp_2014_0421_type5.txt