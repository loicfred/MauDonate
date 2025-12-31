package mau.donate.objects.derived;

import mau.donate.objects.Warehouse;

import java.text.DecimalFormat;
import java.time.LocalDateTime;

public class D_Warehouse extends Warehouse {

    public int ItemQuantity;
    public long CapacityUtilization;
    public double CapacityUtilizationRate;
    public LocalDateTime LatestAdditionAt;

    public D_Warehouse() {}

    public String getUtilizationRate() {
        return new DecimalFormat("#.##").format(CapacityUtilizationRate);
    }
}
