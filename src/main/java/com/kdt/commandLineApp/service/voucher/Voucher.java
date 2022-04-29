package com.kdt.commandLineApp.service.voucher;

import com.kdt.commandLineApp.exception.CanNotDiscountException;
import com.kdt.commandLineApp.exception.WrongVoucherParamsException;
import lombok.Getter;

import java.io.Serializable;
import java.util.UUID;

@Getter
public class Voucher implements Serializable {
    private long id;
    private VoucherType type;
    private int discountAmount;

    public Voucher(long id, String type, int discountAmount) throws WrongVoucherParamsException {
        this.id = id;
        this.type = VoucherType.fromString(type);
        if (this.type.isValidAmount(discountAmount)) {
            this.discountAmount = discountAmount;
        }
        else {
            throw new WrongVoucherParamsException();
        }
    }

    public Voucher(String type, int discountAmount) throws WrongVoucherParamsException {
        this.type = VoucherType.fromString(type);
        if (this.type.isValidAmount(discountAmount)) {
            this.discountAmount = discountAmount;
        }
        else {
            throw new WrongVoucherParamsException();
        }
    }

    @Override
    public String toString() {
        return "id: " + id + "\ntype: " + type.toString() + "\namount: " + discountAmount +"\n" ;
    }

    public Float discount(int currentPrice) throws CanNotDiscountException {
        if (checkFixedVoucherValidationError(currentPrice) || (currentPrice < 0)) {
            throw new CanNotDiscountException();
        }
        return type.discount(currentPrice, discountAmount);
    }

    //Fixed voucher apply only if you purchase more than the discount amount.
    public boolean checkFixedVoucherValidationError(int currentPrice) {
        return (this.type == VoucherType.FiXED) && (currentPrice < this.discountAmount);
    }

    public String getType() {
        return type.toString();
    }
}
