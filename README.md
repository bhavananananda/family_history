
To run the webservce:- 
Simply Run the FamilyHistoryService.java
(The web service takes about a couple of minutes to load fully as it loads the ontology, runs the SWRL rules and does post reasoning.)


To access the startpage/loginpage simply type in the following url in a browser after the webservice starts running:-
http://localhost:8004/html/FamilyHistory.htm
Test login name: Bert
       password: bert


Ontology file:-
The ontology gets loaded from the Web3FamilyTree.owl file

JARS to be included in the project build path:-
saxon9he.jar
and 
jess.jar needs to be in the eclipse project path


Jess installation:-
Needs Jess70p2

Protege GUI Tool:-
Project 3.4.4 needs to be installed first.
Then run the Protege_3.4.4\Protege.exe
Place the Web3FamilyTree.owl as a sibling file to the Protege.exe

To open the project model and view it in protege GUI tool:-
Click on Open Project in the tool bar and select and open the Project\Protege_3.4.4\Web3FamilyTree.owl
The classes, datatype properties and object properties can be viewed.
Also the readable SWL rules can be read in the SWRL tab.
All the individuals in the system can be seen in the individuals tab.
