public class Enquiry {
    private static int counter = 1000;

    private int enquiryID;
    private Applicant applicant;
    private Project project;
    private String content;
    private String response;

    public Enquiry(Applicant applicant, Project project, String content) {
        this.enquiryID = counter++;
        this.applicant = applicant;
        this.project = project;
        this.content = content;
        this.response = "";
    }

    public void setEnquiryID(int enquiryID) {
        this.enquiryID = enquiryID;
        // Update the counter if needed to prevent duplicate IDs
        if (enquiryID >= counter) {
            counter = enquiryID + 1;
        }
    }
    


    public int getEnquiryID() {
        return enquiryID;
    }

    public Applicant getApplicant() {
        return applicant;
    }

    public Project getProject() {
        return project;
    }

    public String getContent() {
        return content;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String newResponse) {
        this.response = newResponse;
    }

    public void updateContent(String newContent) {
        this.content = newContent;
    }
}
