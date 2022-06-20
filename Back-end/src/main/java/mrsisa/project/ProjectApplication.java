package mrsisa.project;

import mrsisa.project.service.AdminService;
import mrsisa.project.service.CottageService;
import mrsisa.project.service.PeriodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
@EnableCaching
public class ProjectApplication implements CommandLineRunner {

	@Autowired
	private AdminService adminService;

	@Autowired
	private CottageService cottageService;

	@Autowired
	private PeriodService periodService;

	@Override
	public void run(String... args) {
		this.adminService.createFirstAdmin();

		//Cottage cottage = this.cottageService.createFirstCottage();
		//this.periodService.createPeriodForCottage(cottage);

	}

	public static void main(String[] args) {
		SpringApplication.run(ProjectApplication.class, args);
	}

}
