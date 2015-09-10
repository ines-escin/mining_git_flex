package br.ufpe.cin.GitMiner.ComputeChangeSet;

import br.ufpe.cin.GitMiner.Commits.BaseCommit;
import br.ufpe.cin.GitMiner.Commits.Directory;
import br.ufpe.cin.GitMiner.Commits.TaskCommit;
import br.ufpe.cin.GitMiner.io.FileHandler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/*changeset is the delta between the base commit and the task commit, i.e. base + task A
 * it records the names of the files that were added, deleted and modified.
 * Each changeset instance contains
 * */
public class ChangeSet {

    public HashMap<String, String> removedFiles;
    private List<String> addedFiles;
    private List<String> modifiedFiles;
    private List<String> addedDirectories;
    private List<String> removedDirectories;
    private List<ChangeSet> subChangeSets;
    private BaseCommit baseCommit;
    private TaskCommit taskCommit;
    private String directoryName;
    private FileHandler fileHandler = new FileHandler();

    public ChangeSet() {
        this.removedFiles = new HashMap<>();
        this.addedFiles = new ArrayList<>();
        this.modifiedFiles = new ArrayList<>();
        this.addedDirectories = new ArrayList<>();
        this.removedDirectories = new ArrayList<>();
        this.subChangeSets = new ArrayList<>();
    }

    public static void main(String[] args) {
        File f = new File("C:/Users/Paola2/Desktop/teste.txt");
        System.out.println(f.getParent());
    }

