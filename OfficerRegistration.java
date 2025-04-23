import java.util.Date;

public class OfficerRegistration {

    private int registrationId;
    private HDBOfficer officer;
    private Project project;
    private OfficerRegistrationStatus status;
    private Date registrationDate;

    public OfficerRegistration(int registrationId, HDBOfficer officer, Project project,
    OfficerRegistrationStatus status, Date registrationDate) {
    this.registrationId = registrationId;
    this.officer = officer;
    this.project = project;
    this.status = status;
    this.registrationDate = registrationDate;
    }
    public OfficerRegistration(HDBOfficer officer, Project project){
        this.officer=officer;
        this.project=project;
    }

    public OfficerRegistrationStatus getRegistrationStatus() {
        return status;
    }

    public HDBOfficer getOfficer() {
        return officer;
    }

    public Project getProject() {
        return project;
    }

    public int getRegistrationId() {
        return registrationId;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setStatus(OfficerRegistrationStatus status) {
        this.status = status;
    }
}