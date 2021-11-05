public class Star {
//    private String mid;
    private String realName;
    private String roleName;
    private String dob;

    public Star(String realName, String roleName, String dob) {
        this.realName = realName;
        this.roleName = roleName;
        this.dob = dob;
    }

    public Star() {
    }

    @Override
    public String toString() {
        return "Star{" +
                "realName='" + realName + '\'' +
                ", roleName='" + roleName + '\'' +
                ", dob='" + dob + '\'' +
                '}';
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

}
