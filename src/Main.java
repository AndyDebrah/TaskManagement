
import services.ProjectService;
import services.ReportService;
import services.TaskService;
import utils.ConsoleMenu;
import utils.Seed;

import java.util.Scanner;


public class Main {


    static void main(String[] args) {
        final ProjectService projectService = new ProjectService(Seed.seedProjects());
       final TaskService taskService = new TaskService(Seed.seedTasks(), projectService);
       final ReportService reportService = new ReportService(projectService, taskService);

        final  ConsoleMenu menu;
        final  Scanner scanner;

        scanner = new Scanner(System.in);
        menu = new ConsoleMenu(projectService, taskService, reportService, scanner);
        ConsoleApp app = new ConsoleApp(menu, scanner, projectService, taskService, reportService);
        app.runApplication();


        scanner.close();
        menu.displayExitMessage();
    }
}
