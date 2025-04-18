import java.util.*;

public class Project {
    private int projectID;
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
    private List<Enquiry> enquiries;

    public Project(int projectID, String projectName, String neighborhood, Date applicationOpenDate,
                   Date applicationCloseDate, boolean visibility, List<FlatType> flatTypes,
                   HDBManager managerInCharge, int officerSlots) {
        this.projectID = projectID;
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

    public boolean isOpen(){
        Date now= new Date();
        return now.after(applicationOpenDate) && now.before(applicationCloseDate)
    }

    public FlatType(String name){
        for(FlatType namelist: flatTypes){
            if(namelist.getName().equals(name)){
                return namelist;
            }
        }
        return null;
    }

    public int getRemainingOfficerSlots(){
        return officerSlots - assignedOfficers.size();
    }

    public void addOfficerRegistration(OfficerRegistration reg){
        registrationRequests.add(reg);
    }

    public OfficerRegistration getRegistrationByOfficer(HDBOfficer officer){
        for(OfficerRegistration reg:registrationRequests){
            if(reg.getOfficer().equals(officer)){
                return reg;
            }
        }
        return null;
    }

    public void assignOfficer(HDBOfficer officer){
        if(getRemainingOfficerSlots>0 && !assignedOfficers.contains(officer)){
            assignedOfficers.add(officer);
        }
    }
    public Boolean isOverlappingWith(Date appStartDate, Date appEndDate){
        return !applicationCloseDate.before(appStartDate) && !applicationOpenDate.after(appEndDate);
    }
    public Boolean IsOverlappingWith(Project otherProject){
        return isOverlappingWith(otherProject.applicationOpenDate, otherProject.applicationCloseDate);
    }

    public void addEnquiry(Enquiry e){
        enquiries.add(e);
    }
    public Date getApplicationStartDate(){
        return applicationOpenDate;
    }
    public Date getApplicationEndDate(){
        return applicationCloseDate;
    }


}
        
            
