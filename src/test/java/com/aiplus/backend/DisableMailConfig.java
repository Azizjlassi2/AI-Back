package com.aiplus.backend;

import org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

@Configuration
@EnableAutoConfiguration(exclude = MailSenderAutoConfiguration.class)
public class DisableMailConfig {
}