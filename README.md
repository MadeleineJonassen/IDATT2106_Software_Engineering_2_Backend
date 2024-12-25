# IDATT2106_Software_Engineering_2_Backend
This is the backend repo for the Sparesti web application, a project for the course IDATT2106 - Software Engineering 2 with Agile project. <br/>
This project lasted around a month and was made by me and 5 other students at NTNU, Trondheim. </br>
Want the full project? Take a look at the frontend [here](https://github.com/MadeleineJonassen/IDATT2106_Software_Engineering_2_Frontend).
</br> </br>
This guide should help with running your downloaded backend project for the Sparesti application.
## Recommended IDE Setup
[IntelliJ IDEA](https://www.jetbrains.com/idea/) + Checkstyle Plugin (Google configuration)

## Project Setup
Ensure Docker is downloaded and running with no processes occupying port 3306, typically MySQL. 

### Install Dependencies, Compile, Build, Test, Checkstyle Check
```sh
mvn clean install
```

### Package the Application
```sh
mvn clean package
```

### Package, Run Tests, Checkstyle Check
```sh
mvn clean test
```

### Format code
```sh
mvn googleformatter:format
```

### Checkstyle Check (Google)
```sh
mvn checkstyle:check
```

### Generate Jacoco Coverage if missing
```sh
mvn jacoco:report
```

### Generate Javadoc, stored in target/site/apidocs
```sh
mvn javadoc:javadoc
```

## Running the Project
### Can run the project directly in IntelliJ by pressing run
### Can run the project by running maven command
```sh
mvn spring-boot:run
```
### Can run the project by running the built .jar file from the terminal
```sh
cd .\target\
Java -jar sparesti-0.0.1-SNAPSHOT.jar
```

### Dev Page
To simulate real money transactions, the developers have created a dev-page in the application. This is only for testing the application and would not be in production if a real-world banksystem was in place. Here a user can go to: 
```sh
localhost:5173/dev
```
This page will prompt you for a transaction sum, bank account, transaction description and transaction category. To create an outgoing transaction, put in a negative number corresponding to the transaction amount. This page has no confirmation after pressing the button, so we suggest having the developer tab open to make sure the requests go through.

To enable the application to generate suggestions for saving challenges, it first calculates the average weekly expenditure for each category over the past month. This amount must exceed the category’s suggested minimum weekly expenditure, which can be found in the suggested_amount field of the transaction category database table. If the application fails to display any saving challenges, it is likely because the monthly expenditure within a category does not meet the minimum threshold, or there is already an active saving challenge associated with that category.

