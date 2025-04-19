public class EnquiryController {
    private EnquiryView view;

    public EnquiryController(EnquiryView view) {
        this.view = view;
    }

    public void submitEnquiry(Applicant applicant, Project project, String content) {
        Enquiry enquiry = new Enquiry(applicant, project, content, "");
        applicant.getEnquiries().add(enquiry);
        System.out.println("Enquiry submitted with ID: " + enquiry.getEnquiryID());
    }

    public void editEnquiry(int enquiryId, String newContent) {
        
    }

    public void deleteEnquiry(int enquiryId) {

    }

    public void replyToEnquiry(int enquiryId, String reply) {

    }

    public void viewEnquiryByManager(HDBManager manager) {

    }
}
