package navi.parsers;


import navi.exceptions.FileIOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;

import java.io.*;
import java.util.HashMap;
import java.util.Map;


public class InputFileParserImpl implements InputParser {
    @Autowired
    ResourceLoader resourceLoader;

    private static InputFileParserImpl parser = null;

    private Map<String, BufferedReader> sourceMap;

    private InputFileParserImpl() {
        sourceMap = new HashMap<>();
    }

    public static InputFileParserImpl getInstance() {
        if (parser == null) {
            parser = new InputFileParserImpl();
        }
        return parser;
    }

    @Override
    public String getNextInput(String filePath) throws navi.exceptions.FileNotFoundException {
        BufferedReader br = getReader(filePath);
        String line = null;
        try {
            line = br.readLine();
            if (line == null) {
                closeSource(filePath);
                return null;
            }
            return line;
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
            throw new navi.exceptions.FileNotFoundException("File not found : " + filePath);
        } catch (IOException e) {
            System.out.println("Error reading file");
            throw new FileIOException("Error reading file : " + filePath);
        }
    }

    private BufferedReader getReader(String filePath) throws navi.exceptions.FileNotFoundException {
        BufferedReader br = sourceMap.get(filePath);
        if (br == null) {
            try {
                ClassLoader classLoader = getClass().getClassLoader();
                FileReader fr = new FileReader(filePath);
                br = new BufferedReader(fr);
            } catch (FileNotFoundException e) {
                System.out.println("File not found");
                throw new navi.exceptions.FileNotFoundException("File not found : " + filePath);
            }
            sourceMap.put(filePath, br);
        }
        return br;
    }

    @Override
    public void closeSource(String filePath) {
        BufferedReader reader = sourceMap.get(filePath);
        if (reader != null) {
            try {
                reader.close();
            } catch (IOException e) {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e1) {
                        System.out.println("Error closing source");
                        throw new FileIOException("Error closing source" + filePath);
                    }
                }
            }
        }
        sourceMap.remove(filePath);
    }
}
