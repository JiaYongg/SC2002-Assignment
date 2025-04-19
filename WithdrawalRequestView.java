import java.util.List;

public class WithdrawalRequestView {
    public WithdrawalRequestView() {}

    public void displayPendingWithdrawals(List<WithdrawalRequest> requests) {
        System.out.println("=== Withdrawl Requests ===");

        if (requests == null || requests.isEmpty()) {
            System.out.println("No withdrawal requests found.");
            return;
        }

        for (WithdrawalRequest req : requests) {
            System.out.println("Application: " + req.getApplication().getProject().getProjectName());
            System.out.println("Flat Type: " + req.getApplication().getFlatType().getName());
            System.out.println("Requested On: " + req.getDateRequested());
            System.out.println("Status: " + req.getStatus());
            System.out.println();
        }

    }
}
