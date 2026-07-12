package com.railconnect.admin.service;

import com.railconnect.admin.dtorequestresponse.FareRuleRequest;
import com.railconnect.admin.dtorequestresponse.FareRuleResponse;
import com.railconnect.admin.mapper.FareRuleMapper;
import com.railconnect.admin.repository.FareRuleRepository;
import com.railconnect.common.enums.CoachType;
import com.railconnect.common.exception.ResourceNotFoundException;
import com.railconnect.entity.FareRule;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FareManagementServiceImpl implements FareManagementService {

    private final FareRuleRepository fareRuleRepository;
    private final FareRuleMapper fareRuleMapper;

    public FareManagementServiceImpl(FareRuleRepository fareRuleRepository, FareRuleMapper fareRuleMapper) {
        this.fareRuleRepository = fareRuleRepository;
        this.fareRuleMapper = fareRuleMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<FareRuleResponse> getAllFareRules() {
        return fareRuleRepository.findAll().stream()
                .map(fareRuleMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public FareRuleResponse upsertFareRule(FareRuleRequest request) {
        FareRule fareRule = fareRuleRepository.findByCoachType(request.coachType())
                .orElseGet(FareRule::new);

        fareRule.coachType = request.coachType();
        fareRule.ratePerKm = request.ratePerKm();
        fareRule.updatedAt = LocalDateTime.now();

        return fareRuleMapper.toResponse(fareRuleRepository.save(fareRule));
    }

    @Override
    @Transactional
    public void deleteFareRule(CoachType coachType) {
        FareRule fareRule = fareRuleRepository.findByCoachType(coachType)
                .orElseThrow(() -> new ResourceNotFoundException("Fare rule", "coachType", coachType));
        fareRuleRepository.delete(fareRule);
    }

    @Override
    @Transactional(readOnly = true)
    public Double getEffectiveRatePerKm(CoachType coachType) {
        return fareRuleRepository.findByCoachType(coachType)
                .map(fareRule -> fareRule.ratePerKm)
                .orElse(null);
    }
}
