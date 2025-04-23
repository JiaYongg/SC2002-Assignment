import java.util.ArrayList;
import java.util.List;
import java.util.Date;


public class HDBOfficerController {
    private HDBOfficer currentOfficer;
    private List<Project> allProjects;
    private HDBOfficerView view;
    private static int receiptIdCounter=1;
    private static int registrationid=1;
    
    public HDBOfficerController(HDBOfficer currentOfficer, List<Project> allProjects,HDBOfficerView view){
        this.currentOfficer=currentOfficer;
        this.allProjects = new ArrayList<>();
        this.view=view;
        // In a full implementation, you would load projects from a file
    }
    
    public List<Project> getVisibleProjects() {
     List<Project> visibleProjects = new ArrayList<>();
         for (Project project : allProjects) {
             if (project.isVisible() && currentOfficer.canRegisterForProject(project)) {
                 visibleProjects.add(project);
             }
         }
         return visibleProjects;
     }
   
    public List<Project> getAvailableProjectsForRegistration() {
         List<Project> availableProjects = new ArrayList<>();
         for (Project project : allProjects) {
             if (project.getRemainingOfficerSlots() > 0 && 
                 currentOfficer.canRegisterForProject(project)) {
                 availableProjects.add(project);
             }
         }
         return availableProjects;
     }
    
    public boolean registerForProject(Project project) {
         if (!currentOfficer.canRegisterForProject(project)) {
             return false;
         }
        
         OfficerRegistration registration = new OfficerRegistration(currentOfficer, project);
         currentOfficer.addRegistration(registration);
         project.addOfficerRegistration(registration);
         return true;
     }
    
    public List<OfficerRegistration> getRegistrations() {
        return currentOfficer.getRegistrations();
    }
    
    public Project getAssignedProject() {
        return currentOfficer.getAssignedProject();
    }
    
     public List<Enquiry> getProjectEnquiries() {
         if (currentOfficer.getAssignedProject() == null) {
             return new ArrayList<>();
         }
        
         return currentOfficer.getAssignedProject().getEnquiries();
     }
    
     public boolean replyToEnquiry(Enquiry enquiry, String reply) {
         if (currentOfficer.getAssignedProject() == null || 
             !enquiry.getProject().equals(currentOfficer.getAssignedProject())) {
             return false;
         }
        
         enquiry.setResponse(reply);
         return true;
     }
    
     public Application getApplicationByNric(String nric) {
         if (currentOfficer.getAssignedProject() == null) {
             return null;
         }
        
         // This is a simplified implementation
         // In a real system, you would search through all applications for the project
         return null;
     }
    
     public boolean updateApplicationStatus(Application app, ApplicationStatus newStatus) {
         if (currentOfficer.getAssignedProject() == null || 
             !app.getProject().equals(currentOfficer.getAssignedProject())) {
             return false;
         }
       
         app.setStatus(newStatus);
         return true;
     }
    
    
    
    public HDBOfficer getCurrentOfficer() {
        return currentOfficer;
    }
    public void registerToHandleProject(Project project) {
        HDBOfficer officer = getCurrentOfficer();
    
        
        if (project.getRegistrationByOfficer(officer) != null) {
            System.out.println("You have already registered for this project.");
            return;
        }
    
        
        if (project.getRemainingOfficerSlots() <= 0) {
            System.out.println("No officer slots available in this project.");
            return;
        }
    
        
        OfficerRegistration registration = new OfficerRegistration(registrationid, officer, project, OfficerRegistrationStatus.pending, new Date() );
    
        
        project.addOfficerRegistration(registration);
        officer.addRegistration(registration);
    

        System.out.println("Registration submitted for project: " + project.getProjectName());
    }
    
    
    public void applyForProject(Project project, FlatType flatType) {
        if (!project.isOpen()) {
            System.out.println("Application is closed for this project.");
            return;
        }

        FlatType matchedFlatType = project.getFlatTypeByName(flatType.getName());

        if (matchedFlatType == null) {
            System.out.println("Flat type not available in this project.");
            return;
        }

        System.out.println("Application submitted for " + flatType.getName() + " in " + project.getProjectName());
    }
    public void bookFlat(Application application) {
        FlatType flat = application.getFlatType();
        if (flat.getRemainingUnits() <= 0) {
            System.out.println("Booking not allowed: No remaining units.");
            return;
        }
    
        application.setStatus(ApplicationStatus.BOOKED); // Also decrements unit inside
        System.out.println("Flat booked successfully for applicant: " + application.getApplicant().getName());
    }

    public Receipt generateReceipt(Application app) {
        Receipt receipt = new Receipt( receiptIdCounter++,  app, currentOfficer);
        System.out.println("Receipt generated for: " + app.getApplicant().getName());
        return receipt;
    }
    public OfficerRegistrationStatus getRegistrationStatus(Project project) {
        OfficerRegistration reg = project.getRegistrationByOfficer(currentOfficer);
        if (reg != null) {
            return reg.getRegistrationStatus();
        }
        return OfficerRegistrationStatus.none;
    }
    public void viewHandledProjectDetails(HDBOfficer officer) {
        Project assign = officer.getAssignedProject();
        if (assign == null) {
            System.out.println("No project assigned.");
        } else {
            System.out.println("Assigned project: " + assign.getProjectName());
            view.viewAssignedProject();
        }
    }
        public void viewEnquiries(HDBOfficer officer) {
            Project assigned = officer.getAssignedProject();
            if (assigned == null) {
                System.out.println("No project assigned.");
                return;
            }
    
            List<Enquiry> enquiries = assigned.getEnquiries();
            if (enquiries.isEmpty()) {
                System.out.println("No enquiries.");
            } else {
                view.viewProjectEnquiries();
            }
    }
}       
    

