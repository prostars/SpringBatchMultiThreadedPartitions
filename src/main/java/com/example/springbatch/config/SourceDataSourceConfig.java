package com.example.springbatch.config;

import com.example.springbatch.repository.source.SourceNameRepository;
import javax.sql.DataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableJpaRepositories(
    basePackageClasses = SourceNameRepository.class,
    entityManagerFactoryRef = "sourceEntityManger",
    transactionManagerRef = "sourceTransactionManager"
)
public class SourceDataSourceConfig {
  
  @Primary
  @Bean
  @ConfigurationProperties(prefix = "spring.datasource.source")
  public DataSource sourceDataSource() {
    return DataSourceBuilder.create().build();
  }

  @Primary
  @Bean
  public LocalContainerEntityManagerFactoryBean sourceEntityManger() {
    LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
    entityManagerFactoryBean.setDataSource(sourceDataSource());
    entityManagerFactoryBean.setPackagesToScan("com.example.springbatch.entity");
    entityManagerFactoryBean.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
    return entityManagerFactoryBean;
  }

  @Primary
  @Bean
  public PlatformTransactionManager sourceTransactionManager() {
    JpaTransactionManager transactionManager = new JpaTransactionManager();
    transactionManager.setEntityManagerFactory(sourceEntityManger().getObject());
    return transactionManager;
  }

  @Bean
  public JdbcTemplate sourceJdbcTemplate(DataSource sourceDataSource) {
    return new JdbcTemplate(sourceDataSource);
  }
}
