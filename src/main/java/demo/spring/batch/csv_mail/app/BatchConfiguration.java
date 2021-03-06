package demo.spring.batch.csv_mail.app;

import javax.mail.internet.MimeMessage;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import demo.spring.batch.csv_mail.model.Student;
import demo.spring.batch.csv_mail.processor.StudentItemProcessor;
import demo.spring.batch.csv_mail.writer.MailBatchItemWriter;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Value("${spring.mail.sender}")
    private String sender;

    @Value("${demo.batch.data}")
    public String data;

    @Value("${demo.batch.attachment}")
    private String attachment;

    @Value("${demo.batch.notifications.email}")
    private String email;

    // tag::jobstep[]
    @Bean
    public Job importUserJob() {
	return jobBuilderFactory.get("importUserJob").incrementer(new RunIdIncrementer()).listener(listener())
		.flow(step1()).end().build();
    }

    @Bean
    public JobExecutionListener listener() {
	return new JobCompletionNotificationListener(email);
    }

    @Bean
    public StudentItemProcessor processor() {
	return new StudentItemProcessor(sender, attachment);
    }

    // tag::listener[]

    // tag::readerwriterprocessor[]
    @Bean
    public FlatFileItemReader<Student> reader() {
	FlatFileItemReader<Student> reader = new FlatFileItemReader<>();
	reader.setResource(new FileSystemResource(data));
	reader.setLinesToSkip(1);
	reader.setLineMapper(new DefaultLineMapper<Student>() {
	    {
		setLineTokenizer(new DelimitedLineTokenizer() {
		    {
			setNames(new String[] { "fullname", "code", "email" });
		    }
		});
		setFieldSetMapper(new BeanWrapperFieldSetMapper<Student>() {
		    {
			setTargetType(Student.class);
		    }
		});
	    }
	});
	return reader;
    }

    // end::listener[]

    @Bean
    public Step step1() {
	return stepBuilderFactory.get("step1").<Student, MimeMessage>chunk(10).reader(reader()).processor(processor())
		.writer(writer()).build();
    }
    // end::jobstep[]

    @Bean
    public MailBatchItemWriter writer() {
	MailBatchItemWriter writer = new MailBatchItemWriter();
	return writer;
    }
    // end::readerwriterprocessor[]
}
