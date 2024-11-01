package io.github.dfauth.mrfixit;

import io.github.dfauth.mrfixit.messages.ExecutionReport;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertNotNull;

@Slf4j
public class TestCase {

    @Test
    public void testIt() {
        var something = new Something();
        ExecutionReport er = ExecutionReport.newBuilder().populate(something).build();
        assertNotNull(er);
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Something {
        private BigDecimal cumQty;
    }
}
