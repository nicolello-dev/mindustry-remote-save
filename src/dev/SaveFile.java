package dev;

import arc.struct.Seq;

import java.util.Base64;

public class SaveFile {
    /**
     * A byte array with the file contents
     */
    private Byte[] saveFileContents;

    public Byte[] getSaveFileContents() {
        return saveFileContents;
    }

    public void setSaveFileContents(Byte[] saveFileContents) {
        this.saveFileContents = saveFileContents;
    }

    /**
     * A string representing the file path with respect to the save folder
     */
    private String relativeFilePath;

    public String getRelativeFilePath() {
        return relativeFilePath;
    }

    public void setRelativeFilePath(String relativeFilePath) {
        this.relativeFilePath = relativeFilePath;
    }

    /**
     * The file name
     */
    private String fileName;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public SaveFile() {
    }

    public SaveFile(String relativeFilePath) {
        this.relativeFilePath = relativeFilePath;
        fileName = relativeFilePath.substring(relativeFilePath.lastIndexOf('/') + 1);
    }

    public SaveFile(String relativeFilePath, String fileName) {
        this.relativeFilePath = relativeFilePath;
        this.fileName = fileName;
    }

    public SaveFile(String relativeFilePath, String fileName, Byte[] contents) {
        this.relativeFilePath = relativeFilePath;
        this.fileName = fileName;
        this.saveFileContents = contents;
    }

    public SaveFile(String relativeFilePath, String fileName, Seq<Byte> contents) {
        new SaveFile(relativeFilePath, fileName, contents.toArray());
    }

    private byte[] ByteToPrimitive(Byte[] input) {
        byte[] bytes = new byte[input.length];
        for (int i = 0; i < input.length; i++) {
            bytes[i] = input[i];
        }
        return bytes;
    }

    private String base64Encode(Byte[] input) {
        return base64Encode(ByteToPrimitive(input));
    }

    private String base64Encode(byte[] input) {
        return Base64.getEncoder().encodeToString(input);
    }

    public String toJSON() {
        return "{\"name\":\"" + fileName + "\",\"path\":\"" + relativeFilePath + "\",\"content\":\""
                + base64Encode(saveFileContents) + "\"}";
    }

}
