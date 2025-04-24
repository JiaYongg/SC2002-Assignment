import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class ApplicantController {
    private Applicant applicant;
    private List<Project> allProjects;
    private ProjectFileReader projectReader;
    private EnquiryController enquiryController;


    public ApplicantController(Applicant applicant) {
        this.applicant = applicant;
        this.projectReader = new ProjectFileReader();
        this.allProjects = new ArrayList<>(projectReader.readFromFile().values());
        this.enquiryController = new EnquiryController(allProjects);
        this.enquiryController.linkEnquiriesTo(applicant);

        Map<String, Project> projectMap = new HashMap<>();
        for (Project p : allProjects) {
            projectMap.put(p.getProjectName(), p);
        }

        Map<String, Applicant> applicantMap = new HashMap<>();
        applicantMap.put(applicant.getNric(), applicant);

        ApplicationFileReader applicationReader = new ApplicationFileReader(projectMap, applicantMap);
        applicationReader.readFromFile(); 
    }

    public Applicant getApplicant() {
        return applicant;
    }

    public void submitApplication(Applicant applicant, Project project, FlatType flatType) {
        ApplicationController applicationController = new ApplicationController();
        boolean success = applicationController.applyForProject(applicant, project, flatType);

        if (success != true) {
            System.out.println("Failed to submit application.");
        } 
    }

    public List<Project> getEligibleProjects(Applicant applicant) {
        List<Project> eligibleProjects = new ArrayList<>();

        for (Project p : allProjects) {
            if (!p.isVisible())
                continue;

            for (FlatType ft : p.getFlatTypes()) {
                if (ft.getUnitCount() > 0 && checkEligibility(applicant, p, ft)) {
                    eligibleProjects.add(p);
                    break;
                }
            }
        }

        return eligibleProjects;
    }

    public List<ProjectFlatTypeInfo> getEligibleProjectsWithDetails(Applicant applicant) {
        List<ProjectFlatTypeInfo> eligibleDetails = new ArrayList<>();

        for (Project p : allProjects) {
            if (!p.isVisible())
                continue;

            for (FlatType ft : p.getFlatTypes()) {
                if (ft.getUnitCount() > 0 && checkEligibility(applicant, p, ft)) {
                    eligibleDetails.add(new ProjectFlatTypeInfo(p, ft));
                }
            }
        }

        return eligibleDetails;
    }

    
    public class ProjectFlatTypeInfo {
        private Project project;
        private FlatType flatType;

        public ProjectFlatTypeInfo(Project project, FlatType flatType) {
            this.project = project;
            this.flatType = flatType;
        }

        public Project getProject() {
            return project;
        }

        public FlatType getFlatType() {
            return flatType;
        }
    }

    public FlatType selectFlatType(Project project, Applicant applicant) {
        List<FlatType> eligibleFlatTypes = new ArrayList<>();

        
        for (FlatType ft : project.getFlatTypes()) {
            if (ft.getUnitCount() > 0 && checkEligibility(applicant, project, ft)) {
                eligibleFlatTypes.add(ft);
            }
        }

        if (eligibleFlatTypes.isEmpty()) {
            System.out.println("No eligible flat types available for this project.");
            return null;
        }

        Scanner scanner = new Scanner(System.in);
        System.out.println("\nAvailable Flat Types:");

        for (int i = 0; i < eligibleFlatTypes.size(); i++) {
            System.out.println((i + 1) + ". " + eligibleFlatTypes.get(i).getName() + 
                              " ($" + eligibleFlatTypes.get(i).getPrice() + ")");
        }
    
        System.out.print("Select flat type (1-" + eligibleFlatTypes.size() + "): ");
        try {
            int choice = Integer.parseInt(scanner.nextLine());
            if (choice >= 1 && choice <= eligibleFlatTypes.size()) {
                return eligibleFlatTypes.get(choice - 1);
            }
            System.out.println("Invalid choice. Please select between 1 and " + eligibleFlatTypes.size());
        } catch (Exception e) {
            scanner.nextLine(); 
            System.out.println("Invalid input. Please enter a number.");
            return selectFlatType(project, applicant); 
        }
        System.out.println("Invalid selection.");
        return null;
    }

    public boolean checkEligibility(Applicant applicant, Project project, FlatType flatType) {
        
        Date currentDate = new Date();
        if (!project.isVisible() ||
                currentDate.before(project.getApplicationOpenDate()) ||
                currentDate.after(project.getApplicationCloseDate())) {
            return false;
        }

        
        if (flatType.getUnitCount() <= 0) {
            return false;
        }

        
        int age = applicant.getAge();
        String maritalStatus = applicant.getMaritalStatus();

        if (maritalStatus.equalsIgnoreCase("Single")) {
            return age >= 35 && flatType.getName().equalsIgnoreCase("2-Room");
        } else if (maritalStatus.equalsIgnoreCase("Married")) {
            return age >= 21 && (flatType.getName().equalsIgnoreCase("2-Room") ||
                    flatType.getName().equalsIgnoreCase("3-Room"));
        }

        return false;
    }

    public void withdraw(Application app) {
        if (app != null) {
            
            Scanner scanner = new Scanner(System.in);
            System.out.println("\nAre you sure you want to withdraw your application for " + app.getProject().getProjectName() + " (Flat Type: " + app.getFlatType().getName() + ")?");
            System.out.print("Type 'yes' to confirm or 'no' to cancel: ");
            
            String confirmation = scanner.nextLine().trim().toLowerCase();
    
            if (confirmation.equals("yes")) {
                
                app.setStatus(ApplicationStatus.PENDING); 
    
                
                WithdrawalRequestController contr = new WithdrawalRequestController();
                boolean created = contr.createWithdrawalRequest(app);
    
                if (created) {
                    System.out.println("Withdrawal request submitted. Pending approval.");
                } else {
                    System.out.println("Failed to submit withdrawal request.");
                }
            } else if (confirmation.equals("no")) {
                System.out.println("Withdrawal cancelled.");
            } else {
                System.out.println("Invalid input. Withdrawal cancelled.");
            }
        } else {
            System.out.println("No application found to withdraw.");
        }
    }

    public List<Project> getVisibleProjects() {
        List<Project> visibleProjects = new ArrayList<>();
        for (Project p : allProjects) {
            if (p.isVisible()) {
                visibleProjects.add(p);
            }
        }
        return visibleProjects;
    }

    public void submitEnquiry(Project project, String content) {
        enquiryController.submitEnquiry(applicant, project, content);
    }

    public List<Enquiry> getApplicantEnquiries() {
        return applicant.getEnquiries();
    }

    public void editEnquiry(Enquiry enquiry, String newContent) {
        enquiryController.editEnquiry(applicant, enquiry.getEnquiryID(), newContent);

    }

    public void deleteEnquiry(Enquiry enquiry) {
        enquiryController.deleteEnquiry(applicant, enquiry.getProject(), enquiry.getEnquiryID());
    }

    public void viewApplicationStatus(Applicant applicant) {
        Application app = applicant.getApplication();
        if (app != null) {
            
            Project appliedProject = null;
            for (Project p : allProjects) {
                if (p.getProjectName().equals(app.getProject().getProjectName())) {
                    appliedProject = p;
                    break;
                }
            }
            
            if (appliedProject != null) {
                System.out.println("Application Status: " + app.getStatus());
                System.out.println("Project: " + appliedProject.getProjectName());
                System.out.println("Flat Type: " + app.getFlatType().getName());
            } else {
                System.out.println("Project data not available");
            }
        } else {
            System.out.println("No application made yet.");
        }
    }
}
