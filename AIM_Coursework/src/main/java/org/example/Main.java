package org.example;

import java.io.IOException;

import com.aimframework.*;

public class Main {
    public static void main(String[] args) {
        try {
            String content = TextFileReader.readFile("./src/main/resources/BPP.txt");
            System.out.println(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}