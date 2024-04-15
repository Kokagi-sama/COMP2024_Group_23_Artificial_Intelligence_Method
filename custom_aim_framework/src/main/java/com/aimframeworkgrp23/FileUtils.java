package com.aimframeworkgrp23;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class FileUtils {

    /**
     * Recursively deletes all files and directories in a specified directory except for a specific file named "BPP.txt" in the top directory.
     * 
     * @param directoryPath The path to the directory to clear.
     * @throws IOException If an I/O error occurs.
     */
    public static void clearOutputDirectory(String directoryPath) throws IOException {
        final Path directory = Paths.get(directoryPath);
        final Path protectedFile = directory.resolve("BPP.txt"); // Resolve the protected file's path

        Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (!file.equals(protectedFile)) { // Check if the file is not the protected file
                    Files.delete(file); // Delete each file that is not protected
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                if (!dir.equals(directory)) { // Do not delete the top-level directory itself
                    Files.delete(dir); // Delete directories after all contents are deleted
                }
                return FileVisitResult.CONTINUE;
            }
        });
    }
}