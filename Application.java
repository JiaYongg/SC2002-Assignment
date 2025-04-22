import java.util.Date;

public class Application {
    private static int counter = 1000;

    private int applicationID;
    private Applicant applicant;
    private Project project;
    private FlatType flatType;
    private ApplicationStatus status;
    private Date dateBooked;

    // Constructor for new applications
    public Application(Applicant applicant, Project project, FlatType flatType) {
        this.applicationID = counter++;
        this.applicant = applicant;
        this.project = project;
        this.flatType = flatType;
        this.status = ApplicationStatus.PENDING;
        this.dateBooked = null;
    }

    // Constructor for loading from file
    public Application(int applicationID, Applicant applicant, Project project, FlatType flatType) {
        this.applicationID = applicationID;
        this.applicant = applicant;
        this.project = project;
        this.flatType = flatType;
        this.status = ApplicationStatus.PENDING;
        this.dateBooked = null;

        // Update counter if needed
        if (applicationID >= counter) {
            counter = applicationID + 1;
        }
    }

    public int getApplicationID() {
        return applicationID;
    }

    public Date getApplicationDate() {
        return this.dateBooked;

    }

    public Applicant getApplicant() {
        return applicant;
    }

    public Project getProject() {
        return project;
    }

    public FlatType getFlatType() {
        return flatType;
    }

    public void setFlatType(FlatType flatType) {
        this.flatType = flatType;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }

    public Date getDateBooked() {
        return dateBooked;
    }

    public void setDateBooked(Date dateBooked) {
        this.dateBooked = dateBooked;
    }
}
