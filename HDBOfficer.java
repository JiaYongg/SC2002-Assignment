import java.util.List;

public class HDBOfficer extends User{

    private List<Project> handledProject;
    private OfficerRegistrationStatus registrationStatus;
    private List<OfficerRegistration> registrationRequests;

    public HDBOfficer(List<Project> handledProject,
    OfficerRegistrationStatus registrationStatus,
    List<OfficerRegistration> registrationRequests) {
        this.handledProject = handledProject;
        this.registrationStatus = registrationStatus;
        this.registrationRequests = registrationRequests;
    }
    public void addHandledProject(Project project){
        if(!handledProject.contains(project)){
            handledProject.add(project);
        }
    }
}
