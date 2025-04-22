import java.util.Map;

public class EnquiryFileReader extends FileReader<Enquiry> {
    private Map<String, Applicant> applicants;
    private Map<String, Project> projects;
    
    public EnquiryFileReader(Map<String, Applicant> applicants, Map<String, Project> projects) {
        super("Enquiries.csv");
        this.applicants = applicants;
        this.projects = projects;
    }

    @Override
    public Map<String, Enquiry> readFromFile() {
        return readCSV();
    }

    @Override
    protected void processLine(String line, Map<String, Enquiry> enquiries) {
        try {
            String[] data = line.split(",");
            
            // Parse the CSV data according to your headers
            // ID, applicant, project_name, content, response
            int enquiryID = Integer.parseInt(data[0].trim());
            String applicantNRIC = data[1].trim();
            String projectName = data[2].trim();
            String content = data[3].trim();
            String response = data.length > 4 ? data[4].trim() : "";
            
            // Get the referenced applicant and project
            Applicant applicant = applicants.get(applicantNRIC);
            Project project = projects.get(projectName);
            
            if (applicant != null && project != null) {
                // Create the enquiry with the applicant and project
                Enquiry enquiry = new Enquiry(applicant, project, content);
                
                // Set the ID and response manually
                enquiry.setEnquiryID(enquiryID);
                if (!response.isEmpty()) {
                    enquiry.setResponse(response);
                }
                
                // Add to the map using enquiry ID as key
                enquiries.put(String.valueOf(enquiryID), enquiry);
                
                // Also add to the applicant's and project's enquiry lists
                if (!applicant.getEnquiries().contains(enquiry)) {
                    applicant.getEnquiries().add(enquiry);
                }
                
                if (!project.getEnquiries().contains(enquiry)) {
                    project.addEnquiry(enquiry);
                }
            }
        } catch (Exception e) {
            System.out.println("Error processing enquiry line: " + line);
            e.printStackTrace();
        }
    }
}
