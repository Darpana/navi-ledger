package navi.parsers;


import navi.exceptions.FileNotFoundException;

public interface InputParser {
    String getNextInput(String filePath) throws FileNotFoundException;
    void closeSource(String filePath);
}
