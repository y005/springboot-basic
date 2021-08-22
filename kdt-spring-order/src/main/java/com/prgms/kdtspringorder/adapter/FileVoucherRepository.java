package com.prgms.kdtspringorder.adapter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.prgms.kdtspringorder.domain.model.voucher.FixedAmountVoucher;
import com.prgms.kdtspringorder.domain.model.voucher.PercentDiscountVoucher;
import com.prgms.kdtspringorder.domain.model.voucher.Voucher;
import com.prgms.kdtspringorder.domain.model.voucher.VoucherRepository;
import com.prgms.kdtspringorder.domain.model.voucher.VoucherType;

@Qualifier("file")
@Repository
public class FileVoucherRepository implements VoucherRepository {
    private static final String FILEPATH = System.getProperty("user.dir") + "/voucher_list.csv";
    private static final File FILE = new File(FILEPATH);
    private static final String COMMA = ",";
    private final Map<UUID, Voucher> storage = new ConcurrentHashMap<>();

    @Override
    public Optional<Voucher> findById(UUID voucherId) {
        return Optional.ofNullable(storage.get(voucherId));
    }

    @Override
    public Voucher save(Voucher voucher) {
        storage.put(voucher.getVoucherId(), voucher);
        return voucher;
    }

    @Override
    public Map<UUID, Voucher> findAll() {
        return storage;
    }

    @PostConstruct
    private void postConstruct() {
        if (!FILE.exists()) {
            return;
        }
        // file 가져와서 storage에 넣기
        try (BufferedReader br = new BufferedReader(new FileReader(FILE))) {
            // Files.newBufferedReader(Paths.get(FILEPATH))
            String line = "";
            while ((line = br.readLine()) != null) {
                String[] voucherInfo = line.split(COMMA);
                UUID id = UUID.fromString(voucherInfo[0]);
                VoucherType type = VoucherType.valueOf(voucherInfo[1]);
                long discount = Long.parseLong(voucherInfo[2]);

                if (type.equals(VoucherType.FIXED)) {
                    storage.put(id, new FixedAmountVoucher(id, discount));
                } else if (type.equals(VoucherType.PERCENT)) {
                    storage.put(id, new PercentDiscountVoucher(id, discount));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @PreDestroy
    private void preDestroy() {
        // storage를 file에 넣기
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE))) {
            storage.forEach((id, voucher) -> {
                VoucherType type = VoucherType.FIXED;
                if (voucher.getClass().getSimpleName().equals("PercentDiscountVoucher")) {
                    type = VoucherType.PERCENT;
                }

                try {
                    bw.write(String.format("%s%s%s%s%d\n", id, COMMA, type.toString(), COMMA, voucher.getDiscount()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            bw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
