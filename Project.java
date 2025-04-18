import java.util.*;

public class Project {
    private String projectName;
    private String neighborhood;
    private Date applicationOpenDate;
    private Date applicationCloseDate;
    private boolean visibility;
    private List<FlatType> flatTypes;
    private Manager managerInCharge;
    private int officerSlots;
    private List<HDBOfficer> assignedOfficers;
    private List<OfficerRegistration> registrationRequests;
    private List<Enquiry> enquiries;

   
    public Project(int projectID, String projectName, String neighborhood, Date applicationOpenDate,
                   Date applicationCloseDate, boolean visibility, List<FlatType> flatTypes,
                   Manager managerInCharge, int officerSlots) {
                    this.projectName = projectName;
                    this.neighborhood = neighborhood;
                    this.applicationOpenDate = applicationOpenDate;
                    this.applicationCloseDate = applicationCloseDate;
                    this.visibility = visibility;
                    this.flatTypes = flatTypes;
                    this.managerInCharge = managerInCharge;
                    this.officerSlots = officerSlots;
                    this.assignedOfficers = new ArrayList<>();
                    this.registrationRequests = new ArrayList<>();
                    this.enquiries = new ArrayList<>();
    }

   
    public boolean isOpen() {
        Date now = new Date(); 
        return now.after(applicationOpenDate) && now.before(applicationCloseDate);
    }
    public FlatType getFlatTypeByName(String name) {
        for (FlatType ft : flatTypes) {
            if (ft.getName().equals(name)) {
                return ft;
            }
        }
        return null; 
    }
    public int getRemainingOfficerSlots() {
        return officerSlots - assignedOfficers.size();
    }
    public void addOfficerRegistration(OfficerRegistration reg) {
        registrationRequests.add(reg);
    }
    public OfficerRegistration getRegistrationByOfficer(HDBOfficer officer) {
        for (OfficerRegistration reg : registrationRequests) {
            if (reg.getOfficer().equals(officer)) {
                return reg;
            }
        }
        return null;
    }
    public void assignOfficer(HDBOfficer officer) {
        if (getRemainingOfficerSlots() > 0 && !assignedOfficers.contains(officer)) {
            assignedOfficers.add(officer);
        }
    }
    public boolean isOverlappingWith(Date startDate, Date endDate) {
        
    }
    public boolean isOverlappingWith(Project otherProject) {
        return isOverlappingWith(otherProject.applicationOpenDate, otherProject.applicationCloseDate);
    }
    public void addEnquiry(Enquiry enquiry) {
        enquiries.add(enquiry);
    }
    public Date getApplicationOpenDate() {
        return applicationOpenDate;
    }
    
    public Date getApplicationCloseDate() {
        return applicationCloseDate;
    }
    
}
