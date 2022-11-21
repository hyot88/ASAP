package com.fourtwod;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.time.YearMonth;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TempTest {

    @Test
    public void test() {
        LocalDate localDate = LocalDate.now();
        int localDateYear = localDate.getYear();
        String thisMonth = String.format("%02d", localDate.getMonthValue());
        String thisMonthCondition = localDateYear + thisMonth + "01";

        LocalDate lastLocalDate = localDate.minusMonths(1);
        int lastLocalDateYear = lastLocalDate.getYear();
        String lastMonth = String.format("%02d", lastLocalDate.getMonthValue());
        String lastMonthCondition = lastLocalDateYear + lastMonth + "01";

        final int HUICK_HALF = 15;
        final int HUICK_FULL = YearMonth.from(LocalDate.now().minusMonths(1)).lengthOfMonth();


        System.out.println(lastMonthCondition);
        System.out.println(thisMonthCondition);
        System.out.println(HUICK_HALF);
        System.out.println(HUICK_FULL);
    }
}
