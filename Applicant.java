import java.util.ArrayList;
import java.util.List;

public class Applicant extends User {
    private Application application;
    private List<Enquiry> enquiries;

    public Applicant() {
        this.enquiries = new ArrayList<>();
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public List<Enquiry> getEnquiries() {
        return enquiries;
    }

    public void addEnquiry(Enquiry enquiry) {
        enquiries.add(enquiry);
    }
}
