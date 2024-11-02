package io.github.dfauth.mrfixit;

import io.github.dfauth.mrfixit.messages.ExecutionReport;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import quickfix.DecimalField;
import quickfix.FieldNotFound;

import java.math.BigDecimal;
import java.util.function.Supplier;

import static io.github.dfauth.trycatch.ExceptionalRunnable.tryCatch;
import static io.github.dfauth.trycatch.ExceptionalRunnable.tryCatchRunnable;
import static io.github.dfauth.trycatch.FunctionalUtils.peek;
import static org.junit.Assert.*;

@Slf4j
public class TestCase {

    @Test
    public void testIt() throws FieldNotFound {
        var something = new Something("orderId","execId",'A','B','C',bd(1.3),bd(1.12));
        ExecutionReport er = ExecutionReport.newBuilder().populate(something).build();
        assertNotNull(er);
        assertEquals(something.getCumQty(),er.getCumQty().getValue());
        er.withCumQty(bd -> assertEquals(something.getCumQty(), bd), failure());
    }

    private Runnable failure() {
        return () -> {
            fail();
        };
    }

    private BigDecimal bd(double d) {
        return BigDecimal.valueOf(d);
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Something {
        private String orderID;
        private String execID;
        private char execType;
        private char ordStatus;
        private char side;
        private BigDecimal leavesQty;
        private BigDecimal cumQty;
    }
}
