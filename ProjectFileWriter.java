import java.util.Map;
import java.text.SimpleDateFormat;
import java.util.List;

public class ProjectFileWriter extends FileWriter <Project> {

    /**
     * Constructor that sets the file path to ProjectList.csv
     */
    public ProjectFileWriter() {
        super("ProjectList.csv");
    }

    /**
     * Writes a map of projects to the CSV file
     * 
     * @param projects A map of project names to Project objects
     */
    @Override
    public void writeToFile(Map<String, Project> projects) {
        writeCSV(projects);
    }

    /**
     * Returns the CSV header line
     * 
     * @return A string containing the CSV header
     */
    @Override
    protected String getHeader() {
        return "Project Name,Neighborhood,Type 1,Number of units for Type 1,Selling price for Type 1," +
               "Type 2,Number of units for Type 2,Selling price for Type 2," +
               "Application opening date,Application closing date,Manager,Officer Slot,Officer,Visibility";
    }

    /**
     * Formats a Project object into a CSV line
     * 
     * @param project The Project object to format
     * @return A string containing the formatted CSV line
     */
    @Override
    protected String formatLine(Project project) {
        StringBuilder sb = new StringBuilder();
        
        // Project name and neighborhood
        sb.append(project.getProjectName()).append(",");
        sb.append(project.getNeighborhood()).append(",");
        
        // Flat types, units and prices
        List<FlatType> flatTypes = project.getFlatTypes();
        
        // Add Type 1 if it exists
        if (flatTypes.size() > 0) {
            FlatType type1 = flatTypes.get(0);
            sb.append(type1.getName()).append(",");
            sb.append(type1.getUnitCount()).append(",");
            sb.append(type1.getPrice()).append(",");
        } else {
            sb.append(",,,");
        }
        
        // Add Type 2 if it exists
        if (flatTypes.size() > 1) {
            FlatType type2 = flatTypes.get(1);
            sb.append(type2.getName()).append(",");
            sb.append(type2.getUnitCount()).append(",");
            sb.append(type2.getPrice()).append(",");
        } else {
            sb.append(",,,");
        }
        
        // Format and add dates
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        sb.append(dateFormat.format(project.getApplicationOpenDate())).append(",");
        sb.append(dateFormat.format(project.getApplicationCloseDate())).append(",");
        
        // Add manager and officer slots
        sb.append(project.getManagerInCharge().getName()).append(",");
        sb.append(project.getOfficerSlots()).append(",");
        
        // Add officers if any
        List<HDBOfficer> officers = project.getAssignedOfficers();
        if (officers.size() > 0) {
            StringBuilder officerList = new StringBuilder();
            for (int i = 0; i < officers.size(); i++) {
                if (i > 0) {
                    officerList.append(";");
                }
                officerList.append(officers.get(i).getNric());
            }
            sb.append(officerList);
        }

        sb.append(",").append(project.isVisible());
        
        return sb.toString();
    }
}
