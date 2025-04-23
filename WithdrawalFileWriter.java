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
    protected String formatLine(WithdrawalRequest withdrawal) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String formattedDate = sdf.format(withdrawal.getDateRequested());
        return String.format("%d,%s,%s,%s,%d,%s",
                withdrawal.getRequestId(),
                withdrawal.getApplication().getApplicant().getNric(),
                withdrawal.getApplication().getProject().getProjectName(),
                withdrawal.getApplication().getFlatType().getName(),
                formattedDate,
                withdrawal.getStatus().toString());
    }
}
