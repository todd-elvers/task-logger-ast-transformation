#TaskLogger AST Transformation

The purpose of this transformation is to simplify logging messages like: <br/>
*Performing task...* <br/>
and <br/>
*Performing task...Done.* <br/>

by simply annotation a method with something like `@TaskLogger`, thereby removing the boilerplate of adding that to every method and the pain of seeing it in every method.

### Example code
```groovy
class Service {
    @TaskLogger
    List<String> executeTask(){
        ...
    }
}
```

or

```groovy
class Service {
    @TaskLogger(
        startingMessage = {"Starting at " + new Date().format("MM/dd/yyyy")},
        endingMessage = {"Ending at " + new Date().format("MM/dd/yyyy")}
    )
    List<String> executeTask(){
        ...
    }
}
```

####NOTE
No releases currently, this is still in alpha and may remain like that forever, depending on priorities.