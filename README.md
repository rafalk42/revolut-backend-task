# revolut-backend-task
Revolut 2019 senior backend developer assignment.

## Build and run
This project is based on Maven build system and it follows standard practices. Additionally, for ease of use, the assembly plugin is used so that a runnable JAR with all dependencies is produced.

### Building it
To build this project, make sure that `mvn` command is available in the path and your current working directory is the root of the project. Then use following command:
```mvn clean package```

### Running it
To execute the application after successfully building it, use:
1. HTTP REST API server
```java -jar target/revolut-backend-task-1.0-jar-with-dependencies.jar -a 0.0.0.0 -p 4242```,
2. HTTP REST API client test
```java -jar target/revolut-backend-task-1.0-jar-with-dependencies.jar --functionalTest -a localhost -p 4242```.

### Full usage description:
```
usage: revolut-backend-task
 -a,--address <arg>    address to listen on or connect to
 -f,--functionalTest   start functional test
 -h,--help             print usage
 -p,--port <arg>       port to listen on or connect to
 ```

### HTTP REST API
| Path                         | Method | Description                           |
|------------------------------|--------|---------------------------------------|
| /bank/accounts               | POST   | Open new account.                     |
| /bank/accounts               | GET    | List all accounts.                    |
| /bank/accounts/<ID>          | GET    | Get info about a single account.      |
| /bank/accounts/<ID>/balance  | GET    | Get balance of a single account.      |
| /bank/accounts/<ID>/deposit  | POST   | Make a deposit to account.            |
| /bank/accounts/<ID>/withdraw | POST   | Make a withdrawal from account.       |
| /bank/accounts/<ID>          | DELETE | Close account.                        |
| /bank/transfers              | POST   | Make a transfer between two accounts. |

## Notes
My general approach was to use simple and plain Java ways, instead of relaying too much on any framework or library. This is mostly to show off rather than the best way to realise given requirements.

### Libraries used
- Spark - all-in-one HTTP server, takes care of all the HTTP and routing concerns,
- Gson - Google's JSON object based serialization/deserialization,
- SLF4J - simple logging facade for Java, used also by Spark, easy to to configure as a simple console output, but can be attached to basically any other logging implementation,
- Commons CLI - The Apache Commons CLI library provides an API for parsing command line options passed to programs,
- Unirest - Lightweight HTTP Request Client Library, only used in the REST API tester,
- JUnit - you know this one, right?

### Missing things
Aspects of this program that would require attention if this was supposed to go into production (in no particular order).
- More tests - load testing and concurrency testing is missing, my focus was on internal/backend correctness,
- Better requirements - more detailed and explicit requirements ;)
- Configuration management - an external config file, at least, or a connection to external configuration API or system.
- Logging - proper logging everywhere, preferably attach to external, central log server.
- Audit system - business layer event log. Irreplaceable when one needs to argue about the use of the API by external systems.
- Breakdown to smaller modules - this project should be split into at least couple of smaller modules, e.g. bank-domain, account-dao-service, http-rest-api-service, rockefeller-bank-service. This of course depends on the overall architecture and chosen design, but already there are too many aspects aggregated in one module.
- Transfer history - REST API methods to browse the transfer history.
- More maintanable implementation - some parts of this application have a bit of code duplication and too much boilerplate.

### Architecture
Main layers, from the top:
- HTTP REST - handles all TCP and HTTP stuff, including HTTP methods, routing, parameter/data extraction etc.,
- Bank JSON facade - a facade for the Bank API, wrapping every input and output as JSON string so that the HTTP REST API doesn't handle data serialization/deserialization,
- Bank - the business layer itself composed of abstract bank interface+domain and an example implementation,
- Account DAO - storage layer abstraction with basic implementation example, in this case a simple in-memory datastore.
