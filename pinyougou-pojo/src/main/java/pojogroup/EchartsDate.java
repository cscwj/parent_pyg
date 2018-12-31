package pojogroup;

import java.io.Serializable;
import java.math.BigDecimal;

public class EchartsDate implements Serializable {

    private String currentdate;
    private Integer count;
    private BigDecimal totalmoney;

    public String getCurrentdate() {
        return currentdate;
    }

    public void setCurrentdate(String currentdate) {
        this.currentdate = currentdate;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public BigDecimal getTotalmoney() {
        return totalmoney;
    }

    public void setTotalmoney(BigDecimal totalmoney) {
        this.totalmoney = totalmoney;
    }
}
