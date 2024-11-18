package ir;

public class IRUse {
    private IRUser user;
    private IRValue value;

    public IRUse(IRUser user, IRValue value) {
        this.user = user;
        this.value = value;
    }
}
