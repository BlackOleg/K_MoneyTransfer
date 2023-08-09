package com.olegivanov.moneytransferservice.utiltests;

import com.olegivanov.moneytransferservice.utils.Utils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class UtilTests {
    @Test
    void formatCardNumberTets(){
        Assertions.assertEquals("1111-1111-1111-1111", Utils.formatCardNumber(1111_1111_1111_1111L));
    }
}
