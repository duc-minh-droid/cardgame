## Run the game from JAR release
1. Open terminal/command prompt in the directory where cards.jar is saved
2. Run this command:
```cmd
java -jar cards.jar
```
3. Type the number of players and ENTER (if you want to use the test pack of cards, type 4)
4. Type the filename of the pack of cards (four.txt will work)

## Run the game from source code
To run the game, run the main method of the `CardGame` class. You will be prompted to enter the number of players. If you want to use the test pack of cards, enter 4 for number of players. When asked for the filename of the pack of cards, enter `four.txt`. The game should ensue, and a winner established. Players will output their turns to seperate files under the `gameOutput` directory, along with the final state of each card deck.

### Command line instructions for Mac/Linux
1. In command prompt, navigate to the root folder of the project 
2. Run the command `javac -classpath lib/junit-4.13.2.jar:src src/ CardGame.java`
3. Type the number of players and ENTER (if you want to use the test pack of cards, type 4)
4. Type the filename of the pack of cards (four.txt will work)

## Run the test suite
To run the full test suite, compile the `src` folder and then run the `CardGameTestSuite` class. This will automatically run the individual test files for each class.

### Command line instructions for Mac/Linux
1. The tests must be run from the `src` directory. To switch to it from the root folder, use:
```cmd
cd src
```
2. Compile the source code to bytecode
```cmd
javac -classpath lib/junit-4.13.2.jar:src CardGameTestSuite.java
```
3. Run the tests with JUnit
```cmd
java -classpath lib/junit-4.13.2.jar:lib/hamcrest-core-1.3.jar:bin:.:src org.junit.runner.JUnitCore CardGameTestSuite
```

To run on windows replace all colons(:) with semi-colons(;)
