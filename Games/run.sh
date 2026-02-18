#!/bin/bash

# Compile all Java files
javac -d bin classes/*.java implementation/*.java io/*.java

# Run the Driver class
java -cp bin Driver 