import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;

public class HDBOfficerController {
    private HDBOfficer currentOfficer;
    private List<Project> allProjects;
    private HDBOfficerView view;
    private static int receiptIdCounter=1;
    
    public HDBOfficerController(HDBOfficer currentOfficer, List<Project> allProjects,HDBOfficerView view){
        this.currentOfficer=currentOfficer;
        this.allProjects = new ArrayList<>();
        this.view=view;
        // In a full implementation, you would load projects from a file
    }
    
    public List<Project> getVisibleProjects() {
     List<Project> visibleProjects = new ArrayList<>();
         for (Project project : allProjects) {
             if (project.isVisible() && currentOfficer.isEligibleForProject(project)) {
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
        
         enquiry.setReply(reply);
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
    
     public boolean updateApplicationStatus(Application application, String newStatus) {
         if (currentOfficer.getAssignedProject() == null || 
             !application.getProject().equals(currentOfficer.getAssignedProject())) {
             return false;
         }
        
         application.setStatus(newStatus);
         return true;
     }
    
    
    
    public HDBOfficer getCurrentOfficer() {
        return currentOfficer;
    }
    public void registerToHandleProject(Project project){
        HDBOfficer officer=getCurrentOfficer();
        if(project.getRemainingOfficerSlots()<=0){
            System.out.println("no available slots");
            return;}

        if(project.getRegistrationByOfficer(officer)!= null) {
            System.out.println("You have already registered for this project.");
            return;
        }
        OfficerRegistration registration = new OfficerRegistration(officer, project);
        officer.setAssignedProject(project);
        officer.getRegistrations().add(registration);
        project.addOfficerRegistration(registration);  
        System.out.println("Registered to handle project: " + project.getProjectName());
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
    public void bookFlat(Applicant applicant, Project project, FlatType flatType){
        FlatType flat=project.getFlatTypeByName(flatType.getName());
        if(flat==null){
            System.out.println("Flat type not found");
            return;
        }
        // not sure
        System.out.println("Flat of type " + flatType.getName() + " booked for applicant: " + applicant.getName());
    }

    public Receipt generateReceipt(Applicant applicant){
        Receipt receipt = new Receipt(receiptIdCounter++, applicant,currentOfficer);
        System.out.println("Receipt generated for: " + applicant.getApplicant().getName());
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
                view.viewProjectEnquiries(enquiries);
            }
    }
}       
    

