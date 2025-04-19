import java.util.List;

public class Applicant extends User {
    private Application applicantion;
    private List<Enquiry> enquiries;

    public Applicant() {}

    public Application getApplication() {
        return applicantion;
    }

    public void setApplication() {
        this.application = application;
    }

    public List<Enquiry> getEnquiries() {
        return enquiries;
    }

    public void setEnquiries(List<Enquiry> enquiries) {
        this.enquiries = enquiries;
    }
}
