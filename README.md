# Standard output word counter

Program executes third party program which emits data in JSON format to standard output. Task of 
this solution is to read emitted data, filter out invalid jsons and count words grouped by 
`event_type` property.

Valid JSON example: 
```json
{ "event_type": "baz", "data": "amet", "timestamp": 1617560854 }
```

Output is accessible at HTTP endpoint on url `http://localhost:8080/count`.

Program is developed in Scala language version 2.13.5
## How to run

To run a program from a command line a fat jar needs to be build. 

### Building jar
To build a fat jar execute in terminal:

```sbt assembly```

### Running program
Program expects two arguments which are a path to executable program which writes to standard output
and number which represents seconds for how many seconds should program operate.

If, none parameters would be provided, program will use default arguments:
 - ```blackbox.macosx```
 - ```30```


To run the jar execute in terminal:

```java -jar target/scala-2.13/whg-platform-developer-test-assembly-0.1.jar <pathToExternalProgram> <lifeDuration>```

## Output
Output example:
```json
{"baz":4,"foo":5,"bar":8}
```

- string represents event type
- number represents count of words for this event type 