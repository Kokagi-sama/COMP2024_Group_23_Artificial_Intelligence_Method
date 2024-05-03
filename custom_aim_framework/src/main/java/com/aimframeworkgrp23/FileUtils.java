package com.aimframeworkgrp23;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class FileUtils {

    /**
     * Recursively deletes all files and directories in a specified directory.
     * 
     * @param directoryPath The path to the directory to clear.
     * @throws IOException If an I/O error occurs.
     */
    public static void clearOutputDirectory(String directoryPath) throws IOException {
        final Path directory = Paths.get(directoryPath);

        // Check if the directory exists before attempting to delete
        if (Files.exists(directory)) {
            Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    
                    // Delete all files in the directory
                    Files.delete(file); 
                
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    
                    // Delete directories after all contents are deleted
                    Files.delete(dir); 

                    return FileVisitResult.CONTINUE;
                }
            });
        }
    }
}