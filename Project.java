import java.util.*;

public class Project {
    private String projectName;
    private String neighborhood;
    private Date applicationOpenDate;
    private Date applicationCloseDate;
    private boolean visibility;
    private List<FlatType> flatTypes;
    private HDBManager managerInCharge;
    private int officerSlots;
    private List<HDBOfficer> assignedOfficers;
    private List<OfficerRegistration> registrationRequests;
    // private List<Enquiry> enquiries;

   
    public Project(String projectName, String neighborhood, Date applicationOpenDate,
                   Date applicationCloseDate, boolean visibility, List<FlatType> flatTypes,
                   HDBManager managerInCharge, int officerSlots) {
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
                    // this.enquiries = new ArrayList<>();
    }

    public String getProjectName (){
        return this.projectName;
    }

    public boolean isVisible () {
        return this.visibility;
    }

    public HDBManager getManagerInCharge (){
        return this.managerInCharge;
    }

    public Date getApplicationOpenDate() {
        return applicationOpenDate;
    }
    
    public Date getApplicationCloseDate() {
        return applicationCloseDate;
    }

    public List<HDBOfficer> getAssignedOfficers(){
        return assignedOfficers;
    }
    public List<FlatType> getFlatTypes(){
        return flatTypes;
    }
    public String getNeighborhood (){
        return this.neighborhood;
    }
    
    public int getOfficerSlots(){
        return this.officerSlots;
    }

    









    public void setVisibility(boolean newVisibility){
        this.visibility = newVisibility;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
    
    public void setNeighborhood(String neighborhood) {
        this.neighborhood = neighborhood;
    }

    public void setFlatTypes(List<FlatType> flatTypes) {
        this.flatTypes = flatTypes;
    }

    public void setApplicationOpenDate(Date applicationOpenDate) {
        this.applicationOpenDate = applicationOpenDate;
    }

    public void setApplicationCloseDate(Date applicationCloseDate) {
        this.applicationCloseDate = applicationCloseDate;
    }

    public void setOfficerSlots(int officerSlots) {
        if (officerSlots <= 0 || officerSlots > 10) {
            throw new IllegalArgumentException("Officer slots must be between 1 and 10");
        }
        this.officerSlots = officerSlots;
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
        // Check if either of the date ranges is completely before or after the other
        return !(applicationCloseDate.before(startDate) || applicationOpenDate.after(endDate));
    }
    public boolean isOverlappingWith(Project otherProject) {
        return isOverlappingWith(otherProject.applicationOpenDate, otherProject.applicationCloseDate);
    }
    // public void addEnquiry(Enquiry enquiry) {
    //     enquiries.add(enquiry);
    // }

    
}
