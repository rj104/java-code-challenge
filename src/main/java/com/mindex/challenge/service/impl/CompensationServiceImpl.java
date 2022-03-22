package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.CompensationRepository;
import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.service.CompensationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class CompensationServiceImpl implements CompensationService {

    private static final Logger LOG = LoggerFactory.getLogger(CompensationServiceImpl.class);

    @Autowired
    private CompensationRepository compensationRepo;

    @Override
    public Compensation create(Compensation compensation) {
        LOG.debug("Creating compensation [{}]", compensation);
        compensationRepo.insert(compensation);
        return compensation;
    }

    @Override
    public Compensation read(String id) {
        LOG.debug("Read compensation for employeeId: " + id);

        Compensation compensation = compensationRepo.findByEmployeeId(id);
        if(compensation == null)
        {
            throw new RuntimeException("Invalid employee id: " + id);
        }
        return compensation;
    }
}
