#!/bin/python

import sys
import re
import subprocess

class TestCase:
    def __init__(self, title, input, output):
        self.title = title
        self.input = input
        self.referenceOutput = output
        self.actualOutput = ""

    def run(self):
        process = subprocess.Popen(
                        ["java -jar "
                       + "-Duser.stocks.dir=client/src/test/system/tmp "
                       + "client/target/client-*.jar"],
                       stdin=subprocess.PIPE,
                       stdout=subprocess.PIPE,
                       shell=True)
        self.actualOutput,dummy = process.communicate(
                str.encode(self.input + "\nquit\n"))
        self.actualOutput = self.actualOutput.decode("utf-8")
        self.actualOutput = self.actualOutput.split("\n")
        self.actualOutput = self.actualOutput[1:len(self.actualOutput)-2]
        self.actualOutput = "\n".join(self.actualOutput)
        print(self.actualOutput)


    def check(self, index):
        matcher = re.match(self.referenceOutput,
                self.actualOutput, re.MULTILINE)
        if matcher is None:
            sys.stderr.write(index + " failed!\n\n")
            sys.stderr.write("Expected: " + self.referenceOutput + "\n\n")
            sys.stderr.write("Actual:   " + self.actualOutput + "\n")
            print("##teamcity[testFailed name='" + self.title + "' message='"
                    + "Comparison failed' expected='" + self.referenceOutput
                    + "' actual='" + self.actualOutput + "']")


def main(arguments):
    if len(arguments) != 1:
        printUsage

    testcaseFileName = arguments[1]
    testcase = parseFileFromName(testcaseFileName)

    print("##teamcity[testStarted name='" + testcase.title + "']")
    testcase.run()
    testcase.check(testcaseFileName)
    print("##teamcity[testFinished name='" + testcase.title + "']")


def parseFileFromName(fileName):
    with open(fileName, 'r') as testcaseFile:
        content = testcaseFile.read()
        lines = content.split("\n")
        title = lines[0].replace("Title: ", "")
        input = lines[1].replace("Input: ", "")
        input = transformEscapes(input)
        output = lines[2]
        output = output.replace("Output: ", "")
        output = transformEscapes(output)
        result = TestCase(title, input, output)
        return result

def transformEscapes(string):
    string = string.replace("\\n", "\n")
    string = string.replace("\\t", "\t")
    return string

def printUsage():
    print("Usage: testcase-driver <filename>")

if __name__ == '__main__':
    main(sys.argv)
