package org.prgrms.kdt;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {"org.prgrms.kdt.command", "org.prgrms.kdt.voucher", "org.prgrms.kdt.order"})

public class AppConfiguration {
}