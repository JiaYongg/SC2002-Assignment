import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class WithdrawalRequestController {
    private List<WithdrawalRequest> allWithdrawalRequests;
    private WithdrawalFileWriter withdrawalWriter;

    public WithdrawalRequestController() {
        this.allWithdrawalRequests = new ArrayList<>();
        this.withdrawalWriter = new WithdrawalFileWriter();
        loadWithdrawalRequests();
    }

    // In HDBManagerController.java

    // In WithdrawalRequestController.java

    // This method doesn't need the isProjectManagedBy helper since the filtering
    // will be done in HDBManagerController
    public List<WithdrawalRequest> getPendingRequests() {
        return allWithdrawalRequests.stream()
                .filter(req -> req.getStatus() == WithdrawalStatus.PENDING)
                .collect(Collectors.toList());
    }

    public boolean approveRequest(WithdrawalRequest request) {
        request.setStatus(WithdrawalStatus.APPROVED);
    
        // Update application status
        Application application = request.getApplication();
        application.setStatus(ApplicationStatus.UNSUCCESSFUL); // Change status
        application.getApplicant().setApplication(application); // Ensure applicant reference updated
    
        // Increase flat availability
        FlatType flatType = application.getFlatType();
        flatType.setUnitCount(flatType.getUnitCount() + 1);
    
        // Save both withdrawal and application updates
        saveWithdrawalRequests();
        saveApplicationUpdate(application);
    
        System.out.println("Withdrawal request approved for application: " +
                application.getProject().getProjectName());
        return true;
    }
    private void saveApplicationUpdate(Application updatedApp) {
        try {
            ApplicationFileReader reader = new ApplicationFileReader(
                new ProjectFileReader().readFromFile(),
                new ApplicantFileReader().readFromFile()
            );
            Map<String, Application> appMap = reader.readFromFile();
    
            // Replace existing application by ID
            appMap.put(String.valueOf(updatedApp.getApplicationID()), updatedApp);
    
            ApplicationFileWriter writer = new ApplicationFileWriter();
            writer.writeToFile(appMap);
    
            System.out.println("✅ Application status updated and saved.");
        } catch (Exception e) {
            System.out.println("❌ Error saving updated application: " + e.getMessage());
            e.printStackTrace();
        }
    }
        

    // The manager authorization check will be done in HDBManagerController before
    // calling this
    public boolean rejectRequest(WithdrawalRequest request) {
        request.setStatus(WithdrawalStatus.REJECTED);

        // Save changes to CSV
        saveWithdrawalRequests();

        System.out.println("Withdrawal request rejected for application: " +
                request.getApplication().getProject().getProjectName());
        return true;
    }

    // In WithdrawalRequestController.java
    private void loadWithdrawalRequests() {
        try {
            // Get applicants directly from file
            ApplicantFileReader applicantReader = new ApplicantFileReader();
            Map<String, User> userMap = applicantReader.readFromFile();
            Map<String, Applicant> applicantMap = new HashMap<>();

            for (User user : userMap.values()) {
                if (user instanceof Applicant && !(user instanceof HDBOfficer)) {
                    Applicant applicant = (Applicant) user;
                    applicantMap.put(applicant.getNric(), applicant);
                }
            }

            // Get projects directly from file
            ProjectFileReader projectReader = new ProjectFileReader();
            Map<String, Project> projectMap = projectReader.readFromFile();

            // Read withdrawal requests from file
            WithdrawalFileReader reader = new WithdrawalFileReader(applicantMap, projectMap);
            Map<String, WithdrawalRequest> withdrawalMap = reader.readFromFile();

            // Store all withdrawal requests in the list
            allWithdrawalRequests.addAll(withdrawalMap.values());

            System.out.println("Successfully loaded " + withdrawalMap.size() + " withdrawal requests.");
        } catch (Exception e) {
            System.out.println("Error loading withdrawal requests: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void saveWithdrawalRequests() {
        try {
            // Create a map of all withdrawal requests
            Map<String, WithdrawalRequest> withdrawalMap = new HashMap<>();
            for (WithdrawalRequest withdrawal : allWithdrawalRequests) {
                withdrawalMap.put(String.valueOf(withdrawal.getRequestId()), withdrawal);
            }

            // Write to file
            withdrawalWriter.writeToFile(withdrawalMap);

            System.out.println("Successfully saved " + withdrawalMap.size() + " withdrawal requests.");
        } catch (Exception e) {
            System.out.println("Error saving withdrawal requests: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean createWithdrawalRequest(Application application) {
        // Check if a withdrawal request already exists for this application
        for (WithdrawalRequest existingRequest : allWithdrawalRequests) {
            if (existingRequest.getApplication().equals(application) &&
                    existingRequest.getStatus() == WithdrawalStatus.PENDING) {
                System.out.println("A pending withdrawal request already exists for this application.");
                return false;
            }
        }

        // Create new request if no pending request exists
        WithdrawalRequest request = new WithdrawalRequest(application);
        allWithdrawalRequests.add(request);
        saveWithdrawalRequests();

        System.out.println("Withdrawal request created for application: " +
                application.getProject().getProjectName());
        return true;
    }

    // Rest of the methods remain the same
}
