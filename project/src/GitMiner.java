
import java.io.IOException;
import java.net.MalformedURLException;
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
 * remove changeset as an atribute of class feature
 * step 6: compute features intersection
 * step 7: write features report
 * */


public class GitMiner {
	
	private String inputFile;
	
	private String outputFile;
	
	private String userId;
	
	private String repoName;
	
	private BaseCommit base;
	
	private List<FeatureCommit> features;
	
	private List<ChangeSet> changeSets;
	
	private List<Intersection> intersections;
	
	private FileHandler fileHandler;
	
	public GitMiner(String inputFile){
		
		this.inputFile = inputFile;
		
		this.fileHandler = new FileHandler();
		
		
	}
	
	public void readInput() throws IOException  {
		
		GitMiner temp = this.fileHandler.reader(this.inputFile);
		
		this.userId = temp.getUserId();
		this.repoName = temp.getRepoName();
		this.base = temp.getBase();
		this.features = temp.getFeatures();
		System.out.println("done reading input file.");
		
	}
	
	public void downloadCommits() throws MalformedURLException, IOException{
		
		this.fileHandler.downloader(this);
		
	}
	
	public void unzipCommits() throws IOException{
		this.fileHandler.unzipper(this);
	}
	
		
	public void loadDirectories(){
		
		this.base.loadDirectory();
		
		for(FeatureCommit f : this.features){
			
			f.loadDirectory();
		}
		
	}

	
	public void computeChangeSet() throws IOException{
		
		this.changeSets = new ArrayList<ChangeSet>();
		
		for(FeatureCommit f : this.features){
			
			ChangeSet changeSet = new ChangeSet(); 
			changeSet.setFeature(f);
			changeSet.loadChangeSet(f.getDirectory(), this.base.getDirectory());
			
			this.changeSets.add(changeSet);
			
		}
	}
	
	public void computeIntersection(){
		
		this.intersections = new ArrayList<Intersection>();
		
		for(int i = 0; i < this.changeSets.size(); i++){
			
			ChangeSet changeSetA = this.changeSets.get(i);
			List<ChangeSet> listRemaining = this.changeSets.subList((i+1), (this.changeSets.size() - 1));
			
			for(ChangeSet changeSetB : listRemaining ){
				
				Intersection intersection = changeSetA.computeChangeSetIntersection(changeSetB);
				this.intersections.add(intersection);
				
			}
			
		}
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

	public List<FeatureCommit> getFeatures() {
		return features;
	}

	public void setFeatures(List<FeatureCommit> features) {
		this.features = features;
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

	public static void main(String[] args) {
		
		GitMiner repo = new GitMiner("input.csv");
		try {
			repo.readInput();
//			repo.downloadCommits();
//			repo.unzipCommits();
			repo.loadDirectories();
			//String base = repo.getBase().getDirectory().toString();
			/*System.out.println("Commit base: \n" + base);
			for(FeatureCommit feature : repo.getFeatures()){
				String f = feature.getDirectory().toString();
				System.out.println("Commit feature " + feature.getName() + ":\n" + f);
			}*/
			repo.computeChangeSet();
			for(ChangeSet cs : repo.getChangeSets()){
				String x = cs.toString();
				System.out.println("Changeset feature: " + cs.getFeature().getName() + "\n" + x);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
