package com.wms.goals.service.implementation;

import com.wms.goals.service.interfacing.FinanceFormulaService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

@Service
public class FinanceFormulaServiceImpl implements FinanceFormulaService {
    private static final MathContext MC = new MathContext(16, RoundingMode.HALF_UP);

    @Override
    public BigDecimal pow(BigDecimal base, int n) {
        return new BigDecimal(Math.pow(base.doubleValue(), n), MC);
    }

    @Override
    public BigDecimal compound(BigDecimal present, double monthlyRate, int months){
        if (months <= 0) return present;
        BigDecimal onePlusR = BigDecimal.valueOf(1 + monthlyRate);
        return present.multiply(pow(onePlusR, months), MC);
    }

    @Override
    public BigDecimal pmt(BigDecimal fvGoal, BigDecimal fvLump, double monthlyRate, int months){
        if (months <= 0) return BigDecimal.ZERO;
        if (monthlyRate == 0d) return fvGoal.subtract(fvLump, MC).divide(new BigDecimal(months), MC);
        BigDecimal r = BigDecimal.valueOf(monthlyRate);
        BigDecimal onePlusR = BigDecimal.valueOf(1 + monthlyRate);
        BigDecimal pow = pow(onePlusR, months);
        BigDecimal numerator = fvGoal.subtract(fvLump, MC).multiply(r, MC);
        BigDecimal denom = pow.subtract(BigDecimal.ONE, MC);
        return denom.signum()==0? BigDecimal.ZERO : numerator.divide(denom, MC);
    }
}

