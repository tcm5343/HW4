/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package studentregistrationapp;

/**
 *
 * @author miller
 */
public class StudentRecord {
    
    private String fName;
    private String lName;
    private String degreeStatus;
    private String major;
    
    // default constructor
    public StudentRecord(){
        fName = "";
        lName = "";
        degreeStatus = "";
        major = "";
    }
    
    public StudentRecord(String fName, String lName, String degreeStatus, String major){
        this.fName = fName;
        this.lName = lName;
        this.degreeStatus = degreeStatus;
        this.major = major;
    }

    public String getFirstName() {
        return fName;
    }

    public void setFirstName(String firstName) {
        this.fName = firstName;
    }

    public String getLastName() {
        return lName;
    }

    public void setLastName(String lastName) {
        this.lName = lastName;
    }

    public String getDegreeStatus() {
        return degreeStatus;
    }

    public void setDegreeStatus(String degreeStatus) {
        this.degreeStatus = degreeStatus;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }
    
    @Override
    // is there a reason tabs do not work here?
    public String toString(){
        String result = this.getFirstName() + " " + this.getLastName() + "\t " 
                + this.getDegreeStatus() + "\t " + this.getMajor();
        return result;
    }
    
}
