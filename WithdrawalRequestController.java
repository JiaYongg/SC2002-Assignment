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

    public List<WithdrawalRequest> getPendingRequests() {
        return allWithdrawalRequests.stream()
                .filter(req -> req.getStatus() == WithdrawalStatus.PENDING)
                .collect(Collectors.toList());
    }

    public boolean approveRequest(WithdrawalRequest request) {
        request.setStatus(WithdrawalStatus.APPROVED);

        Application application = request.getApplication();
        application.setStatus(ApplicationStatus.UNSUCCESSFUL);

        saveApplicationUpdate(application);

        saveWithdrawalRequests();
        return true;
    }

    private void saveApplicationUpdate(Application updatedApp) {
        try {
            ProjectFileReader projectReader = new ProjectFileReader();
            ApplicantFileReader applicantReader = new ApplicantFileReader();

            Map<String, Project> projectMap = projectReader.readFromFile();
            Map<String, User> userMap = applicantReader.readFromFile();
            Map<String, Applicant> applicantMap = new HashMap<>();

            for (User user : userMap.values()) {
                if (user instanceof Applicant && !(user instanceof HDBOfficer)) {
                    applicantMap.put(user.getNric(), (Applicant) user);
                }
            }

            ApplicationFileReader reader = new ApplicationFileReader(projectMap, applicantMap);
            Map<String, Application> appMap = reader.readFromFile();

            for (Map.Entry<String, Application> entry : appMap.entrySet()) {
                Application app = entry.getValue();
                if (app.getApplicant().getNric().equals(updatedApp.getApplicant().getNric()) &&
                        app.getFlatType().getName().equals(updatedApp.getFlatType().getName())) {
                    entry.setValue(updatedApp);
                    break;
                }
            }

            ApplicationFileWriter writer = new ApplicationFileWriter();
            writer.writeToFile(appMap);

            System.out.println("Application updated to UNSUCCESSFUL.");
        } catch (Exception e) {
            System.out.println("Error saving application update: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean rejectRequest(WithdrawalRequest request) {
        request.setStatus(WithdrawalStatus.REJECTED);

        saveWithdrawalRequests();

        System.out.println("Withdrawal request rejected for application: " +
                request.getApplication().getProject().getProjectName());
        return true;
    }

    private void loadWithdrawalRequests() {
        try {

            ApplicantFileReader applicantReader = new ApplicantFileReader();
            Map<String, User> userMap = applicantReader.readFromFile();
            Map<String, Applicant> applicantMap = new HashMap<>();

            for (User user : userMap.values()) {
                if (user instanceof Applicant && !(user instanceof HDBOfficer)) {
                    Applicant applicant = (Applicant) user;
                    applicantMap.put(applicant.getNric(), applicant);
                }
            }

            ProjectFileReader projectReader = new ProjectFileReader();
            Map<String, Project> projectMap = projectReader.readFromFile();

            WithdrawalFileReader reader = new WithdrawalFileReader(applicantMap, projectMap);
            Map<String, WithdrawalRequest> withdrawalMap = reader.readFromFile();

            allWithdrawalRequests.addAll(withdrawalMap.values());

        } catch (Exception e) {
            System.out.println("Error loading withdrawal requests: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void saveWithdrawalRequests() {
        try {

            Map<String, WithdrawalRequest> withdrawalMap = new HashMap<>();
            for (WithdrawalRequest withdrawal : allWithdrawalRequests) {
                withdrawalMap.put(String.valueOf(withdrawal.getRequestId()), withdrawal);
            }

            withdrawalWriter.writeToFile(withdrawalMap);

            System.out.println("Successfully saved " + withdrawalMap.size() + " withdrawal requests.");
        } catch (Exception e) {
            System.out.println("Error saving withdrawal requests: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean createWithdrawalRequest(Application application) {

        for (WithdrawalRequest existingRequest : allWithdrawalRequests) {
            if (existingRequest.getApplication().equals(application) &&
                    existingRequest.getStatus() == WithdrawalStatus.PENDING) {
                System.out.println("A pending withdrawal request already exists for this application.");
                return false;
            }
        }

        WithdrawalRequest request = new WithdrawalRequest(application);
        allWithdrawalRequests.add(request);
        saveWithdrawalRequests();

        System.out.println("Withdrawal request created for application: " +
                application.getProject().getProjectName());
        return true;
    }

}
