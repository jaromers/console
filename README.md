# console
Console test application

The program can be built using maven - i.e. run
mvn clean install 
in the directory, where pom.xml is located.

The program can be run using maven too (again from the directory where the pom.xml file is located), e.g.:
mvn exec:java -Dexec.args="-f sampleFeesFile.txt -i sampleInputFile.txt"

The program accepts the following arguments:
1. -i <path_to_input_file> 
    This argument is optional. If specified, input entries from the provided input file will be read as an initialization phase.
    The format of the file should be the same as described in the task assignment:
    Input line format:
    <weight: positive number, >0, maximal 3 decimal places, . (dot) as decimal separator><space><postal code: fixed 5 digits> 
1. -f <path_to_fees_file>
    This argument is optional. If specified, entries from the provided file will be used to configure fees for different weights.
    The  format of the file should be the same as described in the task assignment:
    Line format:
    <weight: positive number, >0, maximal 3 decimal places, . (dot) as decimal separator><space><fee: positive number, >=0, fixed     
      two decimals, . (dot) as decimal separator> 
      
For most methods javadoc is provided, although it might not be ideal. 
      
The project has unit tests written for the core logic. The program was developed on Windows 10 OS, where the console blocks writes when waiting for input. To prevent this the console was put into raw mode in the program. However in this case the support for terminal operations is not ideal - e.g. the backspace is not working correctly, etc. The simultaneous output and input is handled in such a way, that the input line is repeated once the async write completes. In real life situation it would be best to separate input and output without using a single terminal/console.
