package com.jobseeker.game.manager;

import com.jobseeker.game.entity.Person;
import com.jobseeker.game.entity.Person.PersonState;

public class FinanceManager {
    private static FinanceManager instance;

    private float governmentRevenue;
    private float governmentExpenditure;
    private float fiscalBalance;

    private float nationalDebt;
    private float debtInterestRate;

    private float personalIncomeTaxRate;
    private float corporateTaxRate;
    private float valueAddedTaxRate;

    private float educationFeeLevel;
    private float transportationFeeLevel;
    private float healthcareFeeLevel;

    public static void init() {
        instance = new FinanceManager();
        instance.nationalDebt = 0f;
        instance.debtInterestRate = 0.03f;
        instance.personalIncomeTaxRate = 0.15f;
        instance.corporateTaxRate = 0.20f;
        instance.valueAddedTaxRate = 0.10f;
        instance.educationFeeLevel = 0.5f;
        instance.transportationFeeLevel = 0.3f;
        instance.healthcareFeeLevel = 0.4f;
    }

    public static FinanceManager getInstance() {
        return instance;
    }

    public void update() {
        calculateMonthlyRevenue();
        calculateMonthlyExpenditure();
        fiscalBalance = governmentRevenue - governmentExpenditure;
        updateNationalDebt();
    }

    private void calculateMonthlyRevenue() {
        governmentRevenue = 0f;
        governmentRevenue += calculatePersonalIncomeTax();
        governmentRevenue += calculateCorporateTax();
        governmentRevenue += calculateVAT();
        governmentRevenue += calculateServiceFees();
    }

    private float calculatePersonalIncomeTax() {
        float totalIncome = 0f;
        for (Person person : PopulationManager.getInstance().getLaborForce()) {
            if (person.state == PersonState.EMPLOYED) {
                totalIncome += person.currentWage;
            }
        }
        return totalIncome * personalIncomeTaxRate;
    }

    private float calculateCorporateTax() {
        float totalCorporateProfit = 0f;
        for (var enterprise : EnterpriseManager.getInstance().getAllEnterprises()) {
            if (enterprise.state != com.jobseeker.game.entity.Enterprise.EnterpriseState.BANKRUPT) {
                totalCorporateProfit += Math.max(0, enterprise.profit);
            }
        }
        return totalCorporateProfit * corporateTaxRate;
    }

    private float calculateVAT() {
        float gdp = GameStateManager.getInstance().getGdp();
        return gdp * 0.001f * valueAddedTaxRate;
    }

    private float calculateServiceFees() {
        float educationRevenue = PopulationManager.getInstance().getChildCount() * educationFeeLevel * 10f;
        float transportRevenue = PopulationManager.getInstance().getLaborCount() * transportationFeeLevel * 5f;
        float healthcareRevenue = PopulationManager.getInstance().getTotalPopulation() * healthcareFeeLevel * 2f;
        return educationRevenue + transportRevenue + healthcareRevenue;
    }

    private void calculateMonthlyExpenditure() {
        governmentExpenditure = 0f;
        governmentExpenditure += calculateEducationExpenditure();
        governmentExpenditure += calculateHealthcareExpenditure();
        governmentExpenditure += calculateSocialSecurityExpenditure();
        governmentExpenditure += calculateInfrastructureExpenditure();
        governmentExpenditure += calculateAdministrationExpenditure();
        governmentExpenditure += nationalDebt * debtInterestRate / 12f;
    }

    private float calculateEducationExpenditure() {
        int studentCount = PopulationManager.getInstance().getChildCount() / 2;
        return studentCount * 50f;
    }

    private float calculateHealthcareExpenditure() {
        int population = PopulationManager.getInstance().getTotalPopulation();
        return population * 10f;
    }

    private float calculateSocialSecurityExpenditure() {
        int elderlyCount = PopulationManager.getInstance().getElderlyCount();
        return elderlyCount * 100f;
    }

    private float calculateInfrastructureExpenditure() {
        return 5000f;
    }

    private float calculateAdministrationExpenditure() {
        int population = PopulationManager.getInstance().getTotalPopulation();
        return population * 2f;
    }

    private void updateNationalDebt() {
        if (fiscalBalance < 0) {
            nationalDebt -= fiscalBalance;
        } else if (nationalDebt > 0 && fiscalBalance > 0) {
            nationalDebt = Math.max(0, nationalDebt - fiscalBalance * 0.1f);
        }

        float debtToGdp = nationalDebt / GameStateManager.getInstance().getGdp();
        if (debtToGdp > 1.5f) {
            debtInterestRate = 0.08f;
        } else if (debtToGdp > 1.0f) {
            debtInterestRate = 0.06f;
        } else if (debtToGdp > 0.6f) {
            debtInterestRate = 0.04f;
        } else {
            debtInterestRate = 0.03f;
        }
    }

    public void setPersonalIncomeTaxRate(float rate) { this.personalIncomeTaxRate = rate; }
    public void setCorporateTaxRate(float rate) { this.corporateTaxRate = rate; }
    public void setValueAddedTaxRate(float rate) { this.valueAddedTaxRate = rate; }
    public void setEducationFeeLevel(float level) { this.educationFeeLevel = level; }
    public void setTransportationFeeLevel(float level) { this.transportationFeeLevel = level; }
    public void setHealthcareFeeLevel(float level) { this.healthcareFeeLevel = level; }

    public void issueGovernmentBonds(float amount) {
        nationalDebt += amount;
        GameStateManager.getInstance().addGdp(amount * 0.9f);
    }

    public float getGovernmentRevenue() { return governmentRevenue; }
    public float getGovernmentExpenditure() { return governmentExpenditure; }
    public float getFiscalBalance() { return fiscalBalance; }
    public float getNationalDebt() { return nationalDebt; }
    public float getDebtInterestRate() { return debtInterestRate; }

    public float getDebtToGdpRatio() {
        float gdp = GameStateManager.getInstance().getGdp();
        return gdp > 0 ? nationalDebt / gdp : 0;
    }

    public float getAverageLivingCost() {
        return educationFeeLevel * 50 + transportationFeeLevel * 30 + healthcareFeeLevel * 20;
    }

    public float getPersonalIncomeTaxRate() { return personalIncomeTaxRate; }
    public float getCorporateTaxRate() { return corporateTaxRate; }
    public float getValueAddedTaxRate() { return valueAddedTaxRate; }
    public float getEducationFeeLevel() { return educationFeeLevel; }
    public float getTransportationFeeLevel() { return transportationFeeLevel; }
    public float getHealthcareFeeLevel() { return healthcareFeeLevel; }
}
