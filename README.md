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

|  |Value|Description|
|--|-----|-----------|
|**Path**|`/<userid>/login`|Requests the creation of a new session key. The session key is valid for the amount of minutes configured in the server. A new session key is created every time the endpoint is called.|
|**Method**|`GET`||
|**Response**|`<sessionkey>`|Unique string that represent the session.|

Example:

    curl http://localhost:8080/100/login -> 1B4EB7BE47F046E98E1DC458B80B2D2C

### Score

|  |Value|Description|
|--|-----|-----------|
|**Path**|`/<levelid>/score?sessionkey=<sessionkey>`|Method can be called several times poer user and level. Requests with invalid session keys are ignored.|
|**Method**|`POST`||
|**Request Body**|`<score>`|Integer number that represents the users score for the level.|
|**Response**| |Empty response.|

Example:

    curl -X "POST" "http://localhost:8080/10/score?sessionkey=1B4EB7BE47F046E98E1DC458B80B2D2C" -d "2500"

### Get high score list

|  |Value|Description|
|--|-----|-----------|
|**Path**|`/<levelid>/highscorelist`|Retrieves the high score list for a level. The list size is determined by the Application configuration.|
|**Method**|`GET`||
|**Response**|CSV of `<userid>=<score>`|Comma separated list with user id and scores.|

Example:

    curl http://localhost:8080/10/highscorelist -> 100=2500


## Technical Solution

The architecture of the application was made as simple as possible. It mainly consists of 4 layers.

- The Dispatcher is in charge of receiving the request and forwarding it to the appropriate controller method.
- The Controller gathers the information that it requires for processing the request.
- The Service applies the business logic to the received request.
- The DAO is in charge the CRUD operation on the data.

![Class Diagram](https://raw.githubusercontent.com/Oreste-Luci/httpserver-gamescores/master/images/architecture-layers.png)



![Class Diagram](https://raw.githubusercontent.com/Oreste-Luci/httpserver-gamescores/master/images/class-diagram.png)

Improvements

- removed expired tokens with scheduler to reduce memory consumption
