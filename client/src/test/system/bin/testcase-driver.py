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
                        ["java -jar -Duser.stocks.dir=client/src/test/system/tmp "
                       + "client/target/client-*.jar"],
                       stdin=subprocess.PIPE,
                       stdout=subprocess.PIPE,
                       shell=True)
        self.actualOutput,dummy = process.communicate(str.encode(self.input + "\nquit\n"))
        self.actualOutput = self.actualOutput.decode("utf-8")
        self.actualOutput = self.actualOutput.split("\n")[1:]
        self.actualOutput = self.actualOutput[0:len(self.actualOutput)-2]
        self.actualOutput = "\n".join(self.actualOutput)


    def check(self):
        matcher = re.match(self.referenceOutput, self.actualOutput)
        if matcher is None:
            print("Failed!\nExpected: " + self.referenceOutput + "\nActual:   " +
                    self.actualOutput)
            sys.exit(1)
        else:
            print("Passed  use case '" + self.title + "'")


def main(arguments):
    if len(arguments) != 1:
        printUsage

    testcaseFileName = arguments[1]
    testcase = parseFileFromName(testcaseFileName)

    print("Running use case '" + testcase.title + "'")

    testcase.run()

    testcase.check()

def parseFileFromName(fileName):
    with open(fileName, 'r') as testcaseFile:
        content = testcaseFile.read()
        lines = content.split("\n")
        title = lines[0].replace("Title: ", "")
        input = lines[1].replace("Input: ", "")
        output = lines[2]
        output = output.replace("Output: ", "")
        output = output.replace("\\n", "\n")
        output = output.replace("\\t", "\t")
        result = TestCase(title, input, output)
        return result

def printUsage():
    print("Usage: testcase-driver <filename>")


























if __name__ == '__main__':
    main(sys.argv)
