#!/bin/sh
echo $1 - Parser
java -cp GraphLayout.jar bsh.Parser $1
echo $1 - Interpreter
java -cp GraphLayout.jar bsh.Interpreter $1 2 2 1
