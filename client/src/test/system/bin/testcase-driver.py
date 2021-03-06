#!/bin/python

import sys
import re
import subprocess


class TestCase:
    def __init__(self, title, input, output, count=1):
        self.title = title
        self.input = input
        self.referenceOutput = output
        self.actualOutput = ""
        self.count = count

    def run(self, process):
        process.stdin.write(self.input + "\r\n")
        process.stdin.flush()

        for i in range(0, self.count):
            self.actualOutput = self.actualOutput + consumeOutput(process.stdout)

        print("'" + self.actualOutput + "'")
        print("\n")
        self.actualOutput = self.actualOutput.replace("\r", "")[1:].replace("stocks $", "")
        print("'" + self.actualOutput + "'")

    def check(self, index):
        matcher = re.match(self.referenceOutput,
                self.actualOutput, re.MULTILINE)

        if (matcher is None) \
                or (matcher.start() != 0) \
                or (matcher.end() != len(self.actualOutput)):

            print(index + " failed!\n\n")
            print("Expected: '" + self.referenceOutput + "'\n\n")
            print("Actual:   '" + self.actualOutput + "'\n\n")
            print("##teamcity[testFailed name='" + self.title + "' message='"
                    + "Comparison failed' expected='"
                    + escapeForTeamcity(self.referenceOutput)
                    + "' actual='"
                    + escapeForTeamcity(self.actualOutput)
                    + "' type='comparisonFailure']")


def main(arguments):
    process = setupSshConnection()
    consumeOutput(process.stdout)   # initial prompt for 'stocks $'

    for testcase in arguments[1:len(arguments)]:
        handleOneTest(process, testcase)

    process.stdin.write("quit\n")


def setupSshConnection():
    process = subprocess.Popen(
                    ["ssh dp-server \"LANG=de_DE.UTF-8 stocks\""],
                   stdin=subprocess.PIPE,
                   stdout=subprocess.PIPE,
                   shell=True,
                   encoding="utf-8")
    return process


def handleOneTest(process, testcaseFileName):
    testcase = parseFileFromName(testcaseFileName)

    print("##teamcity[testStarted name='" + testcase.title + "']")
    testcase.run(process)
    testcase.check(testcaseFileName)
    print("##teamcity[testFinished name='" + testcase.title + "']")


def parseFileFromName(fileName):
    with open(fileName, 'r') as testcaseFile:
        content = testcaseFile.read()
        lines = content.split("\n")
        title = lines[0].replace("Title: ", "")
        input = lines[1].replace("Input: ", "")
        input = transformEscapes(input)
        if lines[2].startswith("Commands"):
            count = lines[2].replace("Commands: ", "")
            count = int(count)
            outputIndex = 3
        else:
            outputIndex = 2
            count = 1

        output = lines[outputIndex].replace("Output: ", "")
        output = transformEscapes(output)
        result = TestCase(title, input, output, count)
        return result


def transformEscapes(string):
    string = string.replace("\\n", "\n")
    string = string.replace("\\t", "\t")
    return string


def escapeForTeamcity(string):
    return (string
            .replace("\n", "|n")
            .replace("'", "|'")
            .replace("[", "|[")
            .replace("]", "|]")
            )


def consumeOutput(pipe):
    char = pipe.read(1)
    result = ""
    while char != "$":
        result = result + char
        char = pipe.read(1)
    result = result + char
    return result


def printUsage():
    print("Usage: testcase-driver <filename>...")


if __name__ == '__main__':
    main(sys.argv)
