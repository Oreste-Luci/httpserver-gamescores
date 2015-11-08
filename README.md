# HttpServer Game Scores

Java application that keeps track of the user scores by level.

## Compiling the application

Prerequisites:

- Java SE Development Kit 8
- Apache Maven (tested with version 3.3.3)

To compile execute the following command from the root of the project:

    mvn clean package

## Running the application

### Quick start

To run the application with its default configuration you can use the following command:

    java -jar target/httpserver-scores.jar

> Note: this works if the `jar` file is in the `target` directory.

### Configuration parameters

To customize the behaviour of the application the following parameters can be passed as arguments:

|Parameter|Values|Required|Description|
|---------|------|--------|-----------|
|port|An integer value. Port numbers less than 1024 require root permissions.|No|Port number on which the application will listen for requests.|
|executor|`fixed` or `cached`|No|This parameter determines which strategy to use for HttpServer Executor, it can either be `newCachedThreadPool` or `newFixedThreadPool`. More info [here](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/Executors.html).|
|poolSize|An integer value.|No|This only applies if `fixed` executor is selected.|

For example:

    java -jar target/httpserver-scores.jar -port=8080 -executor=fixed -poolSize=10

## Endpoints

### Login

<table>
    <tr>
        <th>Path</th>
        <td></td>
        <td></td>
    </tr>
    <tr>
        <th>Method</th>
        <td></td>
        <td></td>
    </tr>
    <tr>
        <th>Response</th>
        <td></td>
        <td></td>
    </tr>
    <tr>
        <th></th>
        <td></td>
        <td></td>
    </tr>
</table>


![Class Diagram](https://raw.githubusercontent.com/Oreste-Luci/httpserver-gamescores/master/images/class-diagram.png)

Improvements

- removed expired tokens with scheduler to reduce memory consumption
