public class EnquiryFileWriter extends FileWriter<Enquiry> {
    public EnquiryFileWriter() {
        super("EnquiryList.csv");
    }

    @Override
    protected String getHeader() {
        return "EnquiryID,ApplicantNRIC,ProjectName,Content,Response";
    }

    @Override
    protected String formatLine(Enquiry enquiry) {
        return String.format("%d,%s,%s,%s,%s",
                enquiry.getEnquiryID(),
                enquiry.getApplicant().getNric(),
                enquiry.getProject().getProjectName(),
                enquiry.getContent().replace(",", "\\,"),
                enquiry.getResponse().replace(",", "\\,"));
    }
}

public class EnquiryFileReader extends FileReader<Enquiry> {
    private Map<String, Applicant> applicants;
    private Map<String, Project> projects;
    
    public EnquiryFileReader(Map<String, Applicant> applicants, Map<String, Project> projects) {
        super("EnquiryList.csv");
        this.applicants = applicants;
        this.projects = projects;
    }

    @Override
    protected void processLine(String line, Map<String, Enquiry> enquiries) {
        String[] data = line.split(",(?=([^\\\\]|\\\\[^,])*$)"); // Split by comma, respecting escaped commas
        
        int enquiryID = Integer.parseInt(data[0]);
        String applicantNRIC = data[1];
        String projectName = data[2];
        String content = data[3].replace("\\,", ",");
        String response = data[4].replace("\\,", ",");
        
        Applicant applicant = applicants.get(applicantNRIC);
        Project project = projects.get(projectName);
        
        if (applicant != null && project != null) {
            Enquiry enquiry = new Enquiry(applicant, project, content);
            // Set the ID and response manually since we're loading from file
            enquiry.setEnquiryID(enquiryID);
            enquiry.setResponse(response);
            
            // Add to the collections
            enquiries.put(String.valueOf(enquiryID), enquiry);
            applicant.getEnquiries().add(enquiry);
            project.getEnquiries().add(enquiry);
        }
    }
}
