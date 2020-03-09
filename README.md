# console
Console test application

The program can be built using maven - i.e. run
mvn clean install 
in the directory, where pom.xml is located.

The program can be run using maven too:
mvn exec:java -Dexec.mainClass="cz.test.console.main.Main"

The program accepts the following arguments:
1.) -i <path_to_input_file> 
    This argument is optional. If specified, input entries from the provided input file will be read as an initialization phase.
    The format of the file should be the same as described in the task assignment:
    Input line format:
    <weight: positive number, >0, maximal 3 decimal places, . (dot) as decimal separator><space><postal code: fixed 5 digits> 
2.) -f <path_to_fees_file>
    This argument is optional. If specified, entries from the provided file will be used to configure fees for different weights.
    The  format of the file should be the same as described in the task assignment:
    Line format:
    <weight: positive number, >0, maximal 3 decimal places, . (dot) as decimal separator><space><fee: positive number, >=0, fixed     
      two decimals, . (dot) as decimal separator> 
      
The project has unit tests written for the core logic, however these issues still need to be resolved:
1.) Console input/output - mixing console input and output is not solved yet. When waiting for user input, the console will currently block on most terminals. Therefore no output will be seen until user completes a line. To resolve this issue, the console has to be put in raw mode, and the input and output has to be handled explicitly.
