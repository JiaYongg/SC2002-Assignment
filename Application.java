import java.util.Date;

public class Application {
    private static int counter = 1000;

    private int applicationID;
    private Applicant applicant;
    private Project project;
    private FlatType flatType;
    private ApplicationStatus status;
    private Date dateApplied;
    private Date dateBooked;

    // Constructor for new applications
    public Application(int applicationID, Applicant applicant, Project project, FlatType flatType) {
        this.applicationID = applicationID;
        this.applicant = applicant;
        this.project = project;
        this.flatType = flatType;
        this.status = ApplicationStatus.PENDING;
        this.dateApplied = new Date(); // Set application date to current date (April 23, 2025)
        this.dateBooked = null;
    }

    // Constructor for loading from file with dateApplied
    public Application(int applicationID, Applicant applicant, Project project, FlatType flatType,
                      Date dateApplied) {
        this.applicationID = applicationID;
        this.applicant = applicant;
        this.project = project;
        this.flatType = flatType;
        this.status = ApplicationStatus.PENDING;
        this.dateApplied = dateApplied != null ? dateApplied : new Date();
        this.dateBooked = null;


        if (applicationID >= counter) {
            counter = applicationID + 1;
        }
    }

    // Constructor for loading from file with all details
    public Application(int applicationID, Applicant applicant, Project project, FlatType flatType,
                      ApplicationStatus status, Date dateApplied, Date dateBooked) {
        this.applicationID = applicationID;
        this.applicant = applicant;
        this.project = project;
        this.flatType = flatType;
        this.status = status;
        this.dateApplied = dateApplied != null ? dateApplied : new Date();
        this.dateBooked = dateBooked;

        // Update counter if needed
        if (applicationID >= counter) {
            counter = applicationID + 1;
        }
    }

    public int getApplicationID() {
        return applicationID;
    }

    public Date getDateApplied() {
        return this.dateApplied;
    }

    public void setDateApplied(Date dateApplied) {
        this.dateApplied = dateApplied;
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
        
        // Only decrement unit count when status changes to BOOKED
        if (status == ApplicationStatus.BOOKED && this.dateBooked == null) {
            this.dateBooked = new Date();
            // Decrement unit count only when booking is confirmed
            this.flatType.decrementUnit();
        }
    }

    public Date getDateBooked() {
        return dateBooked;
    }

    public void setDateBooked(Date dateBooked) {
        this.dateBooked = dateBooked;
    }
}
