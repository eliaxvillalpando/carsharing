# carsharing

Carsharing
Carsharing is a simple and interactive car rental management system written in Java that allows managers and customers to navigate through various functionalities like creating a company, creating a car, viewing the car list, renting a car, returning a rented car, and viewing the rented car details.

Getting Started
Prerequisites
You will need a Java Development Kit (JDK 11 or later) installed on your system to run and compile this program. It uses an embedded H2 database to persist data, so no additional database setup is required.

Installation
Clone this repository to your local system and navigate into the project directory. You can compile the Main.java file using the following command:

bash
Copy code
javac Main.java
This will generate the necessary bytecode files (*.class files).

To run the program, use the command:

bash
Copy code
java Main
You can optionally pass the -databaseFileName argument followed by a filename to specify a database file for the program to use. If you don't specify this argument, the program will use a default database file named "default".

Example:

bash
Copy code
java Main -databaseFileName carsharing
Usage
Upon running the program, you will be presented with several options:

markdown
Copy code
1. Log in as a manager
2. Log in as a customer
3. Create a customer
0. Exit
Selecting 1 will allow you to log in as a manager and perform various operations like viewing the company list, creating a new company, viewing the car list, and creating a new car.
Selecting 2 will allow you to log in as a customer and perform actions like renting a car, returning a rented car, and viewing the rented car's details.
Selecting 3 will prompt you to create a new customer.
Selecting 0 will exit the program.
Contributing
Please feel free to fork this repository, make changes, and submit pull requests. If you have any questions or ideas about improvements, please open an issue.





