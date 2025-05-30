import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnquiryController {
    private List<Applicant> allApplicants;
    private List<Project> allProjects;
    private ApplicantFileReader applicantReader;
    private EnquiryFileWriter enquiryWriter;
    private List<Enquiry> allEnquiries;

    public EnquiryController(List<Project> projects) {
        this.applicantReader = new ApplicantFileReader();
        this.enquiryWriter = new EnquiryFileWriter();

        
        this.allProjects = projects;

        
        this.allApplicants = new ArrayList<>();
        for (User user : applicantReader.readFromFile().values()) {
            if (user instanceof Applicant) {
                allApplicants.add((Applicant) user);
            }
        }

        
        this.allEnquiries = new ArrayList<>();

        
        loadEnquiries();
    }

    private void loadEnquiries() {
        try {
            
            Map<String, Applicant> applicantMap = new HashMap<>();
            for (Applicant applicant : allApplicants) {
                applicantMap.put(applicant.getNric(), applicant);
            }

            Map<String, Project> projectMap = new HashMap<>();
            for (Project project : allProjects) {
                projectMap.put(project.getProjectName(), project);
            }

            
            EnquiryFileReader reader = new EnquiryFileReader(applicantMap, projectMap);
            Map<String, Enquiry> enquiryMap = reader.readFromFile();

            
            for (Enquiry enquiry : enquiryMap.values()) {
                allEnquiries.add(enquiry);

                Applicant applicant = enquiry.getApplicant();
                Project project = enquiry.getProject();

                if (!applicant.getEnquiries().contains(enquiry)) {
                    applicant.getEnquiries().add(enquiry);
                }
                if (!project.getEnquiries().contains(enquiry)) {
                    project.addEnquiry(enquiry);
                }
            }

            
        } catch (Exception e) {
            System.out.println("Error loading enquiries: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void saveEnquiries() {
        
        Map<String, Enquiry> enquiryMap = new HashMap<>();
        for (Enquiry enquiry : allEnquiries) {
            enquiryMap.put(String.valueOf(enquiry.getEnquiryID()), enquiry);
        }

        
        enquiryWriter.writeToFile(enquiryMap);
    }

    public void submitEnquiry(Applicant applicant, Project project, String content) {
        Enquiry enq = new Enquiry(applicant, project, content);
        applicant.getEnquiries().add(enq);
        project.addEnquiry(enq);
        allEnquiries.add(enq);

        System.out.println("Enquiry submitted with ID: " + enq.getEnquiryID());

        
        saveEnquiries();
    }

    public List<Project> getAllProjects() {
        return allProjects;
    }

    public List<Applicant> getAllApplicants() {
        return allApplicants;
    }

    public void editEnquiry(Applicant applicant, int enquiryId, String newContent) {
        if (applicant.getEnquiries() == null) {
            System.out.println("Applicant has no enquiries.");
            return;
        }

        for (Enquiry enq : applicant.getEnquiries()) {
            if (enq.getEnquiryID() == enquiryId) {
                enq.updateContent(newContent);
                updateAllEnquiryReferences(enq);
                saveEnquiries();
                System.out.println("Enquiry ID " + enquiryId + " updated.");
                return;
            }
        }
        System.out.println("Enquiry not found.");
    }

    public void deleteEnquiry(Applicant applicant, Project project, int enquiryId) {
        if (applicant.getEnquiries() == null) {
            System.out.println("Applicant has no enquiries.");
            return;
        }

        Enquiry target = null;

        for (Enquiry enq : allEnquiries) {
            if (enq.getEnquiryID() == enquiryId) {
                target = enq;
                break;
            }
        }

        if (target != null) {
            applicant.getEnquiries().remove(target);
            project.getEnquiries().remove(target);
            allEnquiries.remove(target);

            
            saveEnquiries();
            ;

            System.out.println("Enquiry ID " + enquiryId + " deleted.");
            saveEnquiries();
        } else {
            System.out.println("Enquiry not found.");
        }
    }

    public void replyToEnquiry(Project project, int enquiryId, String reply) {
        if (project.getEnquiries() == null) {
            System.out.println("Project has no enquiries.");
            return;
        }

        for (Enquiry enq : project.getEnquiries()) {
            if (enq.getEnquiryID() == enquiryId) {
                enq.setResponse(reply);
                System.out.println("Replied to Enquiry ID " + enquiryId);
                saveEnquiries();
                return;
            }
        }
        System.out.println("Enquiry not found.");
    }

    public void viewEnquiryByManager(HDBManager manager) {
        System.out.println("===== Enquiries for Manager: " + manager.getName() + " =====");
        boolean hasEnquiries = false;

        for (Project project : manager.getManagedProjects()) {
            if (project.getEnquiries() == null || project.getEnquiries().isEmpty())
                continue;

            hasEnquiries = true;
            for (Enquiry enq : project.getEnquiries()) {
                System.out.println("Enquiry ID: " + enq.getEnquiryID());
                System.out.println("Applicant: " + enq.getApplicant().getName());
                System.out.println("Project: " + enq.getProject().getProjectName());
                System.out.println("Content: " + enq.getContent());
                System.out.println("Reply: " + (enq.getResponse().isEmpty() ? "No reply yet" : enq.getResponse()));
                System.out.println("------------------------");
            }
        }

        if (!hasEnquiries) {
            System.out.println("No enquiries found for this manager.");
        }
    }

    public void viewAllEnquiries() {
        System.out.println("===== All Enquiries in the System =====");
        boolean hasEnquiries = false;

        for (Project project : allProjects) {
            List<Enquiry> enquiries = project.getEnquiries();
            if (enquiries == null || enquiries.isEmpty())
                continue;

            hasEnquiries = true;
            System.out.println("\nProject: " + project.getProjectName());
            System.out.println("Manager: " + project.getManagerInCharge().getName());
            System.out.println("------------------------");

            for (Enquiry enq : enquiries) {
                System.out.println("Enquiry ID: " + enq.getEnquiryID());
                System.out.println("Applicant: " + enq.getApplicant().getName());
                System.out.println("Content: " + enq.getContent());
                System.out.println("Reply: " + (enq.getResponse().isEmpty() ? "No reply yet" : enq.getResponse()));
                System.out.println("------------------------");
            }
        }

        if (!hasEnquiries) {
            System.out.println("No enquiries found in the system.");
        }
    }

    public void linkEnquiriesTo(Applicant activeApplicant) {
        for (Applicant loadedApplicant : allApplicants) {
            if (loadedApplicant.getNric().equals(activeApplicant.getNric())) {
                List<Enquiry> loaded = loadedApplicant.getEnquiries();
                List<Enquiry> actual = activeApplicant.getEnquiries();

                actual.clear(); 
                actual.addAll(loaded); 
                break;
            }
        }
    }

    private void updateAllEnquiryReferences(Enquiry updatedEnquiry) {
        
        updatedEnquiry.getApplicant().getEnquiries().stream()
                .filter(e -> e.getEnquiryID() == updatedEnquiry.getEnquiryID())
                .findFirst()
                .ifPresent(e -> {
                    e.updateContent(updatedEnquiry.getContent());
                    e.setResponse(updatedEnquiry.getResponse());
                });

        
        updatedEnquiry.getProject().getEnquiries().stream()
                .filter(e -> e.getEnquiryID() == updatedEnquiry.getEnquiryID())
                .findFirst()
                .ifPresent(e -> {
                    e.updateContent(updatedEnquiry.getContent());
                    e.setResponse(updatedEnquiry.getResponse());
                });
    }
}
