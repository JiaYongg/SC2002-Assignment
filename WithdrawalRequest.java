import java.util.Date;

public class WithdrawalRequest {
    private Application application;
    private Date dateRequested;
    private WithdrawalStatus status;

    public WithdrawalRequest(Application application) {
        this.application = application;
        this.dateRequested = new Date();
        this.status = WithdrawalStatus.PENDING;
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
