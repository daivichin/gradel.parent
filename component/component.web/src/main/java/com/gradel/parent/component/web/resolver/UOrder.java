package com.gradel.parent.component.web.resolver;

import lombok.Data;
import org.springframework.util.Assert;

@Data
public class UOrder {
    private String column;
    private Boolean asc;

    private UOrder(String column, Boolean asc) {
        this.column = '`' + column + '`';
        this.asc = asc;
    }

    public static UOrder build(String order) {
        String[] s = order.split("@");
        Assert.isTrue(s.length == 2, "pageOrder Invalid");
        return new UOrder(s[0], Boolean.parseBoolean(s[1]));
    }
}
