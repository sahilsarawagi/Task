package com.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class DestinationHashGenerator {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java -jar DestinationHashGenerator.jar <roll_number> <json_file_path>");
            return;
        }

        String rollNumber = args[0].toLowerCase().replaceAll("\\s", ""); // Ensure lowercase, no spaces
        String jsonFilePath = args[1];

        try {
            // Parse the JSON file
            String destinationValue = findFirstDestination(jsonFilePath);
            if (destinationValue == null) {
                System.out.println("Error: No 'destination' key found in the JSON file.");
                return;
            }

            // Generate a random 8-character alphanumeric string
            String randomString = generateRandomString(8);

            // Concatenate values
            String concatenated = rollNumber + destinationValue + randomString;

            // Generate MD5 hash
            String hash = generateMD5Hash(concatenated);

            // Print the output
            System.out.println(hash + ";" + randomString);
        } catch (IOException e) {
            System.out.println("Error reading JSON file: " + e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Error generating MD5 hash: " + e.getMessage());
        }
    }

    // Traverse JSON to find the first "destination" key
    private static String findFirstDestination(String jsonFilePath) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(new File(jsonFilePath));

        return findKey(rootNode, "destination");
    }

    private static String findKey(JsonNode node, String key) {
        if (node.has(key)) {
            return node.get(key).asText();
        }

        for (JsonNode child : node) {
            if (child.isContainerNode()) {
                String result = findKey(child, key);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    // Generate a random 8-character alphanumeric string
    private static String generateRandomString(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    // Generate MD5 hash
    private static String generateMD5Hash(String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] hashBytes = md.digest(input.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : hashBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
