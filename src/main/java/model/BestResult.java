package model;

public class BestResult {

    private String userName;
    private Integer countCorrect;
    private Integer countAll;
    private String time;

    public BestResult(String userName, Integer countAll, Integer countCorrect, String time) {
        this.userName = userName;
        this.countAll = countAll;
        this.countCorrect = countCorrect;
        this.time = time;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getCountAll() {
        return countAll;
    }

    public void setCountAll(Integer countAll) {
        this.countAll = countAll;
    }

    public Integer getCountCorrect() {
        return countCorrect;
    }

    public void setCountCorrect(Integer countCorrect) {
        this.countCorrect = countCorrect;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
