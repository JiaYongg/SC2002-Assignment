import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class WithdrawalFileReader extends FileReader<WithdrawalRequest> {
    private Map<String, Applicant> applicants;
    private Map<String, Project> projects;
    
    public WithdrawalFileReader(Map<String, Applicant> applicants, Map<String, Project> projects) {
        super("Withdrawal.csv");
        this.applicants = applicants;
        this.projects = projects;
    }
    
    @Override
    public Map<String, WithdrawalRequest> readFromFile() {
        return readCSV();
    }
    
    @Override
    protected void processLine(String line, Map<String, WithdrawalRequest> withdrawals) {
        try {
            String[] data = line.split(",");
            
            int requestId = Integer.parseInt(data[0].trim());
            String applicantNRIC = data[1].trim();
            String projectName = data[2].trim();
            Project project = projects.get(projectName);
            if (project == null) {
                System.out.println("Project not found for name: '" + projectName + "'");
            }
            String flatTypeName = data[3].trim();
            

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date dateRequested = sdf.parse(data[4].trim());
            WithdrawalStatus status = WithdrawalStatus.valueOf(data[5].trim());
            
            // Find the applicant and project
            Applicant applicant = applicants.get(applicantNRIC);
            if (applicant == null) {
                System.out.println("Applicant not found for NRIC: " + applicantNRIC);
            }
            
            if (applicant != null && project != null) {
                // Find the flat type
                FlatType flatType = null;
                for (FlatType ft : project.getFlatTypes()) {
                    if (ft.getName().equals(flatTypeName)) {
                        flatType = ft;
                        break;
                    }
                }
                
                if (flatType != null) {
                    // Create a new application
                    Application application = new Application(0, applicant, project, flatType);

                    
                    // Create the withdrawal request
                    WithdrawalRequest withdrawal = new WithdrawalRequest(
                        requestId,
                        application,
                        dateRequested,
                        status
                    );
                    
                    withdrawals.put(String.valueOf(requestId), withdrawal);
                }
            }
        } catch (Exception e) {
            System.out.println("Error processing withdrawal request line: " + line);
            e.printStackTrace();
        }
    }
}
