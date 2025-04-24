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

    
    public Application(int applicationID, Applicant applicant, Project project, FlatType flatType) {
        this.applicationID = applicationID == 0 ? counter++ : applicationID;
        this.applicant = applicant;
        this.project = project;
        this.flatType = flatType;
        this.status = ApplicationStatus.PENDING;
        this.dateApplied = new Date(); 
        this.dateBooked = null;

        if (this.applicationID >= counter) {
            counter = this.applicationID + 1;
        }  
    }

    
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

    
    public Application(int applicationID, Applicant applicant, Project project, FlatType flatType,
                      ApplicationStatus status, Date dateApplied, Date dateBooked) {
        this.applicationID = applicationID;
        this.applicant = applicant;
        this.project = project;
        this.flatType = flatType;
        this.status = status;
        this.dateApplied = dateApplied != null ? dateApplied : new Date();
        this.dateBooked = dateBooked;

        
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
        
        
        if (status == ApplicationStatus.BOOKED && this.dateBooked == null) {
            this.dateBooked = new Date();
            
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
