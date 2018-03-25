package com.akamai.processingsystem;

import com.akamai.processingsystem.job.Job;
import com.univocity.parsers.common.processor.BeanListProcessor;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ProcessingSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProcessingSystemApplication.class, args);
	}

	@Bean
	public BeanListProcessor<Job> jobBeanListProcessor()
	{
		return new BeanListProcessor<>(Job.class);
	}

	@Bean
	public CsvParser csvParser(BeanListProcessor<Job> jobBeanListProcessor)
	{
		CsvParserSettings settings = new CsvParserSettings();
		settings.getFormat().setLineSeparator("\n");
		settings.setProcessor(jobBeanListProcessor);
		settings.setHeaderExtractionEnabled(true);
		return new CsvParser(settings);
	}
}
