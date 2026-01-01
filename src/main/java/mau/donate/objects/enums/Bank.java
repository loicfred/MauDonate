package mau.donate.objects.enums;

public enum Bank {
    MCB("Mauritius Commercial Bank"),
    SBM("State Bank of Mauritius"),
    MAUBANK("MauBank"),
    PAYPAL("PayPal"),
    ABSA_BANK("ABSA Bank"),
    HSBC_BANK("HSBC Bank"),
    STANDARD_CHARTERED("Standard Chartered Bank"),
    BANK_ONE("Bank One"),
    AFRASIA_BANK("AfrAsia Bank"),
    SILVER_BANK("SBM Silver Bank"),
    ABC_BANKING("ABC Banking Corporation"),
    OTHER("Other Bank");

    private String name;
    public String getName() { return name; }
    Bank(String name) { this.name = name; }
}
