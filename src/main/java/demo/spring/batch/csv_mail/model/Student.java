package demo.spring.batch.csv_mail.model;

public class Student {

    private String fullname;
    private String code;
    private String email;

    public Student() {
    }

    public String getCode() {
	return code;
    }

    public String getEmail() {
	return email;
    }

    public String getFullname() {
	return fullname;
    }

    public void setCode(String code) {
	this.code = code;
    }

    public void setEmail(String email) {
	this.email = email;
    }

    public void setFullname(String fullname) {
	this.fullname = fullname;
    }

}
