package org.vgcs.config;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.InputStreamResource;
import org.springframework.transaction.PlatformTransactionManager;
import org.vgcs.JsonFileWriter;
import org.vgcs.model.RowOrder;
import org.vgcs.model.ProcessedOrder;
import org.vgcs.service.OrderProcessor;

import java.io.InputStream;

@Configuration
@RequiredArgsConstructor
public class BatchConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(BatchConfig.class);
    @Autowired
    private ItemWriter<ProcessedOrder> writer;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Value("${inputFile}")
    private String inputFile;

    @Bean
    public RowOrder order() {
        return new RowOrder();
    }

    @Bean
    public FlatFileItemReader<RowOrder> reader() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(inputFile);
        if (inputStream == null) {
            LOGGER.error("File not found");
            throw new RuntimeException("File not found in classpath: order-integration.csv");
        }

        return new FlatFileItemReaderBuilder<RowOrder>()
                .name("orderItemReader")
                .resource(new InputStreamResource(inputStream))
                .linesToSkip(1) // Skip header line
                .delimited()
                .names("id", "firstName", "lastName", "email", "supplierPid", "creditCardNumber", "creditCardType",
                        "orderId", "productPid", "shippingAddress", "country", "dateCreated", "quantity", "fullName",
                        "orderStatus")
                .fieldSetMapper(new BeanWrapperFieldSetMapper<RowOrder>() {{
                    setTargetType(RowOrder.class);
                }})
                .build();
    }

    @Bean
    public ItemProcessor<RowOrder, ProcessedOrder> itemProcessor() {
        return new OrderProcessor();
    }

    @Bean
    public ItemWriter<ProcessedOrder> writer() {
        return new JsonFileWriter();
    }

    @Bean
    public Step step1(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("step1", jobRepository)
                .<RowOrder, ProcessedOrder>chunk(10, transactionManager)
                .reader(reader())
                .processor(itemProcessor())
                .writer(writer())
                .build();
    }

    @Bean
    public Job importOrderJob(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new JobBuilder("importOrderJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(step1(jobRepository, transactionManager))
                .build();
    }
}
