public class Star {
//    private String mid;
//    private String realName;
//    private String roleName;
    private String name;
    private String dob;

    public Star(String name, String dob) {
        this.name = name;
//        this.realName = realName;
//        this.roleName = roleName;
        this.dob = dob;
    }

    public Star() {
    }

    @Override
    public String toString() {
        return "Star{" +
                "name='" + name + '\'' +
                ", dob='" + dob + '\'' +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

}
