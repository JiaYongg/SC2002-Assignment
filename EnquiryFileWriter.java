import java.util.Map;

public class EnquiryFileWriter extends FileWriter<Enquiry> {
    public EnquiryFileWriter() {
        super("Enquiries.csv");
    }

    @Override
    public void writeToFile(Map<String, Enquiry> enquiries) {
        writeCSV(enquiries);
    }

    @Override
    protected String getHeader() {
        return "EnquiryID,ApplicantNRIC,ProjectName,Content,Response";
    }

    @Override
    protected String formatLine(Enquiry enquiry) {

        return String.format("%d,%s,%s,%s,%s",
                enquiry.getEnquiryID(),
                enquiry.getApplicant().getNric(),
                enquiry.getProject().getProjectName(),
                enquiry.getContent().replace(",", "\\,"),
                enquiry.getResponse().replace(",", "\\,"));
    }
}
