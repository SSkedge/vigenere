import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by SPRS1994 on 27/10/15.
 * Implementation of Vigenere Cipher
 * Student No: 130391986
 */
public class VigenereCipher {


    public static void main(String[] args) throws IOException {


        VigenereCipher vC = new VigenereCipher();

        //Text string from read in file
        String t = vC.readIn("Exercise2Ciphertext.txt");

        //Reads in decrypted text
        String dT = vC.readIn("vCdecrypted.txt");

        //Prints IOC of characters
        System.out.println(String.format("IOC of cipher text: " + "%.4f", vC.IOC(t)) + "\n");

        //Key to encrypt/decrypt
        String key = vC.findKey(t);

        System.out.println("Cipher Key is: " + key + "\n");


        //Returns wrong key due to text length see report
        //String key2 = "ncl";
        //String cT = "aghpcdgnphptigcfkel";
        //String nclKey = vC.findKey(cT);
        //System.out.println("newcastleuniversity Key is: " + nclKey + "\n");
        //Encrypts newcastleuniversity with key ncl
        //vC.writeToFile(vC.encrypt(cT, key2), "nclencryption.txt");

        //Text once encrypted with key
        String encryptedText = vC.encrypt(dT, key);

        //writes encrypted and decrypted text
        vC.writeToFile(encryptedText, "vCencrypted.txt");
        vC.writeToFile(vC.decrypt(t, key), "vCdecrypt.txt");


    }

    public String readIn(String PATH_NAME) throws IOException {
        //Read in text file and return it as a String
        String textString = "";
        try {
            BufferedReader reader = new BufferedReader(new FileReader(PATH_NAME));

            String line;

            //While there is text in the file
            while ((line = reader.readLine()) != null) {

                textString += line;

            }


        } catch (Exception e) {
            //if file is not found produce error
            System.err.println("File not Found!");
        }

        return textString;


    }

    public void writeToFile(String text, String filename) throws IOException {
        /*
        *writes text to file
        */
        try {
            File file = new File(filename);

            BufferedWriter bW = new BufferedWriter(new FileWriter(file, false));

            bW.write(text);
            bW.flush();
            bW.close();
            System.out.println(filename + "" + "File Written!");
        } catch (Exception e) {
            System.err.println("File not written!");
        }
    }

    int findKeyLength(String cipherText) {
        /*
        *
        *Finds the length of the key by getting the average IOC
        * and comparing it to 0.06
        *
        */
        int keyLength = 0;
        List<String[]> splitTexts = new ArrayList<>();

        for (int i = 0; i < 20; i++) {
            splitTexts.add(i, textSplit(cipherText, (i + 1)));
        }

        double avgIOC[] = new double[20];


        for (int j = 0; j < 20; j++) {
            double ioc = 0.0;
            System.out.print((j + 1) + ": ");
            for (int k = 0; k < splitTexts.get(j).length; k++) {
                ioc += IOC(splitTexts.get(j)[k]);
                System.out.print(String.format("%.4f", IOC(splitTexts.get(j)[k])) + " ");
            }
            ioc = ioc / splitTexts.get(j).length;
            System.out.println();

            if (ioc >= 0.06) {
                keyLength = j + 1;
                break;
            }
            avgIOC[j] = ioc;
        }


        return keyLength;
    }

    static String[] textSplit(String text, int length) {
        /*
        *
        * Splits the text into columns
        *
        */

        String[] tempArray = new String[length];
        for (int j = 0; j < tempArray.length; j++) {
            tempArray[j] = new String();
        }

        for (int i = 0; i < text.length(); i++) {
            tempArray[i % length] += text.charAt(i);
        }

        return tempArray;
    }

    public String findKey(String text) {
        /*
        *
        * Finds the key using the methods from Frequency analysis to find most frequent char
        * and calculate the best key from the split text. i.e. do frequency analysis on each column and make
        * the best key
        */
        FrequencyAnalysis fA = new FrequencyAnalysis();
        int keyLength = findKeyLength(text);

        String[] splitText = textSplit(text, keyLength);
        char p = ' ';
        char c = ' ';
        try {

            p = fA.mostFreq(fA.MapLetters(fA.readIn("pg1661.txt")));
        } catch (IOException ioe) {
            System.err.println(ioe);
        }

        String bestKey = "";

        for (String s : splitText) {
            try {
                c = fA.mostFreq(fA.MapLetters(s));
            } catch (IOException ioe) {
                System.err.println(ioe);
            }
            bestKey += (char) (fA.calculation(p, c) + 'a');
        }

        return bestKey.toLowerCase();
    }

    public int[] countLetters(String text) {
        /*
        *
        *Counts the frequency of each letter
        *
        */

        int[] freq = new int[26];

        for (Character c : text.toCharArray()) {
            c = Character.toLowerCase(c);
            if (Character.isAlphabetic(c)) {
                if (c >= 'a' && c <= 'z') {
                    freq[c - 'a']++;
                }
            }
        }

        return freq;
    }

    public int countTotalChars(String text) {
        /*
        *
        *Counts the total number of characters in the text
        *
        */

        int total = 0;

        for (Character c : text.toCharArray()) {
            c = Character.toLowerCase(c);
            if (Character.isAlphabetic(c)) {
                if (c >= 'a' && c <= 'z') {
                    total++;
                }
            }
        }
        return total;
    }


    double IOC(String cipherText) {
        /*
        *
        * Finds the IOC of the cipher text
        *
        */
        int total = countTotalChars(cipherText);

        int[] freq = countLetters(cipherText);

        double IOC = 0.0;

        for (int i = 0; i < freq.length; i++) {

            IOC += (freq[i] * (freq[i] - 1));

        }

        IOC = IOC / (double) (total * (total - 1));

        return IOC;

    }

    String encrypt(String text, final String key) {
        /*
        *
        *Take plain text and key, then use vigenere cipher to encrypt
        *Take each letter and complete calculation to change character
        *
        */
        char letters[] = text.toLowerCase().toCharArray();

        char c;
        for (int i = 0, j = 0; i < letters.length; i++) {
            c = letters[i];
            if (c < 'a' || c > 'z') continue;

            letters[i] = (char) ((c + key.charAt(j) - (2 * 'a')) % 26 + 'a');
            j = ++j % key.length();
        }
        return new String(letters);
    }

    String decrypt(String text, final String key) {
        /*
        *
        *Take cipher text and key, then use vigenere cipher to decrypt
        *Take each letter and complete calculation to change character
        *
        */
        char letters[] = text.toLowerCase().toCharArray();
        char c;


        for (int i = 0, j = 0; i < text.length(); i++) {
            c = letters[i];
            if (c < 'a' || c > 'z') continue;

            letters[i] = (char) ((c - key.charAt(j) + 26) % 26 + 'a');
            j = ++j % key.length();
        }
        return new String(letters);
    }


}
