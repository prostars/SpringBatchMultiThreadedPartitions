package com.example.springbatch.config;

import java.util.Collections;
import java.util.List;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

@Configuration
public class SpringBatchDatabaseInitializer {
  
  @Bean
  public DataSourceInitializer sourceDataSourceInitializer(@Qualifier("sourceDataSource") DataSource dataSource) {
    return dataSourceInitializer(dataSource, List.of("org/springframework/batch/core/schema-mysql.sql", "sql/SourceSchema.sql"));
  }
  
  @Bean
  public DataSourceInitializer targetDataSourceInitializer(@Qualifier("targetDataSource") DataSource dataSource) {
    return dataSourceInitializer(dataSource, Collections.singletonList("sql/TargetSchema.sql"));
  }

  private DataSourceInitializer dataSourceInitializer(DataSource dataSource, List<String> schemaScriptPaths) {
    ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
    schemaScriptPaths.forEach(path -> populator.addScript(new ClassPathResource(path)));
    populator.setContinueOnError(true);
    populator.setIgnoreFailedDrops(true);
    
    DataSourceInitializer initializer = new DataSourceInitializer();
    initializer.setDataSource(dataSource);
    initializer.setDatabasePopulator(populator);
    return initializer;
  }
}
