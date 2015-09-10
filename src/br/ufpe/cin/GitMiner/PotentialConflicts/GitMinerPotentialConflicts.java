package br.ufpe.cin.GitMiner.PotentialConflicts;

import br.ufpe.cin.GitMiner.Commits.BaseCommit;
import br.ufpe.cin.GitMiner.Commits.TaskCommit;
import br.ufpe.cin.GitMiner.ComputeChangeSet.ChangeSet;
import br.ufpe.cin.GitMiner.Intersection.Intersection;
import br.ufpe.cin.GitMiner.io.FileHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/*everything related to git repo 
 * step 1: read input file --ok
 * step 2: download zipped commits from github --ok
 * step 3: unzip commits --ok
 * step 4: load commits directories --ok
 * obs: limit the loadDirectory method to file extensions and folder that matter --ok
 * obs: rename commit root directory from rgms-shaKey to "sourcecode" --ok
 * 
 * step 5: compute features changeset --ok (could be optimized)
 * changes suggested by Paulo Borba:
 * remove changeset as an atribute of class feature --ok
 * step 6: compute features intersection --ok
 * step 7: write changesets and intersections reports --ok
 * improvements:
 * 1-rename FeatureCommit to TaskCommit --ok
 * 2-add reference two both commits on a changeset instance --ok
 * */

public class GitMinerPotentialConflicts {

    private String inputFile;
    private String outputFile;
    private String userId;
    private String repoName;
    private BaseCommit base;
    private List<TaskCommit> tasksCommits;
    private List<ChangeSet> changeSets;
    private List<Intersection> intersections;
    private FileHandler fileHandler;

    public GitMinerPotentialConflicts(String inputFile) {
        this.inputFile = inputFile;
        this.fileHandler = new FileHandler();
    }

    public static void main(String[] args) {
        GitMinerPotentialConflicts repo = new GitMinerPotentialConflicts("input.csv");
        repo.readInput();
        repo.downloadCommits();
        repo.unzipCommits();
        repo.loadDirectories();
        repo.computeChangeSet();
        //repo.computeIntersection();
    }

    public void readInput() {
        GitMinerPotentialConflicts temp;
        try {
            temp = this.fileHandler.reader(this.inputFile);
            this.userId = temp.getUserId();
            this.repoName = temp.getRepoName();
            this.base = temp.getBase();
            this.tasksCommits = temp.getTasksCommits();
            System.out.println("done reading input file.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void downloadCommits() {
        try {
            this.fileHandler.downloader(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void unzipCommits() {
        try {
            this.fileHandler.unzipper(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadDirectories() {
        this.base.loadDirectory();
        for (TaskCommit f : this.tasksCommits) {
            f.loadDirectory();
        }
    }

    public void computeChangeSet() {
        this.changeSets = new ArrayList<>();
        for (TaskCommit f : this.tasksCommits) {
            ChangeSet changeSet = new ChangeSet();
            changeSet.setTaskCommit(f);
            changeSet.setBaseCommit(this.base);
            changeSet.loadChangeSet(f.getDirectory(), this.base.getDirectory());
            this.changeSets.add(changeSet);
            this.reportChangeSet(changeSet);
        }
    }

    public void reportChangeSet(ChangeSet cs) {
        this.fileHandler.writer((cs.getTaskCommit().getName() + "ChangeSet"), cs.toString());
    }

    public void computeIntersection() {
        this.intersections = new ArrayList<>();

        for (int i = 0; i < this.changeSets.size(); i++) {
            ChangeSet changeSetA = this.changeSets.get(i);
            List<ChangeSet> listRemaining = this.changeSets.subList((i + 1), (this.changeSets.size()));

            for (ChangeSet changeSetB : listRemaining) {
                Intersection intersection = this.computeChangeSetIntersection(changeSetA, changeSetB);
                this.intersections.add(intersection);
                this.reportIntersection(intersection);
            }
        }
    }

    public void reportIntersection(Intersection it) {
        String title = it.getChangeSetA().getTaskCommit().getName() + "_and_" + it.getChangeSetB().getTaskCommit().getName() + "_intersection";
        this.fileHandler.writer(title, it.toString());
    }

    public Intersection computeChangeSetIntersection(ChangeSet changeSetA, ChangeSet changeSetB) {
        Intersection intersection = new Intersection();
        intersection.loadIntersection(changeSetA, changeSetB);
        return intersection;
    }

    public String getInputFile() {
        return inputFile;
    }

    public void setInputFile(String inputFile) {
        this.inputFile = inputFile;
    }

    public String getOutputFile() {
        return outputFile;
    }

    public void setOutputFile(String outputFile) {
        this.outputFile = outputFile;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRepoName() {
        return repoName;
    }

    public void setRepoName(String repoName) {
        this.repoName = repoName;
    }

    public BaseCommit getBase() {
        return base;
    }

    public void setBase(BaseCommit base) {
        this.base = base;
    }

    public List<TaskCommit> getTasksCommits() {
        return tasksCommits;
    }

    public void setTasksCommits(List<TaskCommit> tasks) {
        this.tasksCommits = tasks;
    }

    public List<ChangeSet> getChangeSets() {
        return changeSets;
    }

    public void setChangeSets(List<ChangeSet> changeSets) {
        this.changeSets = changeSets;
    }

    public List<Intersection> getIntersections() {
        return intersections;
    }

    public void setIntersections(List<Intersection> intersections) {
        this.intersections = intersections;
    }
}
