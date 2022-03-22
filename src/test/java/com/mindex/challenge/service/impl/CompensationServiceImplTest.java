package com.mindex.challenge.service.impl;

import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.service.CompensationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CompensationServiceImplTest {

    private String compensationPostUrl;
    private String compensationGetUrl;

    @Autowired
    private CompensationService compensationService;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Before
    public void setup() {
        compensationPostUrl = "http://localhost:" + port + "/compensation";
        compensationGetUrl = "http://localhost:" + port + "/compensation/{id}";
    }

    @Test
    public void testCreateRead() {
        Compensation testCompensation = new Compensation();
        String testEmployeeId = "16a596ae-edd3-4847-99fe-c4518e82c86f";
        testCompensation.setEmployeeId(testEmployeeId);
        testCompensation.setSalary(100000);
        testCompensation.setDate("3-22-2022");

        //Checks on create
        Compensation createdCompensation = restTemplate.postForEntity(compensationPostUrl, testCompensation,Compensation.class).getBody();
        //This assertion may not be totally necessary.
        assertCompensationEqual(testCompensation, createdCompensation);

        //Check on read
        Compensation readCompensation = restTemplate.getForEntity(compensationGetUrl, Compensation.class, testEmployeeId).getBody();
        assertCompensationEqual(testCompensation, readCompensation);
    }

    private static void assertCompensationEqual(Compensation expected, Compensation actual)
    {
        assertEquals(expected.getEmployeeId(), actual.getEmployeeId());
        assertEquals(expected.getDate(), actual.getDate());
        assertEquals(expected.getSalary(), actual.getSalary());
    }
}
