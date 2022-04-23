package com.kdt.commandLineApp;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@SpringJUnitConfig(classes = {AppContext.class})
@ActiveProfiles("db")
public class AppContextTest {
    @Autowired
    AppProperties appProperties;

    //DI는 잘되는 데 yaml파일에서 값을 가지고 오지 못하네요...
    @Test
    public void test() {
        assertThat(appProperties.getCustomer_blacklist_info(), is("src/main/resources/customer_blacklist.csv"));
        assertThat(appProperties.getVoucher_info(), is("src/main/resources/voucher_info.dat"));
        assertThat(appProperties.getDb_driver_class(), is("jdbc:mysql://localhost:3306"));
        assertThat(appProperties.getDb_url(), is("com.mysql.cj.jdbc.Driver"));
        assertThat(appProperties.getDb_user(), is("root"));
        assertThat(appProperties.getDb_pwd(), is("samho101"));
    }
}
