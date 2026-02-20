#!/bin/bash

# Compile all Java files
javac -d bin classes/*.java implementation/*.java implementation/DotsAndCrosses/*.java implementation/SlidingPuzzle/*.java io/*.java

# Run the Driver class
java -cp bin implementation.Driver 