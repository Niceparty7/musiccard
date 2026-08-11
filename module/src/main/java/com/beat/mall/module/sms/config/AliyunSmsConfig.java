package com.beat.mall.module.sms.config;

import com.aliyun.auth.credentials.Credential;
import com.aliyun.auth.credentials.provider.ICredentialProvider;
import com.aliyun.auth.credentials.provider.StaticCredentialProvider;
import com.aliyun.sdk.service.dypnsapi20170525.AsyncClient;
import darabonba.core.client.ClientOverrideConfiguration;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class AliyunSmsConfig {

    @Resource
    private AliyunSmsProperties props;

    @Bean
    public AsyncClient pnvsClient() throws Exception {
        // V2.0 SDK 期望的包是 com.aliyun.auth.credentials.*（不是 credentials-java 0.3.10 的 credentials.*）
        Credential credential = Credential.builder()
                .accessKeyId(props.getAccessKeyId())
                .accessKeySecret(props.getAccessKeySecret())
                .build();
        ICredentialProvider provider = StaticCredentialProvider.create(credential);

        return AsyncClient.builder()
                .region("cn-hangzhou")
                .credentialsProvider(provider)
                .overrideConfiguration(
                        ClientOverrideConfiguration.create()
                                .setEndpointOverride("dypnsapi.aliyuncs.com")
                )
                .build();
    }

    @Bean
    public Executor smsExecutor() {
        ThreadPoolTaskExecutor exec = new ThreadPoolTaskExecutor();
        exec.setCorePoolSize(16);
        exec.setMaxPoolSize(40);
        exec.setQueueCapacity(500);
        exec.setThreadNamePrefix("sms-");
        exec.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        exec.initialize();
        return exec;
    }
}
