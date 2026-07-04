package com.railconnect.reservation.service;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class PNRServiceImpl implements PNRService {

    @Override
    public String generatePNR() {
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyMMdd"));
        int sequence = ThreadLocalRandom.current().nextInt(1000, 10000);
        return "RC" + datePart + sequence;
    }
}