    public void loadChangeSet(Directory taskDir, Directory baseDir) {
        String equalpath = "[s][o][u][r][c][e][c][o][d][e]";
        try {
            this.baseCase(taskDir, baseDir, equalpath);
            this.recursion(taskDir, baseDir, equalpath);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void baseCase(Directory taskDir, Directory baseDir, String equalpath) throws IOException {

        //set directory name
        if (taskDir.getName().equals(this.taskCommit.getName())) {
            this.setDirectoryName(taskDir.getName());
        } else if (taskDir.getName().endsWith("sourcecode")) {
            this.setDirectoryName("sourcecode");
        } else {
            this.setDirectoryName(taskDir.getName().split(equalpath)[1]);
        }
        //set directory name

        for (String taskFile : taskDir.getFilesNames()) {
            boolean containsTaskFile = false;

            for (String baseFile : baseDir.getFilesNames()) {
                String shortTaskFilename, shortBaseFilename, shortfFilename;
                shortTaskFilename = taskFile.split(equalpath)[1];
                shortBaseFilename = baseFile.split(equalpath)[1];

                if (shortTaskFilename.equals(shortBaseFilename)) {
                    containsTaskFile = true;
                    boolean equals = this.fileHandler.compareTwoFiles(baseFile, taskFile);
                    if (!equals) {
                        //modified
                        this.modifiedFiles.add(shortTaskFilename);
                    }
                }

                //removed file
                boolean removed = true;
                for (String fFile : taskDir.getFilesNames()) {
                    shortfFilename = fFile.split(equalpath)[1];
                    if (shortBaseFilename.equals(shortfFilename)) {
                        removed = false;
                    }
                }
                if (removed) {
                    this.removedFiles.put(shortBaseFilename, shortBaseFilename);
                }
                //end of removed file
            }

            if (!containsTaskFile) {
                //added
                this.addedFiles.add(taskFile.split(equalpath)[1]);
            }
        }
    }

    private void recursion(Directory taskDir, Directory baseDir, String equalpath) throws IOException {
        //recursion
        for (Directory subDirectory : taskDir.getSubDirectories()) {
            String shortSubDirectoryName;

            try {
                shortSubDirectoryName = subDirectory.getName().split(equalpath)[1];
            } catch (Exception e) {
                shortSubDirectoryName = "";
            }

            boolean contains = false;
            for (Directory subBaseDirectory : baseDir.getSubDirectories()) {
                String shortSubBaseDirectoryName;
                try {
                    shortSubBaseDirectoryName = subBaseDirectory.getName().split(equalpath)[1];
                } catch (Exception e) {
                    shortSubBaseDirectoryName = "";
                }

                if (shortSubDirectoryName.equals(shortSubBaseDirectoryName)) {
                    contains = true;
                    ChangeSet sub = new ChangeSet();
                    sub.setTaskCommit(this.taskCommit);
                    sub.loadChangeSet(subDirectory, subBaseDirectory);
                    this.subChangeSets.add(sub);
                }

                //remove directory
                boolean removed = true;
                for (Directory fDirectory : taskDir.getSubDirectories()) {
                    String shortFDirectoryName;
                    try {
                        shortFDirectoryName = fDirectory.getName().split(equalpath)[1];
                    } catch (Exception e) {
                        shortFDirectoryName = "";
                    }
                    if (shortSubBaseDirectoryName.equals(shortFDirectoryName)) {
                        removed = false;
                    }
                }

                if (removed) {
                    this.removedDirectories.add(subBaseDirectory.getName());
                }
                //end of removed directory
            }

            //added
            if (!contains) {
                //here is the code related to add files from added directories
                this.loadFilesFromAddedDirectories(subDirectory);
                //end of this code
                this.addedDirectories.add(subDirectory.getName());
            }
        }
    }

    private void loadFilesFromAddedDirectories(Directory dir) {
        this.addedFiles.addAll(dir.getFilesNames());
        for (Directory sub : dir.getSubDirectories()) {
            this.addedDirectories.add(sub.getName());
            this.loadFilesFromAddedDirectories(sub);
        }
    }

    public String toString() {
        String result = "Directory name: " + this.directoryName + "\n";

        for (String addedFile : this.addedFiles) {
            result = result + "Added: " + addedFile + "\n";
        }

        for (String modifiedFile : this.modifiedFiles) {
            result = result + "Modified: " + modifiedFile + "\n";
        }

        for (String removedFile : this.removedFiles.values()) {
            result = result + "Removed: " + removedFile + "\n";
        }

        for (String addedDirectory : this.addedDirectories) {
            result = result + "Added: " + addedDirectory + "\n";
        }

        for (String removedDirectory : this.removedDirectories) {
            result = result + "Removed: " + removedDirectory + "\n";
        }

        for (ChangeSet change : this.subChangeSets) {
            result = result + change.toString() + "\n";
        }
        return result;
    }

    public List<String> getRemovedFiles() {
        return new ArrayList<>(removedFiles.values());
    }

    public void setRemovedFiles(HashMap<String, String> removedFiles) {
        this.removedFiles = removedFiles;
    }

    public List<String> getAddedFiles() {
        return addedFiles;
    }

    public void setAddedFiles(List<String> addedFiles) {
        this.addedFiles = addedFiles;
    }

    public List<String> getModifiedFiles() {
        return modifiedFiles;
    }

    public void setModifiedFiles(List<String> modifiedFiles) {
        this.modifiedFiles = modifiedFiles;
    }

    public List<ChangeSet> getSubChangeSets() {
        return subChangeSets;
    }

    public void setSubChangeSets(List<ChangeSet> subChangeSets) {
        this.subChangeSets = subChangeSets;
    }

    public TaskCommit getTaskCommit() {
        return taskCommit;
    }

    public void setTaskCommit(TaskCommit task) {
        this.taskCommit = task;
    }

    public BaseCommit getBaseCommit() {
        return baseCommit;
    }

    public void setBaseCommit(BaseCommit baseCommit) {
        this.baseCommit = baseCommit;
    }

    public String getDirectoryName() {
        return directoryName;
    }

    public void setDirectoryName(String directoryName) {
        this.directoryName = directoryName;
    }

    public List<String> getAddedDirectories() {
        return addedDirectories;
    }

    public void setAddedDirectories(List<String> addedDirectories) {
        this.addedDirectories = addedDirectories;
    }

    public List<String> getRemovedDirectories() {
        return removedDirectories;
    }

    public void setRemovedDirectories(List<String> removedDirectories) {
        this.removedDirectories = removedDirectories;
    }

    public String report() {
        SingleChangeset single = new SingleChangeset();
        single.loadSingleChangeSet(this);
        ChangeSet changeset = single.getSingleChangeSet();
        String report = "";

        //modified files
        report = report + changeset.modifiedFiles.size() + " files were modified. These files were the following: \n";
        report = report + this.iterateOverListOfStrings(changeset.modifiedFiles) + "\n";

        //added files
        report = report + changeset.addedFiles.size() + " files were added. These files were the following: \n";
        report = report + this.iterateOverListOfStrings(changeset.addedFiles) + "\n";

        //removed files
        report = report + changeset.removedFiles.size() + " files were removed. These files were the following: \n";
        List<String> teste = new ArrayList<>(changeset.removedFiles.values());
        report = report + this.iterateOverListOfStrings(teste) + "\n";

        //added directories
        report = report + changeset.addedDirectories.size() + " directories were added. These directories were the following: \n";
        report = report + this.iterateOverListOfStrings(changeset.addedDirectories) + "\n";

        //removed directories
        report = report + changeset.removedDirectories.size() + " directories were removed. These directories were the following: \n";
        report = report + this.iterateOverListOfStrings(changeset.removedDirectories) + "\n";

        return report;
    }

    private String iterateOverListOfStrings(List<String> listOfStrings) {
        String result = "";
        for (String s : listOfStrings) {
            result = result + s + "\n";
        }
        return result;
    }

}
