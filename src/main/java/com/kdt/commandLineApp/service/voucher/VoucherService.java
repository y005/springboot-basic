package com.kdt.commandLineApp.service.voucher;

import com.kdt.commandLineApp.exception.WrongVoucherParamsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VoucherService {
    private VoucherRepository voucherRepository;

    @Autowired
    public VoucherService(VoucherRepository voucherRepository) {
        this.voucherRepository = voucherRepository;
    }

    public void addVoucher(String type, int amount) throws WrongVoucherParamsException {
        try {
            voucherRepository.add(new Voucher(type, amount));
        }
        catch (Exception e) {
            throw new WrongVoucherParamsException();
        }
    }

    public Optional<Voucher> getVoucher(long id) {
        return voucherRepository.get(id);
    }

    public List<Voucher> getVouchers(int page, int size, String type) {
        return voucherRepository.getAll(page, size, type);
    }

    public void removeVoucher(long id) {
        voucherRepository.remove(id);
    }
}
