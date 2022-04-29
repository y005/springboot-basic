package com.kdt.commandLineApp.service.voucher;

import com.kdt.commandLineApp.AppContext;
import com.kdt.commandLineApp.exception.WrongVoucherParamsException;
import com.kdt.commandLineApp.service.voucher.JdbcVoucherRepository;
import com.kdt.commandLineApp.service.voucher.Voucher;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isA;

@SpringJUnitConfig(classes = {AppContext.class})
@ActiveProfiles("db")
class JdbcVoucherRepositoryTest {
    @Autowired
    JdbcVoucherRepository jdbcVoucherRepository;

    void settingVoucher(String type, int amount) {
        try {
            jdbcVoucherRepository.deleteAll();
            Voucher voucher = new Voucher(type, amount);
            jdbcVoucherRepository.add(voucher);
        }
        catch (WrongVoucherParamsException e) {
            e.printStackTrace();
        }
    }

    @Test
    void add() {
        settingVoucher("fixed",1000);

        List<Voucher> voucherList = jdbcVoucherRepository.getAll(0,10,null);
        int size = voucherList.size();
        Voucher voucher = voucherList.get(0);

        assertThat(size, is(1));
        assertThat(voucher, isA(Voucher.class));
        assertThat(voucher.getType(), is("fixed"));
        assertThat(voucher.getDiscountAmount(), is(1000));
    }

    @Test
    void remove() {
        settingVoucher("fixed",1000);
        long voucherId = jdbcVoucherRepository.getAll(0,1,null).get(0).getId();

        jdbcVoucherRepository.remove(voucherId);
        int size = jdbcVoucherRepository.getAll(0,10,null).size();

        assertThat(size, is(0));
    }

    @Test
    void get() {
        settingVoucher("fixed",1000);
        long voucherId = jdbcVoucherRepository.getAll(0,1,null).get(0).getId();
        Voucher voucher = jdbcVoucherRepository.get(voucherId).get();

        assertThat(voucher, isA(Voucher.class));
        assertThat(voucher.getId(), is(voucherId));
        assertThat(voucher.getType(), is("fixed"));
        assertThat(voucher.getDiscountAmount(), is(1000));
    }

    @Test
    void getAll() {
        settingVoucher("fixed", 1000);

        int result = jdbcVoucherRepository.getAll(0,10,null).size();

        assertThat(result, is(1));
    }

    @Test
    void deleteAllVoucher() {
        settingVoucher("fixed", 1000);

        int result1 = jdbcVoucherRepository.getAll(0,10,null).size();
        jdbcVoucherRepository.deleteAll();
        int result2 = jdbcVoucherRepository.getAll(0,10,null).size();

        assertThat(result1, is(1));
        assertThat(result2, is(0));
    }
}