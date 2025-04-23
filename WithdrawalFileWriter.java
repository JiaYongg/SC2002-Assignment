import java.text.SimpleDateFormat;
import java.util.Map;

public class WithdrawalFileWriter extends FileWriter<WithdrawalRequest> {
    public WithdrawalFileWriter() {
        super("Withdrawal.csv");
    }

    @Override
    public void writeToFile(Map<String, WithdrawalRequest> withdrawals) {
        writeCSV(withdrawals);
    }

    @Override
    protected String getHeader() {
        return "RequestID,ApplicantNRIC,ProjectName,FlatType,DateRequested,Status";
    }

    @Override
    protected String formatLine(WithdrawalRequest request) {
        return String.format("%s,%s,%s,%s,%s,%s",
                String.valueOf(request.getRequestId()), // ensure string format
                request.getApplication().getApplicant().getNric(),
                request.getApplication().getProject().getProjectName(),
                request.getApplication().getFlatType().getName(),
                new SimpleDateFormat("dd/MM/yyyy").format(request.getDateRequested()),
                request.getStatus().toString());
    }
}
