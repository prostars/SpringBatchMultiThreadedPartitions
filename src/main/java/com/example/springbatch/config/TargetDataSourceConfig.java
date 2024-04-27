package com.example.springbatch.config;

import com.example.springbatch.repository.target.TargetNickNameRepository;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableJpaRepositories(
    basePackageClasses = TargetNickNameRepository.class,
    entityManagerFactoryRef = "targetEntityManger",
    transactionManagerRef = "targetTransactionManager"
)
public class TargetDataSourceConfig {

  @Bean
  @ConfigurationProperties(prefix = "spring.datasource.target")
  public DataSource targetDataSource() {
    return DataSourceBuilder.create().build();
  }

  @Bean
  public LocalContainerEntityManagerFactoryBean targetEntityManger() {
    LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
    entityManagerFactoryBean.setDataSource(targetDataSource());
    entityManagerFactoryBean.setPackagesToScan("com.example.springbatch.entity");
    entityManagerFactoryBean.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
    return entityManagerFactoryBean;
  }

  @Bean
  public PlatformTransactionManager targetTransactionManager() {
    JpaTransactionManager transactionManager = new JpaTransactionManager();
    transactionManager.setEntityManagerFactory(targetEntityManger().getObject());
    return transactionManager;
  }

  @Bean
  public JdbcTemplate targetJdbcTemplate(
      @Qualifier("targetDataSource") DataSource targetDataSource) {
    return new JdbcTemplate(targetDataSource);
  }
}
