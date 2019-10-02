package com.thorinhood.dbtg.configs;

import com.thorinhood.dbtg.models.Student;
import net.sf.jsefa.Deserializer;
import net.sf.jsefa.common.lowlevel.filter.HeaderAndFooterFilter;
import net.sf.jsefa.csv.CsvIOFactory;
import net.sf.jsefa.csv.config.CsvConfiguration;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;

import javax.servlet.MultipartConfigElement;

@Configuration
public class MainConfig {
    @Bean
    MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxFileSize(DataSize.ofMegabytes(1000));
        factory.setMaxRequestSize(DataSize.ofMegabytes(1000));
        return factory.createMultipartConfig();
    }

    @Bean
    Deserializer studentsCsvDeserializer() {
        CsvConfiguration config = new CsvConfiguration();
        config.setLineFilter(new HeaderAndFooterFilter(1, false, true));
        config.setFieldDelimiter(',');
        return CsvIOFactory.createFactory(config, Student.class).createDeserializer();
    }
}
