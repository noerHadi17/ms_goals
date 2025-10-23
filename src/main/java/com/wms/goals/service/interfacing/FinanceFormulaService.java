package com.wms.goals.service.interfacing;

import java.math.BigDecimal;

public interface FinanceFormulaService {
    BigDecimal pow(BigDecimal base, int n);
    BigDecimal compound(BigDecimal present, double monthlyRate, int months);
    BigDecimal pmt(BigDecimal fvGoal, BigDecimal fvLump, double monthlyRate, int months);
}

