package models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * POJO representing a bank customer used in test data and page interactions.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomerData {

    private String firstName;
    private String lastName;
    private String postCode;
    private String expectedFullName;
    private String scenario;

    public CustomerData() {}

    public CustomerData(String firstName, String lastName, String postCode) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.postCode = postCode;
    }

    public String getFirstName()        { return firstName; }
    public String getLastName()         { return lastName; }
    public String getPostCode()         { return postCode; }
    public String getExpectedFullName() { return expectedFullName; }
    public String getScenario()         { return scenario; }

    public void setFirstName(String firstName)               { this.firstName = firstName; }
    public void setLastName(String lastName)                 { this.lastName = lastName; }
    public void setPostCode(String postCode)                 { this.postCode = postCode; }
    public void setExpectedFullName(String expectedFullName) { this.expectedFullName = expectedFullName; }
    public void setScenario(String scenario)                 { this.scenario = scenario; }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    @Override
    public String toString() {
        return "CustomerData{firstName='" + firstName + "', lastName='" + lastName +
               "', postCode='" + postCode + "'}";
    }
}
