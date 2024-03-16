package com.aimframeworkgrp23;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;

public class TextFileReader {

    public static String readFile(String filePath) throws IOException {
        return new String(Files.readAllBytes(Paths.get(filePath)));
    }
}