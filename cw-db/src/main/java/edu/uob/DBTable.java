package edu.uob;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class DBTable {
    private String tableName;

    private List<String> attributes;

    private List<Map<String,String>> entries;

    private Integer numberOfEntries;

    private String tableFilePath;

    private String databaseFolderPath;

    public DBTable(String databaseFolderPath){
        this.tableName = "";
        this.attributes = new ArrayList<>();
        this.entries = new ArrayList<>();
<<<<<<< HEAD
=======
        this.numberOfEntries = 0;
>>>>>>> main
        this.databaseFolderPath = databaseFolderPath;
        this.tableFilePath = databaseFolderPath + File.separator + this.tableName + ".tab";
    }

    //Constructor with table name
    public DBTable(String databaseFolderPath, String tableName){
        this.tableName = tableName;
        this.numberOfEntries = 0;
        this.attributes = new ArrayList<>();
        this.entries = new ArrayList<>();
        this.databaseFolderPath = databaseFolderPath;
        this.tableFilePath = databaseFolderPath + File.separator + this.tableName + ".tab";
    }

    public Integer getNumberOfEntries() { return this.entries.size();}

    public Integer getNumberOfAttributes() { return this.attributes.size(); }

    public void setTable(String tableName) {
        this.tableName = tableName;
        this.tableFilePath = this.databaseFolderPath + File.separator + this.tableName + ".tab";
    }

    public String getTableName() { return this.tableName; }

    public void addAttribute(String attributeName) { this.attributes.add(attributeName); }

    public void deleteAttribute (String attributeName) {
        this.attributes.remove(attributeName);
        for(Map<String, String> entry : entries){
            entry.remove(attributeName);
        }
    }
    public List<String> getAttributes() { return this.attributes; }

    public List <String> getEntryByKey (String primaryKey){
        for(Map<String, String> entry : entries){
            if(entry.get("id").equals(primaryKey)){
                List<String> result = new ArrayList<>();
                for(String attribute : this.attributes){
                    result.add(entry.get(attribute));
                }
                return result;
            }
        }
        return null;
    }

    public String getTableFilePath() { return this.tableFilePath; }

    public List<Map<String, String>> getAllEntries() { return new ArrayList<>(this.entries); }

    public void addEntry(Map<String, String> entry) {
        this.entries.add(new HashMap<>(entry));
        this.numberOfEntries++;
    }

    public void deleteEntry (String primaryKey){
        for(Map<String, String> entry : entries){
            if(entry.get("id").equals(primaryKey)){
                entries.remove(entry);
                this.numberOfEntries--;
                return;
            }
        }
    }

    public void readFromTable() throws IOException {
        if(this.tableFilePath == null || this.tableFilePath.isEmpty()){
            throw new IOException("File path not set");
        }
        File readFile = new File(this.tableFilePath);
        if (!readFile.exists()) {
            throw new IOException("Table File does not exist at " + this.tableFilePath);
        }

        try(BufferedReader bufferedReader= new BufferedReader(new FileReader(readFile))){
            String line = bufferedReader.readLine();
            // the attributes from first line
            if(line!=null){
                String[] attributes = line.split("\\t");
                this.attributes.clear();
                for (String attribute : attributes){
                    this.attributes.add(attribute);
                }
            }
            this.entries.clear();
<<<<<<< HEAD
            while((line = bufferedReader.readLine()) != null){
=======
            while((line = bufferedReader.readLine()) != null && !line.trim().isEmpty()){
>>>>>>> main
                processRows(line);
            }
        } catch (IOException ioe) {
            System.out.println("Error reading table: " + ioe.getMessage());
        }
    }

    public void processRows (String line){
        String[] row = line.split("\\t");
        Map<String, String> rowMap = new HashMap<>();
        for (int i = 0; i< this.attributes.size(); i++){
            String data = i < row.length ? row[i] : "";
            rowMap.put(this.attributes.get(i), data);
        }
<<<<<<< HEAD
    }
    public void writeToTable() throws IOException {
=======
        addEntry(rowMap);
    }
    public void writeTable() throws IOException {
>>>>>>> main
        File writeFile = new File(this.tableFilePath);
        writeFile.createNewFile();
        if (!writeFile.exists()) {
            throw new IOException("Table File does not exist at " + this.tableFilePath);
        }
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(writeFile))) {
            writeFirstLine(bufferedWriter);
            writeRows(bufferedWriter);
        } catch (IOException ioe) {
            System.out.println("Error writing to file: " + ioe.getMessage());
        }
    }

    private void writeFirstLine (BufferedWriter bufferedWriter) throws IOException {
        for (String attribute : this.getAttributes()) {
            bufferedWriter.write(attribute + "\t");
        }
        //move to next line
        bufferedWriter.newLine();
        bufferedWriter.flush();
    }

    private void writeRows (BufferedWriter bufferedWriter) throws IOException {
        List<Map<String, String>> allRows = this.getAllEntries();
        List<String> attributes = this.getAttributes();

        for(Map<String, String> row: allRows){
            for (int i = 0; i< attributes.size(); i++){
                String data = row.get(attributes.get(i));
                if (data == null) { data = ""; }
                bufferedWriter.write(data + "\t");
            }
            //move to next line
            bufferedWriter.newLine();
        }
        bufferedWriter.flush();
    }


}
