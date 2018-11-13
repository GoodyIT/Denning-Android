package it.denning.model;

import java.util.ArrayList;
import java.util.List;

public class CalcModel {

    public List<CalcSectionModel> items = new ArrayList<>();


    public CalcModel() {
        CalcSectionModel malaysiaSection = new CalcSectionModel();
        malaysiaSection.title = "Malaysia";
        malaysiaSection.items.add(new NameCode("Stamp Duty & Legal Costs", "Stamp Duty & Legal Costs"));
        malaysiaSection.items.add(new NameCode("Income Tax", "Income Tax"));
        malaysiaSection.items.add(new NameCode("Real Property Gains Tax", "Real Property Gains Tax"));
        malaysiaSection.items.add(new NameCode("Loan amortisation", "Loan amortisation"));
        this.items.add(malaysiaSection);

        CalcSectionModel singaporeSection = new CalcSectionModel();
        singaporeSection.title = "Singapore";
        singaporeSection.items.add(new NameCode("Coming Soon", "Coming Soon"));
        this.items.add(singaporeSection);

        CalcSectionModel hongkongSection = new CalcSectionModel();
        hongkongSection.title = "Hong Kong";
        hongkongSection.items.add(new NameCode("Coming Soon", "Coming Soon"));
        this.items.add(hongkongSection);

        CalcSectionModel australiaSection = new CalcSectionModel();
        australiaSection.title = "Australia";
        australiaSection.items.add(new NameCode("Coming Soon", "Coming Soon"));
        this.items.add(australiaSection);

        CalcSectionModel newzealandSection = new CalcSectionModel();
        newzealandSection.title = "New Zealand";
        newzealandSection.items.add(new NameCode("Coming Soon", "Coming Soon"));
        this.items.add(newzealandSection);
    }
}
