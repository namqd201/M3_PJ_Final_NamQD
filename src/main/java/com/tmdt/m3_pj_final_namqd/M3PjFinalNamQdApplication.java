package com.tmdt.m3_pj_final_namqd;

import com.tmdt.m3_pj_final_namqd.repository.MentorRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class M3PjFinalNamQdApplication {

    public static void main(String[] args) {
        SpringApplication.run(M3PjFinalNamQdApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        MentorRepository mentorRepository = ctx.getBean(MentorRepository.class);
        return args -> {
            int countt = mentorRepository.countMentorByDepartment("CNTT");
            System.out.println(countt);
        };
    }

}
