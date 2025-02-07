package com.njpa.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.njpa.jamesORMModel.Admin;
import com.njpa.jamesORMRepo.AdminRepo;

@Component
public class AdminDataInitializer implements CommandLineRunner {

    @Autowired
    private AdminRepo adminRepository;
    
    @Value("${userName1}")
    private String userName1;
    
    @Value("${userName2}")
    private String userName2;
    
    @Value("${password1}")
    private String password1;
    
    @Value("${password2}")
    private String password2;

    @Override
    public void run(String... args) throws Exception {
        long adminCount = adminRepository.count();

        if (adminCount == 0) {
            Admin admin1 = new Admin();
            admin1.setEmail(userName1);
            admin1.setPassword(password1);
            adminRepository.save(admin1);

            Admin admin2 = new Admin();
            admin2.setEmail(userName2);
            admin2.setPassword(password2);
            adminRepository.save(admin2);

            System.out.println("Inserted two admin users into the admin table.");
        } else {
            System.out.println("Admin users already exist. Skipping insertion.");
        }
    }
}
