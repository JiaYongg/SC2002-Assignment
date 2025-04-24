import java.util.Date;

public class WithdrawalRequest {
    private static int counter = 1000;

    private int requestId;
    private Application application;
    private Date dateRequested;
    private WithdrawalStatus status;

    public WithdrawalRequest(Application application) {
        this.requestId = counter++;
        this.application = application;
        this.dateRequested = new Date();
        this.status = WithdrawalStatus.PENDING;
    }

    public WithdrawalRequest(int requestId, Application application, Date dateRequested, WithdrawalStatus status) {
        this.requestId = requestId;
        this.application = application;
        this.dateRequested = dateRequested;
        this.status = status;

        if (requestId >= counter) {
            counter = requestId + 1;
        }
    }

    public int getRequestId() {
        return requestId;
    }

    public Application getApplication() {
        return application;
    }

    public Date getDateRequested() {
        return dateRequested;
    }

    public WithdrawalStatus getStatus() {
        return status;
    }

    public void setStatus(WithdrawalStatus status) {
        this.status = status;
    }
}
