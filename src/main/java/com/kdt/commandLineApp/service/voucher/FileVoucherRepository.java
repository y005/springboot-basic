package com.kdt.commandLineApp.service.voucher;

import com.kdt.commandLineApp.AppProperties;
import com.kdt.commandLineApp.exception.FileSaveException;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toMap;

@Repository
public class FileVoucherRepository implements VoucherRepository {
    private String fileName;
    private Map<Long, Voucher> map = new ConcurrentHashMap<>();

    public FileVoucherRepository(AppProperties appProperties) {
        try {
            this.fileName = appProperties.getVoucher_info();
            loadFile(fileName);
        }
        catch (Exception e) {
            //no need to throw exception (it can use empty array list)
            e.printStackTrace();
        }
    }

    public void loadFile(String fileName) throws Exception {
        List<Voucher> vouchers;
        try {
            FileInputStream fis = new FileInputStream(fileName);
            ObjectInputStream ois = new ObjectInputStream(fis);
            vouchers = (List) ois.readObject();
            map = (Map<Long, Voucher>) vouchers.stream().collect(toMap((e)-> e.getId(),(e)-> e));
        }
        catch (Exception e) {
            throw new FileSaveException();
        }
    }

    public void savefile() throws IOException {
        List<Voucher> vouchers = map.values().stream().collect(toCollection(ArrayList::new));

        try {
            FileOutputStream fop = new FileOutputStream(fileName);
            ObjectOutputStream oos = new ObjectOutputStream(fop);
            oos.writeObject(vouchers);
        }
        catch (Exception e) {
            throw e;
        }
    }

    @Override
    public void add(Voucher voucher) {
        try {
            map.put((long) Math.random(), voucher);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Optional<Voucher> get(long id) {
        return Optional.ofNullable(map.get(id));
    }

    @Override
    public List<Voucher> getAll(int page, int size, String type) {
        List<Voucher> voucherList;

        if ((page < 0) || (size < 0)) {
            return List.of();
        }
        voucherList = map.values()
                .stream()
                .filter((e)-> type.equals(null)||e.getType().equals(type))
                .toList();
        if (page * size >= voucherList.size()) {
            return List.of();
        }
        return voucherList.stream()
                .skip(page * size)
                .limit(size)
                .toList();
    }

    @Override
    public void remove(long id) {
        map.remove(id);
    }

    @Override
    public void deleteAll() {
        map.clear();
    }

    @Override
    public void destroy() throws Exception {
        savefile();
    }
}
