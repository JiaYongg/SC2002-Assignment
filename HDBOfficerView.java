import java.util.List;
import java.util.Scanner;

public class HDBOfficerView {
    private HDBOfficerController controller;
    private Scanner scanner;
    
    public HDBOfficerView(HDBOfficerController controller) {
        this.controller = controller;
        this.scanner = new Scanner(System.in);
    }
    
    public boolean displayOfficerMenu() {
        boolean exitProgram = false;
        boolean logout = false;
        
        while (!logout && !exitProgram) {
            System.out.println("\n===== HDB Officer Menu =====");
            System.out.println("1. View Available Projects");
            System.out.println("2. View Eligible Projects"); // NEW
            System.out.println("3. Submit Application as Officer");
            System.out.println("4. Register for Project");
            System.out.println("5. View Registration Status");
            System.out.println("6. View Assigned Project");
            System.out.println("7. View Project Enquiries");
            System.out.println("8. Process Flat Booking");
            System.out.println("0. Logout");
            System.out.println("9. Exit Program");
            System.out.print("Enter your choice: ");
            
            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());
                
                switch (choice) {
                    case 1:
                        viewAvailableProjects();
                        break;
                    case 2:
                        displayEligibleProjects();
                        break;
                    case 3:
                        submitApplicationMenu();
                    case 4:
                        registerForProject();
                        break;
                    case 5:
                        viewRegistrationStatus();
                        break;
                    case 6:
                        viewAssignedProject();
                        break;
                    case 7:
                        viewProjectEnquiries();
                        break;
                    case 8:
                        processFlatBooking();
                        break;
                    case 0:
                        logout = true;
                        System.out.println("Logging out...");
                        break;
                    case 9:
                        exitProgram = true;
                        System.out.println("Exiting program...");
                        break;
                    default:
                        System.out.println("Invalid option. Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
        
        return exitProgram;
    }
    
    private void viewAvailableProjects() {
        System.out.println("\n===== Available Projects =====");
        List<Project> projects= controller.getVisibleProjects();
        if(projects.isEmpty()){
            System.out.println("No Projects available");
        }else{
            for (int i = 0; i < projects.size(); i++) {
                Project p = projects.get(i);
                System.out.printf("%d. %s (%s)\n", i + 1, p.getProjectName(), p.getNeighborhood());
            }
            }
        System.out.println("Press enter to continue....");
        scanner.nextLine();
    }
        
    
    private void displayEligibleProjects() {
        System.out.println("\n===== Eligible Projects =====");
        List<Project> projects = controller.getVisibleProjects();
        boolean found = false;
        for (Project p : projects) {
            if (!controller.canOfficerApply(p)) continue;
            for (FlatType ft : p.getFlatTypes()) {
                if (controller.checkEligibility(controller.getCurrentOfficer(), p, ft)) {
                    System.out.printf("%s - %s ($%.2f) [%d units]\n",
                        p.getProjectName(), ft.getName(), ft.getPrice(), ft.getUnitCount());
                    found = true;
                }
            }
        }
        if (!found) System.out.println("No eligible projects.");
        System.out.println("Press Enter to continue...");
        scanner.nextLine();
    }

    private void submitApplicationMenu() {
        System.out.println("\n===== Submit Application =====");
        List<Project> eligible = controller.getEligibleProjectsToApply();
        if (eligible.isEmpty()) {
            System.out.println("No eligible projects available.");
            return;
        }
    
        for (int i = 0; i < eligible.size(); i++) {
            Project p = eligible.get(i);
            System.out.println((i + 1) + ". " + p.getProjectName());
        }
    
        System.out.print("Select project number: ");
        try {
            int idx = Integer.parseInt(scanner.nextLine());
            if (idx < 1 || idx > eligible.size()) {
                System.out.println("Invalid selection.");
                return;
            }
            Project selected = eligible.get(idx - 1);
            FlatType chosen = controller.selectFlatType(selected);
            if (chosen != null) {
                controller.submitApplicationAsOfficer(selected, chosen);
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
        }
        System.out.println("Press Enter to continue...");
        scanner.nextLine();
    }
    
    
    private void registerForProject() {
        System.out.println("\n===== Register for Project =====");
        List<Project> projects = controller.getAvailableProjectsForRegistration();
        if (projects.isEmpty()) {
            System.out.println("No projects available for registration.");
        } else {
            for (int i = 0; i < projects.size(); i++) {
                Project p = projects.get(i);
                System.out.printf("%d. %s (%s)\n", i + 1, p.getProjectName(), p.getNeighborhood());
            }
    
            System.out.print("Select project number to register: ");
            try {
                int choice = Integer.parseInt(scanner.nextLine());
                if (choice >= 1 && choice <= projects.size()) {
                    controller.registerToHandleProject(projects.get(choice - 1));
                } else {
                    System.out.println("Invalid project number.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input.");
            }
        }
        System.out.println("Press Enter to continue...");
        scanner.nextLine();
    }
    
    private void viewRegistrationStatus() {
        System.out.println("\n===== Registration Status =====");
        List<OfficerRegistration> registrations = controller.getRegistrations();
        
        if (registrations.isEmpty()) {
            System.out.println("You have not registered for any projects yet.");
        } else {
            System.out.println("ID | Project Name | Status");
            System.out.println("-------------------------");
            
            int index = 1;
            for (OfficerRegistration reg : registrations) {
                System.out.printf("%2d | %-20s | %s\n", 
                                 index++, 
                                 reg.getProject().getProjectName(), 
                                 reg.getRegistrationStatus());
            }
        }
        
        System.out.println("Press Enter to continue...");
        scanner.nextLine();
    }
    
    public void viewAssignedProject() {
        System.out.println("\n===== Assigned Project =====");
        Project assignedProject = controller.getAssignedProject();
        
        if (assignedProject == null) {
            System.out.println("You are not assigned to any project yet.");
        } else {
            System.out.println("Project Name: " + assignedProject.getProjectName());
            System.out.println("Neighborhood: " + assignedProject.getNeighborhood());
            System.out.println("Application Period: " + 
                             assignedProject.getApplicationOpenDate() + " to " + 
                             assignedProject.getApplicationCloseDate());
            
            System.out.println("\nFlat Types:");
            for (FlatType flatType : assignedProject.getFlatTypes()) {
                System.out.println(flatType.getName() + " - " + 
                                  flatType.getUnitCount() + " units available - $" + 
                                  flatType.getPrice());
            }
        }
        
        System.out.println("Press Enter to continue...");
        scanner.nextLine();
    }
    
    public void viewProjectEnquiries() {
        System.out.println("\n===== Project Enquiries =====");
        List<Enquiry> enquiries = controller.getProjectEnquiries();
        if (enquiries.isEmpty()) {
            System.out.println("No enquiries found.");
        } else {
            for (Enquiry e : enquiries) {
                System.out.println("From: " + e.getApplicant().getName());
                System.out.println("Content: " + e.getContent());
                System.out.println("Response: " + (e.getResponse().isEmpty() ? "No response yet." : e.getResponse()));
                System.out.println("------");
            }
        }
        System.out.println("Press Enter to continue...");
        scanner.nextLine();
    }
    
    private void processFlatBooking() {
        System.out.println("\n===== Process Flat Booking =====");
        System.out.print("Enter applicant NRIC: ");
        String nric = scanner.nextLine().trim();
    
        Application app = controller.getApplicationByNric(nric);
        if (app == null) {
            System.out.println("Application not found.");
            return;
        }
    
        controller.bookFlat(app);
        Receipt receipt = controller.generateReceipt(app);
        System.out.println(receipt.formatReceipt());
    
        System.out.println("Press Enter to continue...");
        scanner.nextLine();
    }
    
    public void closeScanner() {
        scanner.close();
    }
}
