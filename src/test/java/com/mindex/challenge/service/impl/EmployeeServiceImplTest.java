package com.mindex.challenge.service.impl;

import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.EmployeeService;
import org.assertj.core.util.Arrays;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EmployeeServiceImplTest {

    private String employeeUrl;
    private String employeeIdUrl;
    private String reportingStructureUrl;

    @Autowired
    private EmployeeService employeeService;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Before
    public void setup() {
        employeeUrl = "http://localhost:" + port + "/employee";
        employeeIdUrl = "http://localhost:" + port + "/employee/{id}";
        reportingStructureUrl = "http://localhost:" + port + "/reportingStructure/{id}";
    }

    @Test
    public void testCreateReadUpdate() {
        Employee testEmployee = new Employee();
        testEmployee.setFirstName("John");
        testEmployee.setLastName("Doe");
        testEmployee.setDepartment("Engineering");
        testEmployee.setPosition("Developer");

        // Create checks
        Employee createdEmployee1 = restTemplate.postForEntity(employeeUrl, testEmployee, Employee.class).getBody();

        assertNotNull(createdEmployee1.getEmployeeId());
        assertEmployeeEquivalence(testEmployee, createdEmployee1);


        // Read checks
        Employee readEmployee = restTemplate.getForEntity(employeeIdUrl, Employee.class, createdEmployee1.getEmployeeId()).getBody();
        assertEquals(createdEmployee1.getEmployeeId(), readEmployee.getEmployeeId());
        assertEmployeeEquivalence(createdEmployee1, readEmployee);

        // getReportingStructure checks
        Employee testEmployee2 = new Employee();
        testEmployee2.setFirstName("Pete");
        testEmployee2.setLastName("Best");
        testEmployee2.setDepartment("Engineering");
        testEmployee2.setPosition("Developer");
        Employee createdEmployee2 = restTemplate.postForEntity(employeeUrl, testEmployee2, Employee.class).getBody();

        Employee testEmployee3 = new Employee();
        testEmployee3.setFirstName("Ringo");
        testEmployee3.setLastName("Starr");
        testEmployee3.setDepartment("Engineering");
        testEmployee3.setPosition("Team Lead");
        List<Employee> ringoDirectReports = new ArrayList<Employee>() {{
            add(createdEmployee1);
            add(createdEmployee2);
        }};
        testEmployee3.setDirectReports(ringoDirectReports);
        Employee createdEmployee3 = restTemplate.postForEntity(employeeUrl, testEmployee3, Employee.class).getBody();

        // Test Ringo direct reports.
        ReportingStructure reportingStructure = restTemplate.getForEntity(reportingStructureUrl, ReportingStructure.class, createdEmployee3.getEmployeeId()).getBody();
        assertEquals(2, reportingStructure.getNumReports());

        Employee testEmployee4 = new Employee();
        testEmployee4.setFirstName("Paul");
        testEmployee4.setLastName("McCartney");
        testEmployee4.setDepartment("Engineering");
        testEmployee4.setPosition("Developer");
        Employee createdEmployee4 = restTemplate.postForEntity(employeeUrl, testEmployee4, Employee.class).getBody();

        Employee testEmployee5 = new Employee();
        testEmployee5.setFirstName("John");
        testEmployee5.setLastName("Lennon");
        testEmployee5.setDepartment("Engineering");
        testEmployee5.setPosition("Manager");
        List<Employee> johnDirectReports = new ArrayList<Employee>() {{
            add(createdEmployee3);
            add(createdEmployee4);
        }};
        testEmployee5.setDirectReports(johnDirectReports);
        Employee createdEmployee5 = restTemplate.postForEntity(employeeUrl, testEmployee5, Employee.class).getBody();

        // Test John direct reports.
        ReportingStructure reportingStructure2 = restTemplate.getForEntity(reportingStructureUrl, ReportingStructure.class, createdEmployee5.getEmployeeId()).getBody();
        assertEquals(4, reportingStructure2.getNumReports());

        // Update checks
        readEmployee.setPosition("Development Manager");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Employee updatedEmployee =
                restTemplate.exchange(employeeIdUrl,
                        HttpMethod.PUT,
                        new HttpEntity<Employee>(readEmployee, headers),
                        Employee.class,
                        readEmployee.getEmployeeId()).getBody();

        assertEmployeeEquivalence(readEmployee, updatedEmployee);
    }

    private static void assertEmployeeEquivalence(Employee expected, Employee actual) {
        assertEquals(expected.getFirstName(), actual.getFirstName());
        assertEquals(expected.getLastName(), actual.getLastName());
        assertEquals(expected.getDepartment(), actual.getDepartment());
        assertEquals(expected.getPosition(), actual.getPosition());
    }

}
