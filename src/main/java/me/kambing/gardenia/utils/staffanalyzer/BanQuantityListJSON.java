package me.kambing.gardenia.utils.staffanalyzer;

import com.google.gson.annotations.SerializedName;

class BanQuantityListJSON
{
    @SerializedName("success")
    boolean success;
    @SerializedName("watchdog_lastMinute")
    int watchdogLastMinute;
    @SerializedName("staff_rollingDaily")
    int staffRollingDaily;
    @SerializedName("watchdog_total")
    int watchdogTotal;
    @SerializedName("watchdog_rollingDaily")
    int watchdogRollingDaily;
    @SerializedName("staff_total")
    int staffTotal;

    public BanQuantityListJSON() {
    }

    public boolean isSuccess() {
        return this.success;
    }

    public int getWatchdogLastMinute() {
        return this.watchdogLastMinute;
    }

    public int getStaffRollingDaily() {
        return this.staffRollingDaily;
    }

    public int getWatchdogTotal() {
        return this.watchdogTotal;
    }

    public int getWatchdogRollingDaily() {
        return this.watchdogRollingDaily;
    }

    public int getStaffTotal() {
        return this.staffTotal;
    }

    public void setSuccess(final boolean success) {
        this.success = success;
    }

    public void setWatchdogLastMinute(final int watchdogLastMinute) {
        this.watchdogLastMinute = watchdogLastMinute;
    }

    public void setStaffRollingDaily(final int staffRollingDaily) {
        this.staffRollingDaily = staffRollingDaily;
    }

    public void setWatchdogTotal(final int watchdogTotal) {
        this.watchdogTotal = watchdogTotal;
    }

    public void setWatchdogRollingDaily(final int watchdogRollingDaily) {
        this.watchdogRollingDaily = watchdogRollingDaily;
    }

    public void setStaffTotal(final int staffTotal) {
        this.staffTotal = staffTotal;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof BanQuantityListJSON)) {
            return false;
        }
        final BanQuantityListJSON other = (BanQuantityListJSON)o;
        return other.canEqual(this) && this.isSuccess() == other.isSuccess() && this.getWatchdogLastMinute() == other.getWatchdogLastMinute() && this.getStaffRollingDaily() == other.getStaffRollingDaily() && this.getWatchdogTotal() == other.getWatchdogTotal() && this.getWatchdogRollingDaily() == other.getWatchdogRollingDaily() && this.getStaffTotal() == other.getStaffTotal();
    }

    protected boolean canEqual(final Object other) {
        return other instanceof BanQuantityListJSON;
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = result * 59 + (this.isSuccess() ? 79 : 97);
        result = result * 59 + this.getWatchdogLastMinute();
        result = result * 59 + this.getStaffRollingDaily();
        result = result * 59 + this.getWatchdogTotal();
        result = result * 59 + this.getWatchdogRollingDaily();
        result = result * 59 + this.getStaffTotal();
        return result;
    }

    @Override
    public String toString() {
        return "BanQuantityListJSON(success=" + this.isSuccess() + ", watchdogLastMinute=" + this.getWatchdogLastMinute() + ", staffRollingDaily=" + this.getStaffRollingDaily() + ", watchdogTotal=" + this.getWatchdogTotal() + ", watchdogRollingDaily=" + this.getWatchdogRollingDaily() + ", staffTotal=" + this.getStaffTotal() + ")";
    }
}
