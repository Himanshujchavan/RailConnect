package com.railconnect.pricing.service;

import com.railconnect.common.cache.CacheConfig;
import com.railconnect.common.exception.InvalidRequestException;
import com.railconnect.common.exception.ResourceNotFoundException;
import com.railconnect.entity.FestivalPricingRule;
import com.railconnect.entity.PeakSeasonPricingRule;
import com.railconnect.pricing.dtorequestresponse.FestivalPricingRuleRequest;
import com.railconnect.pricing.dtorequestresponse.FestivalPricingRuleResponse;
import com.railconnect.pricing.dtorequestresponse.PeakSeasonPricingRuleRequest;
import com.railconnect.pricing.dtorequestresponse.PeakSeasonPricingRuleResponse;
import com.railconnect.pricing.mapper.FestivalPricingRuleMapper;
import com.railconnect.pricing.mapper.PeakSeasonPricingRuleMapper;
import com.railconnect.pricing.repository.FestivalPricingRuleRepository;
import com.railconnect.pricing.repository.PeakSeasonPricingRuleRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PricingAdminServiceImpl implements PricingAdminService {

    private final FestivalPricingRuleRepository festivalPricingRuleRepository;
    private final PeakSeasonPricingRuleRepository peakSeasonPricingRuleRepository;
    private final FestivalPricingRuleMapper festivalPricingRuleMapper;
    private final PeakSeasonPricingRuleMapper peakSeasonPricingRuleMapper;

    public PricingAdminServiceImpl(FestivalPricingRuleRepository festivalPricingRuleRepository,
                                    PeakSeasonPricingRuleRepository peakSeasonPricingRuleRepository,
                                    FestivalPricingRuleMapper festivalPricingRuleMapper,
                                    PeakSeasonPricingRuleMapper peakSeasonPricingRuleMapper) {
        this.festivalPricingRuleRepository = festivalPricingRuleRepository;
        this.peakSeasonPricingRuleRepository = peakSeasonPricingRuleRepository;
        this.festivalPricingRuleMapper = festivalPricingRuleMapper;
        this.peakSeasonPricingRuleMapper = peakSeasonPricingRuleMapper;
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = CacheConfig.FESTIVAL_PRICING_RULES_CACHE, key = "'all'")
    public List<FestivalPricingRuleResponse> getAllFestivalRules() {
        return festivalPricingRuleRepository.findAllByOrderByDateAsc().stream()
                .map(festivalPricingRuleMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    @CacheEvict(value = CacheConfig.FESTIVAL_PRICING_RULES_CACHE, allEntries = true)
    public FestivalPricingRuleResponse upsertFestivalRule(FestivalPricingRuleRequest request) {
        FestivalPricingRule rule = festivalPricingRuleRepository.findByDate(request.date())
                .orElseGet(FestivalPricingRule::new);

        rule.date = request.date();
        rule.festivalName = request.festivalName();
        rule.surchargePercent = request.surchargePercent();

        return festivalPricingRuleMapper.toResponse(festivalPricingRuleRepository.save(rule));
    }

    @Override
    @Transactional
    @CacheEvict(value = CacheConfig.FESTIVAL_PRICING_RULES_CACHE, allEntries = true)
    public void deleteFestivalRule(Long id) {
        if (!festivalPricingRuleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Festival pricing rule", "id", id);
        }
        festivalPricingRuleRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = CacheConfig.PEAK_SEASON_PRICING_RULES_CACHE, key = "'all'")
    public List<PeakSeasonPricingRuleResponse> getAllPeakSeasonRules() {
        return peakSeasonPricingRuleRepository.findAllByOrderByStartDateAsc().stream()
                .map(peakSeasonPricingRuleMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    @CacheEvict(value = CacheConfig.PEAK_SEASON_PRICING_RULES_CACHE, allEntries = true)
    public PeakSeasonPricingRuleResponse createPeakSeasonRule(PeakSeasonPricingRuleRequest request) {
        validateDateRange(request);

        PeakSeasonPricingRule rule = new PeakSeasonPricingRule();
        rule.seasonName = request.seasonName();
        rule.startDate = request.startDate();
        rule.endDate = request.endDate();
        rule.surchargePercent = request.surchargePercent();

        return peakSeasonPricingRuleMapper.toResponse(peakSeasonPricingRuleRepository.save(rule));
    }

    @Override
    @Transactional
    @CacheEvict(value = CacheConfig.PEAK_SEASON_PRICING_RULES_CACHE, allEntries = true)
    public PeakSeasonPricingRuleResponse updatePeakSeasonRule(Long id, PeakSeasonPricingRuleRequest request) {
        validateDateRange(request);

        PeakSeasonPricingRule rule = peakSeasonPricingRuleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Peak season pricing rule", "id", id));

        rule.seasonName = request.seasonName();
        rule.startDate = request.startDate();
        rule.endDate = request.endDate();
        rule.surchargePercent = request.surchargePercent();

        return peakSeasonPricingRuleMapper.toResponse(peakSeasonPricingRuleRepository.save(rule));
    }

    @Override
    @Transactional
    @CacheEvict(value = CacheConfig.PEAK_SEASON_PRICING_RULES_CACHE, allEntries = true)
    public void deletePeakSeasonRule(Long id) {
        if (!peakSeasonPricingRuleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Peak season pricing rule", "id", id);
        }
        peakSeasonPricingRuleRepository.deleteById(id);
    }

    private void validateDateRange(PeakSeasonPricingRuleRequest request) {
        if (request.endDate().isBefore(request.startDate())) {
            throw new InvalidRequestException("endDate cannot be before startDate");
        }
    }
}
