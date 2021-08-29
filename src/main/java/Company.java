public class Company {

    private String inn;
    private String kpp;
    private String orgn;
    private String dateOrgn;
    private String status;
    private String capital;
    private String aa;
    private String name;
    private String address;
    private String leader;
    private String desc;
    private int usages;

    public Company() {
        this.usages = 0;
    }

    public int getUsages() {
        return usages;
    }

    public void incUsages() {
        this.usages = this.usages + 1;
    }

    public void setAa(String aa) {
        this.aa = aa;
    }

    public String getAa() {
        return aa;
    }

    public String getCapital() {
        return capital;
    }

    public String getDateOrgn() {
        return dateOrgn;
    }

    public String getInn() {
        return inn;
    }

    public String getKpp() {
        return kpp;
    }

    public String getAddress() {
        return address;
    }

    public String getLeader() {
        return leader;
    }

    public String getName() {
        return name;
    }

    public String getOrgn() {
        return orgn;
    }

    public String getStatus() {
        return status;
    }

    public void setCapital(String capital) {
        this.capital = capital;
    }

    public String getDesc() {
        return desc;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setDateOrgn(String dateOrgn) {
        this.dateOrgn = dateOrgn;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setInn(String inn) {
        this.inn = inn;
    }

    public void setKpp(String kpp) {
        this.kpp = kpp;
    }

    public void setLeader(String leader) {
        this.leader = leader;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOrgn(String orgn) {
        this.orgn = orgn;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
