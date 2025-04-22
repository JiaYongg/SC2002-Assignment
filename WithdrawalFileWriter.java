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
        return String.format("%d,%s,%s,%s,%d,%s",
                withdrawal.getRequestId(),
                withdrawal.getApplication().getApplicant().getNric(),
                withdrawal.getApplication().getProject().getProjectName(),
                withdrawal.getApplication().getFlatType().getName(),
                withdrawal.getDateRequested().getTime(),
                withdrawal.getStatus().toString());
    }
}
