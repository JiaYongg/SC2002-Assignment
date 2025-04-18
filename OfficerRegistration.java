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

    public OfficerRegistrationStatus GetRegistrationStatus(){
        return status;
    }
    public HDBOfficer getOfficer(){
        return officer;
    }
    
